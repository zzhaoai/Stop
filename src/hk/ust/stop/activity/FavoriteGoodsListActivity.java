package hk.ust.stop.activity;

import hk.ust.stop.adapter.CommonListAdapter;
import hk.ust.stop.dao.BaseDaoImpl;
import hk.ust.stop.dao.PictureDaoImpl;
import hk.ust.stop.idao.BaseDaoInterface;
import hk.ust.stop.model.GoodsInformation;
import hk.ust.stop.model.UserInformation;
import hk.ust.stop.util.AccountUtil;
import hk.ust.stop.util.ConnectionUtil;
import hk.ust.stop.util.JsonUtil;
import hk.ust.stop.util.ServerUrlUtil;
import hk.ust.stop.util.ToastUtil;
import hk.ust.stop.widget.RefreshableView;
import hk.ust.stop.widget.RefreshableView.PullToLoadMoreListener;
import hk.ust.stop.widget.RefreshableView.PullToRefreshListener;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FavoriteGoodsListActivity extends ListActivity implements OnItemClickListener {

	public final static String GOODSINFO_KEY = "hk.ust.stop.activity.FavoriteGoodsListActivity";

	private View header;
	private CheckBox checkBox;

	private Handler handler;
	private Thread currentThread;

	// record the first cursor of listView for data, currentNum X times the
	// number of batchSize
	private int currentNum = 0;
	// max records shown on one page
	private int batchSize = 10;

	private List<LatLng> goodsPoints;
	private List<Bitmap> goodsPics;
	private List<GoodsInformation> selectedData;
	private List<GoodsInformation> localData;
	private List<GoodsInformation> adapterData; // model
	private RefreshableView refreshableView; // widget view
	private ListView listView; // sub view
	private CommonListAdapter adapter; // controller

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// initial GUI
		initView();
		// bind events
		initEvent();
		// initial Handler and ListView
		initHandler();
		// get data from server
		getDataFromLocal();

		localData = new ArrayList<GoodsInformation>();
		adapterData = new ArrayList<GoodsInformation>();
		selectedData = new ArrayList<GoodsInformation>();

	}

	/**
	 * initial GUI
	 */
	private void initView() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_favoritegoodslist);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view_favoritegoodslist);
		listView = getListView(); // the way getting listView in ListActivity

		LayoutInflater inflater = getLayoutInflater();
		header = (View) inflater.inflate(R.layout.common_list_header, listView,	false);
		checkBox = (CheckBox) header.findViewById(R.id.fullSelect);

	}

	/**
	 * bind events (scroll event and itemClick event)
	 */
	private void initEvent() {

		listView.setOnItemClickListener(this);
		// set pull-down-refresh listener in self-defined widget
		refreshableView.setOnRefreshListener(new MyPullToRefreshListener(), 0);
		// set pull-up-load listener in self-defined widget
		refreshableView.setOnLoadListener(new MyPullToLoadMoreListener());

	}

	/**
	 * initial Handler, set data, and initial adapter
	 */
	@SuppressLint("HandlerLeak")
	private void initHandler() {
		handler = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				switch (msg.what) {
				case 1:
					// get data successfully
					List<GoodsInformation> goodsItems = (List<GoodsInformation>) bundle.getSerializable(GOODSINFO_KEY);
					localData = goodsItems;
					initAdapter();
					ToastUtil.showToast(getApplicationContext(), "get data from SQLite");
					break;
				case 2:
					// get data unsuccessfully
					ToastUtil.showToast(getApplicationContext(), "fail to get data");
					break;
				case 3:
					// delete successfully
					int[] deleteNums = bundle.getIntArray("deleteNums");
					for (int j = deleteNums.length - 1; j >= 0; j--) {
						int reverseNum = deleteNums[j];
						adapterData.remove(reverseNum);
						adapter.notifyDataSetChanged();
					}
					ToastUtil.showToast(getApplicationContext(), "delete successfully");
					break;
				case 4:
					// delete unsuccessfully
					ToastUtil.showToast(getApplicationContext(), "failed to delete");
					break;
				default:
					ToastUtil.showToast(getApplicationContext(), "logic error");
					break;
				}

			}
		};
	}

	/**
	 * get data from local database
	 */
	private void getDataFromLocal() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				// get data from contentProvider (local database)
				boolean isLogin = AccountUtil.isLogin();
				UserInformation currentUser;
				
				if(isLogin){
					currentUser = AccountUtil.getLoginUser();
				}else{
					currentUser = null;
				}
				
				BaseDaoInterface dao = new BaseDaoImpl(getContentResolver());
				List<GoodsInformation> dbData = new ArrayList<GoodsInformation>();
				dbData = dao.queryAllRecord(currentUser, 0);
				
				// Define ArrayList to store the picture
				ArrayList<String> names = new ArrayList<String>();
				for(GoodsInformation goods : dbData) {
					names.add(goods.getPictureName());
				}
				// Get pictures from SD card.
				goodsPics = PictureDaoImpl.getInstance().getImageFromSdCard(names);
				
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putSerializable(GOODSINFO_KEY, (Serializable) dbData);
				message.setData(bundle);
				message.what = 1;
				handler.sendMessage(message);

			}
		}).start();

	}

	/**
	 * delete data in local database
	 */
	private void deleteDataInLocal(final String nums) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				boolean isLogin = AccountUtil.isLogin();
				UserInformation currentUser;
				
				if(isLogin){
					currentUser = AccountUtil.getLoginUser();
				}else{
					currentUser = null;
				}
				
				BaseDaoInterface dao = new BaseDaoImpl(getContentResolver());
				
				for(GoodsInformation singleData : selectedData){
					dao.deleteByUserAndGoodsId(currentUser, singleData);
				}

				String[] temp = nums.split(" ");
				int[] deleteNums = new int[temp.length];
				for (int i = 0; i < temp.length; i++) {
					deleteNums[i] = Integer.parseInt(temp[i]);
				}

				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putIntArray("deleteNums", deleteNums);
				message.setData(bundle);
				message.what = 3;
				handler.sendMessage(message);

			}
		}).start();

	}

	/**
	 * initial adapter first step: get part of data (model) second step: 
	 * build a new adapter and initial it (controller) third step: set adapter for ListView (view)
	 */
	private void initAdapter() {

		if (listView == null)
			return;

		batchLocalData();

		adapter = new CommonListAdapter();
		adapter.setContext(this);
		adapter.setData(adapterData);
		adapter.setFullChecked(false);
		// restore checkBox to default state
		checkBox.setChecked(false);

		// addHeaderView or addFooterView has to be called before setAdapter
		listView.addHeaderView(header, "header", false);
		listView.setAdapter(adapter);

	}

	/**
	 * handle LocalData, set adapterData with localData of batchSize each time
	 */
	private void batchLocalData() {

		if (localData == null)
			return;

		int totalSize = localData.size();

		// stop condition
		if (currentNum == totalSize)
			return;

		int showSize;
		int result = currentNum + batchSize - totalSize;
		// result<=0 indicates the number of dishes next time is batchSize
		if (result <= 0) {
			showSize = batchSize;
		} else {
			// result>0 indicates the number of dishes next time is less than
			// batchSize
			showSize = totalSize - currentNum;
		}

		for (int i = 0; i < showSize; i++) {
			adapterData.add(localData.get(currentNum + i));
		}

		currentNum += showSize;

	}

	/**
	 * handle loading event : update data in adapter and UI,
	 */
	class DataLoadThread extends Thread {
		@Override
		public void run() {
			try {
				// wait for 2000ms : reserve showing time for loading
				Thread.sleep(2000);

				// handle server data (cut into batches)
				batchLocalData();

				// post request to UI thread to update UI
				handler.post(new Runnable() {
					@Override
					public void run() {
						// remove footer view after finishing loading;
						refreshableView.removeFooterView();
						// change loadStatus after finishing loading
						refreshableView.finishLoading();
						// inform listView update data when data is changed
						adapter.notifyDataSetChanged();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Object itemObject = listView.getItemAtPosition(position);
		Bundle bundle = new Bundle();
		bundle.putSerializable(GOODSINFO_KEY, (Serializable) itemObject);
		Intent intent = new Intent();
		intent.setClass(this, GoodsInfoActivity.class);
		intent.putExtras(bundle);
		intent.putExtra("SerializableKey", GOODSINFO_KEY);
		
		// Change the bitmap to byte array.
		Bitmap bmp = goodsPics.get(position-1);
		ByteArrayOutputStream baos=new ByteArrayOutputStream();  
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);  
		byte [] bitmapByte =baos.toByteArray();  
		intent.putExtra("bitmap", bitmapByte);
		
		startActivity(intent);

	}

	/**
	 * Interface of pull-down-refresh listener accomplish concrete refresh logic here
	 */
	class MyPullToRefreshListener implements PullToRefreshListener {

		@Override
		public void onRefresh() {

			try {
				Thread.sleep(2000);

				handler.post(new Runnable() {
					@Override
					public void run() {

						adapterData.clear();
						adapterData = new ArrayList<GoodsInformation>();
						localData.clear();
						// clear signalNum and adaterData
						currentNum = 0;
						// notice to remove previous header before initial
						// adapter again
						listView.removeHeaderView(header);
						// get data from server again for updating
						getDataFromLocal();
						adapter.notifyDataSetChanged();

					}
				});

				// change loadStatus after finishing loading
				refreshableView.finishRefreshing();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Interface of pull-up-load listener accomplish concrete loading logic here
	 */
	class MyPullToLoadMoreListener implements PullToLoadMoreListener {

		@Override
		public void onLoadMore() {
			// if there is more data to load
			if (localData.size() != currentNum) {
				// change loadStatus when loading
				refreshableView.isLoading();
				// add footer view to the tail of listView
				refreshableView.addFooterView();
				// start Thread to load data in batch
				currentThread = new DataLoadThread();
				currentThread.start();
			} else {
				// change loadStatus when not loading
				refreshableView.finishLoading();
			}
		}

	}

	/**
	 * set selectedData according to selected items in adapterData
	 * @return number of chosen items
	 */
	private String setSelectedData() {

		String chosenNum = "";
		for (int i = 0; i < adapterData.size(); i++) {
			GoodsInformation singleData = adapterData.get(i);
			if (singleData.getSelected()) {
				String temp = i + " ";
				chosenNum += temp;
				selectedData.add(singleData);
			}
		}
		return chosenNum;

	}

	/**
	 * bind onClickListener for delete-items Button
	 * @param view
	 */
	public void deleteOnClickListener(View view) {

		// delete selected items and keep the same with Server synchronized
		String nums = setSelectedData();

		// Thread to delete data in local database
		deleteDataInLocal(nums);

		// test
		ToastUtil.showToast(this, nums);

	}
	private  void getRouteFromServer(final List<LatLng> points){

		new Thread(new Runnable() {
			@Override
			public void run() {
				String staticUrl = ServerUrlUtil.getRouteUrl(points);
				String responseData = ConnectionUtil.getFromServer(staticUrl);
				goodsPoints = JsonUtil.transfer2RoutePointsList(responseData);
				
				try{
					File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.txt");
					
						FileWriter fw = new FileWriter( file );
						BufferedWriter bw = new BufferedWriter( fw );
						for(LatLng str: goodsPoints) {
							String data =  str.latitude+","+str.longitude+"\n";
							bw.write(data);
						}
						bw.close();		     
					
				}
				catch(Exception e )
				{
				}
				
			}
		}).start();
		//return goodsPoints;
	}
	/**
	 * bind onClickListener for showOnMap Button
	 * @param view
	 */
	public void showOnMapOnClickListener(View view){
		// upload selected items to server and get optimized route
		List<LatLng> locationPoints = new ArrayList<LatLng>();
		Iterator<GoodsInformation> iterator = selectedData.iterator();
		ToastUtil.showToast(getApplicationContext(), ""+selectedData);
		while(iterator.hasNext()){
			GoodsInformation goodsItem = iterator.next();
			ToastUtil.showToast(getApplicationContext(), ""+goodsItem.getLongitude());
			LatLng point = new LatLng(goodsItem.getLongitude(),goodsItem.getLatitude());
			locationPoints.add(point);
		}

		getRouteFromServer(locationPoints);
		
		Intent intent = new Intent(this, MainActivity.class);
		//intent.putExtras(bundle);
		FavoriteGoodsListActivity.this.setResult(RESULT_OK, intent);
		FavoriteGoodsListActivity.this.finish();

	}

	/**
	 * bind onClickListener for checkBox set full-checked state
	 */
	public void onCheckboxClicked(View view) {

		if (checkBox.isChecked()) {
			ToastUtil.showToast(this, checkBox.isChecked() + "");
			for (GoodsInformation singleData : adapterData) {
				singleData.setSelected(true);
			}
			adapter.setFullChecked(true);
			adapter.notifyDataSetChanged();
		} else {
			ToastUtil.showToast(this, checkBox.isChecked() + "");
			for (GoodsInformation singleData : adapterData) {
				singleData.setSelected(false);
			}
			adapter.setFullChecked(false);
			adapter.notifyDataSetChanged();
		}

	}

}
