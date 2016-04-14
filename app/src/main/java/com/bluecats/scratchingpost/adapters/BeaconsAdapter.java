package com.bluecats.scratchingpost.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecats.scratchingpost.BeaconSnifferActivity;
import com.bluecats.scratchingpost.R;
import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.List;

public class BeaconsAdapter extends RecyclerView.Adapter<BeaconsAdapter.ViewHolder>
{
	private static final int[] mRowColours = new int[]{ Color.parseColor( "#33b5e5" ), Color.parseColor( "#0099cc" ) };
	private final List<BCBeacon> mBeacons;

	public BeaconsAdapter( final List<BCBeacon> beacons )
	{
		mBeacons = beacons;
	}

	@Override
	public ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		return new ViewHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_item_beacon, parent, false ) );
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

		public ViewHolder( final View itemView )
		{
			super( itemView );

			mView = itemView;
			mTxtName = (TextView) itemView.findViewById( R.id.name );
			mTxtRSSI = (TextView) itemView.findViewById( R.id.rssi );
			mTxtCategories = (TextView) itemView.findViewById( R.id.categories );
		}

		public void bindView( final BCBeacon beacon, final int position )
		{
			mTxtName.setText( beacon.getProximityUUIDString() );
			mTxtRSSI.setText( beacon.getRSSI() + " rssi" );

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

			final int colourPos = position % mRowColours.length;
			itemView.setBackgroundColor( mRowColours[colourPos] );

			mView.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					final Intent intent = new Intent( v.getContext(), BeaconSnifferActivity.class );
					final Bundle bundle = new Bundle();
					bundle.putParcelable( BlueCatsSDK.EXTRA_BEACON, beacon );
					intent.putExtras( bundle );
					v.getContext().startActivity( intent );
				}
			} );
		}
	}
}
