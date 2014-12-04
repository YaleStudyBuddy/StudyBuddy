package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
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

public class MyCoursesFragment extends StudyBuddyFragment {
//	private ArrayList<String> courses;
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
				StudyBuddy.args.putString(StudyBuddy.COURSE, StudyBuddy.currentUser.getCourses().get(position));
				((MainActivity)getActivity()).displayRoster.setArguments(StudyBuddy.args);
				
				replaceFrameWith(((MainActivity)getActivity()).displayRoster, StudyBuddy.args, true);
			}
		});
		
		if (StudyBuddy.currentUser.getCourses() != null){
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, StudyBuddy.currentUser.getCourses());
			listView.setAdapter(adapter);
		}
		
		return view;
	}
	
	@Override
	public void onPause(){
		super.onPause();
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
					adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, StudyBuddy.currentUser.getCourses());
					listView.setAdapter(adapter);
				} else {
					StudyBuddy.currentUser.addCourse(newCourse);
					adapter.notifyDataSetChanged();
				}
				
				Map<String, Object> roster = new HashMap<String, Object>();
				roster.put(StudyBuddy.currentUID, StudyBuddy.currentName);
				StudyBuddy.ROOT_REF.child("courses").child(newCourse).updateChildren(roster);
				
				Map<String, Object> courseMap = new HashMap<String, Object>();
				courseMap.put(Integer.toString(StudyBuddy.currentUser.getCourses().size() - 1), newCourse);
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
	
	public void removeCourse(int i, Activity activity){
		
		final int position = i;
		
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(activity);
		confirmationDialog.setTitle("Remove Course");
		confirmationDialog.setMessage("Are you sure you want to remove " + StudyBuddy.currentUser.getCourses().get(position));
		
		confirmationDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				StudyBuddy.currentUser.removeCourse(StudyBuddy.currentUser.getCourses().get(position));
				StudyBuddy.ROOT_REF.child("users").child(StudyBuddy.currentUser.getID()).child("courses").child(String.valueOf(position)).removeValue();
				StudyBuddy.ROOT_REF.child("courses").child(StudyBuddy.currentUser.getCourses().get(position)).child(StudyBuddy.currentUser.getID()).removeValue();
				
				if (StudyBuddy.currentUser.getCourses() == null){
					adapter.clear();
				} else {
					adapter.notifyDataSetChanged();	
				}
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