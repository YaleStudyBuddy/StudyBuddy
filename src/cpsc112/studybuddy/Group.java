package cpsc112.studybuddy;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {
	private HashMap<String, Object> groupInfo, members, chat;
	
	public Group(String id, String name, HashMap<String, Object> members, HashMap<String, Object> chat){
		this.groupInfo = new HashMap<String, Object>();
		this.groupInfo.put("id", id);
		this.groupInfo.put("name", name);
	
		if (members != null){
			this.members = members;
		} else {
			this.members = new HashMap<String, Object>();
		}
		
		if (chat != null){
			this.chat = chat;	
		} else {
			this.chat = new HashMap<String, Object>();
		}
		
	}
	
	//parcelable interface method signatures from http://sohailaziz05.blogspot.com/2012/04/passing-custom-objects-between-android.html
	@SuppressWarnings("unchecked")
	public Group(Parcel in){
		Bundle group = in.readBundle();
	
		this.groupInfo = (HashMap<String, Object>) group.getSerializable("group info");
		
		if ((HashMap<String, Object>) group.getSerializable("members") != null){
			this.members = (HashMap<String, Object>) group.getSerializable("members");
		} else {
			this.members = new HashMap<String, Object>();
		}
		
		if ((HashMap<String, Object>) group.getSerializable("chat") != null){
			this.chat = (HashMap<String, Object>) group.getSerializable("chat");	
		} else {
			this.chat = new HashMap<String, Object>();
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle user = new Bundle();
		user.putSerializable("group info", groupInfo);
		user.putSerializable("members", members);
		user.putSerializable("chat", chat);
		dest.writeBundle(user);
	}

	public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
		@Override
		public Group createFromParcel(Parcel source) {
		return new Group(source);  //using parcelable constructor
		}
		 
		@Override
		public Group[] newArray(int size) {
		return new Group[size];
		}
	};
	
	protected HashMap<String, Object> getGroupInfo(){
		return groupInfo;
	}
	
	protected String getID(){
		return groupInfo.get("id").toString();
	}
	
	protected String getName(){
		return groupInfo.get("name").toString();
	}
	
	protected void setName(String name){
		this.groupInfo.put("name", name);
	}
	
	protected HashMap<String, Object> getMembers(){
		return members;
	}
	
	protected HashMap<String, Object> getChat(){
		return chat;
	}
}
