package hk.ust.stop.activity;

import java.util.ArrayList;

import hk.ust.stop.dao.BaseDaoImpl;
import hk.ust.stop.idao.BaseDaoInterface;
import hk.ust.stop.model.GoodsInformation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{

	// Class to do operations on the Map
	GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		// setup location manager to perform location related operations
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Requesting locationmanager for location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);

		// To get map from MapFragment from layout
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		// To show our current location in the map with dot
		googleMap.setMyLocationEnabled(true);
		
		// set map type
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		//insert a record to the database
		ContentResolver resolver = getContentResolver();
		BaseDaoInterface dao = new BaseDaoImpl(resolver);
		GoodsInformation goods = new GoodsInformation(1,2,89.1,26.3,45.5,"book","worth to read");
		dao.insert(1,goods,1);
		
		// To listen action whenever we click on the map
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				
				ContentResolver resolver = getContentResolver();
				BaseDaoInterface dao = new BaseDaoImpl(resolver);
				ArrayList<GoodsInformation> list = dao.queryAllRecord();
				if(list != null && list.size() != 0) {
					Toast.makeText(MainActivity.this, list.get(0).getGoodsDescription(), Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		// To hold latitude and longitude values
		LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

		// Creating object to pass our current location to the map
		MarkerOptions markerOptions = new MarkerOptions();
		// To store current location in the markeroptions object
		markerOptions.position(position).title("You are here");

		// clear previous marker
		googleMap.clear();
		
		// Zooming to our current location with zoom level 17.0f
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17f));

		// show our current location in the map with help of default marker
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
