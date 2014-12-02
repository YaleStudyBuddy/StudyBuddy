package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.FirebaseError;

public class CreateUserFragment extends Fragment
{
	private String email, password;
	private ProgressDialog createAccountDialog;
//	private Activity thisActivity = this;
	private Intent intent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_create_user, container, false);
		
//		super.onCreate(savedInstanceState);
//		Firebase.setAndroidContext(this);
//		setContentView(R.layout.activity_create_user);
		
		intent = new Intent(getActivity(), MainActivity.class);
		
		createAccountDialog = new ProgressDialog(getActivity());
		createAccountDialog.setTitle("Loading");
		createAccountDialog.setMessage("Creating new user account, please wait...");
		createAccountDialog.setCancelable(false);
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.create_user, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()){
		
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void createAccount(View view){
		
		createAccountDialog.show();
		
		EditText emailText = (EditText) view.findViewById(R.id.emailText);
		EditText passwordText = (EditText) view.findViewById(R.id.passwordText);
		EditText nameText = (EditText) view.findViewById(R.id.nameText);
		
		email = emailText.getText().toString();
		password = passwordText.getText().toString();
		StudyBuddy.currentUName = nameText.getText().toString();
		StudyBuddy.ROOT_REF.createUser(email, password, new ResultHandler(){
			public void onSuccess(){
				StudyBuddy.ROOT_REF.authWithPassword(email, password, new AuthResultHandler("password"));
			}
			public void onError(FirebaseError firebaseError){
				createAccountDialog.hide();
				ProgressDialog errorDialog = new ProgressDialog(getActivity());
				errorDialog.setTitle("Error creating user");
				errorDialog.setMessage(firebaseError.getDetails());
			}
		});
	}
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {

		public AuthResultHandler(String provider) {}
		
		public void onAuthenticated(AuthData authData) {
			createAccountDialog.hide();
			
			StudyBuddy.currentUID = authData.getUid();
			
			Map<String, Object> newUser = new HashMap<String, Object>();
			newUser.put("name", StudyBuddy.currentUName);
			StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).setValue(newUser);

			FragmentManager fragmentManager = getActivity().getFragmentManager();
			fragmentManager.popBackStackImmediate();
			
			startActivity(intent);
		}
		
		public void onAuthenticationError(FirebaseError firebaseError) {
			createAccountDialog.hide();
			ProgressDialog errorDialog = new ProgressDialog(getActivity());
			errorDialog.setTitle("Error authenticating user");
			errorDialog.setMessage(firebaseError.getDetails());
		}	
	}
	
}
