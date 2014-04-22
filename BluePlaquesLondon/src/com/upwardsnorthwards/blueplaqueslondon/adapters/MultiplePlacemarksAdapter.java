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

public class MultiplePlacemarksAdapter extends ArrayAdapter<Placemark> {

	private List<Placemark> placemarks;

	public MultiplePlacemarksAdapter(Context context, int resource,
			List<Placemark> objects) {
		super(context, resource, objects);
		placemarks = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.multiple_placemarks_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) v
					.findViewById(R.id.multiple_placemarks_title);
			v.setTag(viewHolder);
		}
		if (placemarks != null && position < placemarks.size()) {
			final Placemark placemark = placemarks.get(position);
			final ViewHolder holder = (ViewHolder) v.getTag();
			holder.placemark = placemark;
			holder.title.setText(placemark.getTitle());
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

	public static class ViewHolder {
		public Placemark placemark;
		public TextView title;
	}
}
