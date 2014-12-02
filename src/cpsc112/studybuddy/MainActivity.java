package cpsc112.studybuddy;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends StudyBuddy {
	private DrawerLayout dLayout;
	private ListView dList;
	private ArrayAdapter<String> adapter;
	private FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fragmentManager = getFragmentManager();

		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		dList = (ListView) findViewById(R.id.left_drawer);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, StudyBuddy.NAV_MENU);
		dList.setAdapter(adapter);
		dList.setSelector(android.R.color.holo_blue_dark);
		dList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				dLayout.closeDrawers();
				Bundle args = new Bundle();
				Fragment fragment;
				
				switch (position){
					case 0:
						setTitle(getString(R.string.app_name));
						break;
					case 1:
						setTitle(StudyBuddy.NAV_MENU[position]);
						break;
					case 2:
						setTitle(StudyBuddy.NAV_MENU[position]);
						fragment = new MyCoursesFragment();
						fragment.setArguments(args);
						fragmentManager.beginTransaction().replace(R.id.main_content_frame, fragment).commit();
						break;
					default:
						break;
				}

			}
		});
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()){

			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
}
