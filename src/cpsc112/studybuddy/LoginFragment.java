package cpsc112.studybuddy;

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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthStateListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginFragment extends Fragment implements OnClickListener{
	private ProgressDialog mAuthProgressDialog;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		
		mAuthProgressDialog = new ProgressDialog(getActivity());
		mAuthProgressDialog.setTitle("Please wait");
		mAuthProgressDialog.setMessage("Authenticating with server...");
		mAuthProgressDialog.setCancelable(false);
		
		StudyBuddy.ROOT_REF.addAuthStateListener(new AuthStateListener() {
			public void onAuthStateChanged(AuthData authData) {
				mAuthProgressDialog.hide();
			}
		});
		
		view.findViewById(R.id.login_button).setOnClickListener(this);
		view.findViewById(R.id.create_account_text).setOnClickListener(this);
		
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
			case R.id.login_button:
				mAuthProgressDialog.show();
				
				EditText emailText = (EditText) getView().findViewById(R.id.email_text);
				EditText passwordText = (EditText) getView().findViewById(R.id.password_text);
				StudyBuddy.ROOT_REF.authWithPassword(emailText.getText().toString(), passwordText.getText().toString(), new AuthResultHandler("password"));
				break;
		
			case R.id.create_account_text:
				getActivity().getFragmentManager().beginTransaction().replace(R.id.auth_content_frame, ((AuthActivity) getActivity()).createUser).addToBackStack(null).commit();
				break;
		}
	}
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {
		public AuthResultHandler(String provider) {}
		
		public void onAuthenticated(AuthData authData) {
			StudyBuddy.USERS_REF.child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener(){
				public void onDataChange(DataSnapshot snapshot){
					Intent intent = new Intent(getActivity(), MainActivity.class);
					Bundle args = new Bundle();
					args.putParcelable(StudyBuddy.USER, StudyBuddy.getUser(snapshot));
					intent.putExtras(args);
					
					startActivity(intent);
					mAuthProgressDialog.hide();
				}
				public void onCancelled(FirebaseError firebaseError){}
			});
		}
		
		public void onAuthenticationError(FirebaseError firebaseError) {
			mAuthProgressDialog.hide();
			switch (firebaseError.getCode()) {
	            case FirebaseError.USER_DOES_NOT_EXIST:
	                // handle a non existing user
	            	showErrorDialog ("Account does not exist.");
	                break;
	            case FirebaseError.INVALID_PASSWORD:
	                // handle an invalid password
	            	showErrorDialog ("Password is incorrect.");
	                break;
	            default:
	                // handle other errors
	            	showErrorDialog ("Unknown error, please try again");
	                break;
	        }
	    }
	};	
	
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

