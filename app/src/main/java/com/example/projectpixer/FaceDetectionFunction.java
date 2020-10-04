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
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FaceDetectionFunction extends AppCompatActivity {

    private Button snapBtn;
    private Button detectBtn;
    private Button selectBtn;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private TextView textView;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private StorageTask mUploadTask;
    private Uri imageUri;
    private int face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection_function);

        snapBtn = findViewById(R.id.snapBtn);
        detectBtn = findViewById(R.id.detectBtn);
        selectBtn = findViewById(R.id.selectBtn);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("FaceDetection");

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("FaceDetection");

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

            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.RED);
            myRectPaint.setStyle(Paint.Style.STROKE);

            Bitmap tempBitmap = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(imageBitmap, 0, 0, null);

            FaceDetector faceDetector = new
                    FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                    .build();
            if (!faceDetector.isOperational()) {
                Toast.makeText(FaceDetectionFunction.this, "Could not set up the face detector!", Toast.LENGTH_SHORT).show();
                //new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                textView.setText("");
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frame);

            face = faces.size();

            for (int i = 0; i < faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
            }
            imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            textView.setTextSize(20);
            textView.setText("No of Faces Detected : " + faces.size());

            UploadImage();
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
                            Toast.makeText(FaceDetectionFunction.this,"Sucessfully Updated.",Toast.LENGTH_SHORT).show();

                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();

                            FaceUpload upload = new FaceUpload(url.toString(),face );

                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(FaceDetectionFunction.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
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