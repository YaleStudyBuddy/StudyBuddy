package cpsc112.studybuddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

public class AuthActivity extends Activity {
	protected LoginFragment login = new LoginFragment();
	protected CreateUserFragment createUser = new CreateUserFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		Firebase.setAndroidContext(this);
		
		getFragmentManager().beginTransaction().replace(R.id.auth_content_frame, login).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
