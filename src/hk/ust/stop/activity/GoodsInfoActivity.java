package hk.ust.stop.activity;

import hk.ust.stop.model.GoodsInformation;
import hk.ust.stop.util.ToastUtil;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GoodsInfoActivity extends Activity {

	private ImageView goodsImageView;
	private TextView goodsName;
	private TextView goodsPrice;
	private TextView goodsPlace;
	private Button returnToSearchList;
	
	private GoodsInformation goodsItem;
	private Bitmap goodsPic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		String serializableKey = extras.getString("SerializableKey");
		goodsItem = (GoodsInformation)extras.getSerializable(serializableKey);
		//goodsPic = extras.getParcelable("picture");
		byte [] bis=getIntent().getByteArrayExtra("bitmap");  
		goodsPic = BitmapFactory.decodeByteArray(bis, 0, bis.length);
		
		initView();
		initEvent();

	}

	private void initView() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_goodsinfo);
		
		goodsImageView = (ImageView) findViewById(R.id.goods_picture);
		goodsName = (TextView) findViewById(R.id.goodsNameItem);
		goodsPrice = (TextView) findViewById(R.id.goodsPriceItem);
		goodsPlace = (TextView) findViewById(R.id.goodsPlaceItem);
		returnToSearchList = (Button) findViewById(R.id.returnToSearchList);
		
		goodsName.setText(goodsItem.getGoodsName());
		goodsPrice.setText(goodsItem.getPrice()+"HKD");
		goodsPlace.setText("place");

		if(null != goodsPic) {
			//goodsPic = scaleDownBitmap(goodsPic, 100);
			goodsImageView.setImageBitmap(goodsPic);
		}
		else {
			ToastUtil.showToast(this, "picture is empty");
		}
	}

	private void initEvent() {

		returnToSearchList.setOnClickListener(new returnToSearchListListener());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.first, menu);
		return true;
	}

	class returnToSearchListListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			finish();
		}

	}

	
	 public Bitmap scaleDownBitmap(Bitmap photo, int newHeight) {

		 final float densityMultiplier = getResources().getDisplayMetrics().density;        

		 int h= (int) (newHeight*densityMultiplier);
		 int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

		 photo=Bitmap.createScaledBitmap(photo, w, h, true);

		 return photo;
		 }
}
