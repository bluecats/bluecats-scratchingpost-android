bluecats-scratchingpost-android
===============================

##Getting Started

####Step 1.
Create your app using min Android SDK version 4.3.

####Step 2. 
Copy contents of the /libs folder into your project's /libs folder.

####Step 3.
Hook the Google Pay Services SDK up to your project as a library project as the SDK depends on this:

http://developer.android.com/google/play-services/setup.html

And add the correct version to your manifest (see the AndroidManifest.xml step).

####Step 3. 
Generate your app token from the Blue Cats Dashboard.

####Step 4. 
Fire up the BlueCatsSDK in your applications main activity onCreate() method.

``` java
BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "YourBCAppToken");
```

## What the ...?

####BCSite

A BCSite object represents a group of beacons. A site is any place or building that has a physical address or coordinate. In some buildings such as malls there can be sites within a site. With our managment app you can control which apps access your sites and its beacons.  

####BCBeacon

A BCBeacon object represents a beacon device. Beacon devices are uniquely identified by their composite key (ProximityUUID:Major:Minor). Characterisitics such as beacon loudness and target speed can be changed to customize behaviours for your use case. In addition, you can use our management apps to assign categories such as text, hashtags, or urls to a beacon. The SDK eagerly syncs and caches beacons from nearby sites for your app. 

####BCMicroLocation

A BCMicroLocation object represents the sites and beacons in proximity to the user. When your app needs some context it can query a micro-location for a sites beacons and categories by proximity. Its all the beacon goodness wrapped up into a tiny object. And integrating micro-locations with your app is simple. Simply observe the BCMicroLocationManager did update micro location event.

## Examples

### Start Purring
``` java
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.sites);
		
	BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "YourBCAppToken");
}	
```

You receive location events from the BCMicroLocationManager object, passing in your callback and your activity.

``` java
@Override
protected void onStart() {
    super.onStart();

    BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mBlueCatsSDKCallback, SitesActivity.this);
}
```

If you don't like a shared micro-location manager, then create your very own BCMicroLocationManager.

``` java
private BCMicroLocationManager mMicroLocationManager;

@Override
protected void onStart() {
    super.onStart();

    mMicroLocationManager.startUpdatingMicroLocation(mBlueCatsSDKCallback, SitesActivity.this);
}
```

You receive events from the SDK by passing in a callback.

``` java
private IBlueCatsSDKCallback mBlueCatsSDKCallback = new IBlueCatsSDKCallback() {
    @Override
    public void onDidEnterSite(final BCSite site) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mSitesInside.contains(site)) {
                    mSitesInside.add(site);
                    mAdapterSitesInside.notifyDataSetChanged();
                }
                  
                if (mSitesNearby.remove(site)) {
                    mAdapterSitesNearby.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onDidExitSite(final BCSite site) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSitesInside.remove(site)) {
                    mAdapterSitesInside.notifyDataSetChanged();
                }

                if (!mSitesNearby.contains(site)) {
                    mSitesNearby.add(site);
                    mAdapterSitesNearby.notifyDataSetChanged();
                }
            }
        });
    }

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

    @Override
    public void onDidRangeBeaconsForSiteID(BCSite site, List<BCBeacon> beacons) {
          
    }

    @Override
    public void onDidUpdateMicroLocation(List<BCMicroLocation> microLocations) {
            
    }
};
```

You can start receiving events from the SDK by calling startUpdatingMicroLocation on the onResume() method of your activity and stop receiving events by calling stopUpdatingMicroLocation on the onPause() method of your activity and passing in your callback to unregister.

``` java
@Override
protected void onResume() {
    super.onResume();

    BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mBlueCatsSDKCallback, BeaconsActivity.this);
}

@Override 
protected void onPause() { 
    super.onPause();

    BCMicroLocationManager.getInstance().stopUpdatingMicroLocation(mBlueCatsSDKCallback);
}
```

Alternatively you can continue to receive events for your app in the background and simply notify the SDK that your app has entered the background. The SDK will scan for beacons at different frequencies depending on whether your app is in the foreground or the background, with the foreground scanning at a higher freqeuncy and therefore extending battery life.

``` java
@Override
protected void onResume() {
    super.onResume();

    BCMicroLocationManager.getInstance().didEnterForeground();
}

@Override 
protected void onPause() { 
    super.onPause();

    BCMicroLocationManager.getInstance().didEnterBackground();
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

## AndroidManifest.xml

Update your AndroidManifest.xml file to include the following permissions:

``` xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

You will also need to add the BlueCatsService and the version of Google Play Services that you have hooked up to your app (Currently 4.4.52-000)

``` xml
<service android:name="com.bluecats.sdk.BlueCatsSDKService" />

<meta-data
    android:name="com.google.android.gms.version"
    android:value="4452000" />
```