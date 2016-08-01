package com.bluecats.scratchingpost;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.bluecats.sdk.BlueCatsSDK;

public class Utilities
{
	private static final String TAG = "Utilities";

	public static void ensureBluetoothEnabled( final Context context )
	{
		if( !BlueCatsSDK.isBluetoothEnabled() )
		{
			new AlertDialog.Builder( context )
					.setMessage( "This app requires Bluetooth to be enabled. Would you like to enable Bluetooth now?" )
					.setPositiveButton( "Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick( final DialogInterface dialogInterface, final int i )
						{
							BluetoothAdapter.getDefaultAdapter().enable();
						}
					} )
					.setNegativeButton( "No", null )
					.show();
		}
		else
		{
			Log.e( TAG, "BluetoothAdapter is disabled" );
		}
	}

	public static void ensureLocationServicesEnabled( final Activity activity, final int permissionRequestCode )
	{
		if( ContextCompat.checkSelfPermission( activity, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
		{
			if( ActivityCompat.shouldShowRequestPermissionRationale( activity, Manifest.permission.ACCESS_FINE_LOCATION ) )
			{
				new AlertDialog.Builder( activity )
						.setMessage( activity.getString( R.string.Utilities_location_permission_rationale ) )
						.setPositiveButton( "Ok", null )
						.show();
			}

			ActivityCompat.requestPermissions( activity, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, permissionRequestCode );
		}
		else
		{
			if( !BlueCatsSDK.isLocationAuthorized( activity ) )
			{
				new AlertDialog.Builder( activity )
						.setMessage( "This app requires Location Services to be enabled. Would you like to enable Location Services now?" )
						.setPositiveButton( "Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick( final DialogInterface dialogInterface, final int i )
							{
								final Intent enableLocationServicesIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
								activity.startActivity( enableLocationServicesIntent );
							}
						} )
						.setNegativeButton( "No", null )
						.show();
			}
			else
			{
				Log.e( TAG, "Location is not authorized" );
			}
		}
	}

	public static void ensureDataAccess( final Context context )
	{
		if( !BlueCatsSDK.isNetworkReachable( context ) )
		{
			new AlertDialog.Builder( context )
					.setMessage( "This app requires Wifi or Mobile data access to be enabled. Would you like to enable either one now?" )
					.setPositiveButton( "Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick( final DialogInterface dialogInterface, final int i )
						{
							final Intent settingsIntent = new Intent( Settings.ACTION_SETTINGS );
							settingsIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
							context.startActivity( settingsIntent );
						}
					} )
					.setNegativeButton( "No", null )
					.show();
		}
		else
		{
			new AlertDialog.Builder( context )
					.setMessage( "This app requires Wifi or Mobile data access to be enabled." )
					.show();
		}
	}
}
