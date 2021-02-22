package com.egorvaskon.paranoid.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.egorvaskon.paranoid.ui.fragment.KeysFragment;
import com.egorvaskon.paranoid.ui.fragment.SecretsFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 2;
    private static final String[] PAGE_TITLES = {"Secrets","Keys"};

    private final SecretsFragment mSecretsFragment;
    private final KeysFragment mKeysFragment;

    public MainFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        mSecretsFragment = new SecretsFragment();
        mKeysFragment = KeysFragment.newInstance(true,false);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return mSecretsFragment;
        else if(position == 1)
            return mKeysFragment;
        else
            throw new IllegalArgumentException("Position cannot be less than 0 or greater than 1");
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }
}
