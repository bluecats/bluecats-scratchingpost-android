package com.bluecats.scratchingpost.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecats.scratchingpost.R;
import com.bluecats.sdk.BCBeacon;

import java.util.List;

public class BeaconSnifferAdapter extends RecyclerView.Adapter<BeaconSnifferAdapter.ViewHolder>
{
	private static final int[] mRowColours = new int[]{ Color.parseColor( "#33b5e5" ), Color.parseColor( "#0099cc" ) };
	private final List<BCBeacon> mBeacons;

	public BeaconSnifferAdapter( final List<BCBeacon> beacons )
	{
		mBeacons = beacons;
	}

	@Override
	public ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		return new ViewHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_item_beacon_sniffer, parent, false ) );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position )
	{
		final BCBeacon beacon = mBeacons.get( position );

		holder.mTxtName.setText( beacon.getSerialNumber() );
		holder.mTxtRSSI.setText( beacon.getRSSI() + " rssi" );
		holder.mTxtProximity.setText( beacon.getProximity().toString() );

		final StringBuilder categories = new StringBuilder();
		for( int i = 0; i < beacon.getCategories().length; i++ )
		{
			if( i > 0 )
			{
				categories.append( ", " );
			}

			categories.append( beacon.getCategories()[i].getName() );
		}
		holder.mTxtCategories.setText( categories.toString() );

		int colourPos = position % mRowColours.length;
		holder.mView.setBackgroundColor( mRowColours[colourPos] );
	}

	@Override
	public int getItemCount()
	{
		return mBeacons == null ? 0 : mBeacons.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder
	{
		final View mView;
		final TextView mTxtName;
		final TextView mTxtRSSI;
		final TextView mTxtCategories;
		final TextView mTxtProximity;

		ViewHolder( final View itemView )
		{
			super( itemView );

			mView = itemView;
			mTxtName = (TextView) itemView.findViewById( R.id.txv_name );
			mTxtRSSI = (TextView) itemView.findViewById( R.id.txv_rssi );
			mTxtCategories = (TextView) itemView.findViewById( R.id.txv_categories );
			mTxtProximity = (TextView) itemView.findViewById( R.id.txv_proximity );
		}
	}
}
