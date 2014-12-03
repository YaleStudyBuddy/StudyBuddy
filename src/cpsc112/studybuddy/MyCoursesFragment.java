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

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyCoursesFragment extends StudyBuddyFragment {
	private ArrayList<String> courses;
	private ArrayAdapter<String> adapter;
	private ListView listView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		View view = inflater.inflate(R.layout.fragment_my_courses, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().setTitle(StudyBuddy.NAV_MENU[2]);
		
		listView = (ListView) view.findViewById(R.id.course_list);
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StudyBuddy.args = new Bundle();
				StudyBuddy.args.putString(StudyBuddy.COURSE, courses.get(position));
				MainActivity.displayRoster.setArguments(StudyBuddy.args);
				
				replaceFrameWith(MainActivity.displayRoster, StudyBuddy.args, true);
			}
		});
		
		if (!MainActivity.courseListeners.contains(StudyBuddy.currentUID)){
			MainActivity.courseListeners.add(StudyBuddy.currentUID);
			StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("courses").addValueEventListener(courseListListener);	
		}
		
		return view;
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
				
				Map<String, Object> roster = new HashMap<String, Object>();
				roster.put(StudyBuddy.currentUID, StudyBuddy.currentName);
				StudyBuddy.ROOT_REF.child("courses").child(newCourse).updateChildren(roster);
				
				Map<String, Object> courseMap = new HashMap<String, Object>();
				courseMap.put(Integer.toString(courses.size()), newCourse);
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUID).child("courses").updateChildren(courseMap);
			}
		});
		
		inputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		inputDialog.show();
	}
	
	protected ValueEventListener courseListListener = new ValueEventListener(){
		public void onDataChange(DataSnapshot snapshot){
			Iterable<DataSnapshot> courseList = snapshot.getChildren();

			courses = new ArrayList<String>();
			
			for(DataSnapshot course : courseList){
				courses.add(course.getValue().toString());
			}
			
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, courses);
			listView.setAdapter(adapter);
		}
		
		public void onCancelled(FirebaseError firebaseError){}
	};
}