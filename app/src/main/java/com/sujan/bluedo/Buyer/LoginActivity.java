package com.sujan.bluedo.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sujan.bluedo.Admin.AdminCategoryActivity;
import com.sujan.bluedo.Admin.AdminHomeActivity;
import com.sujan.bluedo.Model.Users;
import com.sujan.bluedo.Prevalent.Prevalent;
import com.sujan.bluedo.R;

import io.paperdb.Paper;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class LoginActivity extends AppCompatActivity {

    Bundle bundle;
    Button LoginButton;
    EditText logUserName,logUserPassword;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox chBoxRememberMe;
    private ImageView AdminLink ,NotAdminLink,RegisterImgLink;
    private TextView ForgetPasswordLink ,RegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.cirLoginButton) ;
        logUserName = (EditText) findViewById(R.id.editTextLogEmail) ;
        logUserPassword = (EditText) findViewById(R.id.editTextLogPassword) ;


        AdminLink = (ImageView)  findViewById(R.id.admin_panel_link);
        NotAdminLink = (ImageView) findViewById(R.id.not_admin_panel_link);
        RegisterImgLink = (ImageView) findViewById(R.id.register_img_link);

        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link) ;
        RegisterLink = (TextView) findViewById(R.id.log_register_link) ;

        loadingBar = new ProgressDialog(this);

        chBoxRememberMe = (CheckBox) findViewById(R.id.rememberMeChBox);
        Paper.init(this);

        //to auto load username password from the registration
        bundle = getIntent().getExtras();
        if (bundle!=null){
            logUserName.setText(bundle.getString("USERNAME"));
            logUserPassword.setText(bundle.getString("PASSWORD"));
        }


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    LoginUser();

            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login as Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                RegisterLink.setVisibility(View.INVISIBLE);
                RegisterImgLink.setClickable(false);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                RegisterLink.setVisibility(View.VISIBLE);
                RegisterImgLink.setClickable(true);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
    }



    //to create user login method
    private void LoginUser() {

//        if(!isConnected((LoginActivity) getApplicationContext())){
//            showCustomDialog();
//        }
//        else {

            String phone = logUserName.getText().toString();
            String password = logUserPassword.getText().toString();

            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait, While we are checking the credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                AllowAccesstoAccount(phone, password);
            }
        //}
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Connection Failed!")
        .setMessage("Please connect the internet to proceed further...")
       .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing to do...
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                });
    }

    private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager=
                (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) ||
                (mobileConn != null && mobileConn.isConnected());

    }

    private void AllowAccesstoAccount(String phone, String password) {

        if (chBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(parentDbName).child(phone).exists()){
                        Users userData = snapshot.child(parentDbName).child(phone).getValue(Users.class);
                        if (userData.getPhone().equals(phone)){

                            if (userData.getPassword().equals(password)){
                                //Allow user
                                if (parentDbName.equals("Admins")){
                                    Toast.makeText(LoginActivity.this,"Welcome Admin, you are logged in Successfully...",Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                    startActivity(intent);
                                }
                                else if (parentDbName.equals("Users")){
                                    Toast.makeText(LoginActivity.this,"Logged in Successfully...",Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    Prevalent.currentOnlineUser = userData;
                                    startActivity(intent);
                                }
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this,"Password is incorrect!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Account with this "+phone+" number do not exists!",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        //if want can put a intent to return register activity
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}