package com.example.rickandmortyapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rickandmortyapi.Adapter.RecyclerViewAdapter;
import com.example.rickandmortyapi.Listener.RecyclerViewScrollListener;
import com.example.rickandmortyapi.Model.ApiResponse;
import com.example.rickandmortyapi.Model.Characters;
import com.example.rickandmortyapi.Util.Client;
import com.example.rickandmortyapi.Util.Service;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView retryMainActivity;

    private RecyclerViewAdapter adapter;

    private Call<ApiResponse> call;
    private Service service;

    //Used to manage loading footer on scroll
    private static int FIRST_PAGE = 1;
    private static int NUMBER_OF_RETRY = 5;

    private int TOTAL_ITEM;
    private int TOTAL_PAGE;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = FIRST_PAGE;
    private int retry = 0;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.mainActivityRecyclerView);
        progressBar = findViewById(R.id.mainActivityProgressBar);
        retryMainActivity = findViewById(R.id.retryMainActivity);

        retryMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove Main Activity Retry and Show Progress Bar
                progressBar.setVisibility(View.VISIBLE);
                retryMainActivity.setVisibility(View.GONE);
                //Set number of retry to zero and get the next page
                retry = 0;
                getPage();
            }
        });

        //Start a characters activity on click
        RecyclerViewAdapter.OnItemClickListener listener = new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int viewType = adapter.getItemViewType(position);
                if (viewType == RecyclerViewAdapter.ITEM) {
                    Intent intent = new Intent(context, CharactersActivity.class);
                    Characters characters = adapter.getApiResult().get(position);
                    intent.putExtra("Characters", characters);
                    startActivity(intent);
                } else if (viewType == RecyclerViewAdapter.RETRY) {
                    adapter.removeFooter();
                    adapter.addLoadingFooter();
                    getPage();
                }
            }
        };

        adapter = new RecyclerViewAdapter(this, listener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener(linearLayoutManager) {

            //loadMoreItems is call when the last item of the page is reached
            @Override
            protected void loadMoreItems() {
                if(retry < NUMBER_OF_RETRY){
                getPage();
                }
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

        if (savedInstanceState != null) {
            progressBar.setVisibility(View.GONE);

            ArrayList<Characters> arrayListCharacters = (ArrayList<Characters>) savedInstanceState.getSerializable("charactersList");
            ArrayList<Integer> arrayListViewType = savedInstanceState.getIntegerArrayList("listViewType");
            adapter.addAll(arrayListCharacters,arrayListViewType);

            currentPage = savedInstanceState.getInt("currentPage");
            TOTAL_PAGE = savedInstanceState.getInt("TOTAL_PAGE");
            TOTAL_ITEM = savedInstanceState.getInt("TOTAL_ITEM");
            isLoading = savedInstanceState.getBoolean("isLoading");
            isLastPage = savedInstanceState.getBoolean("isLastPage");
        }else{
            getPage();
        }
    }

    /**
     * Load pages
     */
    void getPage() {
        call = service.getCharacters(currentPage);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                retry = 0;
                progressBar.setVisibility(View.GONE);

                // For the first page, the numbers of pages and items are store and the mainActivity progressBar is hide
                if (currentPage == FIRST_PAGE) {
                    TOTAL_PAGE = response.body().getInfo().getPages();
                    TOTAL_ITEM = response.body().getInfo().getCount();
                }
                // For all other pages the loadingFooter is remove
                else {
                    adapter.removeFooter();
                    isLoading = false;
                }

                //Get Characters list and add to adapter
                List<Characters> characterList = response.body().getCharacters();
                adapter.addAll(characterList,null);

                //Check if it's the last page
                if (currentPage != TOTAL_PAGE) {
                    adapter.addLoadingFooter();
                    currentPage += 1;
                } else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                retry += 1;
                if (adapter.getItemCount() != 0) {
                    if (retry < NUMBER_OF_RETRY) {
                        getPage();
                    } else {
                        adapter.removeFooter();
                        adapter.addRetry();
                        Toast toast = Toast.makeText(context,"Network error", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    retryMainActivity.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("charactersList", adapter.getApiResult());
        outState.putInt("currentPage", currentPage);
        outState.putInt("TOTAL_PAGE", TOTAL_PAGE);
        outState.putInt("TOTAL_ITEM", TOTAL_ITEM);
        outState.putIntegerArrayList("listViewType",adapter.getListViewType());
        outState.putBoolean("isLoading", isLoading);
        outState.putBoolean("isLastPage", isLastPage);
    }


}
