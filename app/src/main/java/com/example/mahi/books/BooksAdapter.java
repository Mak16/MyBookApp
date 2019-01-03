package com.example.mahi.books;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by MAHI on 20-06-2018.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> implements Filterable{

    TitleFilter titleFilter;
    ArrayList<Book> books,originalBooks;

    @Override
    public Filter getFilter() {
        if(titleFilter==null)
            titleFilter=new TitleFilter();
        return titleFilter;
    }

    BooksAdapter(ArrayList<Book> books)
    {
        this.originalBooks=books;
        this.books=books;
    }
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.book_list_item,parent,false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(),BookDetails.class);
            int selectedItemPosition = getAdapterPosition();
            Book selectedBook = books.get(selectedItemPosition);
            intent.putExtra("Book",selectedBook);
            view.getContext().startActivity(intent);
        }

        TextView tvTitle;
        TextView tvAuthor;
        TextView tvDate;
        TextView tvPublisher;
        ImageView imageView;
        RatingBar ratingBar;

        public BookViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvAuthor = (TextView)itemView.findViewById(R.id.tvAuthor);
            tvDate = (TextView)itemView.findViewById(R.id.tvPublishedDate);
            tvPublisher = (TextView)itemView.findViewById(R.id.tvPublisher);
            imageView = itemView.findViewById(R.id.list_image);
            ratingBar = itemView.findViewById(R.id.ratingbar);
        }
        public void bind(Book book) {
            tvTitle.setText(book.title);
            tvAuthor.setText(book.authors);
            tvDate.setText(book.publishedDate);
            tvPublisher.setText(book.publisher);
            Book.loadImage(imageView,book.thumbnail);
            ratingBar.setRating(book.rating);
        }
    }
    public class TitleFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();

            if(charSequence==null||charSequence.length()==0) {
                    filterResults.values=originalBooks;
                    filterResults.count=originalBooks.size();
            }
            else {
                ArrayList<Book> result = new ArrayList<Book>();
                for (Book book : originalBooks) {
                    if (book.title.toUpperCase().contains(charSequence.toString().toUpperCase().trim())) {
                        result.add(book);
                    }
                }
                filterResults.count = result.size();
                filterResults.values = result;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            books= (ArrayList<Book>) filterResults.values;
            notifyDataSetChanged();
        }

    }
}
