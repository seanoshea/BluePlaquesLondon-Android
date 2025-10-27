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
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.List;

/**
 * RecyclerView adapter for displaying multiple blue plaques at the same location.
 * 
 * <p>This adapter handles the scenario where multiple notable people have lived at the same
 * address, requiring the user to choose which specific plaque they want to view. It provides
 * a clean list interface with each placemark's title displayed for easy selection.</p>
 * 
 * <p><strong>Use Cases:</strong></p>
 * <ul>
 *   <li>Multiple historical figures lived at the same address</li>
 *   <li>Same building has multiple commemorative plaques</li>
 *   <li>Coordinate precision results in overlapping plaque locations</li>
 * </ul>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * List<Placemark> multiplePlacemarks = getPlacemarksAtLocation(latitude, longitude);
 * if (multiplePlacemarks.size() > 1) {
 *     MultiplePlacemarksAdapter adapter = new MultiplePlacemarksAdapter(context, multiplePlacemarks);
 *     listView.setAdapter(adapter);
 *     // Show dialog or fragment with the list
 * }
 * }</pre>
 * 
 * <p><strong>Architecture Integration:</strong></p>
 * <ul>
 *   <li>Used by {@link com.upwardsnorthwards.blueplaqueslondon.fragments.BluePlaquesMapFragment} for marker disambiguation</li>
 *   <li>Integrates with {@link Placemark} model for data display</li>
 *   <li>Follows Android ListView adapter pattern with ViewHolder optimization</li>
 * </ul>
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 * @see android.widget.ArrayAdapter
 * @see Placemark
 * @see ViewHolder
 */
public class MultiplePlacemarksAdapter extends ArrayAdapter<Placemark> {

    private final List<Placemark> placemarks;

    /**
     * Creates a new adapter for displaying multiple placemarks.
     * 
     * @param context the application context for accessing resources and layout inflater
     * @param objects the list of placemarks to display, must not be null
     * @throws IllegalArgumentException if objects is null or empty
     */
    public MultiplePlacemarksAdapter(final Context context, @NonNull final List<Placemark> objects) {
        super(context, R.layout.multiple_placemarks_item, objects);
        placemarks = objects;
    }

    /**
     * Creates and configures the view for a placemark at the specified position.
     * 
     * <p>Implements the ViewHolder pattern for efficient view recycling and performance.
     * Each view displays the placemark's trimmed title for easy identification.</p>
     * 
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent ViewGroup that this view will be attached to
     * @return a View corresponding to the data at the specified position
     */
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            final LayoutInflater vi = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.multiple_placemarks_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) v
                    .findViewById(R.id.multiple_placemarks_title);
            v.setTag(viewHolder);
        }
        if (placemarks != null && position < placemarks.size()) {
            final Placemark placemark = placemarks.get(position);
            final ViewHolder holder = (ViewHolder) v.getTag();
            holder.placemark = placemark;
            holder.title.setText(placemark.getTrimmedTitle());
        }
        return v;
    }

    /**
     * Returns the number of placemarks in the adapter.
     * 
     * @return the count of placemarks, or 0 if the list is null
     */
    @Override
    public int getCount() {
        int size = 0;
        if (placemarks != null) {
            size = placemarks.size();
        }
        return size;
    }

    /**
     * ViewHolder pattern implementation for efficient view recycling.
     * 
     * <p>Caches view references to avoid expensive findViewById calls during scrolling.
     * This significantly improves performance when displaying large lists of placemarks.</p>
     */
    @SuppressWarnings("unused")
    public static class ViewHolder {
        /** The placemark data associated with this view */
        public Placemark placemark;
        
        /** TextView displaying the placemark's title */
        public TextView title;
    }
}