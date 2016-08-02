BlueCats ScratchingPost Android
===============================

## Compiling This App
To test this app, the app token in Constants.java must be replaced with one of your own.

## Getting Started
### Step 1
Follow the installation instructions at [https://github.com/bluecats/bluecats-android-sdk](https://github.com/bluecats/bluecats-android-sdk).

### Step 2.
Fire up the BlueCatsSDK in your main activity's onCreate() method.

``` java
BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "YourBCAppToken");
```

## What the ...?

#### BCSite

A BCSite object represents a group of beacons. A site is any place or building that has a physical address or coordinate. In some buildings such as malls there can be sites within a site. With our managment app you can control which apps access your sites and its beacons.  

#### BCBeacon

A BCBeacon object represents a beacon device. Beacon devices are uniquely identified by their composite key (ProximityUUID:Major:Minor). Characterisitics such as beacon loudness and target speed can be changed to customize behaviours for your use case. In addition, you can use our management apps to assign categories such as text, hashtags, or urls to a beacon. The SDK eagerly syncs and caches beacons from nearby sites for your app.

A BCBeaconManager object is used to monitor beacon events that occur nearby. This is the object to use when your app needs to know which beacons are within the user's location. Using a BCBeaconManagerCallback and passing it to the BCBeaconManager will allow the app to receive events such as when an Eddystone beacon was ranged, or when a beacon entered/exited our vicinity.

#### BCMicroLocation (DEPRECATED)

A BCMicroLocation object represents the sites and beacons in proximity to the user. When your app needs some context it can query a micro-location for a site's beacons and categories by proximity. It's all the beacon goodness wrapped up into a tiny object. Integrating micro-locations with your app is simple as well, simply observe the BCMicroLocationManagerCallback.onDidUpdateMicroLocation() event to receive the relevant micro-locations.

## Examples
### Start Purring

You receive beacon events from the BCBeaconManager object, passing in your callback and your activity.
``` java
@Override
protected void onCreate( final Bundle savedInstanceState )
{
	super.onCreate( savedInstanceState );
	setContentView( R.layout.sites );

	BlueCatsSDK.startPurringWithAppToken( getApplicationContext(), "YOUR_APP_TOKEN_HERE" );

	final BCBeaconManager beaconManager = new BCBeaconManager();
	beaconManager.registerCallback( mBeaconManagerCallback );
}
```

Notify the SDK when you enter the background, or come back in to the foreground. This will help the SDK update the scanning frequency depending on your app state, getting longer life out of the battery.
``` java
@Override
protected void onResume()
{
    super.onResume();

    BlueCatsSDK.didEnterForeground();
}

@Override
protected void onPause()
{
    super.onPause();

    BlueCatsSDK.didEnterBackground();
}
```

You receive events from the SDK by passing in a callback:
``` java
private BCBeaconManagerCallback mBeaconManagerCallback = new BCBeaconManagerCallback()
{
	@Override
	public void didEnterSite( final BCSite site ) {}

	@Override
	public void didExitSite( final BCSite site ) {}

	@Override
	public void didDetermineState( final BCSite.BCSiteState state, final BCSite forSite ) {}

	@Override
	public void didEnterBeacons( final List<BCBeacon> beacons ) {}

	@Override
	public void didExitBeacons( final List<BCBeacon> beacons ) {}

	@Override
	public void didDetermineState( final BCBeacon.BCBeaconState state, final BCBeacon forBeacon ) {}

	@Override
	public void didRangeBeacons( final List<BCBeacon> beacons ) {}

	@Override
	public void didRangeBlueCatsBeacons( final List<BCBeacon> beacons ) {}

	@Override
	public void didRangeNewbornBeacons( final List<BCBeacon> newBornBeacons ) {}

	@Override
	public void didRangeIBeacons( final List<BCBeacon> iBeacons ) {}

	@Override
	public void didRangeEddystoneBeacons( final List<BCBeacon> eddystoneBeacons ) {}

	@Override
	public void didDiscoverEddystoneURL( final URL eddystoneUrl ) {}
};
```

You can call `BCBeaconManager.unregisterCallback()` if you want to explicity stop receiving updates from your BCBeaconManager at any time. This will unregister the callback from the SDK. Simply call `BCBeaconManager.registerCallback()` to begin receiving updates again.
``` java
@Override
protected void onResume()
{
    super.onResume();

    BlueCatsSDK.didEnterForeground();
    mBeaconManager.registerCallback( mBeaconManagerCallback );
}

@Override
protected void onPause()
{
    super.onPause();

    BlueCatsSDK.didEnterBackground();
    mBeaconManager.unregisterCallback( mBeaconManagerCallback );
}
```

Get nearby sites:
``` java
@Override
public void didEnterSite( final BCSite site )
{
	if( mSitesInside.contains( site ) )
	{
		//Update site in list
		mSitesInside.set( mSitesInside.indexOf( site ), site );
	}
	else if( !mSitesNearby.contains( site ) )
	{
		mSitesNearby.add( site );
	}

	runOnUiThread( new Runnable()
	{
		@Override
		public void run()
		{
			mAdapterSitesInside.notifyDataSetChanged();
			mAdapterSitesNearby.notifyDataSetChanged();
		}
	} );
}
```

Schedule a local notification to fire in a site and categories:
``` java
BCLocalNotification localNotification = new BCLocalNotification( NOTIFICATION_ID );

// can add an optional site to trigger in        
final BCSite site = BlueCatsSDK.createEmptySite();
site.setSiteID( "SITE_ID_HERE" );
site.setName( "SITE_NAME_HERE" );

localNotification.setFireInSite( site );

// optional time to trigger the event after, eg 10 seconds from now        
localNotification.setFireAfter( new Date( System.currentTimeMillis() + ( 10 * 1000 ) ) );

// add a category or several categories to trigger the notification
final BCCategory category = new BCCategory();
category.setName( "CATEGORY_NAME" );

final List<BCCategory> categories = Arrays.asList(
	category
);

localNotification.setFireInCategories( categories );

// can add an optional proximity to trigger event        
localNotification.setFireInProximity( BCProximity.BC_PROXIMITY_IMMEDIATE );

// set alert title and content        
localNotification.setAlertContentTitle( "ALERT_TITLE" );
localNotification.setAlertContentText( "ALERT_CONTENT" );

// launch icon and ringtone are optional. will just default ringtone and app icon for defaults        
localNotification.setAlertSmallIcon( R.mipmap.ic_launcher );
localNotification.setAlertSound( RingtoneManager.getActualDefaultRingtoneUri( this, RingtoneManager.TYPE_NOTIFICATION ) );

// this controls where the notification takes you.
// can also contain a bundle or any extra info that you might want to unpack        
Intent contentIntent = new Intent( this, YourActivity.class );
localNotification.setContentIntent( contentIntent );

BCLocalNotificationManager.getInstance().scheduleLocalNotification( localNotification );
```

## Still a bit lost?
Check out our documentation at [https://developer.bluecats.com/](https://developer.bluecats.com/) to read up on how the BlueCats SDK works!
