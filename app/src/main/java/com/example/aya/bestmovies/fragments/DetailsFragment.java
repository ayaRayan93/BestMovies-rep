package com.example.aya.bestmovies.fragments;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aya.bestmovies.R;
import com.example.aya.bestmovies.adapter.ReviewAdapter;
import com.example.aya.bestmovies.app.AppController;
import com.example.aya.bestmovies.json.Parser;
import com.example.aya.bestmovies.models.ModelMovie;
import android.support.v4.app.Fragment;

import com.example.aya.bestmovies.models.ReviewModel;
import com.example.aya.bestmovies.store.DataBaseHelper;
import com.example.aya.bestmovies.store.MovieTable;
import com.example.aya.bestmovies.store.MoviesContentProvider;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aya on 04/11/2016.
 */

public class DetailsFragment extends Fragment {

    @BindView(R.id.textView) TextView titel;
    @BindView(R.id.textView3) TextView releasDate;
    @BindView(R.id.textView2) TextView overView;
    @BindView(R.id.ratingBar) RatingBar rate;
    @BindView(R.id.imageView) ImageView background;
    @BindView(R.id.button) Button review;
    @BindView(R.id.recyclerView) RecyclerView recyclerView1;
    @BindView(R.id.floatingActionButton) FloatingActionButton fab;
    @BindView(R.id.imageButton) ImageButton imageButton;

    public static final String ARG_ITEM_ID = "item_id";
    private static ModelMovie model;
    private DataBaseHelper dataBase;
    private  String video_id;
    protected List<ReviewModel> reviewData;
    private ReviewAdapter ReviewAdapter;
    public  LinearLayoutManager mLayoutManager;
   /* public static SharedPreferences settings;
    public static int index=0;*/
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

       //settings = getActivity().getSharedPreferences("favoritData", MODE_PRIVATE);
        reviewData=new ArrayList<>();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            model = (ModelMovie) getArguments().getSerializable(ARG_ITEM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.detials_fragment, container, false);
        ButterKnife.bind(this, view);

        recyclerView1.setHasFixedSize(true);
        ReviewAdapter = new ReviewAdapter(reviewData);
        recyclerView1.setAdapter(ReviewAdapter);

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestReviews(model.getID(),1);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestReviews(model.getID(),0);
                watchYoutubeVideo(video_id);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                addMovie(model);
            }
        });

        titel.setText(model.getOriginal_title());
        releasDate.setText(model.getRelease_date());
        overView.setText(model.getOverview());
//        rate.setRating(Float.parseFloat(model.getVote_average())/2);

        Picasso.with(getActivity()).load(model.getBackdrop_path()).into(background);
        return view;
    }

    /////////youtupe//////
    public void watchYoutubeVideo(String id){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    public void requestReviews(String id,int i) {
        String Url ="";
        if(i==0) {
            Url = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=f78be50ecdcaa8f00e595d90e6281fc8";
        }
        else
        {
            Url = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=f78be50ecdcaa8f00e595d90e6281fc8";
        }
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(Url);
        if (entry != null)
        {
            try                                   // fetch the data from cache
            {
                String data = new String(entry.data, "UTF-8");
                if(i==0)
                {
                    video_id=Parser.getVideoID(data);
                }
                clearDataSet();
                Iterator iterator = Parser.parseDataToReview(data).iterator();

                while (iterator.hasNext())
                {
                    ReviewModel rMovie = (ReviewModel)iterator.next();
                    reviewData.add(rMovie);
                    ReviewAdapter.notifyItemInserted(reviewData.size() - 1);
                }

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

        }
        /////////////connection//////////
        StringRequest strReq = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("response", response);
                clearDataSet();
                Iterator iterator = Parser.parseDataToReview(response).iterator();
                while (iterator.hasNext())
                {
                    ReviewModel rMovie = (ReviewModel)iterator.next();
                    reviewData.add(rMovie);
                    ReviewAdapter.notifyItemInserted(reviewData.size() - 1);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", error.toString());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void clearDataSet()
    {
        if (reviewData != null){
            reviewData.clear();
            ReviewAdapter.notifyDataSetChanged();
        }
    }
    public void addMovie(ModelMovie movie) {
        // SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieTable.KEY_ID, movie.getID());
        values.put(MovieTable.KEY_TITLE,movie.getOriginal_title());
        values.put(MovieTable.KEY_POSTER, movie.getPoster());
       // values.put(MovieTable.KEY_OVERVIEW,movie.getOverview());
       // values.put(MovieTable.KEY_VOTE_AVERAGE,movie.getVote_average());
       // values.put(MovieTable.KEY_RELEASE_DATE,movie.getRelease_date());
        values.put(MovieTable.KEY_BACKDROP_PATH,movie.getBackdrop_path());
        // Inserting Row
        //db.insert(TABLE_MOVIES, null, values);
        //db.close(); // Closing database connection
        MoviesContentProvider moviesContentProvider=new MoviesContentProvider(getContext());
        moviesContentProvider.insert(MoviesContentProvider.CONTENT_URI_add,values);
    }
}
