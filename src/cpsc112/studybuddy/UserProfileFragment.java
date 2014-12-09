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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class UserProfileFragment extends StudyBuddyFragment {
	private ListView profileCourseListView, profileGroupsListView;
	
	@Override
	public void onStart(){
		super.onStart();
		StudyBuddy.USERS_REF.child(user.getID()).child("user info").addChildEventListener(profileListener);
		System.out.println("profile listener added");
	}
	
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
//	private ChildEventListener courseListener = new ChildEventListener(){
//		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
//		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
//			if (!user.getCourses().contains(snapshot.getValue().toString())){
//				user.addCourse(snapshot.getValue().toString());
//				updateAdapter(courseListView, user.getCourses());
//			}
//		}
//		public void onChildRemoved(DataSnapshot snapshot){
//			int index = user.getCourses().indexOf(snapshot.getValue().toString());
//			if (index > -1){
//				user.removeCourse(index);
//				updateAdapter(courseListView, user.getCourses());
//			}
//		}
//		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
//		public void onCancelled(FirebaseError firebaseError){}
//	};
	
	//Firebase listener for groups
//	private ChildEventListener groupsListener = new ChildEventListener(){
//		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
//		
//		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
//			user.getGroups().put(snapshot.getKey(), snapshot.getValue());
//			if (!groupIDs.contains(snapshot.getKey())){
//				groupIDs.add(snapshot.getKey());
//				groupNames.add(snapshot.getValue().toString());
//				updateAdapter(groupListView, groupNames);
//			}
//		}
//		
//		public void onChildRemoved(DataSnapshot snapshot){
//			user.getGroups().remove(snapshot.getKey());
//			int index = groupIDs.indexOf(snapshot.getKey());
//			groupIDs.remove(index);
//			groupNames.remove(index);
//			updateAdapter(groupListView, groupNames);
//		}
//		
//		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
//		public void onCancelled(FirebaseError firebaseError){}
//	};
	
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
		if (user.getUserInfo().get("major").toString() != null){
			majorTextView.append(user.getUserInfo().get("major").toString());
		}
		
		TextView classYearTextView = (TextView) view.findViewById(R.id.profile_class_year_label);
		if (user.getUserInfo().get("class year").toString() != null){
			classYearTextView.append(user.getUserInfo().get("class year").toString());
		}
		
		profileCourseListView = (ListView) view.findViewById(R.id.profile_course_list);
		profileCourseListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle args = new Bundle();
				args.putString(StudyBuddy.COURSE, getCurrentUser().getCourses().get(position));
				replaceFrameWith(((MainActivity)getActivity()).rosterFragment, args, true);
			}
		});
		
		updateAdapter(profileCourseListView, getCurrentUser().getCourses());
		
		
		
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
			
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.USERS_REF.child(user.getID()).child("user info").removeEventListener(profileListener);
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
