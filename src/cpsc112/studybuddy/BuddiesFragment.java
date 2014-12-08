package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class BuddiesFragment extends StudyBuddyFragment {
	private ArrayList<String> buddyRequestsIDs, buddyRequestsNames,buddyIDs, buddyNames;
	private static ListView buddyRequestsListView, buddyListView;
	
	//Firebase listener for buddy requests
	private ChildEventListener buddyRequestsListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			user.getBuddyRequests().put(snapshot.getKey(), snapshot.getValue());
			if (!buddyRequestsIDs.contains(snapshot.getKey())){
				buddyRequestsIDs.add(snapshot.getKey());
				buddyRequestsNames.add(snapshot.getValue().toString());
			}
			updateAdapter(buddyRequestsListView, buddyRequestsNames);
		}
		
		public void onChildRemoved(DataSnapshot snapshot){
			user.getBuddyRequests().remove(snapshot.getKey());
			int index = buddyRequestsIDs.indexOf(snapshot.getKey());
			buddyRequestsIDs.remove(index);
			buddyRequestsNames.remove(index);
			updateAdapter(buddyRequestsListView, buddyRequestsNames);
		}
		
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	//Firebase listener for buddies
	private ChildEventListener buddiesListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			user.getBuddies().put(snapshot.getKey(), snapshot.getValue());
			if (!buddyIDs.contains(snapshot.getKey())){
				buddyIDs.add(snapshot.getKey());
				buddyNames.add(snapshot.getValue().toString());
			}
			updateAdapter(buddyListView, buddyNames);
		}
		
		public void onChildRemoved(DataSnapshot snapshot){
			user.getBuddies().remove(snapshot.getKey());
			int index = buddyIDs.indexOf(snapshot.getKey());
			buddyIDs.remove(index);
			buddyNames.remove(index);
			updateAdapter(buddyListView, buddyNames);
		}
		
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	@Override
	public void onStart(){
		super.onStart();
		//retrieve buddy requests and buddies
		StudyBuddy.USERS_REF.child(user.getID()).child("buddy requests").addChildEventListener(buddyRequestsListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("buddies").addChildEventListener(buddiesListener);
		System.out.println("buddy listeners added");
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_my_buddies, container, false);
//		user = arguments.getParcelable(StudyBuddy.USER);
		setHasOptionsMenu(false);
		
//		if (user.getID() == getCurrentUserID()){
			user = getCurrentUser();
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
			getActivity().getActionBar().setHomeButtonEnabled(false);
			getActivity().setTitle(StudyBuddy.NAV_MENU[arguments.getInt(StudyBuddy.MENU_INDEX)]);
//		} else {
//			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//			getActivity().setTitle(user.getName() + "'s Buddies");
//		}

		buddyRequestsIDs = new ArrayList<String>();
		buddyRequestsNames = new ArrayList<String>();
		buddyRequestsListView = (ListView) view.findViewById(R.id.buddy_requests_list);
		buddyRequestsListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> newBuddy;
				
				newBuddy = new HashMap<String, Object>();
				newBuddy.put(buddyRequestsIDs.get(position), buddyRequestsNames.get(position));
				StudyBuddy.USERS_REF.child(user.getID()).child("buddies").updateChildren(newBuddy);
				
				newBuddy = new HashMap<String, Object>();
				newBuddy.put(user.getID(), user.getName());
				StudyBuddy.USERS_REF.child(buddyRequestsIDs.get(position)).child("buddies").updateChildren(newBuddy);
				
				StudyBuddy.USERS_REF.child(user.getID()).child("buddy requests").child(buddyRequestsIDs.get(position)).removeValue();
			}
		});

		buddyIDs = new ArrayList<String>();
		buddyNames = new ArrayList<String>();		
		buddyListView = (ListView) view.findViewById(R.id.buddy_list);
		buddyListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				displayUserProfile(buddyIDs.get(position));
			}
		});
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.buddies, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.USERS_REF.child(user.getID()).child("buddy requests").removeEventListener(buddyRequestsListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("buddies").removeEventListener(buddiesListener);
		System.out.println("buddy listeners removed");
	}
}
