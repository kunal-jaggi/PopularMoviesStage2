package com.udacity.popularmovies.stagetwo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.holder.MovieReviewHolder;
import com.udacity.popularmovies.stagetwo.network.model.MovieReview;

import java.util.List;

/**
 * Created by kunaljaggi on 4/29/16.
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewHolder> {

    private Context mContext;
    private List<MovieReview> mMovieReviewList;

    public MovieReviewAdapter(List<MovieReview> reviews) {
        mMovieReviewList = reviews;
    }


    @Override
    public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new MovieReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieReviewHolder holder, int position) {
        MovieReview movieReview = mMovieReviewList.get(position);
        holder.getmAuthor().setText(movieReview.getmAuthor());
        holder.getmContent().setText(movieReview.getmContent());
    }

    @Override
    public int getItemCount() {

        if(mMovieReviewList==null)
            return 0;
        else{
            return mMovieReviewList.size();
        }
    }

    public List<MovieReview> getmMovieReviewList() {
        return mMovieReviewList;
    }

    public void setmMovieReviewList(List<MovieReview> mMovieReviewList) {
        this.mMovieReviewList = mMovieReviewList;
    }
}


//old impl
//public class MovieReviewAdapter extends ArrayAdapter<MovieReview> {
//    private Context mContext;
//    private List<MovieReview> mMovieReviewList;
//
//    public MovieReviewAdapter(Context context, List<MovieReview> reviews) {
//        super(context, 0, reviews);
//        mContext = context;
//        mMovieReviewList = reviews;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.review_item, parent, false);
//        }
//
//        TextView author = (TextView) convertView.findViewById(R.id.review_item_author);
//        author.setText(mMovieReviewList.get(position).getmAuthor());
//
//        TextView textView = (TextView) convertView.findViewById(R.id.review_item_textView);
//        textView.setText(mMovieReviewList.get(position).getmContent());
//
//        return convertView;
//    }
//}