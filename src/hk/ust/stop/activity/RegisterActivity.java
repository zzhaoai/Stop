package hk.ust.stop.activity;

import hk.ust.stop.util.AccountUtil;
import hk.ust.stop.util.ToastUtil;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity 
				implements OnClickListener{

	private EditText nameEditText;
	private EditText passwordEditText;
	private EditText pwdrepeatEditText;
	private EditText emailEditText;
	private EditText phoneEditText;
	private Button registerButton;
	private Handler handler;
	
	private String username;
	private String password;
	private String pwdRepeat;
	private String email;
	private String phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity_layout);
		
		handler = new Handler();
		nameEditText = (EditText) findViewById(R.id.edittext_username);
		passwordEditText = (EditText) findViewById(R.id.edittext_password);
		pwdrepeatEditText = (EditText) findViewById(R.id.edittext_password_repeat);
		emailEditText = (EditText) findViewById(R.id.edittext_email);
		phoneEditText = (EditText) findViewById(R.id.edittext_phone);
		registerButton = (Button) findViewById(R.id.button_register);
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
			finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	
	private boolean isCorrectToRegister() {
		if(null != nameEditText.getText())
			username = nameEditText.getText().toString();
		if(null != passwordEditText.getText())
			password = passwordEditText.getText().toString();
		if(null != pwdrepeatEditText.getText())
			pwdRepeat = pwdrepeatEditText.getText().toString();
		if(null != emailEditText.getText())
			email = emailEditText.getText().toString();
		if(null != phoneEditText.getText())
			phone = phoneEditText.getText().toString();
		
		if(TextUtils.isEmpty(username) || 
				TextUtils.isEmpty(password) ||
				TextUtils.isEmpty(pwdRepeat) ||
				TextUtils.isEmpty(email) ||
				TextUtils.isEmpty(phone) ||
				!password.equals(pwdRepeat))
			return false;
		else {
			return true;
		}
		
	}

	@Override
	public void onClick(View v) {
		registerButton.setClickable(false);
		if(isCorrectToRegister()) {
			// Create a new thread to register 
			new Thread(runnable).start();
		} else {
			registerButton.setClickable(true);
			ToastUtil.showToast(this, "fail to register");
		}
	}
	
	
	/**
	 * This instance is used to register a new user in the server.
	 */
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			boolean result = AccountUtil.registerNewUser(
					username, password, email, phone);
			
			if(true == result) {
				// If registration succeed, then jump to MainActivity.
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						ToastUtil.showToast(
								RegisterActivity.this, "success!");
						Intent intent = new Intent();
						intent.setClass(
								RegisterActivity.this, MainActivity.class);
						intent.putExtra("isLogin", true);
						startActivity(intent);
					}
				});
			} else {
				// If the registration fails, then give user a hint
				registerButton.setClickable(true);
				ToastUtil.showToast(RegisterActivity.this, "fail to register");
			}
		}
	};
}
