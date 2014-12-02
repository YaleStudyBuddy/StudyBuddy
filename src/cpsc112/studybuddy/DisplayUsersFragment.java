package cpsc112.studybuddy;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DisplayUsersFragment extends StudyBuddy {
	
	private ArrayList<String> userNames, userIDs;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private Activity thisActivity = this;
	private String courseFilter;
	private RosterListener rosterListener = new RosterListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_users);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		courseFilter = getIntent().getStringExtra(StudyBuddy.COURSE_FILTER);
		
		setTitle(courseFilter);
		
		listView = (ListView) findViewById(R.id.userList);
		listView.setOnItemClickListener(userClickListener);
		
		StudyBuddy.ROOT_REF.child("courses").child(courseFilter).addValueEventListener(rosterListener);
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
//			case R.id.logout_button:
//				StudyBuddy.ROOT_REF.unauth();
//				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void removeCourse(){		
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("courses").addListenerForSingleValueEvent(new ValueEventListener(){

			public void onDataChange(DataSnapshot snapshot){
				Iterable<DataSnapshot> courseList = snapshot.getChildren();
				ArrayList<String> newCourseList = new ArrayList<String>(); 
				
				for (DataSnapshot course : courseList){
					if (!course.getValue().toString().equals(courseFilter)){
						newCourseList.add(course.getValue().toString());
					}
				}
				
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("courses").setValue(newCourseList);
				
				StudyBuddy.ROOT_REF.child("courses").child(courseFilter).child(StudyBuddy.currentUID).addListenerForSingleValueEvent(new ValueEventListener(){
					public void onDataChange(DataSnapshot snapshot){
						StudyBuddy.ROOT_REF.child("courses").child(courseFilter).removeEventListener(rosterListener);
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
	
	private class RosterListener implements ValueEventListener{
		public void onDataChange(DataSnapshot snapshot){

			Iterable<DataSnapshot> roster = snapshot.getChildren();
			userNames = new ArrayList<String>();
			userIDs = new ArrayList<String>();
			
			for (DataSnapshot student : roster){
				if (!student.getKey().equals(StudyBuddy.currentUID)){
					userNames.add(student.getValue().toString());
					userIDs.add(student.getKey().toString());
				}
			}
			
			adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, userNames);
			listView.setAdapter(adapter);
		}
		
		public void onCancelled(FirebaseError firebaseError){}
	}
}
