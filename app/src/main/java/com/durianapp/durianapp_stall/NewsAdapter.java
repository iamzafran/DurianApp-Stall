package com.durianapp.durianapp_stall;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Amer S Alkatheri on 23-Jun-17.
 */

public class NewsAdapter extends RecyclerView.Adapter <NewsAdapter.NewsHolder> {

    private Context nContext;
    private List<NewsData> nData;

    public NewsAdapter(Context context, List<NewsData> nData) {
        this.nContext = context;
        this.nData = nData;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_cards,parent,false);
        return new NewsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, final int position) {
        holder.newsTitleView.setText(nData.get(position).getTitle());

        holder.cardNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = nData.get(position).getnId();

                Intent intent = new Intent(nContext, NewsDetailActivity.class);
                intent.putExtra("NEWS_ID", id);
                nContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nData.size();
    }

    public  class NewsHolder extends  RecyclerView.ViewHolder {
        public TextView newsTitleView;
        public RelativeLayout rlNews;
        public CardView cardNews;

        public NewsHolder(View itemView) {
            super(itemView);
            newsTitleView = (TextView) itemView.findViewById(R.id.newsTitleView);
            rlNews = (RelativeLayout) itemView.findViewById(R.id.rlNews);
            cardNews = (CardView) itemView.findViewById(R.id.cardNewsHere);
        }
    }
}
