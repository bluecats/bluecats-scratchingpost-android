bluecats-scratchingpost-android
===============================

##Getting Started

####Step 1.
Create your app using min Android SDK version 4.3, or you can go as low as 4.2 if using a Samsung Galaxy S4. 

####Step 2. 
Copy contents of the /libs folder into your project's /libs folder.

####Step 3. 
Generate your app token from the Blue Cats Dashboard.

####Step 4. 
Fire up the BlueCatsSDK in your applications main activity onCreate() method.

``` java
BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "YourBCAppToken");
```

## What the ...?

####BCSite

A BCSite object represents a group of beacons. A site is any place or building that has a physical address or coordinate. In some buildings such as malls there can be sites within a site. With our managment app you can control which apps access your sites and its beacons. Our BCSite gives context to Apple's CLBeaconRegion.    

####BCBeacon

A BCBeacon object represents a beacon device. Beacon devices are uniquely identified by their composite key (ProximityUUID:Major:Minor). Characterisitics such as beacon loudness and target speed can be changed to customize behaviours for your use case. In addition, you can use our management apps to assign categories such as text, hashtags, or urls to a beacon. The SDK eagerly syncs and caches beacons from nearby sites for your app. Our BCBeacon gives context to Apple's CLBeacon.  

####BCMicroLocation

A BCMicroLocation object represents the sites and beacons in proximity to the user. When your app needs some context it can query a micro-location for a sites beacons and categories by proximity. Its all the beacon goodness wrapped up into a tiny object. And integrating micro-locations with your app is simple. Either observe micro-location did update notification or become a delegate of our BCMicroLocationManager and implement the equivalent method.

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

You receive location events from the BCMicroLocationManager object.

``` java
@Override
protected void onStart() {
    super.onStart();

    BCMicroLocationManager.getInstance().startUpdatingMicroLocation();
}
```

If you don't like a shared micro-location manager, then create your very own BCMicroLocationManager.

``` java
private BCMicroLocationManager mMicroLocationManager;

@Override
protected void onStart() {
    super.onStart();

    mMicroLocationManager.startUpdatingMicroLocation();
}
```

You can receive events from the SDK either by passing in a Handler...

``` java
private BCMicroLocationManager mMicroLocationManager;

@Override
protected void onStart() {
    super.onStart();

    BCMicroLocationManager.getInstance().setApplicationHandler(this, mMicroLocationManagerHandler);
    BCMicroLocationManager.getInstance().startUpdatingMicroLocation();

}

private BCHandler<MainActivity> mMicroLocationManagerHandler = new BCHandler<MainActivity>(this) {
    @Override
    public void handleMessage(Message msg) {
    	MainActivity reference = mReference.get();
        if (reference != null) {
        	final Bundle data = msg.getData();
            switch (msg.what) {
            case BlueCatsSDK.ACTION_DID_ENTER_SITE: {
            	BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);
            	break;
            }
        	case BlueCatsSDK.ACTION_DID_EXIT_SITE: {
            	BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);
                break;
            }
            case BlueCatsSDK.ACTION_DID_UPDATE_NEARBY_SITES: {
            	ArrayList<BCSite> sites = data.getParcelableArrayList(BlueCatsSDK.EXTRA_SITES);
            	break;
            }
            case BlueCatsSDK.ACTION_DID_RANGE_BEACONS_FOR_SITE_ID: {
            	BCSite site = data.getParcelable(BlueCatsSDK.EXTRA_SITE);
                ArrayList<BCBeacon> beacons = data.getParcelableArrayList(BlueCatsSDK.EXTRA_BEACONS);
                break;
            }
         	default: break;
     		}
    	}
	}
};
```
...or registering BroadcastReceivers for each event...

``` java
private BCMicroLocationManager mMicroLocationManager;

@Override
protected void onStart() {
    super.onStart();

    LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidEnterSite, new IntentFilter("com.bluecats.sdk.ACTION_DID_ENTER_SITE"));
    LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidExitSite, new IntentFilter("com.bluecats.sdk.ACTION_DID_EXIT_SITE"));
    LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidUpdateNearbySites, new IntentFilter("com.bluecats.sdk.ACTION_DID_UPDATE_NEARBY_SITES"));
    LocalBroadcastManager.getInstance(this).registerReceiver(microLocationManagerDidRangeBeaconsForSiteID, new IntentFilter("com.bluecats.sdk.ACTION_DID_RANGE_BEACONS_FOR_SITE_ID"));

    BCMicroLocationManager.getInstance().startUpdatingMicroLocation();
}

private BroadcastReceiver microLocationManagerDidEnterSite = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
	    BCSite site = intent.getParcelableExtra(BlueCatsSDK.EXTRA_SITE);
	}
};

private BroadcastReceiver microLocationManagerDidExitSite = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
		BCSite site = intent.getParcelableExtra(BlueCatsSDK.EXTRA_SITE);
   	};
}

private BroadcastReceiver microLocationManagerDidUpdateNearbySites = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
    	ArrayList<BCSite> sites = intent.getParcelableArrayListExtra(BlueCatsSDK.EXTRA_SITES);
    }
};

private BroadcastReceiver microLocationManagerDidRangeBeaconsForSiteID = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        BCSite site = intent.getParcelableExtra(BlueCatsSDK.EXTRA_SITE);
	    ArrayList<BCBeacon> beacons = intent.getParcelableArrayListExtra(BlueCatsSDK.EXTRA_BEACONS);
    }
};
```

Remember to unregister your broadcast receivers or remove your handler and stop receiving updates when your application / activity is ended. The SDK will clean up any dead references anyway, but best not to introduce memory leaks if you can help it.

``` java
@Override 
protected void onStop() { 
    super.onStop();
		
    BCMicroLocationManager.getInstance().stopUpdatingMicroLocation();
    BCMicroLocationManager.getInstance().removeApplicationHandler(this);
}
```

...or...


``` java
@Override 
protected void onStop() { 
    super.onStop();
		
    LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidEnterSite);
    LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidExitSite);
    LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidUpdateNearbySites);
    LocalBroadcastManager.getInstance(this).unregisterReceiver(microLocationManagerDidRangeBeaconsForSiteID);
	
    BCMicroLocationManager.getInstance().stopUpdatingMicroLocation();
}
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

You will also need to add the BlueCatsService and the version for the Google Play Services (currently 4.0.30 (889083-30))

``` xml
<service
    android:name="com.bluecats.sdk.BlueCatsService"
    android:enabled="true" />

<meta-data
    android:name="com.google.android.gms.version"
    android:value="4030500" />
```