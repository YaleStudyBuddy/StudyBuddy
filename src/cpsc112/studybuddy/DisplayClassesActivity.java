package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DisplayClassesActivity extends Activity {
	
	private Firebase rootRef;
	private ArrayList<String> courses;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private Activity thisActivity = this;
	public final static String classFilter = "cpsc112.studybuddy.class";
	private String uID, name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		rootRef = new Firebase("https://scorching-heat-1838.firebaseio.com/");
		setContentView(R.layout.activity_display_classes);
		
		Intent intent = getIntent();
		this.uID = intent.getStringExtra(LoginActivity.UID);
		
		rootRef.child("users").child(uID).addListenerForSingleValueEvent(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				name = snapshot.child("name").getValue().toString();
			}
			
			public void onCancelled(FirebaseError firebaseError){
				
			}
		});

			 
		courses = new ArrayList<String>();
		
		listView = (ListView) findViewById(R.id.classList);
		listView.setOnItemClickListener(courseClickHandler);
	
		rootRef.child("users").child(uID).child("courses").addChildEventListener(new ChildEventListener() {
			public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
				courses.add(snapshot.getValue().toString());
				adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, courses);
				listView.setAdapter(adapter);
			}
			
			public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
//				onChildAdded(snapshot, previousChildKey);
			}
			
			public void onChildRemoved(DataSnapshot snapshot) {
//				onChildAdded(snapshot, null);
			}
			
			public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
//				onChildAdded(snapshot, previousChildKey);
			}
			
			public void onCancelled(FirebaseError firebaseError){
//				textView.setText("Could not retrieve other users =(");
			}
			
		});
	}
	
	private OnItemClickListener courseClickHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			Intent intent = new Intent(thisActivity, DisplayUsersActivity.class);
			
			intent.putExtra(classFilter, courses.get(position));
			intent.putExtra(LoginActivity.UID, uID);
			
			startActivity(intent);
		}
	};
	

	public void addClass (View view){
		EditText addClassText = (EditText) findViewById(R.id.addClassText);
		String course = addClassText.getText().toString();
		addClassText.setText("");
		
		Map<String, Object> roster = new HashMap<String, Object>();
		roster.put(uID, name);
		rootRef.child("classes").child(course).updateChildren(roster);
		
		adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, courses);
		listView.setAdapter(adapter);
		
		Map<String, Object> courseMap = new HashMap<String, Object>();
		courseMap.put("" + courses.size(), course);
		rootRef.child("users").child(uID).child("courses").updateChildren(courseMap);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_classes, menu);
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
}
