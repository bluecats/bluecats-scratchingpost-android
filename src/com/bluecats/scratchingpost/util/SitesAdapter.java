package com.bluecats.scratchingpost.util;

import java.util.List;

import com.bluecats.scratchingpost.R;
import com.bluecats.sdk.BCSite;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SitesAdapter extends BaseAdapter {
	private List<BCSite> mSites;
	private LayoutInflater mInflater;
	private int[] mRowColours = new int[] { Color.parseColor("#33b5e5"), Color.parseColor("#0099cc") };
	    
	public SitesAdapter(Context context, List<BCSite> sites) {
		mSites = sites;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		if (mSites != null) {
			return mSites.size();
		}
		return 0;
	}

	public BCSite getItem(int position) {
		if (mSites != null) {
			return mSites.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		SitesViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sites_list_row, null);
			
			holder = new SitesViewHolder();
			holder.txtName = (TextView)convertView.findViewById(R.id.name);
			holder.txtBeaconCount = (TextView)convertView.findViewById(R.id.beacon_count);

			convertView.setTag(holder);
		} else {
			holder = (SitesViewHolder)convertView.getTag();
		}

		final BCSite site = mSites.get(position);
		holder.txtName.setText(site.getName());
		String beaconLabel = " beacons";
		if (site.getBeaconCount() == 1) {
			beaconLabel = " beacon";
		}
		holder.txtBeaconCount.setText(String.valueOf(site.getBeaconCount()) + beaconLabel);
		
		int colourPos = position % mRowColours.length;
		convertView.setBackgroundColor(mRowColours[colourPos]);

		return convertView;
	}

	private static class SitesViewHolder {
		TextView txtName;
		TextView txtBeaconCount;
	}
}
