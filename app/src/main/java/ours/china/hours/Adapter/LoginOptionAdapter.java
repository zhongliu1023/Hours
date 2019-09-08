package ours.china.hours.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoginOptionAdapter extends FragmentPagerAdapter {

    private final List<Fragment> listFragment = new ArrayList<>();
    private final List<String> listTitle = new ArrayList<>();


    public LoginOptionAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull @Override public Fragment getItem(int position) {
        return listFragment.get(position);
    }


    @Override public int getCount() {
        return listFragment.size();
    }


    public void addFragment(Fragment fragment, String title) {
        listFragment.add(fragment);
        listTitle.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position);
    }

}
