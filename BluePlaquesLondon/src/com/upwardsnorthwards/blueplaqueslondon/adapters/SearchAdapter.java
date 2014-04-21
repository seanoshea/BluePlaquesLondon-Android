/*
 Copyright 2014 Sean O' Shea
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.upwardsnorthwards.blueplaqueslondon.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

public class SearchAdapter extends ArrayAdapter<Placemark> {

	private List<Placemark> placemarks;

	public SearchAdapter(Context context, int resource, List<Placemark> objects) {
		super(context, resource, objects);
		placemarks = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.search_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) v.findViewById(R.id.search_title);
			viewHolder.subtitle = (TextView) v
					.findViewById(R.id.search_subtitle);
			v.setTag(viewHolder);
		}
		if (placemarks != null && position < getCount()) {
			final Placemark placemark;
			final ViewHolder holder = (ViewHolder) v.getTag();
			if (position == 0) {
				holder.title.setText(parent.getContext().getString(
						R.string.closest));
			} else {
				placemark = placemarks.get(position);
				holder.placemark = placemark;
				holder.title.setText(placemark.getTitle());
				holder.subtitle.setText("");
			}
		}
		return v;
	}

	@Override
	public int getCount() {
		int size = 1;
		if (placemarks != null) {
			size += placemarks.size();
		}
		return size;
	}

	public static class ViewHolder {
		public Placemark placemark;
		public TextView title;
		public TextView subtitle;
	}

	public List<Placemark> getPlacemarks() {
		return placemarks;
	}

	public void setPlacemarks(List<Placemark> placemarks) {
		this.placemarks = placemarks;
	}
}
