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
		StudyBuddy.args.putString(StudyBuddy.UID, userID);
		StudyBuddy.args.putString(StudyBuddy.NAME, userName);
		
		MainActivity.userProfile.setArguments(StudyBuddy.args);
		replaceFrameWith(MainActivity.userProfile, StudyBuddy.args, true);
	}
	
	protected void back(){
		getActivity().getFragmentManager().popBackStackImmediate();
	}
	
	protected void replaceFrameWith(Fragment fragment, Bundle args, boolean addToBackStack){
		if (fragment.isAdded()){
			getActivity().getFragmentManager().beginTransaction().remove(fragment).commit();
		}
		
		fragment.setArguments(args);
		
		if (addToBackStack){
			getActivity().getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).addToBackStack(null).commit();
		} else {
			getActivity().getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).commit();	
		}
	}
}
