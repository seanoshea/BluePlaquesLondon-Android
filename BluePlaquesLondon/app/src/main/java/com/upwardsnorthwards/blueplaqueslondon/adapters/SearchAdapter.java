// Copyright (c) 2014 - 2016 Upwards Northwards Software Limited
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// 3. All advertising materials mentioning features or use of this software
// must display the following acknowledgement:
// This product includes software developed by Upwards Northwards Software Limited.
// 4. Neither the name of Upwards Northwards Software Limited nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY UPWARDS NORTHWARDS SOFTWARE LIMITED ''AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE UPWARDS NORTHWARDS SOFTWARE LIMITED BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.upwardsnorthwards.blueplaqueslondon.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Used to display the list of placemarks displayed when the user is searching for a particular placemark.
 */
public class SearchAdapter extends ArrayAdapter<Placemark> implements Filterable {

    private static final String TAG = "SearchAdapter";

    private List<Placemark> placemarks;
    private List<Placemark> filteredPlacemarks;
    private PlacemarksFilter placemarksFilter;

    private String closestPlacemarkTitle;

    public SearchAdapter(final Context context, final int resource, final List<Placemark> objects) {
        super(context, resource, objects);
        closestPlacemarkTitle = context.getString(R.string.closest);
        placemarks = objects;
    }

    public Placemark getFilteredPlacemarkAtPosition(final int index) {
        Placemark placemark = getClosestPlacemark();
        final List<Placemark> relevantPlacemarks = getRelevantPlacemarks();
        if (relevantPlacemarks.size() + getOffset() >= index) {
            placemark = relevantPlacemarks.get(index);
        }
        return placemark;
    }

    @Override
    public Filter getFilter() {
        if (placemarksFilter == null) {
            placemarksFilter = new PlacemarksFilter();
        }
        return placemarksFilter;
    }

    @Override
    public Placemark getItem(final int position) {
        Placemark placemark = null;
        final List<Placemark> relevantPlacemarks = getRelevantPlacemarks();
        if (relevantPlacemarks != null && position < relevantPlacemarks.size()) {
            placemark = relevantPlacemarks.get(position);
        }
        return placemark;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            final LayoutInflater vi = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.search_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) v.findViewById(R.id.search_title);
            viewHolder.subtitle = (TextView) v
                    .findViewById(R.id.search_subtitle);
            v.setTag(viewHolder);
        }
        final List<Placemark> relevantPlacemarks = getRelevantPlacemarks();
        if (relevantPlacemarks != null && position < getCount()) {
            final Placemark placemark;
            final ViewHolder holder = (ViewHolder) v.getTag();
            if (position == 0) {
                v.setBackgroundResource(R.drawable.search_item_closest_background);
                holder.title.setTextColor(parent.getContext().getResources().getColor(android.R.color.white));
                holder.title.setText(parent.getContext().getString(
                        R.string.closest));
            } else {
                v.setBackgroundResource(R.drawable.search_item_background);
                holder.title.setTextColor(parent.getContext().getResources().getColor(R.color.blue_color));
                if (relevantPlacemarks.size() > position) {
                    placemark = relevantPlacemarks.get(position);
                    holder.placemark = placemark;
                    holder.title.setText(placemark.getName());
                    holder.subtitle.setText("");
                }
            }
        }
        return v;
    }

    @Override
    public int getCount() {
        int size = getOffset();
        final List<Placemark> relevantPlacemarks = getRelevantPlacemarks();
        if (relevantPlacemarks != null) {
            size += relevantPlacemarks.size();
        }
        return size;
    }

    private List<Placemark> filterPlacemarksWithText(final String filterText) {
        final List<Placemark> localPlacemarks = new ArrayList<Placemark>();
        for (final Placemark placemark : placemarks) {
            if (placemark.getName().toLowerCase(Locale.ENGLISH)
                    .contains(filterText.toLowerCase(Locale.ENGLISH))) {
                localPlacemarks.add(placemark);
            }
        }
        // alphabetically sort 'em
        Collections.sort(localPlacemarks, new Comparator<Placemark>() {
            @Override
            public int compare(final Placemark lhs, final Placemark rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        Log.v(TAG, "Filtering the list of filters with " + filterText + " " + localPlacemarks.size());
        return localPlacemarks;
    }

    public static class ViewHolder {
        public Placemark placemark;
        public TextView title;
        public TextView subtitle;
    }

    public List<Placemark> getRelevantPlacemarks() {
        if (filteredPlacemarks != null) {
            return filteredPlacemarks;
        } else {
            return placemarks;
        }
    }

    public List<Placemark> getPlacemarks() {
        return placemarks;
    }

    public void setPlacemarks(final List<Placemark> placemarks) {
        this.placemarks = placemarks;
    }

    protected Placemark getClosestPlacemark() {
        final Placemark placemark = new Placemark();
        placemark.setName(closestPlacemarkTitle);
        return placemark;
    }

    private class PlacemarksFilter extends Filter {

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            final FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                final List<Placemark> filteredPlacemarks = filterPlacemarksWithText(constraint.toString());
                // always have the closest placemark as an option, regardless of the filtering result
                filteredPlacemarks.add(0, getClosestPlacemark());
                filterResults.values = filteredPlacemarks;
                filterResults.count = filteredPlacemarks.size();
            }
            return filterResults;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(final CharSequence constraint, final FilterResults results) {
            if (results != null && results.count > 0) {
                filteredPlacemarks = (List<Placemark>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private int getOffset() {
        return filteredPlacemarks != null && filteredPlacemarks.size() > 1 ? 0 : 1;
    }
}
