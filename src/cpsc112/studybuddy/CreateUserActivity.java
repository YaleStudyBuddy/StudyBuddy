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
	private Firebase rootRef;
	private String email, password, name, course;
	private Activity thisActivity = this;
	private ProgressDialog createAccountDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		rootRef = new Firebase("https://scorching-heat-1838.firebaseio.com/");
		setContentView(R.layout.activity_create_user);
		
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void createAccount(View view){
		
		createAccountDialog.show();
		
		EditText emailText = (EditText) findViewById(R.id.emailText);
		EditText passwordText = (EditText) findViewById(R.id.passwordText);
		EditText nameText = (EditText) findViewById(R.id.nameText);
		EditText classText = (EditText) findViewById(R.id.courseText);
		
		email = emailText.getText().toString();
		password = passwordText.getText().toString();
		name = nameText.getText().toString();
		course = classText.getText().toString();
		
		
		rootRef.createUser(email, password, new ResultHandler(){
			public void onSuccess(){
				rootRef.authWithPassword(email, password, new AuthResultHandler("password"));
			}
			public void onError(FirebaseError firebaseError){
				
			}
		});
	}
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {
//		private final String provider;
		public AuthResultHandler(String provider) {
//			this.provider = provider;
		}
		
		public void onAuthenticated(AuthData authData) {
			
			Intent intent = new Intent(thisActivity, DisplayClassesActivity.class);
			intent.putExtra(LoginActivity.UID, authData.getUid());
			
			Map<String, Object> newUser = new HashMap<String, Object>();
			newUser.put("name", name);
			Map<String, Object> courses = new HashMap<String, Object>();
			courses.put("0", course);
			newUser.put("courses", courses);
			rootRef.child("users").child(authData.getUid()).setValue(newUser);
			
			newUser = new HashMap<String, Object>();
			newUser.put(authData.getUid(), name);
			rootRef.child("courses").child(course).updateChildren(newUser);
			
			createAccountDialog.hide();
			
			startActivity(intent);
		}
		
		public void onAuthenticationError(FirebaseError firebaseError) {

		}	
	}
	
}
