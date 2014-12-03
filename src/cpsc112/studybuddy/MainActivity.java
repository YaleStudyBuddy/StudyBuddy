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
	
	protected static MyCoursesFragment myCourses = new MyCoursesFragment();
	protected static DisplayUsersFragment displayUsers = new DisplayUsersFragment();
	protected static UserProfileFragment myProfile = new UserProfileFragment();
	protected static UserProfileFragment userProfile = new UserProfileFragment();
	protected static MyBuddiesFragment myBuddies = new MyBuddiesFragment();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);

		StudyBuddy.ROOT_REF.addAuthStateListener(new AuthStateListener(){
			public void onAuthStateChanged(AuthData authData){
				if (authData != null){

				} else {
					StudyBuddy.currentUID = null;
					StudyBuddy.currentName = null;
					finish();
				}
			}
		});
		
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
						getFragmentManager().beginTransaction().remove(myCourses).commit();
						setTitle(getString(R.string.app_name));
						break;
					case 1:
						if (myProfile.getArguments() == null){
							Bundle args = new Bundle();
							myProfile.setArguments(args);
						}
						getFragmentManager().beginTransaction().replace(R.id.main_content_frame, myProfile).commit();
						break;
					case 2:
						getFragmentManager().beginTransaction().replace(R.id.main_content_frame, myCourses).commit();
						break;
					case 3:
						getFragmentManager().beginTransaction().replace(R.id.main_content_frame, myBuddies).commit();
					default:
						break;
				}

			}
		});
		
		
	}
	

	@Override
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
		switch (item.getItemId()){
			case R.id.logout_button:
				StudyBuddy.ROOT_REF.unauth();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
}
