package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private String name, id;
	private ArrayList<String> courses;
	private HashMap<String, Object> buddies, buddyRequests;
	
	public User(String id, String name, ArrayList<String> courses, HashMap<String, Object> buddies, HashMap<String, Object> buddyRequests){
		this.id = id;
		this.name = name;
		
		if (courses != null){
			this.courses = courses;	
		} else {
			this.courses = new ArrayList<String>();
		}
		
		if (buddies != null){
			this.buddies = buddies;
		} else {
			this.buddies = new HashMap<String, Object>();
		}
		
		if (buddyRequests != null){
			this.buddyRequests = buddyRequests;	
		} else {
			this.buddyRequests = new HashMap<String, Object>();
		}
		
	}
	
//	public User(String ID, String name){
//		this.id = ID;
//		this.name = name;
//		
//		HashMap<String, Object> newUser = new HashMap<String, Object>();
//		newUser.put("id", ID);
//		newUser.put("name", name);
////		newUser.put("courses", courses);
////		newUser.put("buddies", buddies);
////		newUser.put("buddy requests", buddyRequests);
//		
////		StudyBuddy.ROOT_REF.child("users").child(UID).setValue(newUser);
//	}
	
	
	//parcelable interface method signatures from http://sohailaziz05.blogspot.com/2012/04/passing-custom-objects-between-android.html
	@SuppressWarnings("unchecked")
	public User(Parcel in){
		Bundle user = in.readBundle();
	
		this.id = user.getString("id");
		this.name = user.getString("name");
		
		if (user.getStringArrayList("courses") != null){
			this.courses = user.getStringArrayList("courses");	
		} else {
			this.courses = new ArrayList<String>();
		}
		
		if ((HashMap<String, Object>) user.getSerializable("buddies") != null){
			this.buddies = (HashMap<String, Object>) user.getSerializable("buddies");
		} else {
			this.buddies = new HashMap<String, Object>();
		}
		
		if ((HashMap<String, Object>) user.getSerializable("buddy requests") != null){
			this.buddyRequests = (HashMap<String, Object>) user.getSerializable("buddy requests");	
		} else {
			this.buddyRequests = new HashMap<String, Object>();
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle user = new Bundle();
		user.putString("id", id);
		user.putString("name", name);
		user.putStringArrayList("courses", courses);
		user.putSerializable("buddies", buddies);
		user.putSerializable("buddy requests", buddyRequests);
		dest.writeBundle(user);
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
		return new User(source);  //using parcelable constructor
		}
		 
		@Override
		public User[] newArray(int size) {
		return new User[size];
		}
	};
	
	protected String getID(){
		return id;
	}
	
	protected String getName(){
		return name;
	}
	
	protected void setName(String name){
		this.name = name;
	}
	
	protected ArrayList<String> getCourses(){
		return courses;
	}
	
	protected void addCourse(String course){
		this.courses.add(course);
	}
	
	protected void removeCourse(String course){
		this.courses.remove(course);
	}
	
	protected HashMap<String, Object> getBuddies(){
		return buddies;
	}
	
	protected HashMap<String, Object> getBuddyRequests(){
		return buddyRequests;
	}
}
