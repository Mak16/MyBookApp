package com.example.mahi.books;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;

import com.example.mahi.books.databinding.ActivityBookDetailsBinding;

public class BookDetails extends AppCompatActivity {

    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Book book = getIntent().getParcelableExtra("Book");
        ActivityBookDetailsBinding binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_book_details
        );
        ratingBar = findViewById(R.id.details_ratingBar);
        ratingBar.setRating(book.rating);
        binding.setBook(book);
    }
}
