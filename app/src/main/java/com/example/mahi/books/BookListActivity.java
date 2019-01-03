package com.example.mahi.books;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {

    private static final String BOOKS = "books";
    private ArrayList<Book> books=null;
    MenuItem item1,item2,item3;
    private TextView errorTextMsg,dataFound;
    private RecyclerView rvBooks;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SearchView searchView;
    BooksAdapter adapter;
    String searchURL,originalURL;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu,menu);
        SubMenu subMenu=menu.addSubMenu("Recent Searches");
        MenuItem menuItem[]=new MenuItem[4];
        item1=menu.findItem(R.id.ebooks);
        item2=menu.findItem(R.id.free_ebooks);
        item3=menu.findItem(R.id.paid_ebooks);
        int n=(SpUtil.full)?SpUtil.MAX_SEARCH:SpUtil.position;
        for(int i=1;i<n;i++) {
            menuItem[i-1]=subMenu.add(android.view.SubMenu.NONE,SpUtil.position,android.view.SubMenu.NONE,SpUtil.getStringFromSp(
                    getApplicationContext(),
                    "query"+String.valueOf(i))
            );
        }

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        URL bookURL = ApiUtil.buildUrl(s);
                        performSearch(bookURL.toString());
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return true;
                    }
                }
        );
        return true;
    }

    private void refresh(){
        performSearch(searchURL);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_advance_search:{
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
                return true;
            }

            case android.R.id.home:drawerLayout.openDrawer(GravityCompat.START);
               return  true;

            case R.id.ebooks:{
                if(!item.isChecked()) {
                    item.setChecked(true);
                    item2.setChecked(false);
                    item3.setChecked(false);
                    Intent intent=getIntent();
                    searchURL= intent.getStringExtra("query");
                    searchURL=searchURL+"&filter=ebooks";
                    rvBooks.setVisibility(View.INVISIBLE);
                    performSearch(searchURL);
            }
                else {
                    item.setChecked(false);
                    performSearch(originalURL);
                    searchURL=originalURL;
                }
                return true;
            }
            case R.id.free_ebooks:{
                if(!item.isChecked()) {
                    item.setChecked(true);
                    item1.setChecked(false);
                    item3.setChecked(false);
                    Intent intent=getIntent();
                    searchURL= intent.getStringExtra("query");
                    searchURL=searchURL+"&filter=free-ebooks";
                    rvBooks.setVisibility(View.INVISIBLE);
                    performSearch(searchURL);
                }
                else {
                    item.setChecked(false);
                    performSearch(originalURL);
                    searchURL=originalURL;
                }
                return true;
            }
            case R.id.paid_ebooks:{
                if(!item.isChecked()) {
                    item.setChecked(true);
                    item2.setChecked(false);
                    item1.setChecked(false);
                    Intent intent=getIntent();
                    searchURL= intent.getStringExtra("query");
                    searchURL=searchURL+"&filter=paid+ebooks";
                    rvBooks.setVisibility(View.INVISIBLE);
                    performSearch(searchURL);
                }
                else {
                    item.setChecked(false);
                    performSearch(originalURL);
                    searchURL=originalURL;
                }
                return true;
            }
            case R.id.action_refresh:{
                refresh();
            }

            default:return super.onOptionsItemSelected(item);
        }
    }
    public void performSearch(String search)
    {
        try {
            new BooksQueryAsync().execute(new URL(search));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        errorTextMsg = (TextView)findViewById(R.id.error_message);
        dataFound = (TextView)findViewById(R.id.no_data_found);
        rvBooks = (RecyclerView)findViewById(R.id.rv_books);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.cooking: {
                                item.setChecked(true);
                                performSearch(ApiUtil.buildUrl("cooking","","","","").toString());
                                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                                {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                return true;
                            }
                            case R.id.programming: {
                                item.setChecked(true);
                                performSearch(ApiUtil.buildUrl("programming","","","","").toString());
                                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                                {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                return true;
                            }
                            case R.id.sports: {
                                item.setChecked(true);
                                performSearch(ApiUtil.buildUrl("sports","","","","").toString());
                                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                                {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                return true;
                            }
                            case R.id.technologies: {
                                item.setChecked(true);
                                performSearch(ApiUtil.buildUrl("technologies","","","","").toString());
                                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                                {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                return true;
                            }
                            case R.id.automobiles: {
                                item.setChecked(true);
                                performSearch(ApiUtil.buildUrl("automobiles","","","","").toString());
                                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                                {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                return true;
                            }

                        case R.id.science: {
                            item.setChecked(true);
                            performSearch(ApiUtil.buildUrl("science","","","","").toString());
                            if(drawerLayout.isDrawerOpen(GravityCompat.START))
                            {
                                drawerLayout.closeDrawer(GravityCompat.START);
                            }
                            return true;
                        }
                            default:return false;
                        }
                    }
                }
        );
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvBooks.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        searchURL= intent.getStringExtra("query");
        if(savedInstanceState==null) {
            URL bookUrl = null;
            try {
                if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                    String query = intent.getStringExtra(SearchManager.QUERY);
                    bookUrl = ApiUtil.buildUrl(query);
                } else if (searchURL == null || searchURL.isEmpty()) {
                    bookUrl = ApiUtil.buildUrl("cooking");
                    originalURL = bookUrl.toString();
                } else {
                    bookUrl = new URL(searchURL);
                    originalURL = searchURL;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new BooksQueryAsync().execute(bookUrl);
        }
    }

    public void setPbVisible()
    {
        swipeRefreshLayout.setRefreshing(true);
        errorTextMsg.setVisibility(View.INVISIBLE);
        rvBooks.setVisibility(View.INVISIBLE);
        dataFound.setVisibility(View.INVISIBLE);
    }
    public void setErrorTextMsgVisible()
    {
        swipeRefreshLayout.setRefreshing(false);
        errorTextMsg.setVisibility(View.VISIBLE);
        rvBooks.setVisibility(View.INVISIBLE);
        dataFound.setVisibility(View.INVISIBLE);
    }
    public void setRvBooksVisible()
    {
        swipeRefreshLayout.setRefreshing(false);
        errorTextMsg.setVisibility(View.INVISIBLE);
        rvBooks.setVisibility(View.VISIBLE);
        dataFound.setVisibility(View.INVISIBLE);
    }
    public void setDataFoundVisible()
    {
        swipeRefreshLayout.setRefreshing(false);
        errorTextMsg.setVisibility(View.INVISIBLE);
        rvBooks.setVisibility(View.INVISIBLE);
        dataFound.setVisibility(View.VISIBLE);
    }
    public class BooksQueryAsync extends AsyncTask<URL,Void,String>{
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String result=null;
            try {
                result = ApiUtil.getJson(searchUrl);
            } catch (IOException e) {
                Log.e("Error",e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result==null) {
                setErrorTextMsgVisible();
            }else {
                books = ApiUtil.getBooksFromJson(result);
                if(books.size()==0)
                {
                    setDataFoundVisible();
                    SpUtil.position--;
                }
                else {
                        adapter = new BooksAdapter(books);
                        setRvBooksVisible();
                        rvBooks.setAdapter(adapter);

                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setPbVisible();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        books=savedInstanceState.getParcelableArrayList(BOOKS);
        adapter=new BooksAdapter(books);
        rvBooks.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BOOKS,books);
    }
}
