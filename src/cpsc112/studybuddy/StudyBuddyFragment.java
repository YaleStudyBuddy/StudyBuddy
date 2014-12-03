package cpsc112.studybuddy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class StudyBuddyFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.activity_main, container, false);
	}
	
	protected void displayProfile(String userID, String userName){
		StudyBuddy.args = new Bundle();
		if (MainActivity.userProfile.isAdded()){
			getFragmentManager().beginTransaction().remove(MainActivity.userProfile).commit();	
		}
		StudyBuddy.args.putString(StudyBuddy.UID, userID);
		StudyBuddy.args.putString(StudyBuddy.NAME, userName);
		
		MainActivity.userProfile.setArguments(StudyBuddy.args);
		getFragmentManager().beginTransaction().replace(R.id.main_content_frame, MainActivity.userProfile).addToBackStack(null).commit();
	}
}
