package com.sujan.bluedo.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sujan.bluedo.Buyer.LoginActivity;
import com.sujan.bluedo.Buyer.RegisterActivity;
import com.sujan.bluedo.R;

import java.util.HashMap;

public class AdminAccountCreateActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText AdminNameInput,AdminPhoneInput,AdminEmailInput,AdminPasswordInput;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account_create);

        CreateAccountButton = (Button) findViewById(R.id.admin_create_new_account_Button);

        AdminNameInput = (EditText) findViewById(R.id.admin_ac_RegName);
        AdminPhoneInput = (EditText) findViewById(R.id.admin_ac_RegMobile);
        AdminEmailInput = (EditText) findViewById(R.id.admin_ac_RegEmail);
        AdminPasswordInput = (EditText) findViewById(R.id.admin_ac_RegPassword);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAdminAccount();
            }
        });
    }

    private void CreateAdminAccount() {
        String name = AdminNameInput.getText().toString();
        String password = AdminPasswordInput.getText().toString();
        String email = AdminEmailInput.getText().toString();
        String phone = AdminPhoneInput.getText().toString();

        //check input fields are empty or not
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write Admin name...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please write Admin email...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write Admin phone number...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write Admin password...",Toast.LENGTH_SHORT).show();
        }
        else {
            //to store data to firebase
            loadingBar.setTitle("Creating Admin Account");
            loadingBar.setMessage("Please wait, While we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name,phone,email,password);
        }
    }

    private void ValidatePhoneNumber(String name, String phone, String email, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Admins").child(phone).exists())){
                    //to create a account
                    HashMap<String,Object> AdminDataMap = new HashMap<>();
                    AdminDataMap.put("phone",phone);
                    AdminDataMap.put("password",password);
                    AdminDataMap.put("name",name);
                    AdminDataMap.put("email",email);

                    RootRef.child("Admins").child(phone).updateChildren(AdminDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AdminAccountCreateActivity.this,"Congratulations, Admin account has been created.",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent= new Intent(AdminAccountCreateActivity.this, AdminHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(AdminAccountCreateActivity.this,"Network Error : please try again after some time...!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(AdminAccountCreateActivity.this,"This "+phone+" number is already exists!",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onLoginClick(View view) {
        Intent intent = new Intent(AdminAccountCreateActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}