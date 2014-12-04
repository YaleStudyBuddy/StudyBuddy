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

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DisplayRosterFragment extends StudyBuddyFragment {
	
	private ArrayList<String> userNames, userIDs;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private String course;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		View view = inflater.inflate(R.layout.fragment_display_roster, container, false);
		course = getArguments().getString(StudyBuddy.COURSE);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().setTitle(course);
		
		listView = (ListView) view.findViewById(R.id.userList);
		listView.setOnItemClickListener(userClickListener);

		StudyBuddy.ROOT_REF.child("courses").child(course).addValueEventListener(rosterListener);
		
		return view;
	}

	@Override
	public void onPause(){
		super.onPause();
		StudyBuddy.ROOT_REF.child("courses").child(course).removeEventListener(rosterListener);
		System.out.println("listener removed from ROOT_REF.courses." + course);
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
				((MainActivity) getActivity()).myCourses.removeCourse(StudyBuddy.currentUser.getCourses().indexOf(course), getActivity());
				back();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private OnItemClickListener userClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			displayProfile(userIDs.get(position), userNames.get(position));
		}
	};

	protected ValueEventListener rosterListener = new ValueEventListener(){
		public void onDataChange(DataSnapshot snapshot){

			Iterable<DataSnapshot> roster = snapshot.getChildren();
			userNames = new ArrayList<String>();
			userIDs = new ArrayList<String>();
			
			for (DataSnapshot student : roster){
				if (!student.getKey().equals(StudyBuddy.currentUID)){
					userNames.add(student.getValue().toString());
					userIDs.add(student.getKey().toString());
				}
			}
			
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userNames);
			listView.setAdapter(adapter);
			
			System.out.println("listener added to ROOT_REF.courses." + course);
		}
		
		public void onCancelled(FirebaseError firebaseError){}
	};
}
