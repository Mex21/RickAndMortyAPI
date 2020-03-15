package com.example.rickandmortyapi;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rickandmortyapi.Model.Characters;

public class CharactersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);
        Intent intent = getIntent();
        Characters characters = (Characters)intent.getSerializableExtra("Characters");

        ImageView imageView = findViewById(R.id.charactersActivityImageView);
        TextView textViewName = findViewById(R.id.charactersActivityName);
        TextView textViewStatus = findViewById(R.id.charactersActivityStatusString);
        TextView textViewSpecies = findViewById(R.id.charactersActivitySpeciesString);
        TextView textViewType = findViewById(R.id.charactersActivityTypeString);
        TextView textViewGender = findViewById(R.id.charactersActivityGenderString);
        TextView textViewLocation = findViewById(R.id.charactersActivityLocationString);
        TextView textViewOrigin = findViewById(R.id.charactersActivityOriginString);
        TextView textViewCreated = findViewById(R.id.charactersActivityCreatedString);

        Glide
                .with(this)
                .load(characters.getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView);

        textViewName.setText(characters.getName());
        textViewCreated.setText(characters.getCreated());
        textViewGender.setText(characters.getGender());
        textViewLocation.setText(characters.getLocation().getName());
        textViewOrigin.setText(characters.getOrigin().getName());
        textViewSpecies.setText(characters.getSpecies());
        textViewStatus.setText(characters.getStatus());
        textViewType.setText(characters.getType());
    }
}
