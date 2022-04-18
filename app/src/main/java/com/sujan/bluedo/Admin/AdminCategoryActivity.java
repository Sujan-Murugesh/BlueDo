package com.sujan.bluedo.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.sujan.bluedo.Buyer.HomeActivity;
import com.sujan.bluedo.Buyer.MainActivity;
import com.sujan.bluedo.Model.Categories;
import com.sujan.bluedo.R;
import com.sujan.bluedo.Seller.SellerHomeActivity;
import com.sujan.bluedo.ViewHolder.CategoryViewHolder;
import com.sujan.bluedo.ViewHolder.ProductViewHolder;

public class AdminCategoryActivity extends AppCompatActivity {

    private RecyclerView categoryList;
    private DatabaseReference CategoryRef;
    RecyclerView.LayoutManager layoutManager;
    private String process ="";
    private TextView pageTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        pageTitle = (TextView) findViewById(R.id.slogan_category);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            process = getIntent().getExtras().get("Process").toString();
            pageTitle.setText("Manage Categories");
        }

        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Categories");

        categoryList = findViewById(R.id.category_list);
        categoryList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        categoryList.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Categories> options =
                new FirebaseRecyclerOptions.Builder<Categories>()
                .setQuery(CategoryRef,Categories.class)
                .build();

        FirebaseRecyclerAdapter<Categories, CategoryViewHolder> adapter =
                new FirebaseRecyclerAdapter<Categories, CategoryViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i, @NonNull Categories categoriesModel) {
                        categoryViewHolder.txtCategoryName.setText(categoriesModel.getName());
                        categoryViewHolder.txtCategoryDescription.setText(categoriesModel.getDescription());
                        Picasso.get().load(categoriesModel.getImage()).into(categoryViewHolder.category_imageView);

                        categoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (process.equals("ManageCategory")){
                                    //to delete category
                                    final String categoryID = categoriesModel.getCid();
                                    CharSequence options[] = new CharSequence[]{
                                            "Yes",
                                            "No"
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminCategoryActivity.this);
                                    builder.setTitle("Do you want to Delete this Category, Are you sure?");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int position) {
                                            if (position == 0){
                                                //Change product state is Approved
                                                DeleteProduct(categoryID);
                                            }
                                            if (position == 1){
                                                //Do nothing...
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                                else {
                                    Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                                    intent.putExtra("category",categoriesModel.getName());
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);
                        CategoryViewHolder holder = new CategoryViewHolder(view);
                        return holder;
                    }
                };
        categoryList.setAdapter(adapter);
        adapter.startListening();
    }

    private void DeleteProduct(String categoryID) {
        CategoryRef.child(categoryID)
        .removeValue()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminCategoryActivity.this,"Category has been Deleted!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}