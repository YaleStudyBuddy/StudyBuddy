package cpsc112.studybuddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthStateListener;

public abstract class StudyBuddy extends Activity {
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static String COURSE_FILTER = "cpsc112.studybuddy.COURSE_FILTER";
	protected final static String UID = "cpsc112.studybuddy.UID";
	protected final static String[] NAV_MENU = {"Home", "My Profile", "My Courses"};
	
	protected static String currentUID;
	protected static String currentUName;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		
		ROOT_REF.addAuthStateListener(new AuthStateListener(){
			public void onAuthStateChanged(AuthData authData){
				if (authData != null){

				} else {
					currentUID = null;
					currentUName = null;
					finish();
				}
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.logout_button:
			StudyBuddy.ROOT_REF.unauth();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
