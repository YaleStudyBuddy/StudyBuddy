package cpsc112.studybuddy;

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
import android.widget.EditText;
import android.widget.ListView;

public class MyCoursesFragment extends StudyBuddyFragment {
	private ListView courseListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		View view = inflater.inflate(R.layout.fragment_my_courses, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().setTitle(StudyBuddy.NAV_MENU[arguments.getInt(StudyBuddy.MENU_INDEX)]);
		
		courseListView = (ListView) view.findViewById(R.id.course_list);
		courseListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle args = new Bundle();
				args.putString(StudyBuddy.COURSE, getCurrentUser().getCourses().get(position));
				replaceFrameWith(((MainActivity)getActivity()).displayRoster, args, true);
			}
		});
		
		updateAdapter(courseListView, getCurrentUser().getCourses());
		
		return view;
	}
	
	@Override
	public void onStop(){
		super.onStop();
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
	
	//asks for course number from user, adds it to user's courses, and adds user to course roster
	protected void addCourse(){
		
		AlertDialog.Builder inputDialog = new AlertDialog.Builder(getActivity());
		inputDialog.setTitle("Add Course");
		inputDialog.setMessage("Enter Course Number:");
		final EditText inputText = new EditText(getActivity());
		inputText.setFocusable(true);
		inputDialog.setView(inputText);
		
		
		inputDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				String newCourse = inputText.getText().toString();
				getCurrentUser().addCourse(newCourse);
				updateAdapter(courseListView, getCurrentUser().getCourses());
				
				Map<String, Object> roster = new HashMap<String, Object>();
				roster.put(getCurrentUserID(), getCurrentUserName());
				StudyBuddy.COURSES_REF.child(newCourse).updateChildren(roster);
				
				Map<String, Object> courseMap = new HashMap<String, Object>();
				courseMap.put(Integer.toString(getCurrentUser().getCourses().size() - 1), newCourse);
				StudyBuddy.USERS_REF.child(getCurrentUserID()).child("courses").updateChildren(courseMap);
			}
		});
		
		inputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		inputDialog.show();
	}
	
	//removes course from user and removes user from course roster
	protected void removeCourse(final String course){
		
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
		confirmationDialog.setTitle("Remove Course");
		confirmationDialog.setMessage("Are you sure you want to remove " + course);
		
		confirmationDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int index = getCurrentUser().getCourses().indexOf(course);
				
				StudyBuddy.USERS_REF.child(getCurrentUserID()).child("courses").child(String.valueOf(index)).removeValue();
				StudyBuddy.COURSES_REF.child(course).child(getCurrentUserID()).removeValue();
				getCurrentUser().removeCourse(index);
				
				updateAdapter(courseListView, getCurrentUser().getCourses());
				back();
			}
		});
		
		confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		confirmationDialog.show();
	}
}