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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.List;

/**
 * Used when there are more than 1 placemark associated with a lat/long key.
 * This can happen when two noteable people have lived at the same address.
 */
public class MultiplePlacemarksAdapter extends ArrayAdapter<Placemark> {

    private final List<Placemark> placemarks;

    public MultiplePlacemarksAdapter(final Context context, @NonNull final List<Placemark> objects) {
        super(context, R.layout.multiple_placemarks_item, objects);
        placemarks = objects;
    }

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

    @Override
    public int getCount() {
        int size = 0;
        if (placemarks != null) {
            size = placemarks.size();
        }
        return size;
    }

    @SuppressWarnings("unused")
    public static class ViewHolder {
        public Placemark placemark;
        public TextView title;
    }
}