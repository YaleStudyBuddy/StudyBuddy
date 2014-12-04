package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
//	private String name, id;
	private ArrayList<String> courses;
	private HashMap<String, Object> userInfo, buddies, buddyRequests;
	
	public User(String id, String name, ArrayList<String> courses, HashMap<String, Object> buddies, HashMap<String, Object> buddyRequests){
		this.userInfo = new HashMap<String, Object>();
		this.userInfo.put("id", id);
		this.userInfo.put("name", name);
		
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
	
	//parcelable interface method signatures from http://sohailaziz05.blogspot.com/2012/04/passing-custom-objects-between-android.html
	@SuppressWarnings("unchecked")
	public User(Parcel in){
		Bundle user = in.readBundle();
	
		this.userInfo = (HashMap<String, Object>) user.getSerializable("user info");
		
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
		user.putSerializable("user info", userInfo);
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
	
	protected HashMap<String, Object> getUserInfo(){
		return userInfo;
	}
	
	protected String getID(){
		return userInfo.get("id").toString();
	}
	
	protected String getName(){
		return userInfo.get("name").toString();
	}
	
	protected void setName(String name){
		this.userInfo.put("name", name);
	}
	
	protected ArrayList<String> getCourses(){
		return courses;
	}
	
	protected void addCourse(String course){
		this.courses.add(course);
	}
	
	protected void removeCourse(int index){
		this.courses.remove(index);
	}
	
	protected HashMap<String, Object> getBuddies(){
		return buddies;
	}
	
	protected HashMap<String, Object> getBuddyRequests(){
		return buddyRequests;
	}
}
