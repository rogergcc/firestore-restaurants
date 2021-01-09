/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.rogergcc.firestorediscovereats.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.Query;
import com.rogergcc.firestorediscovereats.databinding.ItemRatingBinding;
import com.rogergcc.firestorediscovereats.model.Rating;

import java.util.Objects;

/**
 * RecyclerView adapter for a bunch of Ratings.
 */
public class RatingAdapter extends FirestoreAdapter<RatingAdapter.ViewHolder> {

    public RatingAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRatingBinding binding = ItemRatingBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(binding);

//        return new ViewHolder(LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(Objects.requireNonNull(getSnapshot(position).toObject(Rating.class)));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

//        TextView nameView;
//        MaterialRatingBar ratingBar;
//        TextView textView;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            nameView = itemView.findViewById(R.id.rating_item_name);
//            ratingBar = itemView.findViewById(R.id.rating_item_rating);
//            textView = itemView.findViewById(R.id.rating_item_text);
//        }
        ItemRatingBinding binding;
        public ViewHolder(@NonNull ItemRatingBinding binding) {
            super(binding.getRoot());

        }
        public void bind(Rating rating) {

            binding.ratingItemName.setText(rating.getUserName());
            binding.ratingItemRating.setRating((float) rating.getRating());
            binding.ratingItemText.setText(rating.getText());
        }
    }

}
