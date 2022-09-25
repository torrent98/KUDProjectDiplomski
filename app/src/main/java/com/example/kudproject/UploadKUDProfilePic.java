package com.example.kudproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadKUDProfilePic extends AppCompatActivity {

    private ProgressBar loadingPB;
    private ImageView profile_pic;
    private Button choose_btn;
    private Button upload_btn;

    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    private Uri uriImage;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_kudprofile_pic);

        choose_btn = findViewById(R.id.upload_pic_choose_btn);
        upload_btn = findViewById(R.id.upload_pic_btn);
        profile_pic = findViewById(R.id.image_view_pic_dp);
        loadingPB = findViewById(R.id.idPBLoading);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();

        Picasso.with(UploadKUDProfilePic.this).load(uri).into(profile_pic);

        choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPic();
            }
        });

    }

    private void uploadPic() {

        if(uriImage != null){
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "/displaypic." + getFileExtension(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;

                            firebaseUser = authProfile.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });

                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(UploadKUDProfilePic.this, "Upload successfull.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadKUDProfilePic.this, UserSettings.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadKUDProfilePic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            loadingPB.setVisibility(View.GONE);
            Toast.makeText(UploadKUDProfilePic.this, "No File was selected.", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uriImage) {

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uriImage));

    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            profile_pic.setImageURI(uriImage);
        }

    }
}