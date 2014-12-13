package hk.ust.stop.activity;

import hk.ust.stop.util.AccountUtil;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * This activity is used for login, and user can also register a new accound
 * by pressing the register button
 * @author XJR
 *
 */
public class LoginActivity extends Activity
			implements OnClickListener{

	private EditText nameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private Button registerButton;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity_layout);
		
		// Initialize the views
		handler = new Handler();
		nameEditText = (EditText)findViewById(R.id.edittext_user_name);
		passwordEditText = (EditText)findViewById(R.id.edittext_user_password);
		loginButton = (Button)findViewById(R.id.button_login);
		registerButton = (Button)findViewById(R.id.button_register);
		loginButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("back");
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.button_login:
			// Disable the login button
			loginButton.setClickable(false);
			// Create a new thread to check the name and the password
			new Thread(runnable).start();
			break;
			
		case R.id.button_register:
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { 
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}

	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// Check whether the username and the password match
			boolean result = AccountUtil.checkNameAndPassword(
					nameEditText.getText().toString(), 
					passwordEditText.getText().toString());
			if(true == result) {
				// If the name and the password match, then jump back to MainActivity
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,
								MainActivity.class);
						intent.putExtra("isLogin", true);
						startActivity(intent);
						finish();
					}
				});
			} else {
				// If name and the password does't match, give a hint to user
				loginButton.setClickable(true);
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(LoginActivity.this, 
								"can not login", Toast.LENGTH_LONG).show();
					}
				});
			}
		
		}
	};
}
