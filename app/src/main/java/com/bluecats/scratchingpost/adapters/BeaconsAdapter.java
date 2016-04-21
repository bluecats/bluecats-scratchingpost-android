package com.bluecats.scratchingpost.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecats.scratchingpost.BeaconSnifferActivity;
import com.bluecats.scratchingpost.R;
import com.bluecats.sdk.BCBeacon;

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
		final BCBeacon beacon = mBeacons.get( position );

		holder.mTxtName.setText( beacon.getSerialNumber() );
		holder.mTxtRSSI.setText( beacon.getRSSI() + " rssi" );

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

		final int colourPos = position % mRowColours.length;
		holder.mView.setBackgroundColor( mRowColours[colourPos] );
		holder.mView.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( final View v )
			{
				final Intent intent = new Intent( v.getContext(), BeaconSnifferActivity.class );
				v.getContext().startActivity( intent );
			}
		} );
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

		ViewHolder( final View itemView )
		{
			super( itemView );

			mView = itemView;
			mTxtName = (TextView) itemView.findViewById( R.id.txv_name );
			mTxtRSSI = (TextView) itemView.findViewById( R.id.txv_rssi );
			mTxtCategories = (TextView) itemView.findViewById( R.id.txv_categories );
		}
	}
}
