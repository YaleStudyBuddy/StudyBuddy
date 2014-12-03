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
	private String courseFilter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		View view = inflater.inflate(R.layout.fragment_display_roster, container, false);
		courseFilter = getArguments().getString(StudyBuddy.COURSE);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().setTitle(courseFilter);
		
		listView = (ListView) view.findViewById(R.id.userList);
		listView.setOnItemClickListener(userClickListener);
		
		if (!MainActivity.rosterListeners.contains(courseFilter)){
			MainActivity.rosterListeners.add(courseFilter);
			StudyBuddy.ROOT_REF.child("courses").child(courseFilter).addValueEventListener(rosterListener);
		}
		
		return view;
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
				removeCourse();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void removeCourse(){		
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("courses").addListenerForSingleValueEvent(new ValueEventListener(){

			public void onDataChange(DataSnapshot snapshot){
				Iterable<DataSnapshot> courseList = snapshot.getChildren();
				ArrayList<String> newCourseList = new ArrayList<String>(); 
				
				for (DataSnapshot course : courseList){
					if (!course.getValue().toString().equals(courseFilter)){
						newCourseList.add(course.getValue().toString());
					}
				}
				
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("courses").setValue(newCourseList);
				
				StudyBuddy.ROOT_REF.child("courses").child(courseFilter).child(StudyBuddy.currentUID).addListenerForSingleValueEvent(new ValueEventListener(){
					public void onDataChange(DataSnapshot snapshot){
						StudyBuddy.ROOT_REF.child("courses").child(courseFilter).removeEventListener(rosterListener);
						snapshot.getRef().removeValue();
					}
					
					public void onCancelled(FirebaseError firebaseError){}
				});
				
			}
			
			public void onCancelled(FirebaseError firebaseError){}
		});
		
		back();
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
		}
		
		public void onCancelled(FirebaseError firebaseError){}
	};
}
