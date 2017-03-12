package com.example.aya.bestmovies.adapter;

/**
 * Created by aya on 11/11/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aya.bestmovies.R;
import com.example.aya.bestmovies.models.reviewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by aya on 04/11/2016.
 */

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.ViewHolder> {

    private List<reviewModel> DataSet;


    public reviewAdapter(List<reviewModel> dataSet)
    {
        DataSet = dataSet;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.auther)TextView auther;
        @BindView(R.id.link)TextView link;
        @BindView(R.id.review)TextView review;

        public ViewHolder(View v)
        {
            super(v);
            ButterKnife.bind(this,v);
        }

        public TextView getAuther() {
            return auther;
        }

        public TextView getLink() {
            return link;
        }

        public TextView getReview() {
            return review;
        }

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return  new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if (DataSet.get(position) != null) {
            Log.d("", "Element " + position + " set.");
            holder.getAuther().setText(DataSet.get(position).getAuther());
            holder.getLink().setText(Html.fromHtml("<a href=\"" + DataSet.get(position).getUrl() + "\">"
                    + DataSet.get(position).getUrl() + "</a> "));
            holder.getLink().setMovementMethod(LinkMovementMethod.getInstance());
            holder.getReview().setText(DataSet.get(position).getContent());

        }
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }
}
