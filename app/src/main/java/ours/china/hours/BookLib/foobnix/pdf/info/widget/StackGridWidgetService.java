package ours.china.hours.BookLib.foobnix.pdf.info.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.model.AppData;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.sys.ImageExtractor;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StackGridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(getApplicationContext(), intent);
    }

}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private List<FileMeta> recent;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        AppProfile.init(context);
        onDataSetChanged();
    }

    @Override
    public void onDataSetChanged() {
        if (AppState.get().isStarsInWidget) {
            recent = AppData.get().getAllFavoriteFiles();
        } else {
            recent = AppData.get().getAllRecent();
        }
        AppDB.removeClouds(recent);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return recent.size() > AppState.get().widgetItemsCount ? AppState.get().widgetItemsCount : recent.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_grid_image);

        if (recent.size() <= position) {
            return v;
        }

        FileMeta uri = recent.get(position);

        String url = IMG.toUrl(uri.getPath(), ImageExtractor.COVER_PAGE_WITH_EFFECT, IMG.getImageSize());
        Bitmap image = ImageLoader.getInstance().loadImageSync(url, IMG.displayCacheMemoryDisc);
        v.setImageViewBitmap(R.id.imageView1, image);
        // v.setContentDescription(R.id.imageView1, new File(uri.getPath()).getName());

        Bundle extras = new Bundle();
        extras.putInt("pos", position);
        extras.putParcelable("uri", Uri.fromFile(new File(uri.getPath())));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        v.setOnClickFillInIntent(R.id.imageView1, fillInIntent);

        return v;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
