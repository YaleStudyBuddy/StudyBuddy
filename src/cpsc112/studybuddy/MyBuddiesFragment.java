package cpsc112.studybuddy;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyBuddiesFragment extends Fragment {
	private ArrayList<String> buddyRequestsUIDs, buddyRequestsNames, buddyAcceptedUIDs, buddyAcceptedNames;
	private ArrayAdapter<String> requestsAdapter, acceptedAdapter;
	private ListView requestsListView, acceptedListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_my_buddies, container, false);
		setHasOptionsMenu(false);
		
		requestsListView = (ListView) view.findViewById(R.id.buddy_requests_list);
		requestsListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").child(buddyRequestsUIDs.get(position)).setValue(true);
				StudyBuddy.ROOT_REF.child("users").child(buddyRequestsUIDs.get(position)).child("buddies").child(StudyBuddy.currentUID).setValue(true);
			}
		});
		acceptedListView = (ListView) view.findViewById(R.id.accepted_buddies_list);
		acceptedListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").child(buddyAcceptedUIDs.get(position)).setValue(false);
				StudyBuddy.ROOT_REF.child("users").child(buddyAcceptedUIDs.get(position)).child("buddies").child(StudyBuddy.currentUID).setValue(false);
			}
		});
		
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("buddies").addValueEventListener(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				Iterable<DataSnapshot> buddies = snapshot.getChildren();
				
				buddyRequestsUIDs = new ArrayList<String>();
//				buddyRequestsNames = new ArrayList<String>();
				buddyAcceptedUIDs = new ArrayList<String>();
//				buddyAcceptedNames = new ArrayList<String>();
				
				for (DataSnapshot buddy : buddies){
					if ((Boolean) buddy.getValue()){
						buddyAcceptedUIDs.add(buddy.getKey());
					} else {
						buddyRequestsUIDs.add(buddy.getKey());
					}
				}
				
//				for(String buddyUID : buddyRequestsUIDs){
//					StudyBuddy.ROOT_REF.child("users").child(buddyUID).child("name").addListenerForSingleValueEvent(new ValueEventListener(){
//						public void onDataChange(DataSnapshot snapshot){
//							buddyRequestsNames.add(snapshot.getValue().toString());
//						}
//						public void onCancelled(FirebaseError firebaseError){}						
//					});
//				}
//				
//				for(String buddyUID : buddyAcceptedUIDs){
//					StudyBuddy.ROOT_REF.child("users").child(buddyUID).child("name").addListenerForSingleValueEvent(new ValueEventListener(){
//						public void onDataChange(DataSnapshot snapshot){
//							buddyAcceptedNames.add(snapshot.getValue().toString());
//						}
//						public void onCancelled(FirebaseError firebaseError){}						
//					});
//				}
				
				requestsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyRequestsUIDs);
				requestsListView.setAdapter(requestsAdapter);
				
				acceptedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buddyAcceptedUIDs);
				acceptedListView.setAdapter(acceptedAdapter);
				
			}
			public void onCancelled(FirebaseError firebaseError){}
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
