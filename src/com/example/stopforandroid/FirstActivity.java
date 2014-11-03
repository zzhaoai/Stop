package com.example.stopforandroid;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

@SuppressLint("NewApi")
public class FirstActivity extends Activity implements LocationListener{

	GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("test commit!!!!!!");
		setContentView(R.layout.activity_first);
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Requesting locationmanager for location updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1, 1, this);

		// To get map from MapFragment from layout
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		double latti = location.getLatitude();
		double longi = location.getLongitude();

		LatLng position = new LatLng(latti, longi);

		// Creating object to pass our current location to the map
		MarkerOptions markerOptions = new MarkerOptions();
		// To store current location in the markeroptions object
		markerOptions.position(position);

		// Zooming to our current location with zoom level 17.0f
		googleMap.animateCamera(CameraUpdateFactory
				.newLatLngZoom(position, 17f));

		// adding markeroptions class object to the map to show our current
		// location in the map with help of default marker
		googleMap.addMarker(markerOptions);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
