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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class GroupsFragment extends StudyBuddyFragment {
	private ArrayList<String> groupInvitesIDs, groupInvitesNames,groupIDs, groupNames;
	private static ListView groupInvitesListView, groupListView;
	
	//Firebase listener for group requests
	private ChildEventListener groupInvitesListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			user.getGroupInvites().put(snapshot.getKey(), snapshot.getValue());
			if (!groupInvitesIDs.contains(snapshot.getKey())){
				groupInvitesIDs.add(snapshot.getKey());
				groupInvitesNames.add(snapshot.getValue().toString());
			}
			updateAdapter(groupInvitesListView, groupInvitesNames);
		}
		
		public void onChildRemoved(DataSnapshot snapshot){
			user.getGroupInvites().remove(snapshot.getKey());
			int index = groupInvitesIDs.indexOf(snapshot.getKey());
			groupInvitesIDs.remove(index);
			groupInvitesNames.remove(index);
			updateAdapter(groupInvitesListView, groupInvitesNames);
		}
		
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	//Firebase listener for groups
	private ChildEventListener groupsListener = new ChildEventListener(){
		public void onChildChanged(DataSnapshot snapshot, String previousChildKey){}
		
		public void onChildAdded(DataSnapshot snapshot, String previousChildKey){
			user.getGroups().put(snapshot.getKey(), snapshot.getValue());
			if (!groupIDs.contains(snapshot.getKey())){
				groupIDs.add(snapshot.getKey());
				groupNames.add(snapshot.getValue().toString());
				updateAdapter(groupListView, groupNames);
			}
		}
		
		public void onChildRemoved(DataSnapshot snapshot){
			user.getGroups().remove(snapshot.getKey());
			int index = groupIDs.indexOf(snapshot.getKey());
			groupIDs.remove(index);
			groupNames.remove(index);
			updateAdapter(groupListView, groupNames);
		}
		
		public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
		public void onCancelled(FirebaseError firebaseError){}
	};
	
	@Override
	public void onStart(){
		super.onStart();
		//retrieve group invites and groups
		StudyBuddy.USERS_REF.child(user.getID()).child("group invites").addChildEventListener(groupInvitesListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("groups").addChildEventListener(groupsListener);
		System.out.println("group listeners added");
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args){
		View view = inflater.inflate(R.layout.fragment_my_groups, container, false); //create fragment_groups
//		user = arguments.getParcelable(StudyBuddy.USER);
		setHasOptionsMenu(true);
		
//		if (user.getID() == getCurrentUserID()){
		user = getCurrentUser();
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().getActionBar().setHomeButtonEnabled(false);
		getActivity().setTitle(StudyBuddy.NAV_MENU[arguments.getInt(StudyBuddy.MENU_INDEX)]);
//		} else {
//			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//			getActivity().setTitle(user.getName() + "'s Groups");
//		}

		groupInvitesIDs = new ArrayList<String>();
		groupInvitesNames = new ArrayList<String>();
		groupInvitesListView = (ListView) view.findViewById(R.id.group_invites_list);
		groupInvitesListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> newGroup = new HashMap<String, Object>();
				newGroup.put(groupInvitesIDs.get(position), groupInvitesNames.get(position));
				StudyBuddy.USERS_REF.child(user.getID()).child("groups").updateChildren(newGroup);
				StudyBuddy.USERS_REF.child(user.getID()).child("group invites").child(groupInvitesIDs.get(position)).removeValue();
				
				Map<String, Object> newMember = new HashMap<String, Object>();
				newMember.put(user.getID(), user.getName());
				StudyBuddy.GROUPS_REF.child(groupInvitesIDs.get(position)).child("members").updateChildren(newMember);
			}
		});

		groupIDs = new ArrayList<String>();
		groupNames = new ArrayList<String>();		
		groupListView = (ListView) view.findViewById(R.id.group_list);
		groupListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				displayGroupProfile(groupIDs.get(position));
			}
		});
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.groups, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.new_group_button:
				addGroup();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStop(){
		super.onStop();
		StudyBuddy.USERS_REF.child(user.getID()).child("group invites").removeEventListener(groupInvitesListener);
		StudyBuddy.USERS_REF.child(user.getID()).child("groups").removeEventListener(groupsListener);
		System.out.println("group listeners removed");
	}
	
	private void addGroup(){
		AlertDialog.Builder inputDialog = new AlertDialog.Builder(getActivity());
		inputDialog.setTitle("New Group");
		inputDialog.setMessage("Enter group name:");
		final EditText inputText = new EditText(getActivity());
		inputText.setFocusable(true);
		inputDialog.setView(inputText);
		
		inputDialog.setPositiveButton("Create group", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String name = inputText.getText().toString();
				
				Firebase newGroupRef = StudyBuddy.GROUPS_REF.push();
				
				HashMap<String, Object> newGroup = new HashMap<String, Object>();
				
				HashMap<String, Object> members = new HashMap<String, Object>();
				members.put(getCurrentUserID(), getCurrentUserName());
				newGroup.put("members", members);
				
				Group group = new Group(newGroupRef.getKey(), name, members, null);

				HashMap<String, Object> groupInfo;
				
				groupInfo = new HashMap<String, Object>();
				groupInfo.put("id", group.getID());
				groupInfo.put("name", group.getName());
				newGroup.put("group info", groupInfo);
				
				groupInfo = new HashMap<String, Object>();
				groupInfo.put(group.getID(), group.getName());
				
				StudyBuddy.USERS_REF.child(getCurrentUserID()).child("groups").updateChildren(groupInfo);
				newGroupRef.setValue(newGroup);
				updateAdapter(groupListView, groupNames);
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
