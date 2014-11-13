package hk.ust.stop.dao;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import hk.ust.stop.dao.GoodsInfoProviderMetaData.TableMetaData;
import hk.ust.stop.idao.BaseDaoInterface;
import hk.ust.stop.model.GoodsInformation;

public class BaseDaoImpl implements BaseDaoInterface{

	private ContentResolver resolver;
	
	public BaseDaoImpl(ContentResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public void insert(long userId, GoodsInformation info, int flag) {
		ContentValues values = new ContentValues();
		values.put(TableMetaData.USER_ID, userId);
		values.put(TableMetaData.UPLOAD_FLAG, flag);
		values.put(TableMetaData.GOODS_ID, info.getGoodsId());
		values.put(TableMetaData.PICTURE_ID, info.getPictureId());
		values.put(TableMetaData.LONGITUDE, info.getLongitude());
		values.put(TableMetaData.LATITUDE, info.getLatitude());
		values.put(TableMetaData.PRICE, info.getPrice());
		values.put(TableMetaData.GOODS_NAME, info.getGoodsName());
		values.put(TableMetaData.GOODS_DESCRIPTION, info.getGoodsDescription());
		
		Uri uri = resolver.insert(TableMetaData.CONTENT_URI, values);
	}

	@Override
	public ArrayList<GoodsInformation> queryAllRecord() {
		Cursor cursor = resolver.query(TableMetaData.CONTENT_URI, null, null, null, null);
		ArrayList<GoodsInformation> list = new ArrayList<GoodsInformation>();
		
		while(cursor.moveToNext()) {
			GoodsInformation goods = new GoodsInformation();
			goods.setGoodsId(cursor.getLong(cursor.getColumnIndex(TableMetaData.GOODS_ID)));
			goods.setGoodsName(cursor.getString(cursor.getColumnIndex(TableMetaData.GOODS_NAME)));
			goods.setPictureId(cursor.getLong(cursor.getColumnIndex(TableMetaData.PICTURE_ID)));
			goods.setLongitude(cursor.getDouble(cursor.getColumnIndex(TableMetaData.LONGITUDE)));
			goods.setLatitude(cursor.getDouble(cursor.getColumnIndex(TableMetaData.LATITUDE)));
			goods.setPrice(cursor.getDouble(cursor.getColumnIndex(TableMetaData.PRICE)));
			goods.setGoodsDescription(cursor.getString(cursor.getColumnIndex(TableMetaData.GOODS_DESCRIPTION)));
			list.add(goods);
		}
		
		return list;
	}

}
