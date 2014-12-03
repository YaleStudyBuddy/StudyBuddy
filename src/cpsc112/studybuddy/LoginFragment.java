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
import com.firebase.client.ValueEventListener;
import com.firebase.client.Firebase.AuthStateListener;
import com.firebase.client.FirebaseError;

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
		
		view.findViewById(R.id.loginButton).setOnClickListener(this);
		view.findViewById(R.id.createAccountText).setOnClickListener(this);
		
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
			case R.id.loginButton:
				mAuthProgressDialog.show();
				
				EditText emailText = (EditText) getView().findViewById(R.id.emailText);
				EditText passwordText = (EditText) getView().findViewById(R.id.passwordText);
//				StudyBuddy.ROOT_REF.authWithPassword(emailText.getText().toString(), passwordText.getText().toString(), new AuthResultHandler("password"));
				StudyBuddy.ROOT_REF.authWithPassword("aysung@live.com", "hawkfire300", new AuthResultHandler("password"));
				break;
		
			case R.id.createAccountText:
				getActivity().getFragmentManager().beginTransaction().replace(R.id.auth_content_frame, AuthActivity.createUser).addToBackStack(null).commit();
				break;
		}
	}
	
	private class AuthResultHandler implements Firebase.AuthResultHandler {
		public AuthResultHandler(String provider) {}
		
		public void onAuthenticated(AuthData authData) {
			mAuthProgressDialog.hide();
			StudyBuddy.currentUID = authData.getUid();
			StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("name").addListenerForSingleValueEvent(new ValueEventListener(){
				public void onDataChange(DataSnapshot snapshot){
					StudyBuddy.currentName = snapshot.getValue().toString();
				}
				public void onCancelled(FirebaseError firebaseError){}
			});
			
			startActivity(new Intent(getActivity(), MainActivity.class));
		}
		
		public void onAuthenticationError(FirebaseError firebaseError) {
			mAuthProgressDialog.hide();
			showErrorDialog(firebaseError.toString());
		}	
		
	    private void showErrorDialog(String message) {
	        new AlertDialog.Builder(getActivity())
	                .setTitle("Error")
	                .setMessage(message)
	                .setPositiveButton(android.R.string.ok, null)
	                .setIcon(android.R.drawable.ic_dialog_alert)
	                .show();
	    }
	}
	
}
