package ours.china.hours.CustomView;

/**
 * Created by Aladar-PC2 on 1/9/2018.
 */

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ViewPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> infos;

    public ViewPageAdapter(FragmentManager fm, List<Fragment> infos) {
        super(fm);
        this.infos = infos;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object objet) {
        super.destroyItem(container, position, objet);
    }


    @Override
    public Object instantiateItem(ViewGroup arg0, int position) {
        Fragment ff = (Fragment) super.instantiateItem(arg0, position);
        return ff;
    }

    @Override
    public Fragment getItem(int position) {

        return infos.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

}
