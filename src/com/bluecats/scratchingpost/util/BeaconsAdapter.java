package com.bluecats.scratchingpost.util;

import java.util.List;

import com.bluecats.scratchingpost.R;
import com.bluecats.sdk.BCBeacon;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BeaconsAdapter extends BaseAdapter {
	private List<BCBeacon> mBeacons;
	private LayoutInflater mInflater;
	private int[] mRowColours = new int[] { Color.parseColor("#33b5e5"), Color.parseColor("#0099cc") };
	    
	public BeaconsAdapter(Context context, List<BCBeacon> beacons) {
		mBeacons = beacons;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		if (mBeacons != null) {
			return mBeacons.size();
		}
		return 0;
	}

	public BCBeacon getItem(int position) {
		if (mBeacons != null) {
			return mBeacons.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		BeaconsViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.beacons_list_row, null);
			
			holder = new BeaconsViewHolder();
			holder.txtName = (TextView)convertView.findViewById(R.id.name);
			holder.txtRSSI = (TextView)convertView.findViewById(R.id.rssi);
			holder.txtCategories = (TextView)convertView.findViewById(R.id.categories);

			convertView.setTag(holder);
		} else {
			holder = (BeaconsViewHolder)convertView.getTag();
		}

		BCBeacon beacon = mBeacons.get(position);
		holder.txtName.setText(beacon.getIBeaconKey().substring(32));
		holder.txtRSSI.setText(String.valueOf(beacon.getRSSI()) + " rssi");
		String categories = "";
		for (int i = 0; i < beacon.getCategories().length; i++) {
			if (i > 0) {
				categories += ", ";
			}
			categories += beacon.getCategories()[i].getName();
		}
		holder.txtCategories.setText(categories);
		
		int colourPos = position % mRowColours.length;
		convertView.setBackgroundColor(mRowColours[colourPos]);

		return convertView;
	}

	private static class BeaconsViewHolder {
		TextView txtName;
		TextView txtRSSI;
		TextView txtCategories;
	}
}
