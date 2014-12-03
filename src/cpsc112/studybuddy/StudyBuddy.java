package cpsc112.studybuddy;

import android.os.Bundle;

import com.firebase.client.Firebase;

public abstract class StudyBuddy {
	protected final static Firebase ROOT_REF = new Firebase("https://scorching-heat-1838.firebaseio.com/");
	protected final static String COURSE = "cpsc112.studybuddy.COURSE";
	protected final static String UID = "cpsc112.studybuddy.UID";
	protected final static String NAME = "cpsc112.studybuddy.NAME";
	protected final static String IS_BUDDY = "cpsc112.studybuddy.IS_BUDDY";
	protected final static String[] NAV_MENU = {"Home", "My Profile", "My Courses", "My Buddies"};
	
	protected static String currentUID;
	protected static String currentName;
	
	protected static Bundle args;
}
