package cpsc112.studybuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	
	protected CoursesFragment coursesFragment = new CoursesFragment();
	protected RosterFragment rosterFragment = new RosterFragment();
//	protected UserProfileFragment myProfileFragment = new UserProfileFragment();
	protected UserProfileFragment userProfileFragment = new UserProfileFragment();
	protected GroupProfileFragment groupProfileFragment = new GroupProfileFragment();
	protected BuddiesFragment buddiesFragment = new BuddiesFragment();
	protected GroupsFragment groupsFragment = new GroupsFragment();
	
	protected User currentUser;
	
	@Override
	protected void onStart(){
		super.onStart();
		StudyBuddy.ROOT_REF.addAuthStateListener(authListener);
		System.out.println("auth state listener added");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);
		
		currentUser = getIntent().getExtras().getParcelable(StudyBuddy.USER);
		
		//initializes navigation drawer, code adapted from http://www.tutecentral.com/android-custom-navigation-drawer
		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		dList = (ListView) findViewById(R.id.left_drawer);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, StudyBuddy.NAV_MENU);
		dList.setAdapter(adapter);
		dList.setSelector(android.R.color.holo_blue_dark);
		dList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View v, int index, long id) {
				dLayout.closeDrawers();
				Bundle args = new Bundle();
				
				//handles navigation to different fragments
				switch (index){
					case 0:
						args.putParcelable(StudyBuddy.USER, currentUser);
						args.putInt(StudyBuddy.MENU_INDEX, index);
						replaceFrameWith(userProfileFragment, args, false);
						break;
					case 1:
						args.putParcelable(StudyBuddy.USER, currentUser);
						args.putInt(StudyBuddy.MENU_INDEX, index);
						replaceFrameWith(coursesFragment, args, false);
						break;
					case 2:
						args.putParcelable(StudyBuddy.USER, currentUser);
						args.putInt(StudyBuddy.MENU_INDEX, index);
						replaceFrameWith(buddiesFragment, args, false);
						break;
					case 3:
						args.putParcelable(StudyBuddy.USER, currentUser);
						args.putInt(StudyBuddy.MENU_INDEX, index);
						replaceFrameWith(groupsFragment, args, false);
					default:
						break;
				}
			}
		});	
		
		//starts activity with showing user profile
		Bundle args = new Bundle();
		args.putParcelable(StudyBuddy.USER, currentUser);
		args.putInt(StudyBuddy.MENU_INDEX, 0);
		replaceFrameWith(userProfileFragment, args, false);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				getFragmentManager().popBackStackImmediate();
				return true;
			case R.id.logout_button:
				logout();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		StudyBuddy.ROOT_REF.removeAuthStateListener(authListener);
		System.out.println("auth state listener removed");
	}
	
	//replaces activity content frame with fragment
	protected void replaceFrameWith(StudyBuddyFragment fragment, Bundle args, boolean addToBackStack){
		User user = args.getParcelable(StudyBuddy.USER);
		if (user == null || !user.getID().equals(currentUser.getID()) || !fragment.isAdded()){
			fragment.updateArguments(args);	
			if (addToBackStack){
				getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).addToBackStack(null).commit();
			} else {
				getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).commit();	
			}
		}
	}
	
	//handles login timeouts event
	private AuthStateListener authListener = new AuthStateListener(){
		public void onAuthStateChanged(AuthData authData){
			if (authData == null){
				AlertDialog.Builder timeoutDialog = new AlertDialog.Builder(getActivity());
				timeoutDialog.setTitle("Your session has timed out");
				timeoutDialog.setMessage("You will be returned to the log in screen");
				
				timeoutDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						StudyBuddy.ROOT_REF.removeAuthStateListener(authListener);
						StudyBuddy.ROOT_REF.unauth();
						finish();
					}
				});
				
				timeoutDialog.show();
			}
		}
	};
	
	//logs user out
	private void logout(){
		AlertDialog.Builder logoutDialog = new AlertDialog.Builder(getActivity());
		logoutDialog.setTitle("Log out");
		logoutDialog.setMessage("Are you sure you want to log out?");
		
		logoutDialog.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				StudyBuddy.ROOT_REF.removeAuthStateListener(authListener);
				StudyBuddy.ROOT_REF.unauth();
				finish();
			}
		});
		
		logoutDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		logoutDialog.show();
	}
	
	private Activity getActivity(){
		return this;
	}
}
