package com.bluecats.scratchingpost.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecats.scratchingpost.BeaconSnifferActivity;
import com.bluecats.scratchingpost.Constants;
import com.bluecats.scratchingpost.databinding.ItemBeaconBinding;
import com.bluecats.sdk.BCBeacon;

import java.util.List;

public class BeaconsAdapter extends RecyclerView.Adapter<BeaconsAdapter.ViewHolder>
{
	private final List<BCBeacon> mBeacons;

	public BeaconsAdapter( final List<BCBeacon> beacons )
	{
		mBeacons = beacons;
	}

	@Override
	public ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		return new ViewHolder( ItemBeaconBinding.inflate( LayoutInflater.from( parent.getContext() ), parent, false ) );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position )
	{
		final BCBeacon beacon = mBeacons.get( position );
		holder.mBinding.setBeacon( beacon );

		final StringBuilder categories = new StringBuilder();
		for( int i = 0; i < beacon.getCategories().length; i++ )
		{
			if( i > 0 )
			{
				categories.append( ", " );
			}

			categories.append( beacon.getCategories()[i].getName() );
		}
		holder.mBinding.txtCategories.setText( categories.toString() );

		final int colourPos = position % Constants.ROW_COLOURS.length;
		holder.mBinding.getRoot().setBackgroundColor( Constants.ROW_COLOURS[colourPos] );
		holder.mBinding.getRoot().setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( final View v )
			{
				final Context context = v.getContext();
				final Intent intent = new Intent( context, BeaconSnifferActivity.class );
				context.startActivity( intent );
			}
		} );
	}

	@Override
	public int getItemCount()
	{
		return mBeacons == null ? 0 : mBeacons.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder
	{
		final ItemBeaconBinding mBinding;

		ViewHolder( final ItemBeaconBinding binding )
		{
			super( binding.getRoot() );
			mBinding = binding;
		}
	}
}
