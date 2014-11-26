package cpsc112.studybuddy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class DisplayUsersActivity extends Activity {
	
	private Firebase rootRef;
	private AuthData authData;
	private TextView textView;
	private ProgressDialog loadingDialog;
	public String uID, classFilter;
	private ArrayList<String> users;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Firebase.setAndroidContext(this);
		rootRef = new Firebase("https://scorching-heat-1838.firebaseio.com/");
		setContentView(R.layout.activity_display_users);

		Intent intent = getIntent();
		this.uID = intent.getStringExtra(LoginActivity.UID);
		this.classFilter = intent.getStringExtra(DisplayClassesActivity.classFilter);
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setTitle("Loading");
		loadingDialog.setMessage("Searching for other users");
		loadingDialog.show();
		
		users = new ArrayList<String>();
	
		rootRef.child("classes").child(classFilter).addChildEventListener(new ChildEventListener() {
			public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
				if (!snapshot.getKey().equals(uID)){
					users.add(snapshot.getValue().toString());
				}
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
				textView.setText("Could not retrieve other users =(");
			}
			
		});
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
		listView = (ListView) findViewById(R.id.userList);
		listView.setAdapter(adapter);
		
		loadingDialog.hide();
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
		logout();
		startActivity(new Intent(this, LoginActivity.class));
	}
	
    private void logout() {
        if (this.authData != null) {
            /* logout of Firebase */
            rootRef.unauth();
            /* Update authenticated user and show login buttons */
//            setAuthenticatedUser(null);
        }
    }
}
