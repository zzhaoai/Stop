package hk.ust.stop.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * This class is implemented with Singleton Pattern
 * @author XJR
 *
 */
public class PictureDaoImpl {
	
	private String appBaseDir;
	private static PictureDaoImpl dao = null;
	private final static String PICTURE_FORMAT = ".jpg";
	
	/**
	 * Use this method to get an instance of this class
	 * @return
	 */
	public static PictureDaoImpl getInstance() {
		if(null == dao) {
			synchronized (PictureDaoImpl.class) {
				if(null == dao) {
					dao = new PictureDaoImpl();
				}
			}
		}
		
		return dao;
	}
	
	/**
	 * Set the constructor to private, so that the user can only 
	 * create object via getInstance() function.
	 */
	private PictureDaoImpl() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if(sdCardExist){
			// If SD card exist, then get root directory
			sdDir = Environment.getExternalStorageDirectory();
		}
		else{
			// If SD card don't exist, then get system directory
			sdDir = Environment.getDownloadCacheDirectory();
		}
		
		appBaseDir = null;
		if(sdDir != null){
			appBaseDir =  sdDir.getPath()+"/StopForAndroid/Pictures/";
			File dirFile = new File(appBaseDir);
			
			// If the folder doesn't exist in SD card, we will create it
			if(!dirFile.exists())
				dirFile.mkdirs();
		}
	}
	
	
	public String getDirectory() {
		
		return appBaseDir;
	}
	
	
	/**
	 * Cache the picture to SD card. Before doing this, we need to compare the size of this two 
	 * parameters. Only if the size are equal, would the picture be stored.
	 * @param goodsPics
	 * @param names
	 */
	public void cachePictureToSdCard(List<Bitmap> goodsPics, ArrayList<String> names) {
		if(goodsPics.size() != names.size())
			return;
		
		for(int index = 0; index < goodsPics.size(); index++) {
			saveImageToSdcard(goodsPics.get(index), names.get(index));
		}
	}
	
	
	/**
	 * Save a picture of the product to SD card in JPG format.
	 * @param bmp
	 * @param fileName
	 */
	public void saveImageToSdcard(Bitmap bmp, String fileName) {
		if(bmp == null)
			return;

		File file = new File(getDirectory()+fileName+PICTURE_FORMAT);
		
		try {
			// If the file already exist, then delete it and save the new picture.
			if(file.exists())
				file.delete();
			
			file.createNewFile();
			OutputStream outputStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Use the name in the ArrayList to get pictures from SD card.
	 * @param names
	 * @return
	 */
	public ArrayList<Bitmap> getImageFromSdCard(ArrayList<String> names) {
		ArrayList<Bitmap> pictures = new ArrayList<Bitmap>();
		for(int index = 0; index < names.size(); index++) {
			String path = getDirectory() + names.get(index) + PICTURE_FORMAT;
			File file = new File(path);
			if(file.exists())
				pictures.add(BitmapFactory.decodeFile(path));
			else {
				pictures.add(null);
			}
		}
		
		return pictures;
	}
	
	
	public boolean deleteBitmap(String fileName) {
		File file = new File(getDirectory()+fileName+PICTURE_FORMAT);
		return file.delete();
	}
	
}
