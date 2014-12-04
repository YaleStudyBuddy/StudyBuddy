package cpsc112.studybuddy;

import android.os.Bundle;

import com.firebase.client.Firebase;

public class StudyBuddy {
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static String USER = "cpsc112.studybuddy.USER";
	protected final static String COURSE = "cpsc112.studybuddy.COURSE";
	protected final static String USER_INFO = "cpsc112.studybuddy.USER_INFO";
	protected final static String IS_BUDDY = "cpsc112.studybuddy.IS_BUDDY";
	protected final static String[] NAV_MENU = {"Home", "My Profile", "My Courses", "My Buddies"};
	
	protected static User currentUser;
	
	protected static Bundle args;
}
