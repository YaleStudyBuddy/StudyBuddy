package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


public class MyCoursesFragment extends Fragment {
	private ArrayList<String> courses;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private Intent intent;
	private Context parentActivity;
	private String uID, name;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		
		setHasOptionsMenu(true);
		
		parentActivity = getActivity().getApplicationContext();
		
		View view = inflater.inflate(R.layout.fragment_my_courses, container, false);
		
		uID = StudyBuddy.ROOT_REF.getAuth().getUid();
//		uID = new String("simplelogin:3");
		
		StudyBuddy.ROOT_REF.child("users").child(uID).child("name").addListenerForSingleValueEvent(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				name = snapshot.getValue().toString();
			}
			public void onCancelled(FirebaseError firebaseError){}
		});

		intent = new Intent(parentActivity, DisplayUsersActivity.class);
		
		listView = (ListView) view.findViewById(R.id.course_list);
		listView.setOnItemClickListener(courseClickListener);
		
		StudyBuddy.ROOT_REF.child("users").child(uID).child("courses").addValueEventListener(new ValueEventListener(){
			public void onDataChange(DataSnapshot snapshot){
				courses = new ArrayList<String>();
				Iterable<DataSnapshot> courseList = snapshot.getChildren();
				
				for(DataSnapshot course : courseList){
					courses.add(course.getValue().toString());
				}
				
				adapter = new ArrayAdapter<String>(parentActivity, R.layout.custom_list_item, courses);
				listView.setAdapter(adapter);
			}
			
			public void onCancelled(FirebaseError firebaseError){}
		});
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.fragment_my_courses, menu);
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
	
	private OnItemClickListener courseClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			intent.putExtra(StudyBuddy.COURSE_FILTER, courses.get(position));
			startActivity(intent);
		}
	};
	
	public void addCourse (){
		
		AlertDialog.Builder inputDialog = new AlertDialog.Builder(getActivity());
		inputDialog.setTitle("Add Course");
		inputDialog.setMessage("Enter Course Number:");
		final EditText inputText = new EditText(getActivity());
		inputDialog.setView(inputText);
		
		inputDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String course = inputText.getText().toString();
				
				Map<String, Object> roster = new HashMap<String, Object>();
				roster.put(uID, name);
				StudyBuddy.ROOT_REF.child("courses").child(course).updateChildren(roster);
				
				adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_list_item, courses);
				listView.setAdapter(adapter);
				
				Map<String, Object> courseMap = new HashMap<String, Object>();
				courseMap.put("" + courses.size(), course);
				StudyBuddy.ROOT_REF.child("users").child(uID).child("courses").updateChildren(courseMap);
			}
		});
		
		inputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		inputDialog.show();
	}
}