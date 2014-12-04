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

public class ProfileFragment extends StudyBuddyFragment {
	private User user;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		
		user = arguments.getParcelable(StudyBuddy.USER);
		
		setHasOptionsMenu(true);
		
		if (user.getID().equals(getCurrentUserID())){
			getActivity().setTitle(StudyBuddy.NAV_MENU[arguments.getInt(StudyBuddy.MENU_INDEX)]);
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		} else {
			getActivity().setTitle(user.getName());
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		
		if (user.getID().toString().equals(getCurrentUserID())){
			inflater.inflate(R.menu.my_profile, menu);	
		} else {
			if (getCurrentUser().getBuddies().containsKey(user.getID().toString())){
				inflater.inflate(R.menu.profile_remove, menu);
			} else {
				inflater.inflate(R.menu.profile_add, menu);
			}
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
	
	//sends buddy request
	protected void addBuddy(){
		Map<String, Object> newBuddy = new HashMap<String, Object>();
		newBuddy.put(getCurrentUserID(), getCurrentUserName());
		StudyBuddy.USERS_REF.child(user.getID()).child("buddy requests").updateChildren(newBuddy);
	}
	
	//removes buddy
	protected void removeBuddy(){
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
		confirmationDialog.setTitle("Remove Buddy");
		confirmationDialog.setMessage("Are you sure you want to remove " + user.getName());
		
		confirmationDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
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
