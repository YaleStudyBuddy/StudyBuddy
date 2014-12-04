package cpsc112.studybuddy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public abstract class StudyBuddyFragment extends Fragment {
	
	protected Bundle arguments;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.activity_main, container, false);
	}
	
	protected void displayProfile(String id){
		StudyBuddy.ROOT_REF.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				Bundle args = new Bundle();
				args.putParcelable(StudyBuddy.USER, StudyBuddy.getUser(snapshot));
				replaceFrameWith(((MainActivity)getActivity()).userProfile, args, true);
			}
			public void onCancelled(FirebaseError firebaseError){}
		});
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onStop(){
		super.onStop();
	}
	
	protected void back(){
		getActivity().getFragmentManager().popBackStackImmediate();
	}
	
	protected void replaceFrameWith(StudyBuddyFragment fragment, Bundle args, boolean addToBackStack){
		fragment.updateArguments(args);
			
		if (addToBackStack){
			getActivity().getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).addToBackStack(null).commit();
		} else {
			getActivity().getFragmentManager().beginTransaction().replace(R.id.main_content_frame, fragment).commit();	
		}
	}
	
	protected User getCurrentUser(){
		return ((MainActivity) getActivity()).currentUser;
	}
	
	protected void updateArguments(Bundle args){
		arguments = args;
	}
}
