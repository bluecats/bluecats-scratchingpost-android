package com.bluecats.scratchingpost.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecats.scratchingpost.BeaconsActivity;
import com.bluecats.scratchingpost.Constants;
import com.bluecats.scratchingpost.databinding.ItemSiteBinding;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.util.List;

public class SitesAdapter extends RecyclerView.Adapter<SitesAdapter.ViewHolder>
{
	private final List<BCSite> mSites;

	public SitesAdapter( final List<BCSite> sites )
	{
		mSites = sites;
	}

	@Override
	public ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		return new ViewHolder( ItemSiteBinding.inflate( LayoutInflater.from( parent.getContext() ), parent, false ) );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position )
	{
		final BCSite site = mSites.get( position );
		holder.mBinding.setSite( site );

		final int colourPos = position % Constants.ROW_COLOURS.length;
		holder.mBinding.getRoot().setBackgroundColor( Constants.ROW_COLOURS[colourPos] );
		holder.mBinding.getRoot().setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( final View v )
			{
				final Context context = v.getContext();
				final Intent beaconsIntent = new Intent( context, BeaconsActivity.class );
				beaconsIntent.putExtra( BlueCatsSDK.EXTRA_SITE, site );
				context.startActivity( beaconsIntent );
			}
		} );
	}

	@Override
	public int getItemCount()
	{
		return mSites == null ? 0 : mSites.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder
	{
		final ItemSiteBinding mBinding;

		ViewHolder( final ItemSiteBinding binding )
		{
			super( binding.getRoot() );
			mBinding = binding;
		}
	}
}
