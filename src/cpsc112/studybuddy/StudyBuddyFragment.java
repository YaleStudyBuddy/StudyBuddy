package cpsc112.studybuddy;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public abstract class StudyBuddyFragment extends Fragment {
	protected Bundle arguments;
	protected User user;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.activity_main, container, false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
	}
	
	//passes on User object to profile fragment
	protected void displayUserProfile(String id){
		StudyBuddy.ROOT_REF.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				Bundle args = new Bundle();
				args.putParcelable(StudyBuddy.USER, StudyBuddy.getUser(snapshot));
				replaceFrameWith(((MainActivity)getActivity()).userProfileFragment, args, true);
			}
			public void onCancelled(FirebaseError firebaseError){}
		});
	}
	
	protected void displayGroupProfile(String id){
		StudyBuddy.ROOT_REF.child("groups").child(id).addListenerForSingleValueEvent(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				Bundle args = new Bundle();
				args.putParcelable(StudyBuddy.GROUP, StudyBuddy.getGroup(snapshot));
				replaceFrameWith(((MainActivity)getActivity()).groupProfileFragment, args, true);
			}
			public void onCancelled(FirebaseError firebaseError){}
		});
	}
	
	protected void back(){
		getActivity().getFragmentManager().popBackStackImmediate();
	}
	
	//replaces activity content frame with fragment
	protected void replaceFrameWith(StudyBuddyFragment fragment, Bundle args, boolean addToBackStack){
		((MainActivity) getActivity()).replaceFrameWith(fragment, args, addToBackStack);
	}
	
	//returns current user as User object
	protected User getCurrentUser(){
		return ((MainActivity) getActivity()).currentUser;
	}
	
	//returns ID of current user
	protected String getCurrentUserID(){
		return getCurrentUser().getID();
	}
	
	//returns name of current user
	protected String getCurrentUserName(){
		return getCurrentUser().getName();
	}
	
	//updates fragment arguments
	protected void updateArguments(Bundle args){
		arguments.putAll(args);
	}
	
	//updates ArrayAdapter
	@SuppressWarnings("unchecked")
	protected void updateAdapter(ListView listView, ArrayList<String> array){
		if (listView.getAdapter() != null){
			((ArrayAdapter<String>)listView.getAdapter()).notifyDataSetChanged();
		} else {
			listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, array));
		}
	}
}
