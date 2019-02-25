package com.manoj.newsapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONresponse {
   private List<Articles> articles= new ArrayList<>();
   JSONObject parts;


    public List<Articles> response(String result){
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray articlesArray = obj.getJSONArray("articles");
            if(articlesArray!=null){
            for(int i=0;i<articlesArray.length();i++){
            parts= articlesArray.getJSONObject(i);
            articles.add(new Articles(parts.getJSONObject("source"),parts.getString("author"),parts.getString("title"),parts.getString("description"),parts.getString("url"),parts.getString("urlToImage"),parts.getString("publishedAt")));
            }
            }
            return articles;
        } catch (JSONException e) {

            e.printStackTrace();
            return null;
        }

    }

}
