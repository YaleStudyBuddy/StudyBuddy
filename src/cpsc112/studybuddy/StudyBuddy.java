package cpsc112.studybuddy;

import com.firebase.client.Firebase;

public class StudyBuddy {
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static String COURSE_FILTER = "cpsc112.studybuddy.COURSE_FILTER";
	protected final static String UID = "cpsc112.studybuddy.UID";
	protected final static String[] NAV_MENU = {"Home", "My Profile", "My Courses"};
	
	protected static String currentUID = StudyBuddy.ROOT_REF.getAuth().getUid();
	protected static String currentUName;
	
	protected String checkCourseString(String course){
		//check there are no extra spaces in course number input
		//make string all caps
		return null;
	}
}
