package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
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

public class UserProfileFragment extends Fragment {
	String userID, userName;
	boolean isBuddy;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
		userID = getArguments().getString(StudyBuddy.UID, StudyBuddy.currentUID);
		userName = getArguments().getString(StudyBuddy.NAME, StudyBuddy.currentName);
		
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").child(userID).addValueEventListener(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				isBuddy = (!snapshot.getKey().toString().equals(null));
			}
			public void onCancelled(FirebaseError firebaseError){}
		});
		
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
		if (userID.equals(StudyBuddy.currentUID)){
			inflater.inflate(R.menu.my_profile, menu);	
		} else if (isBuddy) {
			inflater.inflate(R.menu.user_profile_remove, menu);
		} else {
			inflater.inflate(R.menu.user_profile_add, menu);
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				getActivity().getFragmentManager().popBackStackImmediate();
				return true;
			case R.id.add_buddy_button:
				Map<String, Object> newBuddy = new HashMap<String, Object>();
				newBuddy.put(StudyBuddy.currentUID, StudyBuddy.currentName);
				StudyBuddy.ROOT_REF.child("users").child(userID).child("buddy requests").updateChildren(newBuddy);
				return true;
			case R.id.edit_profile_button:
			
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
}
