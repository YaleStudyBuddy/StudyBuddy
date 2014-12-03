package cpsc112.studybuddy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthStateListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends Activity {
	private DrawerLayout dLayout;
	private ListView dList;
	private ArrayAdapter<String> adapter;
	
	protected static MyCoursesFragment myCourses = new MyCoursesFragment();
	protected static DisplayRosterFragment displayRoster = new DisplayRosterFragment();
	protected static UserProfileFragment myProfile = new UserProfileFragment();
	protected static UserProfileFragment userProfile = new UserProfileFragment();
	protected static MyBuddiesFragment myBuddies = new MyBuddiesFragment();
	
	protected static ArrayList<String> rosterListeners = new ArrayList<String>();
	protected static ArrayList<String> courseListeners = new ArrayList<String>();
	protected static ArrayList<String> buddyListeners = new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);

		StudyBuddy.ROOT_REF.addAuthStateListener(authListener);
		
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
						StudyBuddy.args.putString(StudyBuddy.UID, StudyBuddy.currentUID);
						StudyBuddy.args.putString(StudyBuddy.NAME, StudyBuddy.currentName);
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
	
	protected void replaceFrameWith(Fragment fragment, Bundle args, boolean addToBackStack){
		if (!fragment.isAdded()){
			fragment.setArguments(args);
			
			if (addToBackStack){
				getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).addToBackStack(null).commit();
			} else {
				getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).commit();	
			}
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
				//remove listeners from myCourses
				for (String user : courseListeners){
					StudyBuddy.ROOT_REF.child("users").child(user).child("courses").removeEventListener(myCourses.courseListListener);	
				}
				
				//remove listeners from displayUsers
				for (String roster : rosterListeners){
					StudyBuddy.ROOT_REF.child("courses").child(roster).removeEventListener(displayRoster.rosterListener);
				}
//				StudyBuddy.ROOT_REF.child("courses").addListenerForSingleValueEvent(removeCourseListeners);
				
				//remove listeners from myBuddies
				for (String user : buddyListeners){
					StudyBuddy.ROOT_REF.child("users").child(user).child("buddy requests").removeEventListener(myBuddies.buddyRequestsListListener);
					StudyBuddy.ROOT_REF.child("users").child(user).child("buddies").removeEventListener(myBuddies.buddyListListener);
				}
				
				courseListeners = new ArrayList<String>();
				rosterListeners = new ArrayList<String>();
				buddyListeners = new ArrayList<String>();
				
				StudyBuddy.ROOT_REF.unauth();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	private AuthStateListener authListener = new AuthStateListener(){
		public void onAuthStateChanged(AuthData authData){
			if (authData != null){
				StudyBuddy.currentUID = authData.getUid();
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("name").addListenerForSingleValueEvent(new ValueEventListener(){
					public void onDataChange(DataSnapshot snapshot){
						StudyBuddy.currentName = snapshot.getValue().toString();
					}
					public void onCancelled(FirebaseError firebaseError){}
				});
			} else {
				StudyBuddy.currentUID = null;
				StudyBuddy.currentName = null;

				StudyBuddy.ROOT_REF.removeAuthStateListener(this);
				finish();
			}
		}
	};
	
//	private ValueEventListener removeCourseListeners = new ValueEventListener(){
//		public void onDataChange(DataSnapshot snapshot){
//			Iterable<DataSnapshot> courses = snapshot.getChildren();
//			
//			for (DataSnapshot course : courses){
//				StudyBuddy.ROOT_REF.child("courses").child(course.getValue().toString()).removeEventListener(displayUsers.rosterListener);
//			}
//		}
//		public void onCancelled(FirebaseError firebaseError){}
//	};
}
