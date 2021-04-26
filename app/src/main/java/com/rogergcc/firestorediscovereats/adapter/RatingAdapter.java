/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rogergcc.firestorediscovereats.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.Query;
import com.rogergcc.firestorediscovereats.databinding.ItemRatingBinding;
import com.rogergcc.firestorediscovereats.model.Rating;

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


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View v = inflater.inflate(R.layout.item_shop_card, parent, false);
//        return new ViewHolder(v);

        com.rogergcc.firestorediscovereats.databinding.ItemRatingBinding itemCardBinding = ItemRatingBinding.inflate(inflater,parent,false);
        return new RatingAdapter.ViewHolder(itemCardBinding);

//        return new ViewHolder(LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rating rating = getSnapshot(position).toObject(Rating.class);

//        holder.nameView.setText(rating.getUserName());
//        holder.ratingBar.setRating((float) rating.getRating());
//        holder.textView.setText(rating.getText());

        //holder.bind(Objects.requireNonNull(getSnapshot(position).toObject(Rating.class)));
        holder.bind(rating);
    }

//
//    @Override
//    public int getItemCount() {
//        return getSnapshot().;
//    }

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
            this.binding = binding;
        }
        public void bind(Rating rating) {
            Log.e("Mylogger"," RatingAdapter: "+ rating.toString());
            binding.ratingItemName.setText(rating.getUserName());
            binding.ratingItemRating.setRating((float) rating.getRating());
            binding.ratingItemText.setText(rating.getText());
        }
    }

}
