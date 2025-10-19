package com.upwardsnorthwards.blueplaqueslondon.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    private final List<Placemark> placemarks;
    private List<Placemark> filteredPlacemarks;

    public SearchAdapter(List<Placemark> placemarks) {
        this.placemarks = placemarks;
        this.filteredPlacemarks = new ArrayList<>(placemarks);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.search_item_layout, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Placemark placemark = filteredPlacemarks.get(position);
        holder.title.setText(placemark.getName());
        holder.subtitle.setText(placemark.getTrimmedOccupation());
    }

    @Override
    public int getItemCount() {
        return filteredPlacemarks.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredPlacemarks = placemarks;
                } else {
                    List<Placemark> filteredList = new ArrayList<>();
                    for (Placemark row : placemarks) {
                        if (row.getName().toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT))) {
                            filteredList.add(row);
                        }
                    }
                    filteredPlacemarks = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredPlacemarks;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPlacemarks = (ArrayList<Placemark>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView title;
        public MaterialTextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.search_title);
            subtitle = itemView.findViewById(R.id.search_subtitle);
        }
    }
}
