package cpsc112.studybuddy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthStateListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DisplayUsersActivity extends Activity {
	
	private ProgressDialog loadingDialog;
	private ArrayList<String> users;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private Activity thisActivity = this;
	private String uID, courseFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		setContentView(R.layout.activity_display_users);
		
		uID = StudyBuddy.ROOT_REF.getAuth().getUid();
		
		StudyBuddy.ROOT_REF.addAuthStateListener(new AuthStateListener(){
			public void onAuthStateChanged(AuthData authData){
				if (authData != null){

				} else {
					thisActivity.finish();
				}
			}
		});
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		courseFilter = getIntent().getStringExtra(StudyBuddy.COURSE_FILTER);
		
		setTitle(courseFilter);
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setTitle("Loading");
		loadingDialog.setMessage("Searching for other users");
		
		StudyBuddy.ROOT_REF.child("courses").child(courseFilter).addValueEventListener(new ValueEventListener(){
			
			public void onDataChange(DataSnapshot snapshot){
				
				loadingDialog.show();
				
				users = new ArrayList<String>();
				Iterable<DataSnapshot> roster = snapshot.getChildren();
				
				for (DataSnapshot student : roster){
					if (!student.getKey().equals(uID)){
						users.add(student.getValue().toString());
					}
				}
				
				adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, users);
				listView = (ListView) findViewById(R.id.userList);
				listView.setAdapter(adapter);
				
				loadingDialog.hide();
				
			}
			
			public void onCancelled(FirebaseError firebaseError){
				
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_users, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return false;
		}
	}
	
	public void removeCourse(MenuItem item){		
		StudyBuddy.ROOT_REF.child("users").child(uID).child("courses").addListenerForSingleValueEvent(new ValueEventListener(){
			@SuppressWarnings("unchecked")
			public void onDataChange(DataSnapshot snapshot){
				ArrayList<String> courses = new ArrayList<String>(); 
				
				for (String course : (ArrayList<String>) snapshot.getValue()){
					if (!course.equals(courseFilter)){
						courses.add(course);
					}
				}
				
				StudyBuddy.ROOT_REF.child("users").child(uID).child("courses").setValue(courses);
				
				StudyBuddy.ROOT_REF.child("courses").child(courseFilter).child(uID).addListenerForSingleValueEvent(new ValueEventListener(){
					public void onDataChange(DataSnapshot snapshot){
						snapshot.getRef().removeValue();
					}
					
					public void onCancelled(FirebaseError firebaseError){}
				});
				
			}
			
			public void onCancelled(FirebaseError firebaseError){}
		});
		
		finish();
	}
	
	public void logoutUser(MenuItem item){
		StudyBuddy.ROOT_REF.unauth();
	}
}
