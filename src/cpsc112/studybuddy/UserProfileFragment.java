package cpsc112.studybuddy;

import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class UserProfileFragment extends StudyBuddyFragment {
	private ListView profileCoursesListView, profileGroupsListView;
	private ArrayList<String> profileGroupsIDs, profileGroupsNames;
	private EditProfileFragment editProfileFragment = new EditProfileFragment();
	
	//Firebase listener for profile
	private ChildEventListener profileListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){
			user.getUserInfo().put(snapshot.getKey(), snapshot.getValue().toString());
		}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){}
		public void onChildRemoved(DataSnapshot snapshot){}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	//Firebase listener for courses
	private ChildEventListener profileCoursesListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			if (!user.getCourses().contains(snapshot.getValue().toString())){
				user.addCourse(snapshot.getValue().toString());
				updateAdapter(profileCoursesListView, user.getCourses());
			}
		}
		public void onChildRemoved(DataSnapshot snapshot){
			int index = user.getCourses().indexOf(snapshot.getValue().toString());
			if (index > -1){
				user.removeCourse(index);
				updateAdapter(profileCoursesListView, user.getCourses());
			}
		}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	//Firebase listener for groups
	private ChildEventListener profileGroupsListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			user.getGroups().put(snapshot.getKey(), snapshot.getValue());
			if (!profileGroupsIDs.contains(snapshot.getKey())){
				profileGroupsIDs.add(snapshot.getKey());
				profileGroupsNames.add(snapshot.getValue().toString());
				updateAdapter(profileGroupsListView, profileGroupsNames);
			}
		}
		
		public void onChildRemoved(DataSnapshot snapshot){
			user.getGroups().remove(snapshot.getKey());
			int index = profileGroupsIDs.indexOf(snapshot.getKey());
			profileGroupsIDs.remove(index);
			profileGroupsNames.remove(index);
			updateAdapter(profileGroupsListView, profileGroupsNames);
		}
		
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	
	@Override
	public void onStart(){
		super.onStart();
		StudyBuddy.USERS_REF.child(user.getID()).child("user info").addChildEventListener(profileListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("courses").addChildEventListener(profileCoursesListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("groups").addChildEventListener(profileGroupsListener);
		System.out.println("profile listeners added");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
		user = arguments.getParcelable(StudyBuddy.USER);
		
		if (user.getID().equals(getCurrentUserID())){
			user = getCurrentUser();
			getActivity().setTitle(StudyBuddy.MY_PROFILE);
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
		
		//populate UI with profile info
		TextView majorTextView = (TextView) view.findViewById(R.id.profile_major_label);
		if (user.getUserInfo().containsKey("major") && user.getUserInfo().get("major").toString() != null){
			majorTextView.setText("Major: " + user.getUserInfo().get("major").toString());
		}
		
		TextView classYearTextView = (TextView) view.findViewById(R.id.profile_class_year_label);
		if (user.getUserInfo().containsKey("class year") && user.getUserInfo().get("class year").toString() != null){
			classYearTextView.setText("Class Year: " + user.getUserInfo().get("class year").toString());
		}
		
		profileCoursesListView = (ListView) view.findViewById(R.id.profile_course_list);
		updateAdapter(profileCoursesListView, user.getCourses());
		
		profileGroupsNames = new ArrayList<String>();
		profileGroupsIDs = new ArrayList<String>();
		
		profileGroupsListView = (ListView) view.findViewById(R.id.profile_groups_list);
		updateAdapter(profileGroupsListView, profileGroupsNames);
		
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
				Bundle args = new Bundle();
				args.putParcelable(StudyBuddy.USER, user);
				replaceFrameWith(editProfileFragment, args, true);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.USERS_REF.child(user.getID()).child("user info").removeEventListener(profileListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("courses").removeEventListener(profileCoursesListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("groups").removeEventListener(profileGroupsListener);
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
