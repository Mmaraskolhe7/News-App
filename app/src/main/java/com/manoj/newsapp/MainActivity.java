package com.manoj.newsapp;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public Adapter adapter;
    int i;


    private SwipeRefreshLayout swipeRefreshLayout;

    Geocoder geocoder;
    LocationManager locationManager;
    LocationListener locationListener;


    JSONresponse jsoNresponse;
    String result;
    String rString;

    String countryCode;


    public static final String BASE_URL = "https://newsapi.org/v2/top-headlines?country=";
    public static final String SEARCH_URL = "https://newsapi.org/v2/everything?q=";
    public static boolean flag;

    Locale locale = Locale.getDefault();
    String COUNTRY = String.valueOf(locale.getCountry()).toLowerCase();
    public static final String API_KEY = "61d282d944884bc19cee1e41c9722564";

    ConnectivityManager connectivityManager;
    SharedPreferences sharedPreferences;

    public List<Articles> articles = new ArrayList<>();

    NotificationCompat.Builder notification1;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout = findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        notification1 = new NotificationCompat.Builder(this);
        notification1.setAutoCancel(true);


        sharedPreferences = getSharedPreferences("com.manoj.newsapp", MODE_PRIVATE);
    database = this.openOrCreateDatabase("News", MODE_PRIVATE, null);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                countryCode = address(location);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {


            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location startlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (startlocation != null) {

                countryCode = address(startlocation);

            }
        }


        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            flag = true;
        } else {
            flag = false;
            Toast.makeText(this, "You are Offline", Toast.LENGTH_SHORT).show();
        }


        jsoNresponse = new JSONresponse();
        DownloadClass task = new DownloadClass();
            result = null;

            if(flag) {
                try {
                    if (countryCode != null)
                        COUNTRY = countryCode.toLowerCase();

                    Toast.makeText(this, "Showing results of " + COUNTRY, Toast.LENGTH_SHORT).show();


                    result = task.execute("https://newsapi.org/v2/top-headlines?country=" + COUNTRY + "&apiKey=" + API_KEY).get();

                    sharedPreferences.edit().putInt("Islogin", 1).apply();
                } catch (ExecutionException e) {
                    Log.i("queeri 1", "Mkmak");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.i("queeri 1", "Mmashd");
                    e.printStackTrace();
                }


                try {

                    database.execSQL("DROP TABLE IF EXISTS data");
                    database.execSQL("CREATE TABLE IF NOT EXISTS data(json VARCHAR)");

                    rString = result.replace("\"", "var1test");
                    rString = rString.replace("\'", "var2test");
                    database.execSQL("INSERT INTO data(json) VALUES ('" + rString + "')");
                    Log.i("Result", "saved");
                } catch (Exception e) {
                    Log.i("Result", "unsaved");
                    e.printStackTrace();
                }
            }
       if(!flag) { swipeRefreshLayout.setEnabled(false);
            if (sharedPreferences.getInt("Islogin", 0) == 1) {
                try {
                    Cursor c = database.rawQuery("SELECT * FROM data", null);
                    int jsonIndex = c.getColumnIndex("json");
                    c.moveToFirst();
                    while (c != null) {
                        result = c.getString(jsonIndex).replace("var1test", "\"").replace("var2test", "\'");
                        Log.i("Result", result);
                        c.moveToNext();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Error", "Unable to fetche");
                }
            }

        }


        if (result != null) {


            articles.clear();
            articles = jsoNresponse.response(result);

            adapter = new Adapter(articles, getApplicationContext());

            if (flag) {

                setNotification(articles.get(1).getTitle(), articles.get(1).getDescription(), notification1, articles.get(1).getUrl());

            }
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
        

    }


    public void setNotification(String ticker, String title, NotificationCompat.Builder notification1, String url) {
        notification1.setSmallIcon(R.drawable.launcher);

        notification1.setTicker(ticker);
        notification1.setWhen(System.currentTimeMillis());
        notification1.setContentTitle(title);

        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification1.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification1.build());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search News.....");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    i = 0;
                    if (s.length() > 2) {
                        swipeRefreshLayout.setEnabled(false);

                             if(flag) {
                                 try {
                                     DownloadClass downloadClass = new DownloadClass();
                                     s = s.replaceAll("\\s+", "-");
                                     result = downloadClass.execute(SEARCH_URL + s + "&apiKey=" + API_KEY).get();

                                 } catch (ExecutionException e) {

                                     e.printStackTrace();
                                 } catch (InterruptedException e) {

                                     e.printStackTrace();
                                 }
                                 if (result != null) {


                                     articles.clear();
                                     articles = jsoNresponse.response(result);

                                     adapter = new Adapter(articles, getApplicationContext());
                                     recyclerView.setAdapter(adapter);
                                     adapter.notifyDataSetChanged();

                                 }
                             }else
                                 Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_SHORT).show();
                       }


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    return false;

                }
            });

        searchMenuItem.getIcon().setVisible(false, false);

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public String address(Location location) {
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {

                String clickAddress = addresses.get(0).getCountryCode();
                return clickAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setEnabled(true);
        try {
            DownloadClass downloadClass = new DownloadClass();
            result = downloadClass.execute(BASE_URL + COUNTRY + "&apiKey=" + API_KEY).get();
            swipeRefreshLayout.setRefreshing(true);
            if (result != null) {


                articles.clear();
                articles = jsoNresponse.response(result);

                adapter = new Adapter(articles, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            swipeRefreshLayout.setRefreshing(false);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}



