package cpsc112.studybuddy;

import java.util.ArrayList;

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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class DisplayRosterFragment extends StudyBuddyFragment {
	
	private ArrayList<String> rosterNames, rosterIDs;
	private ArrayAdapter<String> rosterAdapter;
	private ListView rosterListView;
	private String course;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		View view = inflater.inflate(R.layout.fragment_display_roster, container, false);
		course = arguments.getString(StudyBuddy.COURSE);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().setTitle(course);
		
		rosterIDs = new ArrayList<String>();
		rosterNames = new ArrayList<String>();
		
		rosterListView = (ListView) view.findViewById(R.id.userList);
		rosterListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				displayProfile(rosterIDs.get(position));
			}
		});

		StudyBuddy.ROOT_REF.child("courses").child(course).addChildEventListener(rosterListener);
		
		return view;
	}

	public void onStop(){
		super.onStop();
		StudyBuddy.ROOT_REF.child("courses").child(course).removeEventListener(rosterListener);
		System.out.println("roster listener removed");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.display_roster, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				back();
				return true;
			case R.id.remove_course:
				((MainActivity) getActivity()).myCourses.removeCourse(course);
				back();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected ChildEventListener rosterListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			if (!snapshot.getKey().equals(getCurrentUser().getID())){
				rosterIDs.add(snapshot.getKey());
				rosterNames.add(snapshot.getValue().toString());
				updateRosterAdapter();
			}
		}
		public void onChildRemoved(DataSnapshot snapshot){
			int index = rosterIDs.indexOf(snapshot.getKey());
			rosterIDs.remove(index);
			rosterNames.remove(index);
			updateRosterAdapter();
		}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};

	private void updateRosterAdapter(){
		if (rosterListView.getAdapter() != null){
			rosterAdapter.notifyDataSetChanged();
		} else {
			rosterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, rosterNames);
			rosterListView.setAdapter(rosterAdapter);
		}
	}
}
