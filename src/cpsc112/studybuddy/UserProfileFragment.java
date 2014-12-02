package cpsc112.studybuddy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class UserProfileFragment extends Fragment {
	String userID, userName;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
		userID = getArguments().getString(StudyBuddy.UID, StudyBuddy.currentUID);
		userName = getArguments().getString(StudyBuddy.NAME, StudyBuddy.currentName);
		
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
		} else {
			inflater.inflate(R.menu.user_profile, menu);
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				getActivity().getFragmentManager().popBackStackImmediate();
				return true;
			case R.id.add_buddy_button:
				
				return true;
			case R.id.edit_profile_button:
			
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
}
