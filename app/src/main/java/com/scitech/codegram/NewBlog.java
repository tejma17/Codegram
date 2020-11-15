package com.scitech.codegram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class NewBlog extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;

    ImageView blogImage;
    EditText title, description;
    Button blogSubmit;

    Uri uriBlogImage;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    String blogImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_blog);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Blog");

        title = (EditText)findViewById(R.id.addTitle);
        description = (EditText)findViewById(R.id.addDescription);
        blogSubmit = (Button)findViewById(R.id.blogSubmit);
        blogImage = (ImageView)findViewById(R.id.imageSelect);
        progressBar = (ProgressBar)findViewById(R.id.blogProgressbar);

        blogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        blogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBlogInformation();
            }
        });
    }


    public void saveBlogInformation()
    {
        String blogTitle = title.getText().toString();
        String blogDescription = description.getText().toString();

        if (blogTitle.isEmpty()) {
            title.setError("Blog Title required");
            title.requestFocus();
            return;
        }
        else if(blogDescription.isEmpty())
        {
            description.setError("Blog Description required");
            description.requestFocus();
            return;
        }
        else if(blogImageUrl==null || blogImageUrl.isEmpty()){
            Toast.makeText(getApplicationContext(), "Blog Image required...", Toast.LENGTH_SHORT).show();
            blogImage.requestFocus();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String likes = "0";

        if(user != null && blogImageUrl != null){

            DatabaseReference forumDatabase = FirebaseDatabase.getInstance().getReference("TechForum");

            String blogKey = forumDatabase.push().getKey();
            Blog blog = new Blog(blogKey, blogTitle, blogDescription, user.getDisplayName(), blogImageUrl, likes);
            forumDatabase.child(blogKey).setValue(blog);
            Toast.makeText(getApplicationContext(), "Blog added to TechForum Successfully...", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "Some Error Occurred!!! Try again...", Toast.LENGTH_SHORT).show();
        }

    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriBlogImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriBlogImage);
                blogImage.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("BlogPics/" + System.currentTimeMillis() + ".jpg");

        if (uriBlogImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriBlogImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    blogImageUrl = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}