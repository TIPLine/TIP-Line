package io.github.tipline.android_app;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
/*
layout and organization for news and tip tabs at top of page
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numTabs;

    public PagerAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {

        if (0 == position) {
            TipMenuFragment tab0 = new TipMenuFragment();
            return tab0;
        }
        if (1 == position) {
            NewsMenuFragment tab1 = new NewsMenuFragment();
            return tab1;
        }
        return null;
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}