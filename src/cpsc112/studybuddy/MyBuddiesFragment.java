package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
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

public class MyBuddiesFragment extends Fragment {
	private ArrayList<String> buddyRequestsUIDs, buddyRequestsNames, buddyUIDs, buddyNames;
	private ArrayAdapter<String> requestsAdapter, acceptedAdapter;
	private ListView requestsListView, acceptedListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_my_buddies, container, false);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().setTitle(StudyBuddy.NAV_MENU[3]);
		
		//retrieve buddy requests
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddy requests").addValueEventListener(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				Iterable<DataSnapshot> buddyRequests = snapshot.getChildren();
				
				buddyRequestsUIDs = new ArrayList<String>();
				buddyRequestsNames = new ArrayList<String>();
				
				for (DataSnapshot buddyRequest : buddyRequests){
					buddyRequestsUIDs.add(buddyRequest.getKey().toString());
					buddyRequestsNames.add(buddyRequest.getValue().toString());
				}
				
				requestsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyRequestsNames);
				requestsListView.setAdapter(requestsAdapter);

			}
			public void onCancelled(FirebaseError firebaseError){}
		});
		
		//retrieve buddy list
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").addValueEventListener(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				Iterable<DataSnapshot> buddies = snapshot.getChildren();
				
				buddyUIDs = new ArrayList<String>();
				buddyNames = new ArrayList<String>();
				
				for (DataSnapshot buddy : buddies){
					buddyUIDs.add(buddy.getKey().toString());
					buddyNames.add(buddy.getValue().toString());
				}
				
				acceptedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyNames);
				acceptedListView.setAdapter(acceptedAdapter);
				
			}
			public void onCancelled(FirebaseError firebaseError){}
		});

		requestsListView = (ListView) view.findViewById(R.id.buddy_requests_list);
		requestsListView.setOnItemClickListener(new OnItemClickListener(){
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
		
		acceptedListView = (ListView) view.findViewById(R.id.buddies_list);
		acceptedListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				if (MainActivity.userProfile.isAdded()){
//					getActivity().getFragmentManager().beginTransaction().remove(MainActivity.userProfile);	
//				}
//				Bundle args = new Bundle();
//				args.putString(StudyBuddy.UID, buddyUIDs.get(position));
//				args.putString(StudyBuddy.NAME, buddyNames.get(position));
//				MainActivity.userProfile.setArguments(args);
//				getActivity().getFragmentManager().beginTransaction().replace(R.id.main_content_frame, MainActivity.userProfile).addToBackStack(null).commit();
				
				StudyBuddy.args = new Bundle();
				
				if (MainActivity.userProfile.isAdded()){
					getFragmentManager().beginTransaction().remove(MainActivity.userProfile);	
				}
				StudyBuddy.args.putString(StudyBuddy.UID, buddyUIDs.get(position));
				StudyBuddy.args.putString(StudyBuddy.NAME, buddyNames.get(position));
				
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").child(buddyUIDs.get(position)).addValueEventListener(new ValueEventListener(){
					public void onDataChange(DataSnapshot snapshot){
						StudyBuddy.args.putBoolean(StudyBuddy.IS_BUDDY, snapshot.getValue() != null);
					}
					public void onCancelled(FirebaseError firebaseError){}
				});
				
				MainActivity.userProfile.setArguments(StudyBuddy.args);
				getFragmentManager().beginTransaction().replace(R.id.main_content_frame, MainActivity.userProfile).addToBackStack(null).commit();
			}
		});
		
		return view;
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.my_buddies, menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
