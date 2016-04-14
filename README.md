bluecats-scratchingpost-android
===============================

## Getting Started

#### Step 1.
Follow the installation instructions at [https://github.com/bluecats/bluecats-android-sdk](https://github.com/bluecats/bluecats-android-sdk).

#### Step 2.
Fire up the BlueCatsSDK in your applications main activity onCreate() method.

``` java
BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "YourBCAppToken");
```

## What the ...?

#### BCSite

A BCSite object represents a group of beacons. A site is any place or building that has a physical address or coordinate. In some buildings such as malls there can be sites within a site. With our managment app you can control which apps access your sites and its beacons.  

#### BCBeacon

A BCBeacon object represents a beacon device. Beacon devices are uniquely identified by their composite key (ProximityUUID:Major:Minor). Characterisitics such as beacon loudness and target speed can be changed to customize behaviours for your use case. In addition, you can use our management apps to assign categories such as text, hashtags, or urls to a beacon. The SDK eagerly syncs and caches beacons from nearby sites for your app.

#### BCMicroLocation

A BCMicroLocation object represents the sites and beacons in proximity to the user. When your app needs some context it can query a micro-location for a site's beacons and categories by proximity. Its all the beacon goodness wrapped up into a tiny object. And integrating micro-locations with your app is simple. Simply observe the BCMicroLocationManager did update micro location event.

## Examples

### Start Purring

You receive location events from the BCMicroLocationManager object, passing in your callback and your activity.

``` java
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.sites);

	BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "YourBCAppToken");

    BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mMicroLocationManagerCallback);
}
```

Notify the SDK when you enter the background, or come back in to the foreground. This will help the SDK update the scanning frequency depending on your app state, getting longer life out of the battery.

``` java
@Override
protected void onResume() {
    super.onResume();
    BlueCatsSDK.didEnterForeground();
    BCMicroLocationManager.getInstance().didEnterForeground();
}
```

``` java
@Override
protected void onPause() {
    super.onPause();
    BlueCatsSDK.didEnterBackground();
    BCMicroLocationManager.getInstance().didEnterBackground();
}
```

If you don't like a shared micro-location manager, then create your very own BCMicroLocationManager.

``` java
private BCMicroLocationManager mMicroLocationManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mMicroLocationManagerCallback);
}
```

You receive events from the SDK by passing in a callback.

``` java
private BCMicroLocationManagerCallback mMicroLocationManagerCallback = new BCMicroLocationManagerCallback() {
	@Override
	public void onDidEnterSite( BCSite site ) {

	}

	@Override
	public void onDidExitSite( BCSite site ) {

	}

	@Override
	public void onDidUpdateNearbySites( List<BCSite> sites ) {

	}

	@Override
	public void onDidRangeBeaconsForSiteID( BCSite site, List<BCBeacon> beacons ) {

	}

	@Override
	public void onDidUpdateMicroLocation( List<BCMicroLocation> microLocations ) {

	}

	@Override
	public void didBeginVisitForBeaconsWithSerialNumbers( List<String> serialNumbers ) {

	}

	@Override
	public void didEndVisitForBeaconsWithSerialNumbers( List<String> serialNumbers ) {

	}
};
```

You can call stopUpdatingMicroLocation if you want to explicity stop receiving updates from your BCMicroLocationManager at any time. This will unregister the callback from the SDK. Simply call startUpdatingMicroLocation to begin receiving updates again.

``` java
@Override
protected void onResume() {
    super.onResume();
    BlueCatsSDK.didEnterForeground();
    BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mMicroLocationManagerCallback);
}

@Override
protected void onPause() {
    super.onPause();
    BlueCatsSDK.didEnterBackground();
    BCMicroLocationManager.getInstance().stopUpdatingMicroLocation(mMicroLocationManagerCallback);
}
```

It is advisable to call didEnterForeground and didEnterBackground as your app comes in to and exits the foreground. This will help the SDK extend battery life by lowering the frequency of scanning while in the background.

``` java
@Override
protected void onResume() {
    super.onResume();
    BlueCatsSDK.didEnterForeground();
    BCMicroLocationManager.getInstance().didEnterForeground();
}

@Override
protected void onPause() {
    super.onPause();
    BlueCatsSDK.didEnterBackground();
    BCMicroLocationManager.getInstance().didEnterBackground();
}
```

You can also make an explicit call to requestStateForSites to trigger any onDidEnterSite, onDidExitSite or onDidUpdateNearbySites events that might have occurred from within the background. This is called by default within the didEnterForeground method.

``` java
@Override
protected void onResume() {
    super.onResume();
    BlueCatsSDK.didEnterForeground();
    BCMicroLocationManager.getInstance().requestStateForSites();
}
```

Get Some Context for Your Next App Action

``` java
@Override
public void onDidUpdateMicroLocation(List<BCMicroLocation> microLocations) {
    BCMicroLocation microLocation = microLocations.get(microLocations.size() - 1);

    if (microLocation.getSites().size() > 0) {
        BCSite site = microLocation.getSites().get(0);

        List<BCCategory> categories = microLocation.getCategoriesForSite(site, BCProximity.BC_PROXIMITY_IMMEDIATE);

        List<BCBeacon> beacons = microLocation.getBeaconsForSite(site, BCProximity.BC_PROXIMITY_IMMEDIATE);
    }
}
```

Get Nearby Sites

``` java
@Override
public void onDidUpdateNearbySites(final List<BCSite> sites) {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            mSitesNearby.clear();
            for (BCSite site: sites) {
                if (!mSitesInside.contains(site) && !mSitesNearby.contains(site)) {
                    mSitesNearby.add(site);
                }
            }
            mAdapterSitesNearby.notifyDataSetChanged();
        }
    });
}
```

Monitor Sites and Range Beacons

``` java
BCMicroLocationManager.getInstance().startMonitoringSite(site);
BCMicroLocationManager.getInstance().startRangingBeaconsInSite(site);

@Override
public void onDidRangeBeaconsForSiteID(final BCSite site, final List<BCBeacon> beacons) {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if (site.equals(mSite)) {
                removeExpiredBeacons(beacons);

                mBeaconsImmediate.clear();
                mBeaconsNear.clear();
                mBeaconsFar.clear();
                mBeaconsUnknown.clear();

                // update the beacons lists depending on proximity
                for (BCBeacon beacon: beacons) {
                    if (beacon.getProximity() == BCProximity.BC_PROXIMITY_IMMEDIATE) {
                        mBeaconsImmediate.add(beacon);
                    } else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_NEAR) {
                        mBeaconsNear.add(beacon);
                    } else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_FAR) {
                        mBeaconsFar.add(beacon);
                    } else if (beacon.getProximity() == BCProximity.BC_PROXIMITY_UNKNOWN) {
                        mBeaconsUnknown.add(beacon);
                    }
                }

                mAdapterBeaconsImmediate.notifyDataSetChanged();
                mAdapterBeaconsNear.notifyDataSetChanged();
                mAdapterBeaconsFar.notifyDataSetChanged();
                mAdapterBeaconsUnknown.notifyDataSetChanged();
            }
        }
    });
}
```

Schedule a Local Notification to Fire in a Site and Categories...

``` java
BCLocalNotification localNotification = new BCLocalNotification(NOTIFICATION_ID);

// can add an optional site to trigger in        
BCSite site = new BCSite();
site.setSiteID("SITE_ID_HERE");
site.setName("SITE_NAME_HERE");
localNotification.setFireInSite(site);

// optional time to trigger the event after, eg 10 seconds from now        
localNotification.setFireAfter(new Date(new Date().getTime() + (10 * 1000)));

// add a category or several categories to trigger the notification        
List<BCCategory> categories = new ArrayList<BCCategory>();
BCCategory category = new BCCategory();
category.setName("CATEGORY_NAME");
categories.add(category);
localNotification.setFireInCategories(categories);

// can add an optional proximity to trigger event        
localNotification.setFireInProximity(BCProximity.BC_PROXIMITY_IMMEDIATE);

// set alert title and content        
localNotification.setAlertContentTitle("ALERT_TITLE");
localNotification.setAlertContentText("ALERT_CONTENT");

// launch icon and ringtone are optional. will just default ringtone and app icon for defaults        
localNotification.setAlertSmallIcon(R.drawable.ic_launcher);
localNotification.setAlertSound(RingtoneManager.getActualDefaultRingtoneUri(BeaconsActivity.this, RingtoneManager.TYPE_NOTIFICATION));

// this controls where the notification takes you.
// can also contain a bundle or any extra info that you might want to unpack        
Intent contentIntent = new Intent(BeaconsActivity.this, SitesActivity.class);
localNotification.setContentIntent(contentIntent);

BCLocalNotificationManager.getInstance().scheduleLocalNotification(localNotification);
```
