package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class UserProfileFragment extends StudyBuddyFragment {
	String userID, userName;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
		
		userID = getArguments().getString(StudyBuddy.UID);
		userName = getArguments().getString(StudyBuddy.NAME);
		
		setHasOptionsMenu(true);
		
		if (userID.equals(StudyBuddy.currentUID)){
			getActivity().setTitle(StudyBuddy.NAV_MENU[1]);
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		} else {
			getActivity().setTitle(userName);
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		final MenuInflater thisInflater = inflater;
		final Menu thisMenu = menu;
		
		if (userID == null){
			setHasOptionsMenu(false);
		} else if (userID.equals(StudyBuddy.currentUID)){
			thisInflater.inflate(R.menu.my_profile, thisMenu);	
		} else {
			StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").child(userID).addListenerForSingleValueEvent(new ValueEventListener(){
				public void onDataChange(DataSnapshot snapshot){
					if (snapshot.getValue() != null){
						thisInflater.inflate(R.menu.user_profile_remove, thisMenu);
					} else {
						thisInflater.inflate(R.menu.user_profile_add, thisMenu);
					}
				}
				public void onCancelled(FirebaseError firebaseError){}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				back();
				return true;
			case R.id.add_buddy_button:
				addBuddy();
				return true;
			case R.id.remove_buddy_button:
				removeBuddy();
				return true;
			case R.id.edit_profile_button:
			
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void addBuddy(){
		Map<String, Object> newBuddy = new HashMap<String, Object>();
		newBuddy.put(StudyBuddy.currentUID, StudyBuddy.currentName);
		StudyBuddy.ROOT_REF.child("users").child(userID).child("buddy requests").updateChildren(newBuddy);
	}
	
	protected void removeBuddy(){
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
		confirmationDialog.setTitle("Remove Buddy");
		confirmationDialog.setMessage("Are you sure you want to remove " + userName);
		
		confirmationDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").child(userID).removeValue();
				StudyBuddy.ROOT_REF.child("users").child(userID).child("buddies").child(StudyBuddy.currentUID).removeValue();

				back();
			}
		});
		
		confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		confirmationDialog.show();
	}
	
}
