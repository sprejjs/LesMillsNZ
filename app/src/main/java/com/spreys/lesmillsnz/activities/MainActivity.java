package com.spreys.lesmillsnz.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.splunk.mint.Mint;
import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.adapters.TabsPagerAdapter;
import com.spreys.lesmillsnz.sync.LesMillsSyncAdapter;
import com.spreys.lesmillsnz.utils.TabManager;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, TabManager {

    public static FragmentManager fragmentManager;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Timetable", "News", "Preferences" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Mint configuration
        Mint.initAndStartSession(MainActivity.this, "dc336e30");


        fragmentManager = getSupportFragmentManager();

        //Tabs configuration
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3); //Keep all fragments in memory
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(fragmentManager);

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        LesMillsSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public void openTab(int position){
        viewPager.setCurrentItem(position);
        actionBar.setSelectedNavigationItem(position);
    }
}
