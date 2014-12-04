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
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_buddies, container, false);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().setTitle(StudyBuddy.NAV_MENU[arguments.getInt(StudyBuddy.MENU_INDEX)]);

		//retrieve buddy requests and buddies
		StudyBuddy.USERS_REF.child(getCurrentUserID()).child("buddy requests").addChildEventListener(buddyRequestsListener);
		StudyBuddy.USERS_REF.child(getCurrentUserID()).child("buddies").addChildEventListener(buddiesListener);
		System.out.println("buddy listeners added");
		
		buddyRequestsIDs = new ArrayList<String>();
		buddyRequestsNames = new ArrayList<String>();
		buddyRequestsListView = (ListView) view.findViewById(R.id.buddy_requests_list);
		buddyRequestsListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> newBuddy;
				
				newBuddy = new HashMap<String, Object>();
				newBuddy.put(buddyRequestsIDs.get(position), buddyRequestsNames.get(position));
				StudyBuddy.USERS_REF.child(getCurrentUserID()).child("buddies").updateChildren(newBuddy);
				
				newBuddy = new HashMap<String, Object>();
				newBuddy.put(getCurrentUserID(), getCurrentUserName());
				StudyBuddy.USERS_REF.child(buddyRequestsIDs.get(position)).child("buddies").updateChildren(newBuddy);
				
				StudyBuddy.USERS_REF.child(getCurrentUserID()).child("buddy requests").child(buddyRequestsIDs.get(position)).removeValue();
			}
		});

		buddyIDs = new ArrayList<String>();
		buddyNames = new ArrayList<String>();		
		buddyListView = (ListView) view.findViewById(R.id.buddy_list);
		buddyListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				displayProfile(buddyIDs.get(position));
			}
		});
		
		return view;
	}
	
	@Override
	public void onStop(){
		super.onStop();
		//removes Firebase listeners upon closing fragment or activity
		StudyBuddy.USERS_REF.child(getCurrentUserID()).child("buddy requests").removeEventListener(buddyRequestsListener);
		StudyBuddy.USERS_REF.child(getCurrentUserID()).child("buddies").removeEventListener(buddiesListener);
		System.out.println("buddy listeners removed");
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
	
	//Firebase listener for buddy requests
	protected ChildEventListener buddyRequestsListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			getCurrentUser().getBuddyRequests().put(snapshot.getKey(), snapshot.getValue());
			buddyRequestsIDs.add(snapshot.getKey());
			buddyRequestsNames.add(snapshot.getValue().toString());
			updateAdapter(buddyRequestsListView, buddyRequestsNames);
		}
		
		public void onChildRemoved(DataSnapshot snapshot){
			getCurrentUser().getBuddyRequests().remove(snapshot.getKey());
			int index = buddyRequestsIDs.indexOf(snapshot.getKey());
			buddyRequestsIDs.remove(index);
			buddyRequestsNames.remove(index);
			updateAdapter(buddyRequestsListView, buddyRequestsNames);
		}
		
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	//Firebase listener for buddies
	protected ChildEventListener buddiesListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			getCurrentUser().getBuddies().put(snapshot.getKey(), snapshot.getValue());
			buddyIDs.add(snapshot.getKey());
			buddyNames.add(snapshot.getValue().toString());
			updateAdapter(buddyListView, buddyNames);
		}
		
		public void onChildRemoved(DataSnapshot snapshot){
			getCurrentUser().getBuddies().remove(snapshot.getKey());
			int index = buddyIDs.indexOf(snapshot.getKey());
			buddyIDs.remove(index);
			buddyNames.remove(index);
			updateAdapter(buddyListView, buddyNames);
		}
		
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
//	private void updateBuddyRequestsAdapter(){
//		if (buddyRequestsListView.getAdapter() != null){
//			buddyRequestsAdapter.notifyDataSetChanged();
//		} else {
//			buddyRequestsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyRequestsNames);
//			buddyRequestsListView.setAdapter(buddyRequestsAdapter);
//		}
//	}
//	
//	private void updateBuddyAdapter(){
//		if (buddyListView.getAdapter() != null){
//			buddyAdapter.notifyDataSetChanged();
//		} else {
//			buddyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyNames);
//			buddyListView.setAdapter(buddyAdapter);
//		}
//	}
}
