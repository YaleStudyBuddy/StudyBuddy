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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class GroupProfileFragment extends StudyBuddyFragment {
	private Group group;
	private String groupID, groupName;
	private ArrayList<String> groupMemberIDs, groupMemberNames, groupChat;
	private ListView groupMemberListView, groupChatListView;
	private Button groupChatButton;
	private EditText groupChatField;
	
	@Override
	public void onStart(){
		super.onStart();
		StudyBuddy.GROUPS_REF.child(groupID).child("group info").addChildEventListener(groupProfileInfoListener);
		StudyBuddy.GROUPS_REF.child(groupID).child("members").addChildEventListener(groupProfileMemberListener);
		StudyBuddy.GROUPS_REF.child(groupID).child("chat").addChildEventListener(groupProfileChatListener);
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
			if (!groupMemberIDs.contains(snapshot.getKey()) && !snapshot.getKey().equals(user.getID())){
				groupMemberIDs.add(snapshot.getKey());
				groupMemberNames.add(snapshot.getValue().toString());
				updateAdapter(groupMemberListView, groupMemberNames);
			}
		}
		public void onChildRemoved(DataSnapshot snapshot){
			group.getMembers().remove(snapshot.getKey());
			if (!snapshot.getKey().equals(user.getID())){
				int index = groupMemberIDs.indexOf(snapshot.getKey());
				groupMemberIDs.remove(index);
				groupMemberNames.remove(index);
				updateAdapter(groupMemberListView, groupMemberNames);
			}
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
		user = arguments.getParcelable(StudyBuddy.USER);
		
		groupID = group.getID();
		groupName = group.getName();
		
		System.out.println(groupName);
		System.out.println(groupID);
		System.out.println(getCurrentUserName());
		System.out.println(user.getID());
		
		getActivity().setTitle(groupName);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		
		groupMemberIDs = new ArrayList<String>();
		groupMemberNames = new ArrayList<String>();
		groupMemberListView = (ListView) view.findViewById(R.id.group_members_list);
//		groupMemberListView.setOnItemClickListener(new OnItemClickListener(){
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				displayUserProfile(groupMemberIDs.get(position));
//			}
//		});
		
		groupChat = new ArrayList<String>();
		groupChatListView = (ListView) view.findViewById(R.id.group_chat_list);
		
		groupChatField = (EditText) view.findViewById(R.id.group_chat_field);
		groupChatButton = (Button) view.findViewById(R.id.group_chat_button);
		groupChatButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				Map<String, Object> chatEntry = new HashMap<String, Object>();
				chatEntry.put("id", user.getID());
				chatEntry.put("message", groupChatField.getText().toString());
				groupChatField.setText("");
				StudyBuddy.GROUPS_REF.child(groupID).child("chat").push().setValue(chatEntry);
			}
		});
		
		//viewpager to switch between views?
		
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
			case R.id.add_member_button:
				inviteMember();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.GROUPS_REF.child(groupID).child("group info").removeEventListener(groupProfileInfoListener);
		StudyBuddy.GROUPS_REF.child(groupID).child("members").removeEventListener(groupProfileMemberListener);
		StudyBuddy.GROUPS_REF.child(groupID).child("chat").removeEventListener(groupProfileChatListener);
		System.out.println("group profile listeners removed");
	}
	
	//removes user from group
	protected void leaveGroup(){
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
		confirmationDialog.setTitle("Leave group");
		confirmationDialog.setMessage("Are you sure you want to leave " + group.getName());
		
		confirmationDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				System.out.println(user.getID());
				StudyBuddy.GROUPS_REF.child(groupID).child("members").child(user.getID()).removeValue();
				StudyBuddy.USERS_REF.child(user.getID()).child("groups").child(groupID).removeValue();
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
	
	protected void inviteMember(){		
		AlertDialog.Builder inputDialog = new AlertDialog.Builder(getActivity());
		inputDialog.setTitle("Invite Member");
		inputDialog.setMessage("Enter buddy name:");
		final EditText inputText = new EditText(getActivity());
		inputText.setFocusable(true);
		inputText.requestFocus();
		inputDialog.setView(inputText);
		
		inputDialog.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				String inviteName = inputText.getText().toString();
				int index;
				
				ArrayList<String> buddyIDs = new ArrayList<String>();				
				ArrayList<String> buddyNames = new ArrayList<String>();
				
				for (Map.Entry<String, Object> buddy : getCurrentUser().getBuddies().entrySet()){
					buddyIDs.add(buddy.getKey());
					buddyNames.add(buddy.getValue().toString());
				}
				
				//check input name exists/is buddy
				if (buddyNames.contains(inviteName)){
					index = buddyNames.indexOf(inviteName);
					HashMap<String, Object> groupInvite = new HashMap<String, Object>();
					groupInvite.put(groupID, groupName);
					System.out.println(groupID);
					System.out.println(groupName);
					StudyBuddy.USERS_REF.child(buddyIDs.get(index)).child("group invites").updateChildren(groupInvite);
				} else {
					System.out.println("not friends");
					
					dialog.cancel();
					AlertDialog.Builder errorDialog = new AlertDialog.Builder(getActivity());
					errorDialog.setTitle("Error");
					errorDialog.setMessage("User does not exist:");
					errorDialog.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
				}
			}
		});
		
		inputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		inputDialog.show();
	}
}
