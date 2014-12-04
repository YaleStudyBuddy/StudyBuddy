package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.FirebaseError;

public class CreateUserFragment extends Fragment implements OnClickListener{
	private String email, password, name;
	private ProgressDialog createAccountDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_create_user, container, false);
		
		createAccountDialog = new ProgressDialog(getActivity());
		createAccountDialog.setTitle("Loading");
		createAccountDialog.setMessage("Creating new user account, please wait...");
		createAccountDialog.setCancelable(false);
		
		view.findViewById(R.id.createAccountButton).setOnClickListener(this);
		view.findViewById(R.id.cancelButton).setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.create_user, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void onClick(View view){
		switch(view.getId()){
			case R.id.createAccountButton:
				createAccountDialog.show();
				
				EditText emailText = (EditText) getView().findViewById(R.id.emailText);
				EditText passwordText = (EditText) getView().findViewById(R.id.passwordText);
				EditText nameText = (EditText) getView().findViewById(R.id.nameText);
				
				email = emailText.getText().toString();
				password = passwordText.getText().toString();
				name = nameText.getText().toString();
				
				//create user account checks go here
				
				StudyBuddy.ROOT_REF.createUser(email, password, new ResultHandler(){
					public void onSuccess(){
						StudyBuddy.ROOT_REF.authWithPassword(email, password, new AuthResultHandler("password"));
					}
					public void onError(FirebaseError firebaseError){
						createAccountDialog.hide();
						ProgressDialog errorDialog = new ProgressDialog(getActivity());
						errorDialog.setTitle("Error creating user");
						errorDialog.setMessage(firebaseError.toString());
						errorDialog.show();
					}
				});
				break;
			case R.id.cancelButton:
				getActivity().getFragmentManager().popBackStackImmediate();
				break;
		}
	}
	
	protected static String checkCourseString(String course){
		//check there are no extra spaces in course number input
		//make string all caps
		return null;
	}	
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {

		public AuthResultHandler(String provider) {}
		
		public void onAuthenticated(AuthData authData) {
			//save user data to firebase
			String id = authData.getUid();
			StudyBuddy.currentUser = new User(id, name, null, null, null);
			
			Map<String, Object> newUser = new HashMap<String, Object>();
			newUser.put("id", StudyBuddy.currentUser.getID());
			newUser.put("name", StudyBuddy.currentUser.getName());
			StudyBuddy.ROOT_REF.child("users").child(id).setValue(newUser);
			
			Intent intent = new Intent(getActivity(), MainActivity.class);
			StudyBuddy.args = new Bundle();
			StudyBuddy.args.putParcelable(StudyBuddy.USER, StudyBuddy.currentUser);
			intent.putExtras(StudyBuddy.args);
			
			startActivity(intent);
			getActivity().getFragmentManager().popBackStackImmediate();
			createAccountDialog.hide();
		}
		
		public void onAuthenticationError(FirebaseError firebaseError) {
			createAccountDialog.hide();
			ProgressDialog errorDialog = new ProgressDialog(getActivity());
			errorDialog.setTitle("Error authenticating user");
			errorDialog.setMessage(firebaseError.toString());
			errorDialog.show();
		}	
	}
	
}
