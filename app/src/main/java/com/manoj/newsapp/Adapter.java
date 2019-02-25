package com.manoj.newsapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;



public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Articles> articles;
    private Context context;
    public String url;



    public Adapter(List<Articles> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.news_item,viewGroup,false);


        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {


        viewHolder.textTitle.setText(articles.get(i).getTitle());
        viewHolder.textDescription.setText(articles.get(i).getDescription());
        viewHolder.publishedAt.setText(articles.get(i).getPublishedAt());
        viewHolder.readMOre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                Intent intent = new Intent(context,ReadMore.class);
                intent.putExtra("url",articles.get(i).getUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);}
                catch (Exception e){
                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        Picasso.with(context).load(articles.get(i).getUrlToImage()).
                error(R.drawable.error).resize(375,210).
                into(viewHolder.image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(context).load(articles.get(i).getUrlToImage()).fetch();




    }


    @Override
    public int getItemCount() {
        return articles.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView textTitle;
        TextView publishedAt;
        TextView textDescription;
        Button readMOre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img);
            textTitle= itemView.findViewById(R.id.title);
            publishedAt=itemView.findViewById(R.id.date);
            textDescription = itemView.findViewById(R.id.desc);
            readMOre = itemView.findViewById(R.id.readmore);


        }
    }




}
