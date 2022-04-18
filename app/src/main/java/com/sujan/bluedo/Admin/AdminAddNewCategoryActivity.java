package com.sujan.bluedo.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sujan.bluedo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminAddNewCategoryActivity extends AppCompatActivity {

    private CircleImageView InputProductCategoryImage;
    private Button AddnewCategoryButton;
    private EditText CategoryName ,CategoryDescription;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String categoryRandomKey,downloadImageUrl,new_categoryName,catDescription,saveCurrentDate,saveCurrentTime;
    private StorageReference CategoryImagesRef;
    private DatabaseReference CategoryRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_category);

        CategoryImagesRef = FirebaseStorage.getInstance().getReference().child("Category Images");
        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Categories");
        loadingBar = new ProgressDialog(this);

        InputProductCategoryImage = (CircleImageView) findViewById(R.id.add_category_image);
        AddnewCategoryButton = (Button) findViewById(R.id.admin_create_new_category_Button);
        CategoryName = (EditText) findViewById(R.id.admin_add_category_name);
        CategoryDescription = (EditText) findViewById(R.id.admin_add_category_description);

        //for set category image
        InputProductCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AddnewCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateCategoryData();
            }
        });

    }

    private void ValidateCategoryData() {
        new_categoryName = CategoryName.getText().toString();
        catDescription = CategoryDescription.getText().toString();
        if (ImageUri == null){
            Toast.makeText(this,"Category image is mandatory!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(new_categoryName)){
            Toast.makeText(this,"Write product category name!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(catDescription)){
            Toast.makeText(this,"Write simple category description!",Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductCategoryInformation();
        }
    }

    private void StoreProductCategoryInformation() {
        loadingBar.setTitle("Adding New Product Category");
        loadingBar.setMessage("Dear Admin,please wait While we are adding the new category.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        //for category unique key
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        //we need create unique key to fine every products
        categoryRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = CategoryImagesRef.child(ImageUri.getLastPathSegment() + categoryRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewCategoryActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewCategoryActivity.this,"Category image stored successfully!",Toast.LENGTH_SHORT).show();
                Task<Uri>  urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewCategoryActivity.this,"Got the product image Url successfully...",Toast.LENGTH_SHORT).show();
                            SaveProductCategoryInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductCategoryInfoToDatabase() {
        HashMap<String,Object> categoryMap = new HashMap<>();
        categoryMap.put("cid",categoryRandomKey);
        categoryMap.put("name",new_categoryName);
        categoryMap.put("image",downloadImageUrl);
        categoryMap.put("description",catDescription);

        CategoryRef.child(categoryRandomKey).updateChildren(categoryMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AdminAddNewCategoryActivity.this, AdminHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewCategoryActivity.this,"Product Category is added successfully...",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewCategoryActivity.this,"Error :"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            ImageUri =  data.getData();
            InputProductCategoryImage.setImageURI(ImageUri);
        }
    }
}