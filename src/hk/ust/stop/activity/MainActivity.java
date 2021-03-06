package hk.ust.stop.activity;

import hk.ust.stop.dao.BaseDaoImpl;
import hk.ust.stop.idao.BaseDaoInterface;
import hk.ust.stop.model.GoodsInformation;
import hk.ust.stop.util.AccountUtil;
import hk.ust.stop.util.ToastUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity 
implements LocationListener, OnClickListener,
OnQueryTextListener, OnItemClickListener {

	// Instance to do operations on the Map
	GoogleMap googleMap;
	ImageView overflowButton;
	SearchView searchView;
	PopupWindow popupWindow;
	boolean isMenuPressToShow = false;
	LatLng location_old;
	List<LatLng> pointsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		pointsList = new ArrayList<LatLng>();

		Intent intent = getIntent();
		//boolean isLogin = intent.getBooleanExtra("isLogin", false);

		//Intent intent = getIntent();
		//boolean isLogin = intent.getBooleanExtra("isLogin", false);
		boolean isLogin = AccountUtil.isLogin();
		initActionBar();
		initPopupWindow(isLogin);
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
		GoodsInformation goods = new GoodsInformation(1,"pic",89.1,26.3,45.5,"book","ust library","worth to read");
		dao.insert(null,goods,1);

		// To listen action whenever we click on the map
		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {

				ContentResolver resolver = getContentResolver();
				BaseDaoInterface dao = new BaseDaoImpl(resolver);
				ArrayList<GoodsInformation> list = dao.queryAllRecord(null,1);
				if(list != null && list.size() != 0) {
					Toast.makeText(MainActivity.this, list.get(0).getGoodsAddress(), Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.actionbar_with_searchview_layout);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("");

		searchView = (SearchView)findViewById(R.id.actionbar_searchview);
		searchView.setOnQueryTextListener(this);
		overflowButton = (ImageView) findViewById(R.id.iv_overflow);
		overflowButton.setOnClickListener(this);
	}

	private void initPopupWindow(boolean isLogin) {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.popup_menu_layout, null);

		ArrayList<HashMap<String, Object>> menuList = new ArrayList<HashMap<String, Object>>(); 
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("menu_list_image", R.drawable.ic_action_search);
		map1.put("menu_list_text", "MyFavorite");

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("menu_list_image", R.drawable.ic_action_overflow);
		map2.put("menu_list_text", "Upload!");

		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("menu_list_image", R.drawable.ic_action_search);
		map3.put("menu_list_text", "MyUploaded");

		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("menu_list_image", R.drawable.ic_action_overflow);
		if(isLogin) {
			map4.put("menu_list_text", "Log Out");
		} else {
			map4.put("menu_list_text", "Log In");
		}
		menuList.add(map1);
		menuList.add(map2);
		menuList.add(map3);
		menuList.add(map4);

		SimpleAdapter adapter = new SimpleAdapter(this, menuList, 
				R.layout.menu_listview_layout, 
				new String[]{"menu_list_image", "menu_list_text"}, 
				new int[]{R.id.menu_list_image, R.id.menu_list_text});

		ListView listView = (ListView)contentView.findViewById(R.id.menu_listview);
		listView.setAdapter(adapter);
		listView.setOnKeyListener(new ListView.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
					if(isMenuPressToShow) {
						isMenuPressToShow = false;
					} else {
						popupWindow.dismiss();
						isMenuPressToShow = true;
					}
					return true;
				}
				return false;
			}
		});
		listView.setOnItemClickListener(this);

		popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.wallet_hint_foreground_holo_light));
		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);    
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLocationChanged(Location location) {
		try{
			// To hold latitude and longitude values
			LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
			AccountUtil.setCurrentLocation(position);

			// Creating object to pass our current location to the map
			MarkerOptions markerOptions = new MarkerOptions();
			// To store current location in the markeroptions object
			markerOptions.position(position).title("You are here");

			// clear previous marker
			googleMap.clear();

			// Zooming to our current location with zoom level 17.0f
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f));


			//test-----------------
			View mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getView();
			View btnMyLocation = ((View) mapView.findViewById(1).getParent()).findViewById(2);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80,80); // size of button in dp
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			params.setMargins(0, 0, 20, 0);
			btnMyLocation.setLayoutParams(params);

			// show our current location in the map with help of default marker
			googleMap.addMarker(markerOptions);
			
			try{
				File file = new File(Environment.getExternalStorageDirectory() + File.separator + "opt.txt");
				file.canRead();
				if(file.exists())
				{

					FileInputStream fis = new FileInputStream(file);

					//Construct BufferedReader from InputStreamReader
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));

					String line = null;
					while ((line = br.readLine()) != null) {
						String[] parts = line.split(",");
						MarkerOptions markerOptions1 = new MarkerOptions();
						markerOptions1.position(new LatLng(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]))).title("Results");
						googleMap.addMarker(markerOptions1);
					}

					br.close();

				}
			}
			catch(Exception e)
			{
			}
			
			
			
			
			List<LatLng> allPoints = new ArrayList<LatLng>();
			allPoints.add(position);
			try{
				File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.txt");
				file.canRead();
				if(file.exists())
				{

					FileInputStream fis = new FileInputStream(file);

					//Construct BufferedReader from InputStreamReader
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));

					String line = null;
					while ((line = br.readLine()) != null) {
						String[] parts = line.split(",");
						allPoints.add(new LatLng(Float.parseFloat(parts[0]), Float.parseFloat(parts[1])));
					}

					br.close();

				}
			}
			catch(Exception e)
			{
				allPoints = null;
			}

			if(allPoints != null && !allPoints.isEmpty()){
				PolylineOptions lineOptions = new PolylineOptions().addAll(allPoints);
				lineOptions.width(5).color(Color.BLUE);
				Polyline polyline = googleMap.addPolyline(lineOptions);
				//new RouteTask().execute(pointsList,null,null);

			}
//
//			if (location_old != position)
//			{ToastUtil.showToast(this, "Path Loaded");}
			
			location_old = position;
		}
		catch(Exception e)
		{

		}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_overflow:
			togglePopupWindow();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SearchListActivity.class);
		intent.putExtra("keyWord", searchView.getQuery()+"");
		// get data from the previous closed activity, 1 is request code
		startActivityForResult(intent,1);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	private void togglePopupWindow() {
		if(!popupWindow.isShowing()) {
			int x = popupWindow.getWidth();
			popupWindow.showAsDropDown(overflowButton, -x-180, 20);
		} else {
			popupWindow.dismiss();
		}


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) { 
			isMenuPressToShow = true;
			togglePopupWindow();
			return true;
		}   
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) { 
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		switch (position) {
		case 0:
			intent.setClass(this, FavoriteGoodsListActivity.class);
			startActivity(intent);
			break;
		case 1:
			if(!AccountUtil.isLogin()) {
				// If the user hasn't login, give him a hint.
				ToastUtil.showToast(this, "please login!");
			} else {
				// If the user has login, then jump to AddGoodsActivity.
				intent.setClass(this, AddGoodsActivity.class);
				startActivity(intent);
			}
			break;
		case 2:
			if(!AccountUtil.isLogin()) {
				// If the user hasn't login, give him a hint.
				ToastUtil.showToast(this, "please login!");
			} else {
				// If the user has login, then jump to AddedGoodsListActivity
				intent.setClass(this, AddedGoodsListActivity.class);
				startActivity(intent);
			}
			break;
		case 3:
			if(AccountUtil.isLogin()) {
				// If the user has login, then logout.
				AccountUtil.logoutUser();
				//getIntent().putExtra("isLogin", false);
				popupWindow.dismiss();
				initPopupWindow(false);
			} else {
				// If the user has not login, then jump to LoginActivity.
				popupWindow.dismiss();
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);
				finish();
			}

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// get goods positions from server
		//Bundle extras = data.getExtras();
		//pointsList = (List<LatLng>) extras.getParcelable("points");
	}

}
