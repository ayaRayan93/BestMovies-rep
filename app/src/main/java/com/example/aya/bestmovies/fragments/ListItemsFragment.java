package com.example.aya.bestmovies.fragments;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aya.bestmovies.adapter.ItemsAdapter;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.aya.bestmovies.R;
import com.example.aya.bestmovies.app.AppController;
import com.example.aya.bestmovies.json.Parser;
import com.example.aya.bestmovies.models.ModelMovie;
import com.example.aya.bestmovies.store.DataBaseHelper;
import com.example.aya.bestmovies.store.MovieTable;
import com.example.aya.bestmovies.store.MoviesContentProvider;

import java.io.UnsupportedEncodingException;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aya on 04/11/2016.
 */

public class ListItemsFragment extends Fragment  {
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh) SwipeRefreshLayout mSwipeRefreshLayout;

    private Menu menu;
    protected ItemsAdapter itemAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<ModelMovie> dataSet;
    private int flag;
    private DataBaseHelper movieDB;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public ListItemsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dataSet = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_item, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);
        itemAdapter = new ItemsAdapter(getActivity(),dataSet);
        mRecyclerView.setAdapter(itemAdapter);

        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        //noinspection ResourceAsColor
        mSwipeRefreshLayout.setColorScheme(
                R.color.colorPrimaryDark, R.color.colorAccent,
                R.color.colorAccent, R.color.colorPrimaryDark);

        mLayoutManager = new GridLayoutManager(getActivity(),3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (!mSwipeRefreshLayout.isRefreshing())
        {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        initiateRefresh(0);
        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // handel swipe refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(flag<2)
                initiateRefresh(flag);
                else
                    showFavorite();

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_main, menu);
        this.menu =  menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.popular_rated:
                initiateRefresh(0);
                flag=0;
                return true;

            case R.id.top_rated:
                initiateRefresh(1);
                flag=1;
                return true;

            case R.id.favorite:
                flag=2;
                showFavorite();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void showFavorite()
    {

       // movieDB=new DBHandler(getContext());
        MoviesContentProvider m=new MoviesContentProvider(getContext());
        clearDataSet();
        dataSet.addAll(0,getAllMovies(MoviesContentProvider.CONTENT_URI));

        itemAdapter.notifyDataSetChanged();
    }

    public  void initiateRefresh(int i)
    {
       String Url;
        if(i!=0)
            Url="http://api.themoviedb.org/3/movie/top_rated?api_key=f78be50ecdcaa8f00e595d90e6281fc8";
        else
            Url="http://api.themoviedb.org/3/movie/popular?api_key=f78be50ecdcaa8f00e595d90e6281fc8";
        /**
         * Execute the background task, which uses {@link AsyncTask} to load the data.
         */
        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(Url);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                clearDataSet();
                Iterator iterator = Parser.parseStringToJson(data).iterator();
                while (iterator.hasNext()){
                    ModelMovie movie = (ModelMovie)iterator.next();
                    dataSet.add(movie);
                    itemAdapter.notifyItemInserted(dataSet.size() - 1);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        /////////////connection//////////
        StringRequest strReq = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d("response", response);
                clearDataSet();
                Iterator iterator = Parser.parseStringToJson(response).iterator();
                while (iterator.hasNext()){
                    ModelMovie movie = (ModelMovie)iterator.next();
                    dataSet.add(movie);
                    itemAdapter.notifyItemInserted(dataSet.size() - 1);
                }
                onRefreshComplete();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Stop the refreshing indicator
                mSwipeRefreshLayout.setRefreshing(false);
                Log.d("response", error.toString());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void clearDataSet()
    {
        if (dataSet != null){
            dataSet.clear();
            itemAdapter.notifyDataSetChanged();
        }
    }
    private void onRefreshComplete()
    {
        mSwipeRefreshLayout.setRefreshing(false);

    }

  private List<ModelMovie> getAllMovies(Uri uri)
  {
      String[] projection={MovieTable.KEY_ID,
      MovieTable.KEY_TITLE,
      MovieTable.KEY_POSTER,
    //  MovieTable.KEY_OVERVIEW,
   //   MovieTable.KEY_VOTE_AVERAGE,
   //   MovieTable.KEY_RELEASE_DATE,
      MovieTable.KEY_BACKDROP_PATH};

      List<ModelMovie> movieList = new ArrayList<>();
// Select All Query
      String selectQuery = "SELECT * FROM " + MovieTable.TABLE_MOVIES;
      MoviesContentProvider  movieContentProvider=new MoviesContentProvider(getContext());
      //SQLiteDatabase db = this.getWritableDatabase();
      Cursor cursor = movieContentProvider.query(MoviesContentProvider.CONTENT_URI,projection,selectQuery,null,null); //db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
      if (cursor.moveToFirst()) {
          do {
              ModelMovie movie = new ModelMovie();
              movie.setID(cursor.getString(0));
              movie.setOriginal_title(cursor.getString(1));
              movie.setPoster(cursor.getString(2));
             // movie.setOverview(cursor.getString(3));
            //  movie.setVote_average(cursor.getString(3));
            //  movie.setRelease_date(cursor.getString(4));
              movie.setBackdrop_path(cursor.getString(3));
// Adding contact to list
              movieList.add(movie);
          } while (cursor.moveToNext());
      }
// return contact list
      return movieList;

  }

}
