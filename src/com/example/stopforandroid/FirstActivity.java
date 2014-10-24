package com.example.stopforandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FirstActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("test commit!!!!!!");
		setContentView(R.layout.activity_first);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first, menu);
		return true;
	}

}
