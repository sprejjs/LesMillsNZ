package com.spreys.lesmillsnz.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.spreys.lesmillsnz.fragments.NewsFragment;
import com.spreys.lesmillsnz.fragments.PreferencesFragment;
import com.spreys.lesmillsnz.fragments.TimetableFragment;
import com.spreys.lesmillsnz.utils.DisplayFragmentInterface;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 27/09/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    private Fragment mCurrentFragment;

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new TimetableFragment();
            case 1:
                return new NewsFragment();
            case 2:
                return new PreferencesFragment();
        }
        return null;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (mCurrentFragment != object) {
            mCurrentFragment = (Fragment) object;
            ((DisplayFragmentInterface)mCurrentFragment).fragmentDisplayed();
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
