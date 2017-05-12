package com.viintro.Viintro.Search_Post;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.viintro.Viintro.R;

/**
 * Created by hasai on 12/04/17.
 */
public class LazyLoadingHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressbar;
    public LazyLoadingHolder(View v) {
        super(v);
        progressbar = (ProgressBar) v.findViewById(R.id.progressBar1);
    }
}
