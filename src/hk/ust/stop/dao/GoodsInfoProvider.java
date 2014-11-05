package hk.ust.stop.dao;

import hk.ust.stop.dao.GoodsInfoProviderMetaData.TableMetaData;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class GoodsInfoProvider extends ContentProvider{

	
	private SQLiteDatabase db;
	
	private static final UriMatcher uriMatcher;
	
	private static final int MY_INFO = 1;
	private static final int MY_INFO_SINGLE = 2;
	private static HashMap<String,String> userProjectionMap;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(GoodsInfoProviderMetaData.AUTHORITY, "myinfo", MY_INFO);
		uriMatcher.addURI(GoodsInfoProviderMetaData.AUTHORITY, "myinfo/#", MY_INFO_SINGLE);
	
		userProjectionMap = new HashMap<String, String>();
		userProjectionMap.put(TableMetaData._ID, TableMetaData._ID);
		userProjectionMap.put(TableMetaData.USER_ID, TableMetaData.USER_ID);
		userProjectionMap.put(TableMetaData.UPLOAD_FLAG, TableMetaData.UPLOAD_FLAG);
		userProjectionMap.put(TableMetaData.GOODS_ID, TableMetaData.GOODS_ID);
		userProjectionMap.put(TableMetaData.PICTURE_ID, TableMetaData.PICTURE_ID);
		userProjectionMap.put(TableMetaData.LONGITUDE, TableMetaData.LONGITUDE);
		userProjectionMap.put(TableMetaData.LATITUDE, TableMetaData.LATITUDE);
		userProjectionMap.put(TableMetaData.PRICE, TableMetaData.PRICE);
		userProjectionMap.put(TableMetaData.GOODS_NAME, TableMetaData.GOODS_NAME);
		userProjectionMap.put(TableMetaData.GOODS_DESCRIPTION, TableMetaData.GOODS_DESCRIPTION);
		
	}
	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		StopDatabaseHelper dbHelper = new StopDatabaseHelper(context, GoodsInfoProviderMetaData.databaseName);
		db = dbHelper.getReadableDatabase();
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch (uriMatcher.match(uri)) {
		case MY_INFO:
			builder.setTables(TableMetaData.TABLE_NAME);
			builder.setProjectionMap(userProjectionMap);
			break;

		default:
			break;
		}
		String orderBy;
		if(TextUtils.isEmpty(sortOrder)) {
			orderBy = TableMetaData.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		
		Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId = db.insert(TableMetaData.TABLE_NAME, null, values);
		if(rowId > 0) {
			//发出通知给监听器，说明数据已经改变
            //ContentUris为工具类
            Uri insertedUserUri = ContentUris.withAppendedId(TableMetaData.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertedUserUri, null);
            
            return insertedUserUri;
		}
		throw new SQLException("Failed to insert row into" + uri);	
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
