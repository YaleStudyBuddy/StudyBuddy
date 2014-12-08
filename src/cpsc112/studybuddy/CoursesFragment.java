package cpsc112.studybuddy;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class CoursesFragment extends StudyBuddyFragment {
	private ListView courseListView;
	
	private ChildEventListener courseListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			if (!user.getCourses().contains(snapshot.getValue().toString())){
				user.addCourse(snapshot.getValue().toString());
				updateAdapter(courseListView, user.getCourses());
			}
		}
		public void onChildRemoved(DataSnapshot snapshot){
			int index = user.getCourses().indexOf(snapshot.getValue().toString());
			if (index > -1){
				user.removeCourse(index);
				updateAdapter(courseListView, user.getCourses());
			}
		}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	@Override
	public void onStart(){
		super.onStart();
		StudyBuddy.USERS_REF.child(user.getID()).child("courses").addChildEventListener(courseListener);
		System.out.println("course listener added");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_courses, container, false);
//		user = arguments.getParcelable(StudyBuddy.USER);
		
//		if (user.getID() == getCurrentUserID()){
			user = getCurrentUser();
			setHasOptionsMenu(true);
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
			getActivity().getActionBar().setHomeButtonEnabled(false);
			getActivity().setTitle(StudyBuddy.NAV_MENU[arguments.getInt(StudyBuddy.MENU_INDEX)]);
//		} else {
//			setHasOptionsMenu(false);
//			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//			getActivity().setTitle(user.getName() + "'s Courses");
//		}
		
		courseListView = (ListView) view.findViewById(R.id.course_list);
		courseListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle args = new Bundle();
				args.putString(StudyBuddy.COURSE, getCurrentUser().getCourses().get(position));
				replaceFrameWith(((MainActivity)getActivity()).rosterFragment, args, true);
			}
		});
		
		updateAdapter(courseListView, getCurrentUser().getCourses());
		
		return view;
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.USERS_REF.child(user.getID()).child("courses").removeEventListener(courseListener);
		System.out.println("course listener removed");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.courses, menu);
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
		inputDialog.setMessage("Enter course number:");
		final EditText inputText = new EditText(getActivity());
		inputText.setFocusable(true);
		inputText.requestFocus();
		inputDialog.setView(inputText);
		
		
		inputDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			@SuppressLint("DefaultLocale")
			public void onClick(DialogInterface dialog, int which) {

				String newCourse = inputText.getText().toString();
				
				//checks on user input go here
				newCourse = newCourse.toUpperCase();
				newCourse = newCourse.replaceAll("\\W", "");
				if (newCourse.length()!= 7)
				{
					showErrorDialog("Please enter a valid 7-character course number. (Ex. CPSC112, CHEM220, etc.)");
				} else { 				
					getCurrentUser().addCourse(newCourse);
					updateAdapter(courseListView, getCurrentUser().getCourses());
					
					Map<String, Object> roster = new HashMap<String, Object>();
					roster.put(user.getID(), user.getName());
					StudyBuddy.COURSES_REF.child(newCourse).updateChildren(roster);
					
					Map<String, Object> courseMap = new HashMap<String, Object>();
					courseMap.put(Integer.toString(getCurrentUser().getCourses().size() - 1), newCourse);
					StudyBuddy.USERS_REF.child(user.getID()).child("courses").updateChildren(courseMap);
				}
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
				
				StudyBuddy.USERS_REF.child(user.getID()).child("courses").child(String.valueOf(index)).removeValue();
				StudyBuddy.COURSES_REF.child(course).child(user.getID()).removeValue();
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
private void showErrorDialog(String message) {
       new AlertDialog.Builder(getActivity())
               .setTitle("Error")
               .setMessage(message)
               .setPositiveButton(android.R.string.ok, null)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .show();
    }
}
