package ours.china.hours.Fragment;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.squareup.otto.Subscribe;

import ours.china.hours.Common.ActivityResults.ActivityResultBus;
import ours.china.hours.Common.ActivityResults.ActivityResultEvent;
import ours.china.hours.Common.FragmentsBus.FragmentsBus;
import ours.china.hours.Common.FragmentsBus.FragmentsEvents;
import ours.china.hours.Common.FragmentsBus.FragmentsEventsKeys;

/**
 * Created by liujie on 3/6/18.
 */

public abstract class BaseFragment extends Fragment {
    protected Object mFragmentsSubscriber = new Object() {
        @Subscribe
        public void onFragmentsReceived(FragmentsEvents event) {
            String eventName = event.getEventName();
            if (eventName.equals(FragmentsEventsKeys.BACKPRESSED)){
                onBackPressed();
            }
        }
    };

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };

    abstract protected void onBackPressed();

    @Override
    public void onStart() {
        super.onStart();
        FragmentsBus.getInstance().register(mFragmentsSubscriber);
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentsBus.getInstance().unregister(mFragmentsSubscriber);
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
    }
}
