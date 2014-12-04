package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;

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
			@SuppressWarnings("unchecked")
			public void onDataChange(DataSnapshot snapshot){
				String id = snapshot.child("user info").child("id").getValue().toString();
				String name = snapshot.child("user info").child("name").getValue().toString();
				ArrayList<String> courses = (ArrayList<String>) snapshot.child("courses").getValue();
				HashMap<String, Object> buddies = (HashMap<String, Object>) snapshot.child("buddies").getValue();
				HashMap<String, Object> buddyRequests = (HashMap<String, Object>) snapshot.child("buddy requests").getValue();
								
				StudyBuddy.args = new Bundle();
				StudyBuddy.args.putParcelable(StudyBuddy.USER, new User(id, name, courses, buddies, buddyRequests));
				
				replaceFrameWith(((MainActivity)getActivity()).userProfile, StudyBuddy.args, true);
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
	
	protected void updateArguments(Bundle args){
		arguments = args;
	}
}
