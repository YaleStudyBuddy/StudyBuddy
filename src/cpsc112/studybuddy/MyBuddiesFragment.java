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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyBuddiesFragment extends StudyBuddyFragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_my_buddies, container, false);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().setTitle(StudyBuddy.NAV_MENU[3]);
			
		//retrieve buddy requests
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddy requests").addValueEventListener(buddyRequestsListListener);
		
		//retrieve buddy list
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").addValueEventListener(buddyListListener);

		
		buddyRequestsListView = (ListView) view.findViewById(R.id.buddy_requests_list);
		buddyListView = (ListView) view.findViewById(R.id.buddy_list);
		
		buddyRequestsListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> newBuddy;
				
				newBuddy = new HashMap<String, Object>();
				newBuddy.put(buddyRequestsUIDs.get(position), buddyRequestsNames.get(position));
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").updateChildren(newBuddy);
				
				newBuddy = new HashMap<String, Object>();
				newBuddy.put(StudyBuddy.currentUID, StudyBuddy.currentName);
				StudyBuddy.ROOT_REF.child("users").child(buddyRequestsUIDs.get(position)).child("buddies").updateChildren(newBuddy);
				
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddy requests").child(buddyRequestsUIDs.get(position)).removeValue();
			}
		});
		
		buddyListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				displayProfile(buddyUIDs.get(position), buddyNames.get(position));
			}
		});
		
		return view;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddy requests").removeEventListener(buddyRequestsListListener);
		System.out.println("listener removed from ROOT_REF.users." + StudyBuddy.currentUID + ".buddy requests");

		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").removeEventListener(buddyListListener);
		System.out.println("listener removed from ROOT_REF.users." + StudyBuddy.currentUID + ".buddies");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.my_buddies, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	//firebase listeners
	private ArrayList<String> buddyRequestsUIDs, buddyRequestsNames;
	private ArrayAdapter<String> buddyRequestsAdapter;
	private static ListView buddyRequestsListView;
	protected ValueEventListener buddyRequestsListListener = new ValueEventListener(){
		public void onDataChange(DataSnapshot snapshot){
			Iterable<DataSnapshot> buddyRequests = snapshot.getChildren();
			
			buddyRequestsUIDs = new ArrayList<String>();
			buddyRequestsNames = new ArrayList<String>();
			
			for (DataSnapshot buddyRequest : buddyRequests){
				buddyRequestsUIDs.add(buddyRequest.getKey().toString());
				buddyRequestsNames.add(buddyRequest.getValue().toString());
			}
			
			buddyRequestsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyRequestsNames);
			buddyRequestsListView.setAdapter(buddyRequestsAdapter);
			
			System.out.println("listener added to ROOT_REF.users." + StudyBuddy.currentUID + ".buddy requests");
		}
		public void onCancelled(FirebaseError firebaseError){}
	};

	private ArrayList<String> buddyUIDs, buddyNames;
	private ArrayAdapter<String> buddyAdapter;
	private static ListView buddyListView;
	protected ValueEventListener buddyListListener = new ValueEventListener(){
		public void onDataChange(DataSnapshot snapshot){
			Iterable<DataSnapshot> buddies = snapshot.getChildren();
			
			buddyUIDs = new ArrayList<String>();
			buddyNames = new ArrayList<String>();
			
			for (DataSnapshot buddy : buddies){
				buddyUIDs.add(buddy.getKey().toString());
				buddyNames.add(buddy.getValue().toString());
			}
			
			buddyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyNames);
			buddyListView.setAdapter(buddyAdapter);
			
			System.out.println("listener added to ROOT_REF.users." + StudyBuddy.currentUID + ".buddies");
		}
		public void onCancelled(FirebaseError firebaseError){}
	};
}
