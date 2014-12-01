package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
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

public class DisplayUsersActivity extends Activity {
	
	private ArrayList<String> userNames, userIDs;
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
		
		listView = (ListView) findViewById(R.id.userList);
		listView.setOnItemClickListener(userClickListener);
		
		StudyBuddy.ROOT_REF.child("courses").child(courseFilter).addListenerForSingleValueEvent(new ValueEventListener(){
			
			public void onDataChange(DataSnapshot snapshot){

				@SuppressWarnings("unchecked")
				Map<String, Object> roster = (Map<String, Object>) snapshot.getValue();
				
				userNames = new ArrayList<String>();
				userIDs = new ArrayList<String>();
				
				for (Map.Entry<String, Object> student : roster.entrySet()){
					if (!student.getKey().equals(uID)){
						userNames.add(student.getValue().toString());
						userIDs.add(student.getKey().toString());
					}
				}
				
				adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, userNames);
				listView.setAdapter(adapter);
			}
			
			public void onCancelled(FirebaseError firebaseError){}
			
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
			case R.id.remove_course:
				removeCourse();
				return true;
			case R.id.logout_button:
				StudyBuddy.ROOT_REF.unauth();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void removeCourse(){		
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
	
	private OnItemClickListener userClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			intent.putExtra(StudyBuddy.UID, userIDs.get(position));
//			startActivity(intent);
		}
	};
}
