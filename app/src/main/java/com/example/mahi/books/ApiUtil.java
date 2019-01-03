package com.example.mahi.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by MAHI on 19-06-2018.
 */

public class ApiUtil {
    private static final String QUERY_PARAMETER_KEY="q";
    private static final String KEY = "key";
    private static final String API_KEY="AIzaSyBKP0XCBzdtmLB-ZwEEKzlsSU6QUkfZAew";
    private static final String TITLE = "intitle:";
    private static final String AUTHOR = "inauthor:";
    private static final String PUBLISHER = "inpublisher:";
    private static final String ISBN= "isbn:";
    private static final String SUBJECT= "subject:";

    private ApiUtil(){}

    public static final String BASE_API_URL = "https://www.googleapis.com/books/v1/volumes/";

    public static URL buildUrl(String title)
    {
        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,title)
                .build();
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrl(String subject,String title,String author,String publisher,String isbn){
        URL url=null;

        StringBuilder sb = new StringBuilder();
        if(!subject.isEmpty())sb.append(SUBJECT+subject+"+");
        if(!title.isEmpty())sb.append(TITLE+title+"+");
        if(!author.isEmpty())sb.append(AUTHOR+author+"+");
        if(!publisher.isEmpty())sb.append(PUBLISHER+publisher+"+");
        if(!isbn.isEmpty())sb.append(ISBN+isbn+"+");
        sb.setLength(sb.length()-1);
        String query = sb.toString();
        Uri uri=Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,query)
                .build();
        try {
            url=new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getJson(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if(hasData)
            {
                return scanner.next();
            }else{
                return null;
            }
        }
        catch (Exception e)
        {
            Log.d("Error",e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }

    public static ArrayList<Book> getBooksFromJson(String json)
    {
        final String ID="id";
        final String TITLE="title";
        final String SUBTITLE="subtitle";
        final String AUTHORS="authors";
        final String PUBLISHER="publisher";
        final String PUBLISHED_DATE="publishedDate";
        final String ITEMS="items";
        final String VOLUME_INFO="volumeInfo";
        final String DESCRIPTION="description";
        final String IMAGE_LINKS="imageLinks";
        final String THUMBNAIL="thumbnail";
        final String AVERAGE_RATING="averageRating";
        final String RATING_COUNT="ratingsCount";

        ArrayList<Book> books = new ArrayList<Book>();

        try {
            JSONObject jsonBook = new JSONObject(json);
            JSONArray arrayBooks = jsonBook.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();
            for(int i=0;i<numberOfBooks;i++)
            {
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfo = bookJSON.getJSONObject(VOLUME_INFO);
                int numberOfAuthor = volumeInfo.getJSONArray(AUTHORS).length();
                String authors[] = new String[numberOfAuthor];
                for(int j=0;j<numberOfAuthor;j++)
                {
                    authors[j]=volumeInfo.getJSONArray(AUTHORS).get(j).toString();
                }

                JSONObject imagelinks=null;
                if(volumeInfo.has(IMAGE_LINKS)) {
                    imagelinks = volumeInfo.getJSONObject(IMAGE_LINKS);
                }
                Book book = new Book(
                        bookJSON.getString(ID),
                        volumeInfo.getString(TITLE),
                        (volumeInfo.isNull(SUBTITLE)?"":volumeInfo.getString(SUBTITLE)),
                        authors,
                        (volumeInfo.isNull(PUBLISHER)?"":volumeInfo.getString(PUBLISHER)),
                        (volumeInfo.isNull(PUBLISHED_DATE)?"":volumeInfo.getString(PUBLISHED_DATE)),
                        (volumeInfo.isNull(DESCRIPTION)?"":volumeInfo.getString(DESCRIPTION)),
                        (imagelinks==null?"":imagelinks.getString(THUMBNAIL)),
                        (volumeInfo.isNull(RATING_COUNT)?0:volumeInfo.getInt(RATING_COUNT))
                );
                books.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;

    }
}
