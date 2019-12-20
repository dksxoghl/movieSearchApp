package com.example.startproject2;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    MovieAdapter adapter;
    RecyclerView recyclerView;

    Movie[] movie;
    MovieList list;
    Handler handler = new Handler();

    Uri uri = MovieProvider.CONTENT_URI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        adapter = new MovieAdapter();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        queryMovie();
        SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder()
                        .addHeader("X-Naver-Client-Id", "tu_ZluZH5jCz4IZJ0q0d")
                        .addHeader("X-Naver-Client-Secret", "f3qz6KbkA3")
                        .url("https://openapi.naver.com/v1/search/movie.json?query=" + query)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(e);
                        System.out.println("getMovie 실패");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            Gson gson = new Gson();
                            String a = response.body().string();
                            System.out.println(a + "뭐라도찍어봐ㅠㅠ");
                            list = gson.fromJson(a, MovieList.class);

                            System.out.println(list.items.get(0).title + " " + list.items.get(0).userRating);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clearItems();
                                    clearMovie();
                                    insertMovie(list);
                                    for (int i = 0; i < list.items.size(); i++) {

                                        adapter.addItem(list.items.get(i));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

//                movie.title = query;

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

//        movie = new Movie();
//
//        movie.title = "movie title";
//        movie.director = "director";
//        movie.actor = "person1, pesron2, person3";
//        movie.userRating = 5;


        return rootView;
    }

    private void queryMovie() {
//        Uri uri = new Uri.Builder().build().parse(uriString);
        String[] columns = new String[]{"title", "director", "actor","link","rating","image","pubDate"};
        Cursor cursor = getActivity().getContentResolver().query(uri, columns, null, null, "name ASC");
//        println("query 결과: " + cursor.getCount());
//        int index = 0;
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(columns[0]));
            String director = cursor.getString(cursor.getColumnIndex(columns[1]));
            String actor = cursor.getString(cursor.getColumnIndex(columns[2]));
            String link = cursor.getString(cursor.getColumnIndex(columns[3]));
            float rating = cursor.getFloat(cursor.getColumnIndex(columns[4]));
            String image = cursor.getString(cursor.getColumnIndex(columns[5]));
            String pubDate = cursor.getString(cursor.getColumnIndex(columns[6]));
            adapter.addItem(new Movie(title,link,image,pubDate,director,actor,rating));
//            println("#" + index + " -> " + name + ", " + age);
//            index++;
        }
        adapter.notifyDataSetChanged();
    }

    private void insertMovie(MovieList movieList) {
//        Uri uri = new Uri.Builder().build().parse(uriString);

//        Cursor cursor = getActivity().getContentResolver().query(uri, null, null,
//                null, null);
//        getActivity().getContentResolver().query(uri, null, null,
//                null, null);
        if (movieList.items.size() != 0) {
            for (int i = 0; i < movieList.items.size(); i++) {
                Movie movie = movieList.items.get(i);
                ContentValues values = new ContentValues();
                values.put("title", movie.title);
                values.put("director", movie.director);
                values.put("actor", movie.actor);
                values.put("link", movie.link);
                values.put("rating", movie.userRating);
                values.put("image", movie.image);
                values.put("pubDate", movie.pubDate);
                uri = getActivity().getContentResolver().insert(uri, values);
                System.out.println(uri + " uri잘들어감");

            }
        }
    }

    private void clearMovie() {
//        Uri uri = new Uri.Builder().build().parse(uriString);
        int count = getActivity().getContentResolver().delete(uri, null, null);
        System.out.println(count+"무비uri지우기 ----");
    }
}
