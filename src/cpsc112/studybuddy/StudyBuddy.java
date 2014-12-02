package cpsc112.studybuddy;

import com.firebase.client.Firebase;

public abstract class StudyBuddy {
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static String COURSE_FILTER = "cpsc112.studybuddy.COURSE_FILTER";
	protected final static String UID = "cpsc112.studybuddy.UID";
	protected final static String NAME = "cpsc112.studybuddy.NAME";
	protected final static String[] NAV_MENU = {"Home", "My Profile", "My Courses", "My Buddies"};
	
	protected static String currentUID;
	protected static String currentName;
}
