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
import android.widget.ImageView;
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

public class AdminAddBannerActivity extends AppCompatActivity {

    ImageView bannerImageInput;
    EditText bannerName,bannerDiscount,BannerDescription;
    Button addNewBannerButton;

    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String bannerRandomKey;
    private String downloadImageUrl;
    private String banner_Name;
    private String banner_Description;
    private String banner_Discount;
    private StorageReference BannerImagesRef;
    private DatabaseReference BannerRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_banner);

        BannerImagesRef = FirebaseStorage.getInstance().getReference().child("Banner Images");
        BannerRef = FirebaseDatabase.getInstance().getReference().child("Banners");
        loadingBar = new ProgressDialog(this);

        bannerImageInput = (ImageView) findViewById(R.id.add_banner_image);
        bannerName = (EditText) findViewById(R.id.admin_add_banner_name);
        bannerDiscount = (EditText) findViewById(R.id.admin_add_banner_discount);
        BannerDescription = (EditText) findViewById(R.id.admin_add_banner_description);
        addNewBannerButton = (Button) findViewById(R.id.admin_add_new_banner_Button);

        bannerImageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addNewBannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateBannerData();
            }
        });

    }

    private void ValidateBannerData() {
        banner_Name = bannerName.getText().toString();
        banner_Description = BannerDescription.getText().toString();
        banner_Discount = bannerDiscount.getText().toString();
        if (ImageUri == null){
            Toast.makeText(this,"Banner image is mandatory!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(banner_Name)){
            Toast.makeText(this,"Write Banner name!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(banner_Description)){
            Toast.makeText(this,"Write attractive Banner description!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(banner_Discount)){
            Toast.makeText(this,"Banner discount is mandatory!",Toast.LENGTH_SHORT).show();
        }
        else {
            StoreBannerInformation();
        }
    }

    private void StoreBannerInformation() {
        loadingBar.setTitle("Adding New Banner...");
        loadingBar.setMessage("Dear Admin,please wait While we are adding the new banner.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        //for banner unique key
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        //we need create unique key to fine every products
        bannerRandomKey = saveCurrentDate + saveCurrentTime;

        //to upload image
        StorageReference filePath = BannerImagesRef.child(ImageUri.getLastPathSegment() + bannerRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddBannerActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddBannerActivity.this,"Banner image stored successfully!",Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            Toast.makeText(AdminAddBannerActivity.this,"Got the banner image Url successfully...",Toast.LENGTH_SHORT).show();
                            SaveBannerInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveBannerInfoToDatabase() {
        //to inserting data
        HashMap<String,Object> bannerMap = new HashMap<>();
        bannerMap.put("bid",bannerRandomKey);
        bannerMap.put("name",banner_Name);
        bannerMap.put("image",downloadImageUrl);
        bannerMap.put("description",banner_Description);
        bannerMap.put("discount",banner_Discount);

        BannerRef.child(bannerRandomKey).updateChildren(bannerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(AdminAddBannerActivity.this, AdminHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    loadingBar.dismiss();
                    Toast.makeText(AdminAddBannerActivity.this,"Banner is added successfully...",Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddBannerActivity.this,"Error :"+message,Toast.LENGTH_SHORT).show();
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
            bannerImageInput.setImageURI(ImageUri);
        }
    }
}