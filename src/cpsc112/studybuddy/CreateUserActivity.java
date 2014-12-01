package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.FirebaseError;

public class CreateUserActivity extends Activity {
	private String email, password, name;
	private ProgressDialog createAccountDialog;
	private Activity thisActivity = this;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		setContentView(R.layout.activity_create_user);
		
		intent = new Intent(this, MainActivity.class);
		
		createAccountDialog = new ProgressDialog(this);
		createAccountDialog.setTitle("Loading");
		createAccountDialog.setMessage("Creating new user account, please wait...");
		createAccountDialog.setCancelable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()){
		
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void createAccount(View view){
		
		createAccountDialog.show();
		
		EditText emailText = (EditText) findViewById(R.id.emailText);
		EditText passwordText = (EditText) findViewById(R.id.passwordText);
		EditText nameText = (EditText) findViewById(R.id.nameText);
		
		email = emailText.getText().toString();
		password = passwordText.getText().toString();
		name = nameText.getText().toString();
		
		
		StudyBuddy.ROOT_REF.createUser(email, password, new ResultHandler(){
			public void onSuccess(){
				StudyBuddy.ROOT_REF.authWithPassword(email, password, new AuthResultHandler("password"));
			}
			public void onError(FirebaseError firebaseError){
				createAccountDialog.hide();
				ProgressDialog errorDialog = new ProgressDialog(thisActivity);
				errorDialog.setTitle("Error creating user");
				errorDialog.setMessage(firebaseError.getDetails());
			}
		});
	}
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {

		public AuthResultHandler(String provider) {}
		
		public void onAuthenticated(AuthData authData) {
			createAccountDialog.hide();
			
			Map<String, Object> newUser = new HashMap<String, Object>();
			newUser.put("name", name);
			StudyBuddy.ROOT_REF.child("users").child(authData.getUid()).setValue(newUser);
			thisActivity.finish();
			
			startActivity(intent);
		}
		
		public void onAuthenticationError(FirebaseError firebaseError) {
			createAccountDialog.hide();
			ProgressDialog errorDialog = new ProgressDialog(thisActivity);
			errorDialog.setTitle("Error authenticating user");
			errorDialog.setMessage(firebaseError.getDetails());
		}	
	}
	
}
