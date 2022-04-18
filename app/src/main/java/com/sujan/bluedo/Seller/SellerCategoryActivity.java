package com.sujan.bluedo.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.sujan.bluedo.Model.Categories;
import com.sujan.bluedo.R;
import com.sujan.bluedo.ViewHolder.CategoryViewHolder;

public class SellerCategoryActivity extends AppCompatActivity {

    private RecyclerView categoryList;
    private DatabaseReference CategoryRef;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);

        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Categories");

        categoryList = findViewById(R.id.seller_category_list);
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
                                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProductActivity.class);
                                intent.putExtra("category",categoriesModel.getName());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
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


}