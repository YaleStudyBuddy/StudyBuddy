package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DisplayCoursesActivity extends Activity {
	
	private ArrayList<String> courses;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private Activity thisActivity = this;
	private Intent intent;
	private String uID, name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		setContentView(R.layout.activity_display_courses);
		
		uID = getIntent().getStringExtra(StudyBuddy.UID);
		name = getIntent().getStringExtra(StudyBuddy.USER_NAME);
		
		intent = new Intent(this, DisplayUsersActivity.class);

		listView = (ListView) findViewById(R.id.classList);
		listView.setOnItemClickListener(courseClickHandler);
		
		StudyBuddy.ROOT_REF.child("users").child(uID).child("courses").addValueEventListener(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				courses = new ArrayList<String>();
				Iterable<DataSnapshot> courseList = snapshot.getChildren();
				
				for(DataSnapshot course : courseList){
					courses.add(course.getValue().toString());
				}
				
				adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, courses);
				listView.setAdapter(adapter);
			}
			
			public void onCancelled(FirebaseError firebaseError){
				
			}
		});
	}
	
	private OnItemClickListener courseClickHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			Intent intent = new Intent(thisActivity, DisplayUsersActivity.class);
			intent.putExtra(StudyBuddy.COURSE_FILTER, courses.get(position));
			intent.putExtra(StudyBuddy.UID, uID);
			startActivity(intent);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_courses, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void logoutUser(MenuItem item){
		StudyBuddy.ROOT_REF.unauth();
		startActivity(new Intent(this, LoginActivity.class));
	}
	
	public void addCourse (MenuItem item){
		
		AlertDialog.Builder inputDialog = new AlertDialog.Builder(thisActivity);
		inputDialog.setTitle("Add Course");
		inputDialog.setMessage("Enter course number");
		final EditText inputText = new EditText(thisActivity);
		inputDialog.setView(inputText);
		
		inputDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String course = inputText.getText().toString();
				
				Map<String, Object> roster = new HashMap<String, Object>();
				roster.put(uID, name);
				StudyBuddy.ROOT_REF.child("courses").child(course).updateChildren(roster);
				
				adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, courses);
				listView.setAdapter(adapter);
				
				Map<String, Object> courseMap = new HashMap<String, Object>();
				courseMap.put("" + courses.size(), course);
				StudyBuddy.ROOT_REF.child("users").child(uID).child("courses").updateChildren(courseMap);
			}
		});
		
		inputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		inputDialog.show();
	}
}
