package cpsc112.studybuddy;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthStateListener;

public class MainActivity extends Activity {
	private DrawerLayout dLayout;
	private ListView dList;
	private ArrayAdapter<String> adapter;
	
	protected MyCoursesFragment myCourses = new MyCoursesFragment();
	protected DisplayRosterFragment displayRoster = new DisplayRosterFragment();
	protected UserProfileFragment myProfile = new UserProfileFragment();
	protected UserProfileFragment userProfile = new UserProfileFragment();
	protected MyBuddiesFragment myBuddies = new MyBuddiesFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);
		
		StudyBuddy.currentUser = getIntent().getExtras().getParcelable(StudyBuddy.USER);
		
		StudyBuddy.ROOT_REF.addAuthStateListener(authListener);
		System.out.println("auth state listener added");
		
		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		dList = (ListView) findViewById(R.id.left_drawer);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, StudyBuddy.NAV_MENU);
		dList.setAdapter(adapter);
		dList.setSelector(android.R.color.holo_blue_dark);
		dList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				dLayout.closeDrawers();
				
				switch (position){
					case 0:
						//placeholder code
						getFragmentManager().beginTransaction().remove(myCourses).commit();
						setTitle(getString(R.string.app_name));
						break;
					case 1:
						StudyBuddy.args = new Bundle();
						StudyBuddy.args.putParcelable(StudyBuddy.USER, StudyBuddy.currentUser);
						replaceFrameWith(myProfile, StudyBuddy.args, false);
						break;
					case 2:
						replaceFrameWith(myCourses, null, false);
						break;
					case 3:
						replaceFrameWith(myBuddies, null, false);
					default:
						break;
				}

			}
		});	
	}
	
	protected void replaceFrameWith(StudyBuddyFragment fragment, Bundle args, boolean addToBackStack){
		fragment.updateArguments(args);
		if (addToBackStack){
			getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).addToBackStack(null).commit();
		} else {
			getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).commit();	
		}
	}
	

	@Override
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
	
	private AuthStateListener authListener = new AuthStateListener(){
		public void onAuthStateChanged(AuthData authData){
			if (authData != null){

			} else {
			
				removeFirebaseListeners();
				StudyBuddy.currentUser = null;
				StudyBuddy.ROOT_REF.removeAuthStateListener(this);
				System.out.println("auth state listener removed");
				finish();
			}
		}
	};
	
	private void removeFirebaseListeners(){
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("buddy requests").removeEventListener(myBuddies.buddyRequestsListener);
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("buddies").removeEventListener(myBuddies.buddiesListener);
		System.out.println("buddy listeners removed");
		
		for (String course : StudyBuddy.currentUser.getCourses()){
			StudyBuddy.ROOT_REF.child("courses").child(course).removeEventListener(displayRoster.rosterListener);
		}
		System.out.println("roster listeners removed");
		
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("courses").removeEventListener(myCourses.courseListener);
		System.out.println("course listener removed");
	}
}
