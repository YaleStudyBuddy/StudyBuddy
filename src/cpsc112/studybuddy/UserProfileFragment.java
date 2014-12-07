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
	
	@Override
	public void onStart(){
		super.onStart();
		StudyBuddy.USERS_REF.child(user.getID()).child("user info").addValueEventListener(profileListener);
		System.out.println("profile listener added");
	}
	
	//Firebase listener for profile
	private ValueEventListener profileListener = new ValueEventListener(){
		public void onDataChange(DataSnapshot snapshot){
			//handle changes in user info
		}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
		user = arguments.getParcelable(StudyBuddy.USER);
		
		if (user.getID().equals(getCurrentUserID())){
			user = getCurrentUser();
			getActivity().setTitle(StudyBuddy.NAV_MENU[arguments.getInt(StudyBuddy.MENU_INDEX)]);
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
			getActivity().getActionBar().setHomeButtonEnabled(false);
			setHasOptionsMenu(true);
		} else {
			getActivity().setTitle(user.getName());
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			if (user.getBuddyRequests().containsKey(getCurrentUserID())){
				setHasOptionsMenu(false);
			} else {
				setHasOptionsMenu(true);
			}
		}
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		if (user.getID().equals(getCurrentUserID())){
			inflater.inflate(R.menu.my_profile, menu);	
		} else {
			if (getCurrentUser().getBuddies().containsKey(user.getID())){
				inflater.inflate(R.menu.user_profile_remove, menu);
			} else if (!getCurrentUser().getBuddyRequests().containsKey(user.getID()) || !user.getBuddyRequests().containsKey(getCurrentUserID())){
				inflater.inflate(R.menu.user_profile_add, menu);
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.USERS_REF.child(user.getID()).child("user info").removeEventListener(profileListener);
		System.out.println("profile listener removed");
	}
	
	//sends buddy request
	protected void addBuddy(){
		Map<String, Object> newBuddy = new HashMap<String, Object>();
		newBuddy.put(getCurrentUserID(), getCurrentUserName());
		StudyBuddy.USERS_REF.child(user.getID()).child("buddy requests").updateChildren(newBuddy);
		setHasOptionsMenu(false);
	}
	
	//removes buddy
	protected void removeBuddy(){
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
		confirmationDialog.setTitle("Remove Buddy");
		confirmationDialog.setMessage("Are you sure you want to remove " + user.getName());
		
		confirmationDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//not handled by buddy listener - need to change locally
				getCurrentUser().getBuddies().remove(user.getID());
				user.getBuddies().remove(getCurrentUserID());
				StudyBuddy.USERS_REF.child(getCurrentUserID()).child("buddies").child(user.getID()).removeValue();
				StudyBuddy.USERS_REF.child(user.getID()).child("buddies").child(getCurrentUserID()).removeValue();
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
