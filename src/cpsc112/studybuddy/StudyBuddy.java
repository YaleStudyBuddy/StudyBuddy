package cpsc112.studybuddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

public abstract class StudyBuddy extends Activity {
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static String COURSE_FILTER = "cpsc112.studybuddy.COURSE_FILTER";
	protected final static String UID = "cpsc112.studybuddy.UID";
	protected final static String[] NAV_MENU = {"Home", "My Profile", "My Courses"};
	
	protected static String currentUID = StudyBuddy.ROOT_REF.getAuth().getUid();
	protected static String currentUName;
	
	protected static String checkCourseString(String course){
		//check there are no extra spaces in course number input
		//make string all caps
		return null;
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);	
	}
	
}
