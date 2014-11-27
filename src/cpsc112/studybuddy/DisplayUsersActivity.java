package cpsc112.studybuddy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
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

		Intent intent = getIntent();
		this.uID = intent.getStringExtra(StudyBuddy.UID);
		this.courseFilter = intent.getStringExtra(StudyBuddy.COURSE_FILTER);
		
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
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void logoutUser (View view){
		StudyBuddy.ROOT_REF.unauth();
		startActivity(new Intent(this, LoginActivity.class));
	}
}
