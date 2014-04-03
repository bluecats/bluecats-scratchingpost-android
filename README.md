bluecats-scratchingpost-android
===============================

##Getting Started

####Step 1. Create your app using min Android SDK version 4.3, or you can go as low as 4.2 if using a Samsung Galaxy S4. 

####Step 2. Copy contents of the /libs folder into your project's /libs folder.

####Step 3. Generate your app token from the Blue Cats Dashboard.

####Step 4. Fire up the BlueCatsSDK in your applications main activity onCreate() method.

``` java
BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "AppToken");
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
		
	BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), "d79662a7-a01d-4e04-b8d7-cb4399df1464");
}	
```


