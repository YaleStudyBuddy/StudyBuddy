package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
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
		
		view.findViewById(R.id.create_account_button).setOnClickListener(this);
		view.findViewById(R.id.cancel_button).setOnClickListener(this);
		
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
			case R.id.create_account_button:
				createAccountDialog.show();
				
				EditText emailText = (EditText) getView().findViewById(R.id.new_email_text);
				EditText passwordText = (EditText) getView().findViewById(R.id.new_password_text);
				EditText nameText = (EditText) getView().findViewById(R.id.name_text);
				
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
						 switch (firebaseError.getCode()) {
				            case FirebaseError.EMAIL_TAKEN:
				                // handle a redundant sign-up
				            	showErrorDialog ("The new user account cannot be created because the specified email address is already in use.");
				                break;
				            case FirebaseError.INVALID_EMAIL:
				                // handle an invalid email 
				            	showErrorDialog ("The specified email is not a valid email.");
				                break;
				            default:
				                // handle other errors
				            	showErrorDialog ("Unknown error, please try again");
				                break;
				        }
					}
				});
				break;
			case R.id.cancel_button:
				getActivity().getFragmentManager().popBackStackImmediate();
				break;
		}
	}
	
	private void showErrorDialog(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {

		public AuthResultHandler(String provider) {}
		
		public void onAuthenticated(AuthData authData) {
			//save user data to firebase
//			String id = authData.getUid();
			HashMap<String, Object> userInfo = new HashMap<String, Object>();
			userInfo.put("id", authData.getUid());
			userInfo.put("name", name);
			User newUser = new User(userInfo, null, null, null, null, null);
			
			Map<String, Object> newUserMap = new HashMap<String, Object>();
			newUserMap.put("id", newUser.getID());
			newUserMap.put("name", newUser.getName());
			StudyBuddy.USERS_REF.child(authData.getUid()).child("user info").setValue(newUserMap);
			
			Intent intent = new Intent(getActivity(), MainActivity.class);
			Bundle args = new Bundle();
			args.putParcelable(StudyBuddy.USER, newUser);
			intent.putExtras(args);
			
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
