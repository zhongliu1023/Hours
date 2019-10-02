package ours.china.hours.Utility;

import android.content.Context;
import android.view.View;

import cn.xm.weidongjian.popuphelper.PopupWindowHelper;

public class MyPopupWindowHelper extends PopupWindowHelper {

    private Context mContext;
    AfterPopupWindowDismissActionListener listener;

    public MyPopupWindowHelper(View view) {
        super(view);
    }

    public MyPopupWindowHelper(Context context, View view) {
        super(view);
        this.mContext = context;
        listener = (AfterPopupWindowDismissActionListener) context;
    }

    @Override
    public void dismiss() {
        listener.afterDisissAction();
        super.dismiss();
    }

    public interface AfterPopupWindowDismissActionListener {
        public void afterDisissAction();
    }
}
