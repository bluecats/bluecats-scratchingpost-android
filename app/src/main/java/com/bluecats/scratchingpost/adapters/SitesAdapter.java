package com.bluecats.scratchingpost.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecats.scratchingpost.BeaconsActivity;
import com.bluecats.scratchingpost.R;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.List;

public class SitesAdapter extends RecyclerView.Adapter<SitesAdapter.ViewHolder>
{
	private static final int[] mRowColours = new int[]{ Color.parseColor( "#33b5e5" ), Color.parseColor( "#0099cc" ) };
	private final List<BCSite> mSites;

	public SitesAdapter( final List<BCSite> sites )
	{
		mSites = sites;
	}

	@Override
	public ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		return new ViewHolder( LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_item_site, parent, false ) );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position )
	{
		final BCSite site = mSites.get( position );

		holder.mTxtName.setText( site.getName() );

		final int beaconCount = site.getBeaconCount();
		final String beaconLabel = beaconCount == 1 ? "beacon" : "beacons";
		holder.mTxtBeaconCount.setText( beaconCount + " " + beaconLabel );

		final int colourPos = position % mRowColours.length;
		holder.mView.setBackgroundColor( mRowColours[colourPos] );
		holder.mView.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( final View v )
			{
				final Intent beaconsIntent = new Intent( v.getContext(), BeaconsActivity.class );
				beaconsIntent.putExtra( BlueCatsSDK.EXTRA_SITE, site );
				v.getContext().startActivity( beaconsIntent );
			}
		} );
	}

	@Override
	public int getItemCount()
	{
		return mSites == null ? 0 : mSites.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder
	{
		final View mView;
		final TextView mTxtName;
		final TextView mTxtBeaconCount;

		ViewHolder( final View itemView )
		{
			super( itemView );

			mView = itemView;
			mTxtName = (TextView) itemView.findViewById( R.id.txv_name );
			mTxtBeaconCount = (TextView) itemView.findViewById( R.id.txv_beacon_count );
		}
	}
}
