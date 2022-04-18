package com.sujan.bluedo.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sujan.bluedo.Buyer.HomeActivity;
import com.sujan.bluedo.Buyer.MainActivity;
import com.sujan.bluedo.R;

public class AdminHomeActivity extends AppCompatActivity {

    private CardView CheckOrdersBtn;
    private CardView maintainProductBtn;
    private CardView checkApprovedproductBtn;
    private CardView AdminAddProductbtn;
    private CardView CreateNewAccount;
    private CardView createNewCategory;
    private CardView manageCategory;
    private CardView addBanners;
    private ImageView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        logoutBtn = (ImageView) findViewById(R.id.admin_logout_btn);
        CheckOrdersBtn = (CardView) findViewById(R.id.check_order_details_btn);
        maintainProductBtn = (CardView) findViewById(R.id.maintaine_btn);
        checkApprovedproductBtn = (CardView) findViewById(R.id.check_approved_product_btn);
        AdminAddProductbtn = (CardView) findViewById(R.id.admin_add_new_btn);
        CreateNewAccount = (CardView) findViewById(R.id.admin_create_new_account_btn);
        createNewCategory = (CardView) findViewById(R.id.admin_create_new_category_btn);
        manageCategory = (CardView) findViewById(R.id.admin_manage_category_btn);
        addBanners = (CardView) findViewById(R.id.admin_add_banner_btn);

        addBanners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAddBannerActivity.class);
                startActivity(intent);
            }
        });


        manageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminCategoryActivity.class);
                intent.putExtra("Process","ManageCategory");
                startActivity(intent);
            }
        });


        createNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAddNewCategoryActivity.class);
                startActivity(intent);
            }
        });


        CreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAccountCreateActivity.class);
                startActivity(intent);
            }
        });

        AdminAddProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
            }
        });

        checkApprovedproductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminCheckNewProductActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        maintainProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });
    }
}