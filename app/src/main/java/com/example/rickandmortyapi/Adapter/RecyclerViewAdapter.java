package com.example.rickandmortyapi.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rickandmortyapi.Model.Characters;
import com.example.rickandmortyapi.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    public static final int ITEM = 0;
    public static final int LOADING = 1;
    public static final int RETRY = 2;
    private ArrayList<Characters> apiResult;
    private ArrayList<Integer> listViewType;
    private OnItemClickListener listener;


    public RecyclerViewAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        apiResult = new ArrayList<>();
        listViewType = new ArrayList<>();
        this.listener = listener;
    }

    /**
     * Create the viewHolder with the good item
     *
     * @param viewType type of the view (0 if it's a character item, 1 if it's a loading item)
     * @return a viewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType){
            case ITEM :
                View viewItem = inflater.inflate(R.layout.recycler_view_item,parent,false);
                viewHolder = new CharactersViewHolder(viewItem,listener);
                break;
            case LOADING :
                View viewLoading = inflater.inflate(R.layout.recycler_view_progress_bar,parent,false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
            case RETRY :
                View viewRetry = inflater.inflate(R.layout.recycler_view_retry,parent,false);
                viewHolder = new RetryViewHolder(viewRetry,listener);
                break;
        }
        return viewHolder;
    }

    /**
     * Update the viewHolder
     *
     * @param holder the viewHolder to update
     * @param position of the viewHolder to update
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Characters characters = apiResult.get(position);
        switch (getItemViewType(position)){
            case ITEM:
                final CharactersViewHolder charactersViewHolder = (CharactersViewHolder) holder;
                charactersViewHolder.textView.setText(characters.getName());
                Glide.with(context)
                        .load(characters.getImage())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                charactersViewHolder.progressBar.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                charactersViewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(charactersViewHolder.imageView);
                break;
            case LOADING:
            case RETRY:
                //Do nothing
                break;
        }
    }

    /**
     * @return the current number of items
     */
    @Override
    public int getItemCount() {
        return apiResult == null ? 0 : apiResult.size();
    }

    /**
     * Get the viewType
     *
     * @param position of the item
     * @return the item viewType
     */
    @Override
    public int getItemViewType(int position) {
        return listViewType.get(position);
    }

    /**
     * Add a character to the recyclerView and add the view type into listViewType and notify observer
     *
     * @param character the character object to add
     */
    public void add(Characters character, int viewType){
        apiResult.add(character);
        listViewType.add(viewType);
        notifyItemInserted(apiResult.size()-1);
    }

    /**
     * Add a List of character to the recyclerView and notify observer
     *
     * @param charactersList the list of character object to add
     * @param typeViewList the list of viewType to add
     */
    public void addAll(List<Characters> charactersList, List<Integer> typeViewList) {
        for (int i = 0; i < charactersList.size(); i++) {
            int type;
            if (typeViewList == null){
                type = ITEM;
            }else {
                type = typeViewList.get(i);
            }
            add(charactersList.get(i),type);
        }
    }

    /**
     * Add the Progress Bar at the end of the recyclerView
     */
    public void addLoadingFooter() {
        add(new Characters(),LOADING);
    }

    /**
     * Add the Retry at the end of the recyclerView
     */
    public void addRetry(){
        add(new Characters(),RETRY);
    }

    /**
     * Remove footer and notify observer
     */
    public void removeFooter() {
        int position = apiResult.size() - 1;
        Characters characters = apiResult.get(position) ;

        if (characters != null) {
            apiResult.remove(position);
            listViewType.remove(position);
            notifyItemRemoved(position);
        }
    }


    public ArrayList<Characters> getApiResult(){
        return apiResult;
    }

    public ArrayList<Integer> getListViewType() {
        return listViewType;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    /**
     * Character item View Holder
     */
    protected static class CharactersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageView;
        private TextView textView;
        private ProgressBar progressBar;
        private OnItemClickListener listener;

        CharactersViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerViewItemImageView);
            textView = itemView.findViewById(R.id.recyclerViewItemTextView);
            progressBar = itemView.findViewById(R.id.recyclerViewItemProgress);
            progressBar.setVisibility(View.VISIBLE);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v,getAdapterPosition());
        }
    }

    /**
     * Loading item View Holder
     */
    protected static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Retry item View Holder
     */
    protected static class RetryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnItemClickListener listener;

        RetryViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }


}
