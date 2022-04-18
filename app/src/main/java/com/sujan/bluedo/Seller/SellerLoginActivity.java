package com.sujan.bluedo.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sujan.bluedo.R;

public class SellerLoginActivity extends AppCompatActivity {

    private Button SellerLoginButton;
    private EditText emailInput,passwordInput;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        SellerLoginButton = (Button) findViewById(R.id.sellerLogin_btn) ;
        emailInput = (EditText) findViewById(R.id.sellerLogEmail) ;
        passwordInput = (EditText) findViewById(R.id.sellerLogPassword) ;
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        SellerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });
    }

    private void loginSeller() {
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!email.equals("") && !password.equals("")) {

            //loading bar
            loadingBar.setTitle("Seller Account Login");
            loadingBar.setMessage("Please wait, While we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingBar.dismiss();
                                Toast.makeText(SellerLoginActivity.this,"You are Login successfully!",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });


        }
        else {
            Toast.makeText(this,"please complete the login form!",Toast.LENGTH_SHORT).show();
        }
    }

    public void onsellerLoginClick(View view) {
        startActivity(new Intent(this, SellerRegistrationActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}