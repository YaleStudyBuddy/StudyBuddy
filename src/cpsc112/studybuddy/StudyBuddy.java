package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

public class StudyBuddy {
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static String USER = "cpsc112.studybuddy.USER";
	protected final static String COURSE = "cpsc112.studybuddy.COURSE";
	protected final static String USER_INFO = "cpsc112.studybuddy.USER_INFO";
	protected final static String IS_BUDDY = "cpsc112.studybuddy.IS_BUDDY";
	protected final static String[] NAV_MENU = {"Home", "My Profile", "My Courses", "My Buddies"};
	
	@SuppressWarnings("unchecked")
	protected static User getUser(DataSnapshot snapshot){
		String id = snapshot.child("user info").child("id").getValue().toString();
		String name = snapshot.child("user info").child("name").getValue().toString();
		ArrayList<String> courses = (ArrayList<String>) snapshot.child("courses").getValue();
		HashMap<String, Object> buddies = (HashMap<String, Object>) snapshot.child("buddies").getValue();
		HashMap<String, Object> buddyRequests = (HashMap<String, Object>) snapshot.child("buddy requests").getValue();
		return new User(id, name, courses, buddies, buddyRequests);
	}
}
