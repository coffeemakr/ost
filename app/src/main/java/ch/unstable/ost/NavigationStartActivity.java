package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ch.unstable.ost.api.transport.model.ConnectionQuery;
import ch.unstable.ost.preference.SettingsActivity;
import ch.unstable.ost.theme.ThemedActivity;

public class NavigationStartActivity extends ThemedActivity
        implements BaseNavigationFragment.OnRouteSelectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_start);

        if(getSupportActionBar() == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar == null) throw new NullPointerException("toolbar is null");
            setSupportActionBar(toolbar);
        }


        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new StandardNavigationFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onRouteSelected(ConnectionQuery query) {
        Intent intent = new Intent(this, ConnectionListActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(ConnectionListActivity.EXTRA_QUERY, query);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
