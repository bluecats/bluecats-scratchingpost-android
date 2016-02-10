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
		holder.bindView( mBeacons.get( position ), position );
	}

	@Override
	public int getItemCount()
	{
		return mBeacons == null ? 0 : mBeacons.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		private final View mView;
		private final TextView mTxtName;
		private final TextView mTxtRSSI;
		private final TextView mTxtCategories;
		private final TextView mTxtProximity;

		public ViewHolder( final View itemView )
		{
			super( itemView );

			mView = itemView;
			mTxtName = (TextView) itemView.findViewById( R.id.name );
			mTxtRSSI = (TextView) itemView.findViewById( R.id.rssi );
			mTxtCategories = (TextView) itemView.findViewById( R.id.categories );
			mTxtProximity = (TextView) itemView.findViewById( R.id.proximity );
		}

		public void bindView( final BCBeacon beacon, final int position )
		{
			mTxtName.setText( beacon.getIBeaconKey().substring( 32 ) );
			mTxtRSSI.setText( String.format( "%d rssi", beacon.getRSSI() ) );
			mTxtProximity.setText( beacon.getProximity().toString() );

			String categories = "";
			for( int i = 0; i < beacon.getCategories().length; i++ )
			{
				if( i > 0 )
				{
					categories += ", ";
				}

				categories += beacon.getCategories()[i].getName();
			}
			mTxtCategories.setText( categories );

			int colourPos = position % mRowColours.length;
			mView.setBackgroundColor( mRowColours[colourPos] );
		}
	}
}
