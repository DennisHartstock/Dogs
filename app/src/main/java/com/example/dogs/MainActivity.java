package com.example.dogs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ImageView ivDogImage;
    private ProgressBar pbLoadImage;
    private Button btNextImage;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.loadDogImage();

        viewModel.getIsLoading().observe(this, loading -> {
            if (loading) {
                pbLoadImage.setVisibility(View.VISIBLE);
            } else {
                pbLoadImage.setVisibility(View.GONE);
            }
        });

        viewModel.getIsError().observe(this, error -> {
            if (error) {
                Toast.makeText(MainActivity.this,
                        R.string.toast_error,
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getDogImageMutableLiveData().observe(
                this,
                dogImage -> Glide.with(MainActivity.this)
                        .load(dogImage.getMessage())
                        .into(ivDogImage));

        btNextImage.setOnClickListener(view -> viewModel.loadDogImage());
    }

    private void initViews() {
        ivDogImage = findViewById(R.id.ivDogImage);
        pbLoadImage = findViewById(R.id.pbLoadImage);
        btNextImage = findViewById(R.id.btNextImage);
    }
}