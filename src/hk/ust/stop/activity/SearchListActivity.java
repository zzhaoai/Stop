package hk.ust.stop.activity;

import hk.ust.stop.adapter.SearchListAdapter;
import hk.ust.stop.model.GoodsInformation;
import hk.ust.stop.widget.RefreshableView;
import hk.ust.stop.widget.RefreshableView.PullToLoadMoreListener;
import hk.ust.stop.widget.RefreshableView.PullToRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchListActivity extends ListActivity implements OnItemClickListener{

public final static String GOODSINFO_KEY = "hk.ust.stop.activity.SearchListActivity";

	private View header;
	
	private Handler handler;
	private Thread currentThread;
	
	// record the first cursor of listView for data, currentNum X times the number of batchSize
	private int currentNum = 0;
	// max records shown on one page
	private int batchSize = 16;
	
	private List<GoodsInformation> serverData;
	private List<GoodsInformation> adapterData; // model
	private RefreshableView refreshableView; // widget view
	private ListView listView; // sub view 
	private SearchListAdapter adapter; // controller
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// initial GUI
		initView();
		// bind events
		initEvent();
		// initial Handler and ListView
		initHandler();
		// get data from server
		getDataFromServer();
		
		serverData = new ArrayList<GoodsInformation>();
		adapterData = new ArrayList<GoodsInformation>();
		
	}
	
	/**
	 * initial GUI
	 */
	private void initView(){
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchlist);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = getListView(); // the way getting listView in ListActivity
		
		LayoutInflater inflater = getLayoutInflater();
		header = (View)inflater.inflate(R.layout.goodsinfo_list_header, listView, false);
		
	}
	
	/**
	 * bind events (scroll event and itemClick event)
	 */
	private void initEvent(){
		
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
	private void initHandler(){
		handler = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				List<GoodsInformation> goodsItems = (List<GoodsInformation>) bundle.getSerializable(GOODSINFO_KEY);
				serverData = goodsItems;
				initAdapter();
			}
		};
	}
	
	/**
	 *  get data from server
	 */
	private void getDataFromServer(){
		
		 new Thread(new Runnable() {
				@Override
				public void run() {
					
					/*String staticUrl = UrlConstant.DISHINFO_URL;
					String responseData = ConnectionUtil.getFromServer(staticUrl);
					List<GoodsItem> dishInfos = Transfer2JsonUtil.dishInfoJsonTransfer(responseData);*/
					
					List<GoodsInformation> goodsItems = new ArrayList<GoodsInformation>();
					
					for (int i = 0; i < 40; i++) {
						goodsItems.add(new GoodsInformation("name"+i,i));
					}
						

					Message message=new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable(GOODSINFO_KEY, (Serializable) goodsItems);
					message.setData(bundle);
					handler.sendMessage(message);
					
				}
		}).start();
		
	}
	
	/**
	 *  initial adapter
	 *  first step: get part of data (model)
	 *  second step: build a new adapter and initial it (controller)
	 *  third step: set adapter for ListView (view)
	 */
	private void initAdapter() {
	
		if (listView == null)
			return;
		
		batchServerData();
		
		adapter = new SearchListAdapter();
		adapter.setContext(this);
		adapter.setData(adapterData);

		// addHeaderView or addFooterView has to be called before setAdapter
		listView.addHeaderView(header, "header", false);
		listView.setAdapter(adapter);
		
	}
	
	/**
	 *  handle ServerData, set adapterData with serverData of batchSize each time
	 */
 	private void batchServerData() {
		
		if (serverData == null)
			return;

		int totalSize = serverData.size();

		// stop condition
		if (currentNum == totalSize)
			return;

		int showSize;
		int result = currentNum + batchSize - totalSize;
		// result<=0 indicates the number of dishes next time is batchSize
		if(result<=0){
			showSize = batchSize;
		}else{
			// result>0 indicates the number of dishes next time is less than batchSize
			showSize = totalSize - currentNum;
		}
		
		for(int i=0;i<showSize;i++){
			adapterData.add( serverData.get(currentNum+i) );
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
				batchServerData();
				
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
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		// to do
		
	}
	
	/**
	 * Interface of pull-down-refresh listener
	 * accomplish concrete refresh logic here 
	 */
	class MyPullToRefreshListener implements PullToRefreshListener {

		@Override
		public void onRefresh() {

			try {
				Thread.sleep(2000);

				handler.post(new Runnable() {
					@Override
					public void run() {
						/*
						// clear signalNum and adaterData
						currentNum = 0;
						adapterData.clear();
						// get data from server again for updating
						getDataFromServer();
						adapter.notifyDataSetChanged();
					    */
						
						adapterData.clear();
						adapter = new SearchListAdapter();
						adapter.setContext(getApplicationContext());
						
						List<GoodsInformation> goodsItems = new ArrayList<GoodsInformation>();
						for (int i = 0; i < 16; i++) {
							goodsItems.add(new GoodsInformation("name"+10*i,10*i));
						}
						for (GoodsInformation goodsItem : goodsItems) {
							adapterData.add(goodsItem);
						}
						adapter.setData(adapterData);
						currentNum = 16;
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
	 * Interface of pull-up-load listener
	 * accomplish concrete loading logic here 
	 */
	class MyPullToLoadMoreListener implements PullToLoadMoreListener{

		@Override
		public void onLoadMore() {
			// if there is more data to load
			if(serverData.size() != currentNum){
				// change loadStatus when loading
				refreshableView.isLoading();
				// add footer view to the tail of listView
				refreshableView.addFooterView();
				// start Thread to load data in batch
				currentThread = new DataLoadThread();
				currentThread.start();
			}else{
				// change loadStatus when not loading
				refreshableView.finishLoading();
			}
		}
		
	}
	
}