package cpsc112.studybuddy;

import java.util.HashMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class EditProfileFragment extends StudyBuddyFragment implements OnClickListener {
	private EditText nameText, classYearText, majorText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
		getActivity().setTitle("Edit Profile");
		setHasOptionsMenu(false);
		
		user = getCurrentUser();

		nameText = (EditText) view.findViewById(R.id.edit_name_text);
		classYearText = (EditText) view.findViewById(R.id.edit_class_year_text);
		majorText = (EditText) view.findViewById(R.id.edit_major_text);
		
		view.findViewById(R.id.profile_save_button).setOnClickListener(this);
		view.findViewById(R.id.profile_cancel_button).setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.edit_profile, menu);
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
			case R.id.profile_save_button:
				updateUserInfo();
				back();
				break;
			case R.id.profile_cancel_button:
				getActivity().getFragmentManager().popBackStackImmediate();
				break;
		}
	}
	
	private void updateUserInfo(){
		HashMap<String, Object> update = new HashMap<String, Object>();
		
		if (!nameText.getText().toString().isEmpty()){
			update.put("name", nameText.getText().toString());
		}
		
		if (classYearText.getText().toString() != null){
			update.put("class year", classYearText.getText().toString());
		}
		
		if (majorText.getText().toString() != null){
			update.put("major", majorText.getText().toString());
		}
		
		StudyBuddy.USERS_REF.child(user.getID()).child("user info").updateChildren(update);
		user.getUserInfo().putAll(update);
	}
}
