package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

public class StudyBuddy {
	//Firebase references 
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static Firebase USERS_REF = ROOT_REF.child("users");
	protected final static Firebase COURSES_REF = ROOT_REF.child("courses");
	protected final static Firebase GROUPS_REF = ROOT_REF.child("groups");
	
	//fragment titles
	protected final static String MY_PROFILE = new String("My Profile");
	protected final static String MY_COURSES = new String("My Courses");
	protected final static String MY_BUDDIES = new String("My Buddies");
	protected final static String MY_GROUPS = new String("My Groups");
	
	//argument keys
	protected final static String USER = "cpsc112.studybuddy.USER";
	protected final static String COURSE = "cpsc112.studybuddy.COURSE";
	protected final static String GROUP = "cpsc112.studybuddy.GROUP";
	protected final static String MENU_INDEX = "cpsc112.studybuddy.MENU_INDEX";
	protected final static String[] NAV_MENU = {MY_PROFILE, MY_COURSES, MY_BUDDIES, MY_GROUPS};
	
	@SuppressWarnings("unchecked")
	protected static User getUser(DataSnapshot snapshot){
		HashMap<String, Object> userInfo = (HashMap<String, Object>) snapshot.child("user info").getValue();
		ArrayList<String> courses = (ArrayList<String>) snapshot.child("courses").getValue();
		HashMap<String, Object> buddies = (HashMap<String, Object>) snapshot.child("buddies").getValue();
		HashMap<String, Object> buddyRequests = (HashMap<String, Object>) snapshot.child("buddy requests").getValue();
		HashMap<String, Object> groups = (HashMap<String, Object>) snapshot.child("groups").getValue();
		HashMap<String, Object> groupRequests = (HashMap<String, Object>) snapshot.child("group requests").getValue();
		return new User(userInfo, courses, buddies, buddyRequests, groups, groupRequests);
	}
	
	@SuppressWarnings("unchecked")
	protected static Group getGroup(DataSnapshot snapshot){
		String id = snapshot.child("group info").child("id").getValue().toString();
		String name = snapshot.child("group info").child("name").getValue().toString();
		HashMap<String, Object> members = (HashMap<String, Object>) snapshot.child("members").getValue();
		HashMap<String, Object> chat = (HashMap<String, Object>) snapshot.child("chat").getValue();
		return new Group (id, name, members, chat);
	}
}
