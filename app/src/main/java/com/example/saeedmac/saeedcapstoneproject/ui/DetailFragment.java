package com.example.saeedmac.saeedcapstoneproject.ui;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.saeedmac.saeedcapstoneproject.BuildConfig;
import com.example.saeedmac.saeedcapstoneproject.R;
import com.example.saeedmac.saeedcapstoneproject.database.MovieContentProvider;
import com.example.saeedmac.saeedcapstoneproject.model.Movie;
import com.example.saeedmac.saeedcapstoneproject.model.SingleMovie;
import com.example.saeedmac.saeedcapstoneproject.network.ApiClient;
import com.example.saeedmac.saeedcapstoneproject.network.ApiInterface;
import com.example.saeedmac.saeedcapstoneproject.network.FetchReview;
import com.example.saeedmac.saeedcapstoneproject.ui.widget.AppWidget;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    private final static String API_KEY = BuildConfig.API_KEY;
    public TextView plotView, voteAvg, releaseDate, Title, reviews, voteCount, status;
    public ImageView imageView, imageView2, trailerview;
    public Toolbar myToolbar;
    public CollapsingToolbarLayout appbar;
    public String key;
    public long movie_id;
    public String[] review = new String[10];
    public String[] author = new String[10];
    FloatingActionButton viewtrailer;
    private Movie movie;
    private SingleMovie sMovie;
    private String resultJSON = null;
    private Cursor cursor;
    private boolean flag = false;
    private Context mContext;
    private Snackbar snackbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.movie_detail, container, false);
        mContext = rootView.getContext();
        Title = (TextView) rootView.findViewById(R.id.name);
        plotView = (TextView) rootView.findViewById(R.id.synopsis);
        voteAvg = (TextView) rootView.findViewById(R.id.vote_average);
        releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        Title = (TextView) rootView.findViewById(R.id.name);
        imageView = (ImageView) rootView.findViewById(R.id.big_poster);
        imageView2 = (ImageView) rootView.findViewById(R.id.small_poster);
        trailerview = (ImageView) rootView.findViewById(R.id.moviestills);
        reviews = (TextView) rootView.findViewById(R.id.reviews);
        status = (TextView) rootView.findViewById(R.id.status);
        voteCount = (TextView) rootView.findViewById(R.id.vote_count);
        viewtrailer = (FloatingActionButton) rootView.findViewById(R.id.trailer);
        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable(getString(R.string.MOVIE));
            movie_id = getArguments().getLong(getString(R.string.MOVIE_ID));
        }
        isFavorite();

        final FloatingActionButton share = (FloatingActionButton) rootView.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(getString(R.string.Concatenated));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.i_share) + movie_id);
                sendIntent.setType(getString(R.string.text_plain));
                startActivity(Intent.createChooser(sendIntent, getString(R.string.Share_Movie)));
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if (flag) {
            fab.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_border));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    flag = false;
                    fab.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_border));
                    mContext.getContentResolver().delete(MovieContentProvider.CONTENT_URI, Long.toString(movie_id), null);
                    Snackbar.make(view, R.string.sb_removed_favorite, Snackbar.LENGTH_LONG)
                            .setAction(R.string.Action, null).show();
                    AppWidgetManager awm = AppWidgetManager.getInstance(mContext);
                    Intent intent = new Intent(mContext, AppWidget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    getActivity().sendBroadcast(intent);
                } else {
                    if (isNetworkAvailable()) {
                        if (sMovie.getPosterPath() != null) {
                            flag = true;
                            fab.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite));
                            ContentValues values = new ContentValues();
                            values.put(getString(R.string.movie_id), movie_id);
                            values.put(getString(R.string.poster_path), sMovie.getPosterPath());
                            getActivity().getContentResolver().insert(MovieContentProvider.CONTENT_URI, values);
                            Snackbar.make(view, R.string.sb_saved_favorite, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.Action, null).show();
                            AppWidgetManager awm = AppWidgetManager.getInstance(mContext);
                            Intent intent = new Intent(mContext, AppWidget.class);
                            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            getActivity().sendBroadcast(intent);
                        } else {
                            Snackbar.make(view, R.string.sb_problem_saving_favorite, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.Action, null).show();
                        }
                    } else {
                        snackbar = Snackbar.make(view, R.string.sb_no_internet, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(getString(R.string.dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        }).show();
                    }
                }
            }
        });

        myToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);

        if (!MainActivity.tablet) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        appbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);

        //System.out.println(movie.getId());
        getData(movie_id);
        return rootView;
    }

    void getData(long movie_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SingleMovie> call = apiService.getMovieDetails(movie_id, API_KEY, getString(R.string.videos_p));
        call.enqueue(new Callback<SingleMovie>() {
            @Override
            public void onResponse(Call<SingleMovie> call, Response<SingleMovie> response) {
                int statusCode = response.code();
                sMovie = response.body();
                displayData(sMovie);
                System.out.println(getString(R.string.sMovie) + sMovie.getId());
                System.out.println(getString(R.string.single_movie) + call.request().url());
            }

            @Override
            public void onFailure(Call<SingleMovie> call, Throwable t) {
                // Log error here since request failed
                //Log.e("error : ", t.toString());
                Log.e(getString(R.string.error), t.getMessage());
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void displayData(SingleMovie sMovie) {
        appbar.setTitle(sMovie.getTitle());

        System.out.println(getString(R.string.sMovie) + sMovie.getTitle());
        //Toast.makeText(getActivity(), "Showing " + sMovie.getTitle(), Toast.LENGTH_SHORT).show();
        status.setText(sMovie.getStatus());
        Glide.with(mContext)
                .load(getString(R.string.image_tmdb) + sMovie.getBackdropPath())
                .into(imageView);
        System.out.println(getString(R.string.backdrop_path) + sMovie.getBackdropPath());
        Glide.with(mContext)
                .load(getString(R.string.image_tmdb) + sMovie.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView2);
        System.out.println(getString(R.string.image_tmdb) + sMovie.getPosterPath());
        Title.setText(sMovie.getTitle());
        releaseDate.setText(sMovie.getReleaseDate());
        voteAvg.setText(Double.toString(sMovie.getVoteAverage()));
        plotView.setText(sMovie.getOverview());
        voteCount.setText((sMovie.getVoteCount() + getString(R.string.votes)));
        //reviews.setText(sMovie.getReviews().getResults().toString());

        System.out.println(getString(R.string.isVideoAvailable)+sMovie.getVideo());
        key = sMovie.getVideos().getResults().get(0).getKey();
        Glide.with(mContext)
                .load(getString(R.string.img_youtube) + key + getString(R.string.hqdefault))
                .fitCenter()
                .into(trailerview);
        System.out.println(getString(R.string.trailer) + getString(R.string.img_youtube) + key + getString(R.string.hqdefault));
        viewtrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.m_youtube) + key));
                startActivity(intent1);
            }
        });
       /* } else {
            key = null;
            viewtrailer.setVisibility(View.GONE);
            trailerview.setVisibility(View.GONE);
        }*/


        FetchReview task1 = new FetchReview();
        reviews.setText("");
        try {
            resultJSON = task1.execute(movie_id).get();
            if (resultJSON != null) {
                JSONObject movie = new JSONObject(resultJSON);
                JSONArray movieDetails = movie.getJSONArray(getString(R.string.results));
                for (int i = 0; i <= 5; i++) {
                    JSONObject mov_reviews = movieDetails.getJSONObject(i);
                    author[i] = mov_reviews.getString(getString(R.string.author));
                    review[i] = mov_reviews.getString(getString(R.string.content));
                    reviews.append("\n"+getString(R.string.Review_By) + author[i] + "\n" + review[i] + "\n");
                    reviews.append("\n"+getString(R.string.space));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void isFavorite() {
        flag = false;
        cursor = mContext.getContentResolver().query(MovieContentProvider.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getLong(0) == movie_id) {
                    flag = true;
                }
            } while (cursor.moveToNext());
        }
    }
}