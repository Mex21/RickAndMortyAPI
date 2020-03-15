package com.example.rickandmortyapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.rickandmortyapi.Adapter.RecyclerViewAdapter;
import com.example.rickandmortyapi.Listener.RecyclerViewScrollListener;
import com.example.rickandmortyapi.Model.ApiResponse;
import com.example.rickandmortyapi.Model.Characters;
import com.example.rickandmortyapi.Util.Client;
import com.example.rickandmortyapi.Util.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private RecyclerViewAdapter adapter;

    private Call<ApiResponse> call;
    private Service service;

    //Used to manage loading footer on scroll
    private static final int PAGE_START = 1;
    private int TOTAL_ITEM;
    private int TOTAL_PAGE;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.mainActivityRecyclerView);
        progressBar = findViewById(R.id.mainActivityProgressBar);

        //Start a characters activity on click
        RecyclerViewAdapter.OnItemClickListener listener = new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, CharactersActivity.class);
                Characters characters = adapter.getApiResult().get(position);
                intent.putExtra("Characters", characters);
                startActivity(intent);
            }
        };

        adapter = new RecyclerViewAdapter(this, listener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener(linearLayoutManager) {
            /**
             * loadMoreItems is call want the last item of the page is reached
             */
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getPage();
            }

            @Override
            protected int getTotalItemsCount() {
                return TOTAL_ITEM;
            }

            @Override
            protected boolean isLastPage() {
                return isLastPage;
            }

            @Override
            protected boolean isLoading() {
                return isLoading;
            }
        });

        service = Client.getClient().create(Service.class);
        firstPage();
    }

    /**
     * Load the first page
     */
    private void firstPage(){
        call = service.getCharacters(1);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                TOTAL_PAGE = response.body().getInfo().getPages();
                TOTAL_ITEM = response.body().getInfo().getCount();

                //Get Characters list and add to adapter
                List<Characters> characterList = response.body().getCharacters();
                adapter.addAll(characterList);

                progressBar.setVisibility(View.GONE);

                //Check if it's the last page
                if (currentPage != TOTAL_PAGE){
                    adapter.addLoadingFooter();
                }
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                call.clone().enqueue(this);
            }
        });
    }

    /**
     * Load next pages
     */
    void getPage(){
        call = service.getCharacters(currentPage);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                //Get Characters list and add to adapter
                List<Characters> characterList = response.body().getCharacters();
                adapter.addAll(characterList);

                //Check if it's the last page
                if (currentPage != TOTAL_PAGE){
                    adapter.addLoadingFooter();
                }
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                call.clone().enqueue(this);
            }
        });
    }
}
