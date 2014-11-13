package hk.ust.stop.adapter;

import hk.ust.stop.activity.R;
import hk.ust.stop.model.GoodsInformation;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter{

		// data source
		private List<GoodsInformation> data;
		// context
		private Context context;
		
		public List<GoodsInformation> getData() {
			return data;
		}

		public void setData(List<GoodsInformation> data) {
			this.data = data;
		}
		
		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}


		/**
		 * the number of items shown in listview
		 */
		@Override
		public int getCount() {
			return data.size();
		}

		/**
		 * get data binded to the item
		 */
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		/**
		 * return itemId
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * the recall times depends on how many items shown in listview
		 * @param position
		 * @param convertView ：if the listview can't show all items, previous view will be used for many times
		 */
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			if (convertView == null) {
				// 通过Activity的Context获得上下文
				view = LayoutInflater.from(context).inflate(R.layout.goodsinfo_list_item, null);
			} else {
				view = convertView;
			}
			
			TextView goodsNameTextView = (TextView) view.findViewById(R.id.goodsName);
			TextView goodsPriceTextView = (TextView) view.findViewById(R.id.goodPrice);
			TextView goodsPlaceTextView = (TextView) view.findViewById(R.id.goodsPlace);
			
			goodsNameTextView.setText(data.get(position).getGoodsName());
			goodsPriceTextView.setText(data.get(position).getPrice()+"");
			//goodsPlaceTextView.setText(data.get(position).getPlace());
			
			return view;
			
		}
	
}
