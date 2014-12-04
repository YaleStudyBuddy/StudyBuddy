package cpsc112.studybuddy;

import java.util.ArrayList;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class MyCoursesFragment extends StudyBuddyFragment {
	private ArrayList<String> courses;
	private ArrayAdapter<String> courseAdapter;
	private ListView courseListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		View view = inflater.inflate(R.layout.fragment_my_courses, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().setTitle(StudyBuddy.NAV_MENU[2]);
		
		courses = new ArrayList<String>();
		
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("courses").addChildEventListener(courseListener);
		System.out.println("course listener added");
		
		courseListView = (ListView) view.findViewById(R.id.course_list);
		courseListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				StudyBuddy.args = new Bundle();
				StudyBuddy.args.putString(StudyBuddy.COURSE, courses.get(position));
				((MainActivity)getActivity()).displayRoster.updateArguments(StudyBuddy.args);
				
				replaceFrameWith(((MainActivity)getActivity()).displayRoster, StudyBuddy.args, true);
			}
		});
		
		return view;
	}
	
	public void onStop(){
		super.onStop();
		StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("courses").removeEventListener(courseListener);
		System.out.println("course listener removed");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.my_courses, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.add_course_button:
				addCourse();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void addCourse (){
		
		AlertDialog.Builder inputDialog = new AlertDialog.Builder(getActivity());
		inputDialog.setTitle("Add Course");
		inputDialog.setMessage("Enter Course Number:");
		final EditText inputText = new EditText(getActivity());
		inputDialog.setView(inputText);
		
		inputDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				String newCourse = inputText.getText().toString();
				
				if (StudyBuddy.currentUser.getCourses() == null){
					StudyBuddy.currentUser.addCourse(newCourse);
					courseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, StudyBuddy.currentUser.getCourses());
					courseListView.setAdapter(courseAdapter);
				} else {
					StudyBuddy.currentUser.addCourse(newCourse);
					courseAdapter.notifyDataSetChanged();
				}
				
				Map<String, Object> roster = new HashMap<String, Object>();
				roster.put(StudyBuddy.currentUser.getID(), StudyBuddy.currentUser.getName());
				StudyBuddy.ROOT_REF.child("courses").child(newCourse).updateChildren(roster);
				
				Map<String, Object> courseMap = new HashMap<String, Object>();
				courseMap.put(Integer.toString(StudyBuddy.currentUser.getCourses().size() - 1), newCourse);
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("courses").updateChildren(courseMap);
			}
		});
		
		inputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		inputDialog.show();
	}
	
	protected void removeCourse(String course){
		
		final int position = StudyBuddy.currentUser.getCourses().indexOf(course);
		
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
		confirmationDialog.setTitle("Remove Course");
		confirmationDialog.setMessage("Are you sure you want to remove " + StudyBuddy.currentUser.getCourses().get(position));
		
		confirmationDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("courses").child(String.valueOf(position)).removeValue();
				StudyBuddy.ROOT_REF.child("courses").child(StudyBuddy.currentUser.getCourses().get(position)).child(StudyBuddy.currentUser.getID()).removeValue();
			}
		});

		confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		confirmationDialog.show();
	}
	
	protected ChildEventListener courseListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			courses.add(snapshot.getValue().toString());
			if (!StudyBuddy.currentUser.getCourses().contains(snapshot.getValue().toString())){
				StudyBuddy.currentUser.addCourse(snapshot.getValue().toString());	
			}
			updateCourseAdapter();
		}
		public void onChildRemoved(DataSnapshot snapshot){
			int index;
			index = courses.indexOf(snapshot.getValue().toString());
			courses.remove(index);
			index = StudyBuddy.currentUser.getCourses().indexOf(snapshot.getValue().toString());
			StudyBuddy.currentUser.removeCourse(index);
			updateCourseAdapter();
		}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	private void updateCourseAdapter(){
		if (courseListView.getAdapter() != null){
			courseAdapter.notifyDataSetChanged();
		} else {
			courseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, courses);
			courseListView.setAdapter(courseAdapter);
		}
	}

}