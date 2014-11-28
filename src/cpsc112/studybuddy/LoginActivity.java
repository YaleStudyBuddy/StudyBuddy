package cpsc112.studybuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthStateListener;
import com.firebase.client.FirebaseError;

public class LoginActivity extends Activity {
	private ProgressDialog mAuthProgressDialog;
	private Intent intent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		setContentView(R.layout.activity_login);
		
		intent = new Intent(this, DisplayCoursesActivity.class);
				
		mAuthProgressDialog = new ProgressDialog(this);
		mAuthProgressDialog.setTitle("Please wait");
		mAuthProgressDialog.setMessage("Authenticating with Firebase...");
		mAuthProgressDialog.setCancelable(false);
		
		StudyBuddy.ROOT_REF.addAuthStateListener(new AuthStateListener() {
			public void onAuthStateChanged(AuthData authData) {
//				mAuthProgressDialog.hide();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	
	public void loginUser(View view){
		mAuthProgressDialog.show();
		
		EditText emailText = (EditText) findViewById(R.id.emailText);
		EditText passwordText = (EditText) findViewById(R.id.passwordText);
		
		StudyBuddy.ROOT_REF.authWithPassword(emailText.getText().toString(), passwordText.getText().toString(), new AuthResultHandler("password"));
	}
	
	public void createAccount(View view){
		startActivity(new Intent(this, CreateUserActivity.class));
	}
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {
		public AuthResultHandler(String provider) {}
		
		public void onAuthenticated(AuthData authData) {
			mAuthProgressDialog.hide();
			intent.putExtra(StudyBuddy.UID, authData.getUid());
			startActivity(intent);
		}
		
		public void onAuthenticationError(FirebaseError firebaseError) {
			mAuthProgressDialog.hide();
			showErrorDialog(firebaseError.toString());
		}	
	}

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
