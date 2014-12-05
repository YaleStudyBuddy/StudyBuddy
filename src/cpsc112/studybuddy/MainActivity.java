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
	
	protected CoursesFragment coursesFragment = new CoursesFragment();
	protected RosterFragment rosterFragment = new RosterFragment();
	protected ProfileFragment profileFragment = new ProfileFragment();
	protected BuddiesFragment buddiesFragment = new BuddiesFragment();
	
	protected User currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);
		
		currentUser = getIntent().getExtras().getParcelable(StudyBuddy.USER);

		StudyBuddy.ROOT_REF.addAuthStateListener(authListener);
		System.out.println("auth state listener added");
		
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
						replaceFrameWith(profileFragment, args, false);
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
					default:
						break;
				}

			}
		});	
		
		//starts activity with showing user profile
		Bundle args = new Bundle();
		args.putParcelable(StudyBuddy.USER, currentUser);
		args.putInt(StudyBuddy.MENU_INDEX, 0);
		replaceFrameWith(profileFragment, args, false);
		
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
				StudyBuddy.ROOT_REF.unauth();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	//replaces activity content frame with fragment
	protected void replaceFrameWith(StudyBuddyFragment fragment, Bundle args, boolean addToBackStack){
		User user = args.getParcelable(StudyBuddy.USER);
		
		if (!user.getID().equals(currentUser.getID()) || !fragment.isAdded()){
			fragment.updateArguments(args);	
			if (addToBackStack){
				getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).addToBackStack(null).commit();
			} else {
				getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).commit();	
			}
		}
	}
	
	//handles logout event
	private AuthStateListener authListener = new AuthStateListener(){
		public void onAuthStateChanged(AuthData authData){
			if (authData != null){

			} else {
				StudyBuddy.ROOT_REF.removeAuthStateListener(this);
				System.out.println("auth state listener removed");
				finish();
			}
		}
	};
}
