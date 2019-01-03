package com.example.mahi.books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    EditText etSubject,etTitle,etAuthors,etPublisher,etIsbn;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etAuthors = (EditText)findViewById(R.id.etAuthor);
        etPublisher = (EditText)findViewById(R.id.etPublisher);
        etIsbn = (EditText)findViewById(R.id.etIsbn);
        button = (Button)findViewById(R.id.btnSearch);
        etSubject = (EditText) findViewById(R.id.etSubject);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String subject = etSubject.getText().toString().trim();
                        String title = etTitle.getText().toString().trim();
                        String author = etAuthors.getText().toString().trim();
                        String publisher = etPublisher.getText().toString().trim();
                        String isbn = etIsbn.getText().toString().trim();
                        if(subject.isEmpty() && title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty()){
                                Toast.makeText(getApplicationContext(),"Enter Atleast One Search Field",Toast.LENGTH_LONG).show();
                        }
                        else {
                            URL queryURL = ApiUtil.buildUrl(subject,title,author,publisher,isbn);
                            Intent intent = new Intent(getApplicationContext(),BookListActivity.class);
                            intent.putExtra("query",queryURL.toString());
                            if(SpUtil.position==0 || SpUtil.position==SpUtil.MAX_SEARCH){
                                SpUtil.position=1;
                                if(SpUtil.position==SpUtil.MAX_SEARCH){
                                    SpUtil.full=true;
                                }
                            }
                            StringBuilder sb = new StringBuilder();
                            if(!subject.isEmpty())sb.append(subject+" , ");
                            if(!title.isEmpty())sb.append(title+" , ");
                            if(!author.isEmpty())sb.append(author+" , ");
                            if(!publisher.isEmpty())sb.append(publisher+" , ");
                            if(!isbn.isEmpty())sb.append(isbn+" , ");
                            sb.setLength(sb.length()-3);
                            String query = sb.toString();
                            SpUtil.putStringInSp(getApplicationContext(),"query"+String.valueOf(SpUtil.position),query );
                            SpUtil.position++;
                            startActivity(intent);
                            finish();
                        }
                    }
                }
        );
    }
}
