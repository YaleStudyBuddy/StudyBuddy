package cpsc112.studybuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class GroupProfileFragment extends StudyBuddyFragment {
	private Group group;
	private ArrayList<String> groupMemberIDs, groupMemberNames, groupChat;
	private ListView groupMemberListView, groupChatListView;
	private Button groupChatButton;
	private EditText groupChatField;
	
	@Override
	public void onStart(){
		super.onStart();
		StudyBuddy.GROUPS_REF.child(group.getID()).child("group info").addChildEventListener(groupProfileInfoListener);
		StudyBuddy.GROUPS_REF.child(group.getID()).child("members").addChildEventListener(groupProfileMemberListener);
		StudyBuddy.GROUPS_REF.child(group.getID()).child("chat").addChildEventListener(groupProfileChatListener);
		System.out.println("group profile listeners added");
	}
	
	//Firebase listener for profile
	private ChildEventListener groupProfileInfoListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){
			group.getGroupInfo().put(snapshot.getKey(), snapshot.getValue());
		}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			group.getGroupInfo().put(snapshot.getKey(), snapshot.getKey());
		}
		public void onChildRemoved(DataSnapshot snapshot){
			group.getGroupInfo().remove(snapshot.getKey());
		}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	private ChildEventListener groupProfileMemberListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			group.getMembers().put(snapshot.getKey(), snapshot.getValue());
			if (!groupMemberIDs.contains(snapshot.getKey()) && !snapshot.getKey().equals(getCurrentUserID())){
				groupMemberIDs.add(snapshot.getKey());
				groupMemberNames.add(snapshot.getValue().toString());
				updateAdapter(groupMemberListView, groupMemberNames);
			}
		}
		public void onChildRemoved(DataSnapshot snapshot){
			group.getMembers().remove(snapshot.getKey());
			int index = groupMemberIDs.indexOf(snapshot.getKey());
			groupMemberIDs.remove(index);
			groupMemberNames.remove(index);
			updateAdapter(groupMemberListView, groupMemberNames);
		}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	private ChildEventListener groupProfileChatListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			int index = groupMemberIDs.indexOf(snapshot.child("id").getValue().toString());
			if (index > -1){
				groupChat.add(groupMemberNames.get(index) + ": " + snapshot.child("message").getValue().toString());
			} else {
				groupChat.add(getCurrentUserName() + ": " + snapshot.child("message").getValue().toString());
			}
			updateAdapter(groupChatListView, groupChat);
		}
		public void onChildRemoved(DataSnapshot snapshot){}
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_group_profile, container, false);
		group = arguments.getParcelable(StudyBuddy.GROUP);
//		final String groupID = group.getID();
		
		getActivity().setTitle(group.getName());
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		
		groupMemberIDs = new ArrayList<String>();
		groupMemberNames = new ArrayList<String>();
		groupMemberListView = (ListView) view.findViewById(R.id.group_members_list);
		groupMemberListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				displayUserProfile(groupMemberIDs.get(position));
			}
		});
		
		groupChat = new ArrayList<String>();
		groupChatListView = (ListView) view.findViewById(R.id.group_chat_list);
		
		groupChatField = (EditText) view.findViewById(R.id.group_chat_field);
		groupChatButton = (Button) view.findViewById(R.id.group_chat_button);
		groupChatButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				Map<String, Object> chatEntry = new HashMap<String, Object>();
				chatEntry.put("id", getCurrentUserID());
				chatEntry.put("message", groupChatField.getText().toString());
				StudyBuddy.GROUPS_REF.child(group.getID()).child("chat").push().setValue(chatEntry);
				System.out.println(group.getID());
			}
		});
		
		//viewpager to switch between views? yes
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.group_profile, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.leave_group_button:
				leaveGroup();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.GROUPS_REF.child(group.getID()).child("group info").removeEventListener(groupProfileInfoListener);
		StudyBuddy.GROUPS_REF.child(group.getID()).child("members").removeEventListener(groupProfileMemberListener);
		StudyBuddy.GROUPS_REF.child(group.getID()).child("chat").removeEventListener(groupProfileChatListener);
		System.out.println("group profile listeners removed");
	}
	
	//removes user from group
	protected void leaveGroup(){
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
		confirmationDialog.setTitle("Leave group");
		confirmationDialog.setMessage("Are you sure you want to leave " + group.getName());
		
		confirmationDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
//				group.getMembers().remove(getCurrentUserID());
				StudyBuddy.GROUPS_REF.child(group.getID()).child("members").child(getCurrentUserID()).removeValue();
				StudyBuddy.USERS_REF.child(getCurrentUserID()).child("groups").child(group.getID()).removeValue();
				back();
			}
		});
		
		confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		confirmationDialog.show();
	}
	
	protected void addMember(String id){		
		HashMap<String, Object> memberRequest = new HashMap<String, Object>();
		memberRequest.put(group.getID(), group.getName());
		StudyBuddy.USERS_REF.child(id).child("group requests").updateChildren(memberRequest);
	}
}
