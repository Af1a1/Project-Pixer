package com.example.projectpixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ObjectFunction extends AppCompatActivity {

    private Button snapBtn;
    private Button detectBtn;
    private Button selectBtn;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private Paint myRectPaint;
    private TextView textView;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private StorageTask mUploadTask;
    private Uri imageUri;
    private int objects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_function);

        snapBtn = findViewById(R.id.snapBtn);
        detectBtn = findViewById(R.id.detectBtn);
        selectBtn = findViewById(R.id.selectBtn);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("ObjectDetection");

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("ObjectDetection");

        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectImage();
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

    private void selectImage(){
        Intent selectImage = new Intent(Intent.ACTION_GET_CONTENT);
        selectImage.setType("image/*");
        startActivityForResult(Intent.createChooser(selectImage,"pick an image"),REQUEST_IMAGE_SELECT);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            textView.setText("");
        }
        else if(requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK){
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

    private void detectImage(){
        String input = textView.getText().toString();
        if(input.equals("")) {


            myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(3);
            myRectPaint.setColor(Color.RED);
            myRectPaint.setStyle(Paint.Style.STROKE);

            FirebaseVisionObjectDetectorOptions options =
                    new FirebaseVisionObjectDetectorOptions.Builder()
                            .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
                            .enableMultipleObjects()
                            .enableClassification()  // Optional
                            .build();

            FirebaseVisionObjectDetector objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options);

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

            objectDetector.processImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<FirebaseVisionObject>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionObject> detectedObjects) {

                                    objects = detectedObjects.size();

                                    if (detectedObjects.size() == 0) {
                                        Toast.makeText(ObjectFunction.this, "No Objects :(", Toast.LENGTH_LONG).show();
                                    }

                                    Bitmap tempBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.RGB_565);
                                    Canvas tempCanvas = new Canvas(tempBitmap);
                                    tempCanvas.drawBitmap(imageBitmap, 0, 0, null);

                                    for (FirebaseVisionObject obj : detectedObjects) {
                                        Integer id = obj.getTrackingId();
                                        Rect bounds = obj.getBoundingBox();
                                        tempCanvas.drawRect(bounds, myRectPaint);


                                    }
                                    imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
                                    textView.setTextSize(20);
                                    textView.setText("No of Objects Detected : " + detectedObjects.size());
                                    UploadImage();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ObjectFunction.this, "Error :(", Toast.LENGTH_LONG).show();
                                    textView.setText("");
                                }
                            });
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
                            Toast.makeText(ObjectFunction.this,"Sucessfully Updated.",Toast.LENGTH_SHORT).show();

                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();

                            ObjectUpload upload = new ObjectUpload(url.toString(),objects );

                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(ObjectFunction.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
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