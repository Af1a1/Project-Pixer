package com.example.projectpixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class expressionFunction extends AppCompatActivity {

    private FirebaseVisionImage image; // preparing the input image
    private TextView textView; // Displaying the face detection data for the input image
    private ImageView imageView; //To display the selected image
    private Button snapBtn;
    private Button detectBtn;
    private Button selectBtn; // To select the image from device
    private Bitmap imageBitmap;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private float smileProb;
    private float rightEyeOpenProb;
    private float leftEyeOpenProbability;
    private FirebaseVisionPoint rightEarPos;
    private FirebaseVisionPoint leftEarPos;
    private float rotY;
    private float rotZ;
    private Rect bounds;

    private StorageTask mUploadTask;
    private Uri imageUri;

    //static final int LENGTH_LONG = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expression_function);

        snapBtn = findViewById(R.id.snapBtn);
        detectBtn = findViewById(R.id.detectBtn);
        selectBtn = findViewById(R.id.selectBtn);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("ExpressionDetection");

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("ExpressionDetection");

        textView.setMovementMethod(new ScrollingMovementMethod());

        // textView.setLineSpacing(1f, 1.1f);

        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectFaceFromImage();
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    static final int REQUEST_IMAGE_SELECT = 2;

    private void selectImage() {
        Intent selectImage = new Intent(Intent.ACTION_GET_CONTENT);
        selectImage.setType("image/*");
        startActivityForResult(Intent.createChooser(selectImage, "pick an image"), REQUEST_IMAGE_SELECT);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
       // Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        //takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
       // imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "pic_"+ String.valueOf(System.currentTimeMillis()) + ".jpg"));
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);


       if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }




    //get image uri from take picture intent
   /* public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            imageView.setImageBitmap(imageBitmap);
            textView.setText("");

        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                imageBitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(imageBitmap);
                textView.setText("");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void detectFaceFromImage() {
        String input = textView.getText().toString();
        if(input.equals("")) {
            try {

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

                FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                                .build();


                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(highAccuracyOpts);


                Toast.makeText(expressionFunction.this, "Calculating , Please wait ...", Toast.LENGTH_LONG).show();

                detector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {


                                if (faces.size() == 0) {
                                    Toast.makeText(expressionFunction.this, "No Face Detected :(", Toast.LENGTH_LONG).show();
                                    textView.setText("");

                                } else if (faces.size() == 1) {

                                    for (FirebaseVisionFace face : faces) {
                                        bounds = face.getBoundingBox();
                                        textView.append("Bounding Polygon " + "(" + bounds.centerX() + "," + bounds.centerY() + ")" + "\n");
                                        rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                        rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                                        textView.append("Angles of rotation " + "Y:" + rotY + "," + "Z: " + rotZ + "\n");
                                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                        // nose available):
                                        // If face tracking was enabled:
                                        if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                            int id = face.getTrackingId();
                                            textView.append("id: " + id + "\n\n");
                                        }
                                        FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                        if (leftEar != null) {
                                            leftEarPos = leftEar.getPosition();
                                            textView.append("LeftEarPos: " + "(" + leftEarPos.getX() + "," + leftEarPos.getY() + ")" + "\n");
                                        }
                                        FirebaseVisionFaceLandmark rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
                                        if (rightEar != null) {
                                            rightEarPos = rightEar.getPosition();
                                            textView.append("RightEarPos: " + "(" + rightEarPos.getX() + "," + rightEarPos.getY() + ")" + "\n");
                                        }

                                        // If contour detection was enabled:
                                        List<FirebaseVisionPoint> leftEyeContour =
                                                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                        List<FirebaseVisionPoint> upperLipBottomContour =
                                                face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

                                        // If classification was enabled:
                                        if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                            smileProb = face.getSmilingProbability();
                                            textView.append("SmileProbability: " + ("" + smileProb * 100).subSequence(0, 4) + "%" + "\n");
                                        }
                                        if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                            rightEyeOpenProb = face.getRightEyeOpenProbability();
                                            textView.append("RightEyeOpenProbability: " + ("" + rightEyeOpenProb * 100).subSequence(0, 4) + "%" + "\n");
                                        }
                                        if (face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                            leftEyeOpenProbability = face.getLeftEyeOpenProbability();
                                            textView.append("LeftEyeOpenProbability: " + ("" + leftEyeOpenProbability * 100).subSequence(0, 4) + "%" + "\n");
                                        }
                                    }
                                    //  if (mUploadTask == null ) {
                                    UploadImage();

                                    //  }
                                } else {
                                    Toast.makeText(expressionFunction.this, "Many Faces..Capture only one face :(", Toast.LENGTH_LONG).show();
                                    textView.setText("");
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Toast.makeText(expressionFunction.this, "Error :(", Toast.LENGTH_LONG).show();
                                    }
                                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadImage() {

        if(imageUri != null) {
            //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(expressionFunction.this,"Sucessfully Updated.",Toast.LENGTH_SHORT).show();

                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();

                            ExpressionDetectionUpload upload = new ExpressionDetectionUpload(url.toString(),
                                    "(" +bounds.centerX() + "," + bounds.centerY() + ")" ,
                                    "Y:" + rotY + "," + "Z: " + rotZ,
                                    "(" + leftEarPos.getX() + "," + leftEarPos.getY() + ")",
                                    "(" + rightEarPos.getX() + "," + rightEarPos.getY() + ")",
                                    ("" + smileProb * 100).subSequence(0, 4) + "%",
                                    ("" + rightEyeOpenProb * 100).subSequence(0, 4) + "%",
                                    ("" + leftEyeOpenProbability * 100).subSequence(0, 4) + "%");

                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(expressionFunction.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
                Toast.makeText(this,"No image selected ...",Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}