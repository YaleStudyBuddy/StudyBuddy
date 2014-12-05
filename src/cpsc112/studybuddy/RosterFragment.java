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
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class RosterFragment extends StudyBuddyFragment {
	
	private ArrayList<String> rosterNames, rosterIDs;
	private ListView rosterListView;
	private String course;
	
	//Firebase listener for roster
	private ChildEventListener rosterListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			if (!snapshot.getKey().equals(getCurrentUserID()) && !rosterIDs.contains(snapshot.getKey())){
				rosterIDs.add(snapshot.getKey());
				rosterNames.add(snapshot.getValue().toString());
				updateAdapter(rosterListView, rosterNames);
			}
		}
		public void onChildRemoved(DataSnapshot snapshot){
			int index = rosterIDs.indexOf(snapshot.getKey());
			if (index > -1){
				rosterIDs.remove(index);
				rosterNames.remove(index);
				updateAdapter(rosterListView, rosterNames);
			}
		}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	@Override
	public void onStart(){
		super.onStart();
		//add roster listener
		StudyBuddy.COURSES_REF.child(course).addChildEventListener(rosterListener);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		View view = inflater.inflate(R.layout.fragment_roster, container, false);
		course = arguments.getString(StudyBuddy.COURSE);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().setTitle(course);
		
		rosterIDs = new ArrayList<String>();
		rosterNames = new ArrayList<String>();
		
		rosterListView = (ListView) view.findViewById(R.id.roster_list);
		rosterListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				displayProfile(rosterIDs.get(position));
			}
		});
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.roster, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.remove_course:
				((MainActivity) getActivity()).coursesFragment.removeCourse(course);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.COURSES_REF.child(course).removeEventListener(rosterListener);
		System.out.println("roster listener removed");
	}
}
