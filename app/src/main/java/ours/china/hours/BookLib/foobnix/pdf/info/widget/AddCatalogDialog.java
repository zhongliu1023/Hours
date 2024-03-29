package ours.china.hours.BookLib.foobnix.pdf.info.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ours.china.hours.BookLib.foobnix.android.utils.AsyncTasks;
import ours.china.hours.BookLib.foobnix.android.utils.Keyboards;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.opds.Entry;
import ours.china.hours.BookLib.foobnix.opds.Feed;
import ours.china.hours.BookLib.foobnix.opds.Hrefs;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.nostra13.universalimageloader.core.ImageLoader;

public class AddCatalogDialog {

    public static final String OPDS = "opds";

    public static void showDialogLogin(final Activity a, final String url, final Runnable onRefresh) {
        LOG.d("showDialogLogin", url);
        AlertDialog.Builder builder = new AlertDialog.Builder(a);

        builder.setTitle(R.string.authentication_required);
        View dialog = LayoutInflater.from(a).inflate(R.layout.dialog_add_catalog_login, null, false);

        final SharedPreferences sp = a.getSharedPreferences(OPDS, Context.MODE_PRIVATE);

        final EditText login = (EditText) dialog.findViewById(R.id.login);

        final EditText password = (EditText) dialog.findViewById(R.id.password);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final String string = sp.getString(url, "");
        if (TxtUtils.isNotEmpty(string)) {
            try {
                final String[] split = string.split(TxtUtils.TTS_PAUSE);
                TempHolder.get().login = split[0];
                TempHolder.get().password = split[1];
                LOG.d("showDialogLogin GET", url);
            } catch (Exception e) {
                LOG.e(e);
            }
        }


        login.setText(TempHolder.get().login);
        password.setText(TempHolder.get().password);


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setView(dialog);
        final AlertDialog infoDialog = builder.create();
        infoDialog.show();
        infoDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String l = login.getText().toString().trim();
                String p = password.getText().toString().trim();
                if (TxtUtils.isEmpty(l) || TxtUtils.isEmpty(p)) {
                    Toast.makeText(a, R.string.incorrect_value, Toast.LENGTH_SHORT).show();
                    return;
                }

                TempHolder.get().login = l;
                TempHolder.get().password = p;
                sp.edit().putString(Uri.parse(url).getHost(), l + TxtUtils.TTS_PAUSE + p).commit();
                LOG.d("showDialogLogin SAVE", Uri.parse(url).getHost(),url);

                infoDialog.dismiss();
                onRefresh.run();
            }

        });

    }

    public static void showDialog(final Activity a, final Runnable onRefresh, final Entry e, final boolean validate) {

        AlertDialog.Builder builder = new AlertDialog.Builder(a);

        View dialog = LayoutInflater.from(a).inflate(R.layout.dialog_add_catalog, null, false);

        final EditText url = (EditText) dialog.findViewById(R.id.url);

        url.setText("http://");

        url.setSelection(url.getText().length());
        url.setEnabled(validate);

        final EditText name = (EditText) dialog.findViewById(R.id.name);
        final EditText description = (EditText) dialog.findViewById(R.id.description);
        final MyProgressBar MyProgressBar = (MyProgressBar) dialog.findViewById(R.id.MyProgressBarAdd);
        TintUtil.setDrawableTint(MyProgressBar.getIndeterminateDrawable().getCurrent(), TintUtil.color);
        final ImageView image = (ImageView) dialog.findViewById(R.id.image);
        final CheckBox addAsWEb = (CheckBox) dialog.findViewById(R.id.addAsWEb);
        addAsWEb.setVisibility(View.GONE);
        final String editAppState = e != null ? e.appState : null;
        if (editAppState != null) {
            String line[] = e.appState.replace(";", "").split(",");
            url.setText(line[0]);
            name.setText(line[1]);
            description.setText(line[2]);
            ImageLoader.getInstance().displayImage(line[3], image, IMG.displayCacheMemoryDisc);

            if (e.logo != null) {
                image.setTag(e.logo);
            }
        }

        MyProgressBar.setVisibility(View.GONE);
        image.setVisibility(View.GONE);

        builder.setView(dialog);
        builder.setTitle(R.string.add_catalog);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Keyboards.close(a);
            }
        });

        final AlertDialog infoDialog = builder.create();
        infoDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Keyboards.close(a);

            }
        });
        infoDialog.show();

        url.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                infoDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.add);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        infoDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            AsyncTask asyncTask;

            @Override
            public void onClick(View v) {
                final String feedUrl = url.getText().toString();
                if (infoDialog.getButton(AlertDialog.BUTTON_POSITIVE).getText().equals(a.getString(R.string.ok)) || addAsWEb.isChecked() || !validate) {
                    Entry entry = new Entry();
                    entry.setAppState(feedUrl, name.getText().toString(), description.getText().toString(), image.getTag().toString());
                    if (editAppState != null) {
                        AppState.get().myOPDSLinks = AppState.get().myOPDSLinks.replace(editAppState, "");
                    }
                    AppState.get().myOPDSLinks = entry.appState + AppState.get().myOPDSLinks;
                    onRefresh.run();
                    infoDialog.dismiss();
                    AppProfile.save(a);
                    return;

                }

                if (AsyncTasks.isRunning(asyncTask)) {
                    AsyncTasks.toastPleaseWait(a);
                    return;
                }

                asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        return ours.china.hours.BookLib.foobnix.opds.OPDS.getFeed(feedUrl, a);
                    }

                    @Override
                    protected void onPreExecute() {
                        MyProgressBar.setVisibility(View.VISIBLE);
                        image.setVisibility(View.GONE);
                    }



                    @Override
                    protected void onPostExecute(Object result) {
                        try {
                            MyProgressBar.setVisibility(View.GONE);
                            if (result == null || ((Feed) result).entries.isEmpty()) {
                                if (result != null && ((Feed) result).isNeedLoginPassword) {
                                    AddCatalogDialog.showDialogLogin(a, feedUrl, new Runnable() {

                                        @Override
                                        public void run() {

                                        }
                                    });

                                } else {
                                    Toast.makeText(a, a.getString(R.string.incorrect_value) + " OPDS " + feedUrl, Toast.LENGTH_LONG).show();
                                    infoDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.add);
                                    addAsWEb.setVisibility(View.VISIBLE);
                                    name.setText(feedUrl);
                                    image.setTag("assets://opds/web.png");
                                }
                                return;
                            }
                            Feed feed = (Feed) result;
                            name.setText(TxtUtils.nullToEmpty(feed.title));
                            if (TxtUtils.isNotEmpty(feed.subtitle)) {
                                description.setText(TxtUtils.nullToEmpty(feed.subtitle));
                            }

                            if (feed.icon != null) {
                                image.setVisibility(View.VISIBLE);
                                feed.icon = Hrefs.fixHref(feed.icon, feedUrl);
                                image.setTag(feed.icon);
                                ImageLoader.getInstance().displayImage(feed.icon, image, IMG.displayCacheMemoryDisc);
                            } else {
                                image.setTag("assets://opds/web.png");
                            }

                            infoDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.ok);
                        } catch (Exception e) {
                            LOG.e(e);
                        }

                    }
                }.execute();

            }
        });
    }

}
