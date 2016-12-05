package io.github.tipline.android_app;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.github.tipline.android_app.async.PhoneNumbersUpdateAsyncTask;

public class MainPage extends AppCompatActivity {

    private static final int PERMISSION_ALL = 3333;
    private ListView drawerList;
    private ArrayAdapter<String> adapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String activityTitle;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new PhoneNumbersUpdateAsyncTask(this).execute(); //update the phone numbers from the internet

        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.addTab(tabLayout.newTab().setText("News"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount()); //adapter generates the pages that the viewpager shows
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        activityTitle = getTitle().toString();

        drawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE};

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }
    /*
    add items to side menu. This includes all tips
     */
    private void addDrawerItems() {
        String[] menuArray = { "Home", "Tip Call", "Text Tip", "Voice Tip", "Camera/Video Tip", "", "", "", "News", "Settings" };
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray);
        drawerList.setAdapter(adapter);
        drawerList.setDivider(null);


        //Handle button presses for items in the hamburger menu
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tester = Integer.toString(position);
                //Toast.makeText(MainPage.this, tester, Toast.LENGTH_SHORT).show();

                switch (position) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        startActivity(new Intent(MainPage.this, TipCall.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainPage.this, TextTip.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainPage.this, AudioTip.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainPage.this, CameraTip.class));
                        break;
                    case 8:
                        viewPager.setCurrentItem(1);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 9:
                        startActivity(new Intent(MainPage.this, Settings.class));
                        break;
                }
            }
        });
    }


    /*
    setup the side menu
     */
    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(activityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    /*
    create menu
     */
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    /*
    if selected execute the action
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionSettings) {
            Intent settingsIntent= new Intent(this, Settings.class);
            startActivity(settingsIntent);
            return true;
        }

        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}