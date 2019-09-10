package ours.china.hours.BookLib.foobnix.ui2.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import ours.china.hours.BookLib.foobnix.android.utils.Dips;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.ResultResponse;
import ours.china.hours.BookLib.foobnix.android.utils.StringDB;
import ours.china.hours.BookLib.foobnix.android.utils.StringResult;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.model.AppData;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.pdf.info.AppsConfig;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.FileMetaComparators;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.pdf.info.io.SearchCore;
import ours.china.hours.BookLib.foobnix.pdf.info.model.BookCSS;
import ours.china.hours.BookLib.foobnix.pdf.info.view.AlertDialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.MyPopupMenu;
import ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.ShareDialog;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.PopupHelper;
import ours.china.hours.BookLib.foobnix.pdf.search.view.AsyncProgressResultToastTask;
import ours.china.hours.BookLib.foobnix.pdf.search.view.AsyncProgressTask;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.foobnix.ui2.FileMetaCore;
import ours.china.hours.BookLib.foobnix.ui2.MainTabs2;
import ours.china.hours.BookLib.foobnix.ui2.adapter.DefaultListeners;
import ours.china.hours.BookLib.foobnix.ui2.adapter.FileMetaAdapter;
import ours.china.hours.BookLib.foobnix.ui2.fast.FastScrollRecyclerView;

import org.ebookdroid.BookType;
import org.ebookdroid.droids.FolderContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BrowseFragment2 extends UIFragment<FileMeta> {

    public static final Pair<Integer, Integer> PAIR = new Pair<Integer, Integer>(R.string.folders, R.drawable.glyphicons_145_folder_open);
    public static final String EXTRA_INIT_PATH = "EXTRA_PATH";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    public static int TYPE_DEFAULT = 0;
    public static int TYPE_SELECT_FOLDER = 1;
    public static int TYPE_SELECT_FILE = 2;
    public static int TYPE_SELECT_FILE_OR_FOLDER = 4;

    public static int TYPE_CREATE_FILE = 3;

    FileMetaAdapter searchAdapter;

    private LinearLayout paths;
    HorizontalScrollView scroller;
    private TextView stub;
    private ImageView onListGrid, starIcon, onSort, starIconDir, sortOrder;
    private EditText editPath;
    private View pathContainer, onClose, onAction, openAsBook;

    private int fragmentType = TYPE_DEFAULT;
    private String fragmentText = "";
    Map<String, Integer> rememberPos = new HashMap<String, Integer>();

    public BrowseFragment2() {
        super();
    }

    public static BrowseFragment2 newInstance(Bundle bundle) {
        BrowseFragment2 br = new BrowseFragment2();
        br.setArguments(bundle);
        return br;
    }

    @Override
    public Pair<Integer, Integer> getNameAndIconRes() {
        return PAIR;
    }

    private ResultResponse<String> onPositiveAction;
    private ResultResponse<String> onCloseAction;

    @Override
    public void onTintChanged() {
        TintUtil.setBackgroundFillColor(pathContainer, TintUtil.color);
        TintUtil.setBackgroundFillColor(onClose, TintUtil.color);
        TintUtil.setBackgroundFillColor(onAction, TintUtil.color);
        TintUtil.setTintImageWithAlpha(openAsbookImage, getActivity() instanceof MainTabs2 ? TintUtil.getColorInDayNighth() : TintUtil.getColorInDayNighthBook());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse2, container, false);

        Bundle arguments = getArguments();

        pathContainer = view.findViewById(R.id.pathContainer);
        View onCloseActionPaner = view.findViewById(R.id.onCloseActionPaner);
        onClose = view.findViewById(R.id.onClose);
        openAsBook = view.findViewById(R.id.openAsBook);
        openAsbookImage = (ImageView) view.findViewById(R.id.openAsbookImage);
        TintUtil.setTintImageWithAlpha(openAsbookImage, getActivity() instanceof MainTabs2 ? TintUtil.getColorInDayNighth() : TintUtil.getColorInDayNighthBook());

        starIcon = (ImageView) view.findViewById(R.id.starIcon);
        starIconDir = (ImageView) view.findViewById(R.id.starIconDir);
        onSort = (ImageView) view.findViewById(R.id.onSort);
        sortOrder = (ImageView) view.findViewById(R.id.sortOrder);


        sortOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AppState.get().sortByReverse = !AppState.get().sortByReverse;
                onSort.setImageResource(AppState.get().sortByReverse ? R.drawable.glyphicons_410_sort_by_attributes_alt : R.drawable.glyphicons_409_sort_by_attributes);
                sortOrder.setImageResource(AppState.get().sortByReverse ? R.drawable.glyphicons_601_chevron_up : R.drawable.glyphicons_602_chevron_down);

                populate();

            }
        });

        sortOrder.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                AppState.get().isVisibleSorting = !AppState.get().isVisibleSorting;
                sortOrder.setVisibility(TxtUtils.visibleIf(AppState.get().isVisibleSorting));
                return true;
            }
        });

        onSort.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                AppState.get().isVisibleSorting = !AppState.get().isVisibleSorting;
                sortOrder.setVisibility(TxtUtils.visibleIf(AppState.get().isVisibleSorting));
                return true;
            }
        });
        sortOrder.setVisibility(TxtUtils.visibleIf(AppState.get().isVisibleSorting));

        openAsBook.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File lxml = FolderContext.genarateXML(searchAdapter.getItemsList(), displayPath, true);
                ExtUtils.showDocument(getActivity(), lxml);
            }
        });
        openAsBook.setVisibility(View.GONE);

        onSort.setImageResource(AppState.get().sortByReverse ? R.drawable.glyphicons_410_sort_by_attributes_alt : R.drawable.glyphicons_409_sort_by_attributes);
        sortOrder.setImageResource(AppState.get().sortByReverse ? R.drawable.glyphicons_601_chevron_up : R.drawable.glyphicons_602_chevron_down);

        onAction = view.findViewById(R.id.onAction);
        editPath = (EditText) view.findViewById(R.id.editPath);

        fragmentType = TYPE_DEFAULT;
        if (arguments != null) {
            fragmentType = arguments.getInt(EXTRA_TYPE, TYPE_DEFAULT);
            fragmentText = arguments.getString(EXTRA_TEXT);
            editPath.setText(fragmentText);
        }

        onClose.setOnClickListener(onCloseButtonActoin);
        onAction.setOnClickListener(onSelectAction);

        if (TYPE_DEFAULT == fragmentType) {
            editPath.setVisibility(View.GONE);
            onCloseActionPaner.setVisibility(View.GONE);
        }
        if (TYPE_SELECT_FOLDER == fragmentType) {
            editPath.setVisibility(View.VISIBLE);
            editPath.setEnabled(false);
            onCloseActionPaner.setVisibility(View.VISIBLE);
        }
        if (TYPE_SELECT_FILE == fragmentType) {
            editPath.setVisibility(View.VISIBLE);
            editPath.setEnabled(false);
            onCloseActionPaner.setVisibility(View.VISIBLE);
        }
        if (TYPE_CREATE_FILE == fragmentType) {
            editPath.setVisibility(View.VISIBLE);
            editPath.setEnabled(true);
            onCloseActionPaner.setVisibility(View.VISIBLE);
        }
        if (TYPE_SELECT_FILE_OR_FOLDER == fragmentType) {
            editPath.setVisibility(View.VISIBLE);
            editPath.setEnabled(false);
            onCloseActionPaner.setVisibility(View.VISIBLE);
        }

        View onBack = view.findViewById(R.id.onBack);
        recyclerView = (FastScrollRecyclerView) view.findViewById(R.id.recyclerView);


        paths = (LinearLayout) view.findViewById(R.id.paths);
        scroller = (HorizontalScrollView) view.findViewById(R.id.scroller);
        final View onHome = view.findViewById(R.id.onHome);
        onListGrid = (ImageView) view.findViewById(R.id.onListGrid);
        onListGrid.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupMenu(onListGrid);
            }
        });

        searchAdapter = new FileMetaAdapter();
        bindAdapter(searchAdapter);
        bindAuthorsSeriesAdapter(searchAdapter);

        onGridList();

        onHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                List<String> extFolders = new ArrayList<String>();

                extFolders = ExtUtils.getExternalStorageDirectories(getActivity());
                String sdPath = ExtUtils.getSDPath();
                if (TxtUtils.isNotEmpty(sdPath) && !extFolders.contains(sdPath)) {
                    extFolders.add(sdPath);
                }

                MyPopupMenu menu = new MyPopupMenu(getActivity(), onHome);

                menu.getMenu().add(R.string.internal_storage).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        displayAnyPath(Environment.getExternalStorageDirectory().getPath());
                        return false;
                    }
                }).setIcon(R.drawable.glyphicons_146_folder_sd1);

                for (final String info : extFolders) {

                    String name;

                    if (ExtUtils.isExteralSD(info)) {
                        name = ExtUtils.getExtSDDisplayName(getContext(), info);
                    } else {
                        name = new File(info).getName();
                    }

                    menu.getMenu().add(name).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            displayAnyPath(info);
                            return false;
                        }
                    }).setIcon(R.drawable.glyphicons_146_folder_sd1);

                }

                menu.getMenu().add(R.string.downloads).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        displayAnyPath(BookCSS.get().downlodsPath);
                        return false;
                    }
                }).setIcon(R.drawable.glyphicons_591_folder_heart);


                menu.getMenu().add(R.string.sync).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        displayAnyPath(AppProfile.SYNC_FOLDER_ROOT.getPath());
                        return false;
                    }
                }).setIcon(R.drawable.glyphicons_sync);

                // resources

                if (Build.VERSION.SDK_INT >= 21 && getActivity() instanceof MainTabs2) {
                    List<String> safs = StringDB.asList(BookCSS.get().pathSAF);

                    for (final String saf : safs) {
                        LOG.d("saf", saf);
                        if (TxtUtils.isEmpty(saf)) {
                            continue;
                        }
                        String fileName = DocumentsContract.getTreeDocumentId(Uri.parse(saf));
                        menu.getMenu().add(fileName).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                displayAnyPath(saf);
                                return false;
                            }
                        }).setOnMenuItemLongClickListener(new OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                StringDB.delete(BookCSS.get().pathSAF, saf, new StringResult() {
                                    @Override
                                    public void onResult(String db) {
                                        BookCSS.get().pathSAF = db;
                                    }
                                });
                                return false;
                            }
                        }).setIcon(R.drawable.glyphicons_146_folder_plus);
                    }
                }

                // stars
                List<FileMeta> starFolders = AppData.get().getAllFavoriteFolders();
                List<String> names = new ArrayList<String>();
                for (FileMeta f : starFolders) {
                    names.add(f.getPath());
                }

                Collections.sort(names, String.CASE_INSENSITIVE_ORDER);

                for (final String info : names) {

                    String name;

                    if (ExtUtils.isExteralSD(info)) {
                        name = ExtUtils.getExtSDDisplayName(getContext(), info);
                    } else {
                        name = new File(info).getName();
                    }

                    menu.getMenu().add(name).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            displayAnyPath(info);
                            return false;
                        }
                    }).setIcon(R.drawable.glyphicons_591_folder_star);

                }

                if (Build.VERSION.SDK_INT >= 21 && getActivity() instanceof MainTabs2) {
                    menu.getMenu().add(R.string.add_resource).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

                            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION//
                                    | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION//
                                    | Intent.FLAG_GRANT_READ_URI_PERMISSION//
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION//
                            );

                            getActivity().startActivityForResult(intent, MainTabs2.REQUEST_CODE_ADD_RESOURCE);
                            return true;
                        }
                    }).setIcon(R.drawable.glyphicons_146_add_folder_plus);

                }



                menu.show();

            }
        });
        onBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackAction();
            }
        });

        searchAdapter.setOnItemClickListener(new ResultResponse<FileMeta>() {

            @Override
            public boolean onResultRecive(FileMeta result) {
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    rememberPos.put(displayPath, pos);
                    LOG.d("rememberPos LinearLayoutManager", displayPath, pos);
                } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    int pos = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPositions(null)[0];
                    rememberPos.put(displayPath, pos);
                    LOG.d("rememberPos StaggeredGridLayoutManager", displayPath, pos);
                }

                if (result.getCusType() != null && result.getCusType() == FileMetaAdapter.DISPLAY_TYPE_DIRECTORY) {
                    displayAnyPath(result.getPath());
                    if (fragmentType == TYPE_SELECT_FOLDER) {
                        editPath.setText(fragmentText);
                    }
                } else {
                    if (fragmentType == TYPE_DEFAULT) {
                        DefaultListeners.getOnItemClickListener(getActivity()).onResultRecive(result);
                    } else if (fragmentType == TYPE_SELECT_FILE) {
                        editPath.setText(ExtUtils.getFileName(result.getPath()));
                    } else if (fragmentType == TYPE_SELECT_FILE_OR_FOLDER) {
                        editPath.setText(result.getPath());
                    }

                }
                return false;
            }
        });

        searchAdapter.setOnItemLongClickListener(new ResultResponse<FileMeta>() {
            @Override
            public boolean onResultRecive(FileMeta result) {
                if (result.getCusType() != null && result.getCusType() == FileMetaAdapter.DISPLAY_TYPE_DIRECTORY) {
                    // displayAnyPath(result.getPath());
                    if (TxtUtils.isNotEmpty(TempHolder.get().copyFromPath)) {
                        ShareDialog.dirLongPress(getActivity(), result.getPath(), new Runnable() {

                            @Override
                            public void run() {
                                resetFragment();
                            }
                        });
                    } else {
                        deleteFolderPopup(getActivity(), result.getPath());
                    }
                } else {
                    DefaultListeners.getOnItemLongClickListener(getActivity(), searchAdapter).onResultRecive(result);
                }
                return false;
            }
        });

        onSort.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                List<String> names = Arrays.asList(//
                        getActivity().getString(R.string.by_file_name), //
                        getActivity().getString(R.string.by_date), //
                        getActivity().getString(R.string.by_size), //
                        getActivity().getString(R.string.by_title), //
                        getActivity().getString(R.string.by_number_in_serie), //
                        getActivity().getString(R.string.by_number_of_pages), //
                        getActivity().getString(R.string.by_extension) //
                );//

                final List<Integer> ids = Arrays.asList(//
                        AppState.BR_SORT_BY_PATH, //
                        AppState.BR_SORT_BY_DATE, //
                        AppState.BR_SORT_BY_SIZE, //
                        AppState.BR_SORT_BY_TITLE, //
                        AppState.BR_SORT_BY_NUMBER, //
                        AppState.BR_SORT_BY_PAGES, //
                        AppState.BR_SORT_BY_EXT//
                );//

                MyPopupMenu menu = new MyPopupMenu(getActivity(), v);
                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i);
                    final int j = i;
                    menu.getMenu().add(name).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AppState.get().sortByBrowse = ids.get(j);
                            populate();
                            return false;
                        }
                    });
                }
                menu.show();
            }
        });

        displayAnyPath(getInitPath());
        onTintChanged();

        MyProgressBar = (MyProgressBar) view.findViewById(R.id.MyProgressBarBrowse);
        MyProgressBar.setVisibility(View.GONE);
        TintUtil.setDrawableTint(MyProgressBar.getIndeterminateDrawable().getCurrent(), Color.WHITE);

        final View bankSpace = view.findViewById(R.id.bankSpace);

        bankSpace.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TxtUtils.isNotEmpty(TempHolder.get().copyFromPath)) {
                    ShareDialog.dirLongPress(getActivity(), displayPath, new Runnable() {

                        @Override
                        public void run() {
                            resetFragment();
                        }
                    });
                }
            }
        });


        if (AppState.get().appTheme == AppState.THEME_DARK_OLED) {
            view.findViewById(R.id.openAsBookBg).setBackgroundColor(Color.BLACK);


            if (fragmentType == TYPE_DEFAULT) {
                searchAdapter.tempValue2 = FileMetaAdapter.TEMP2_NONE;
            } else {
                searchAdapter.tempValue2 = FileMetaAdapter.TEMP2_RECENT_FROM_BOOK;
            }
        }

        return view;
    }

    @Override
    public void onReviceOpenDir(String path) {
        displayAnyPath(path);
    }

    OnClickListener onCloseButtonActoin = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (onCloseAction != null) {
                onCloseAction.onResultRecive("");
            }
        }
    };

    OnClickListener onSelectAction = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (fragmentType == TYPE_SELECT_FOLDER) {

                if (ExtUtils.isExteralSD(BookCSS.get().dirLastPath) || new File(BookCSS.get().dirLastPath).canRead()) {
                    onPositiveAction.onResultRecive(BookCSS.get().dirLastPath);
                } else {
                    Toast.makeText(getContext(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
                }
            } else if (fragmentType == TYPE_SELECT_FILE) {
                onPositiveAction.onResultRecive(BookCSS.get().dirLastPath + "/" + editPath.getText());
            } else if (fragmentType == TYPE_SELECT_FILE_OR_FOLDER) {
                onPositiveAction.onResultRecive(editPath.getText().toString());
            } else if (fragmentType == TYPE_CREATE_FILE) {
                onPositiveAction.onResultRecive(BookCSS.get().dirLastPath + "/" + editPath.getText());
            }

        }
    };

    public String getInitPath() {
        try {
            String pathArgument = getArguments() != null ? getArguments().getString(EXTRA_INIT_PATH, null) : "";
            if (TxtUtils.isNotEmpty(pathArgument)) {
                return pathArgument;
            }
        } catch (Exception e) {
            LOG.e(e);
        }
        String path = BookCSS.get().dirLastPath == null ? Environment.getExternalStorageDirectory().getPath() : BookCSS.get().dirLastPath;
        if (ExtUtils.isExteralSD(path)) {
            return path;
        }
        if (new File(path).isDirectory()) {
            return path;
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    String displayPath;

    @Override
    public List<FileMeta> prepareDataInBackground() {

        try {



            if (ExtUtils.isExteralSD(getInitPath())) {

                List<FileMeta> items = new ArrayList<FileMeta>();

                Uri uri = Uri.parse(displayPath);

                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri childrenUri = null;

                childrenUri = ExtUtils.getChildUri(getContext(), uri);

                if (childrenUri != null) {

                    LOG.d("newNode uri >> ", uri);
                    LOG.d("newNode childrenUri >> ", childrenUri);

                    Cursor childCursor = contentResolver.query(childrenUri, new String[]{ //
                                    Document.COLUMN_DISPLAY_NAME, //
                                    Document.COLUMN_DOCUMENT_ID, //
                                    Document.COLUMN_ICON, //
                                    Document.COLUMN_LAST_MODIFIED, //
                                    Document.COLUMN_MIME_TYPE, //
                                    Document.COLUMN_SIZE, //
                                    Document.COLUMN_SUMMARY, //
                            }, //
                            null, null, null); //
                    try {
                        while (childCursor.moveToNext()) {
                            String COLUMN_DISPLAY_NAME = childCursor.getString(0);
                            String COLUMN_DOCUMENT_ID = childCursor.getString(1);
                            String COLUMN_ICON = childCursor.getString(2);
                            String COLUMN_LAST_MODIFIED = childCursor.getString(3);
                            String COLUMN_MIME_TYPE = childCursor.getString(4);
                            String COLUMN_SIZE = childCursor.getString(5);
                            String COLUMN_SUMMARY = childCursor.getString(6);

                            LOG.d("found- child 2=", COLUMN_DISPLAY_NAME, COLUMN_DOCUMENT_ID, COLUMN_ICON);

                            FileMeta meta = new FileMeta();
                            meta.setAuthor(SearchFragment2.EMPTY_ID);

                            final Uri newNode = DocumentsContract.buildDocumentUriUsingTree(uri, COLUMN_DOCUMENT_ID);
                            meta.setPath(newNode.toString());
                            LOG.d("newNode", newNode);

                            if (Document.MIME_TYPE_DIR.equals(COLUMN_MIME_TYPE)) {
                                meta.setCusType(FileMetaAdapter.DISPLAY_TYPE_DIRECTORY);
                                meta.setPathTxt(COLUMN_DISPLAY_NAME);
                                meta.setTitle(COLUMN_DISPLAY_NAME);

                            } else {
                                try {
                                    if (COLUMN_SIZE != null) {
                                        long size = Long.parseLong(COLUMN_SIZE);
                                        meta.setSize(size);
                                        meta.setSizeTxt(ExtUtils.readableFileSize(size));
                                    }
                                    if (COLUMN_LAST_MODIFIED != null) {
                                        meta.setDateTxt(ExtUtils.getDateFormat(Long.parseLong(COLUMN_LAST_MODIFIED)));
                                    }
                                } catch (Exception e) {
                                    LOG.e(e);
                                }
                                meta.setExt(ExtUtils.getFileExtension(COLUMN_DISPLAY_NAME));

                                if (BookType.FB2.is(COLUMN_DISPLAY_NAME)) {
                                    meta.setTitle(TxtUtils.encode1251(COLUMN_DISPLAY_NAME));
                                } else {
                                    meta.setTitle(COLUMN_DISPLAY_NAME);
                                }

                            }
                            items.add(meta);

                        }
                    } finally {
                        closeQuietly(childCursor);
                    }
                }
                return items;

            } else {
                return SearchCore.getFilesAndDirs(displayPath, fragmentType == TYPE_DEFAULT);
            }
        } catch (Exception e) {
            LOG.e(e);
        }
        return Collections.emptyList();
    }

    @Override
    public void populateDataInUI(List<FileMeta> items) {
        displayItems(items);
        showPathHeader();

        if (isRestorePos) {
            final int pos = rememberPos.get(displayPath) == null ? 0 : rememberPos.get(displayPath);
            recyclerView.getLayoutManager().scrollToPosition(pos);
            LOG.d("rememberPos go", displayPath, pos);
        }

    }

    boolean isRestorePos = false;

    public boolean onBackAction() {
        if (ExtUtils.isExteralSD(BookCSS.get().dirLastPath)) {
            String path = BookCSS.get().dirLastPath;
            LOG.d("pathBack before", path);
            if (path.contains("%2F")) {
                path = path.substring(0, path.lastIndexOf("%2F"));
            } else {
                path = path.substring(0, path.lastIndexOf("%3A") + 3);
            }
            LOG.d("pathBack after", path);

            if (path.endsWith("%3A")) {
                displayAnyPath(path);
                return false;
            } else {
                displayAnyPath(path);
                return true;
            }

        } else {
            File file = new File(displayPath);
            String path = file.getParent();

            if (TxtUtils.isEmpty(path)) {
                return false;
            }

            LOG.d("parent", path);

            displayAnyPath(path);
            isRestorePos = true;
            return true;
        }
    }

    public void displayAnyPath(String path) {
        LOG.d("Display-path", path);
        isRestorePos = false;
        displayPath = path;
        BookCSS.get().dirLastPath = path;

        populate();
    }

    private ImageView openAsbookImage;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    public void displayItems(List<FileMeta> items) {
        if (searchAdapter == null) {
            return;
        }

        searchAdapter.clearItems();

        try {
            if (AppState.get().sortByBrowse == AppState.BR_SORT_BY_PATH) {
                Collections.sort(items, FileMetaComparators.BY_PATH);
            } else if (AppState.get().sortByBrowse == AppState.BR_SORT_BY_DATE) {
                Collections.sort(items, FileMetaComparators.BY_DATE);
            } else if (AppState.get().sortByBrowse == AppState.BR_SORT_BY_SIZE) {
                Collections.sort(items, FileMetaComparators.BY_SIZE);
            } else if (AppState.get().sortByBrowse == AppState.BR_SORT_BY_NUMBER) {
                Collections.sort(items, FileMetaComparators.BR_BY_NUMBER1);
            } else if (AppState.get().sortByBrowse == AppState.BR_SORT_BY_PAGES) {
                Collections.sort(items, FileMetaComparators.BR_BY_PAGES);
            } else if (AppState.get().sortByBrowse == AppState.BR_SORT_BY_TITLE) {
                Collections.sort(items, FileMetaComparators.BR_BY_TITLE);
            } else if (AppState.get().sortByBrowse == AppState.BR_SORT_BY_EXT) {
                Collections.sort(items, FileMetaComparators.BR_BY_EXT);
            }
            if (AppState.get().sortByReverse) {
                Collections.reverse(items);
            }
            Collections.sort(items, FileMetaComparators.DIRS);

        } catch (Exception e) {
            LOG.e(e);
        }

        for (int i = 0; i < items.size(); i++) {
            FileMeta m = items.get(i);
            if (m.getCusType() == null) {// directory
                LOG.d("DISPALY_TYPE_LAYOUT_TITLE_FOLDERS", i);
                FileMeta it = new FileMeta();
                it.setCusType(FileMetaAdapter.DISPALY_TYPE_LAYOUT_TITLE_NONE);
                items.add(i, it);
                break;
            }
        }

        searchAdapter.getItemsList().addAll(items);
        recyclerView.setAdapter(searchAdapter);

        scroller.postDelayed(new Runnable() {

            @Override
            public void run() {
                scroller.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }

        }, 100);

        openAsBook.setVisibility(TxtUtils.visibleIf(FolderContext.isFolderWithImage(items)));

    }

    public void showPathHeader() {
        paths.removeAllViews();

        if (TYPE_SELECT_FILE_OR_FOLDER == fragmentType) {
            editPath.setText(displayPath);
        }

        if (ExtUtils.isExteralSD(displayPath)) {
            String id = ExtUtils.getExtSDDisplayName(getContext(), displayPath);

            TextView slash = new TextView(getActivity());
            slash.setText(id);
            slash.setTextColor(getResources().getColor(R.color.white));
            paths.addView(slash);
        } else {


        }

        if (AppDB.get().isStarFolder(displayPath)) {
            starIcon.setImageResource(R.drawable.star_1);
        } else {
            starIcon.setImageResource(R.drawable.star_2);
        }
        TintUtil.setTintImageWithAlpha(starIcon, Color.WHITE);

        starIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FileMeta fileMeta = AppDB.get().getOrCreate(displayPath);
                fileMeta.setCusType(FileMetaAdapter.DISPLAY_TYPE_DIRECTORY);
                fileMeta.setPathTxt(ExtUtils.getFileName(displayPath));
                DefaultListeners.getOnStarClick(getActivity()).onResultRecive(fileMeta, null);
                if (AppDB.get().isStarFolder(displayPath)) {
                    starIcon.setImageResource(R.drawable.star_1);
                } else {
                    starIcon.setImageResource(R.drawable.star_2);
                }
            }
        });

        final String ldir = FolderContext.genarateXML(searchAdapter.getItemsList(), displayPath, false).toString();
        if (AppDB.get().isStarFolder(ldir)) {
            starIconDir.setImageResource(R.drawable.star_1);
        } else {
            starIconDir.setImageResource(R.drawable.star_2);
        }
        TintUtil.setTintImageWithAlpha(starIconDir, getActivity() instanceof MainTabs2 ? TintUtil.getColorInDayNighth() : TintUtil.getColorInDayNighthBook());

        starIconDir.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File genarateXMLBook = FolderContext.genarateXML(searchAdapter.getItemsList(), displayPath, true);

                FileMeta fileMeta = AppDB.get().getOrCreate(genarateXMLBook.getPath());
                FileMetaCore.createMetaIfNeed(genarateXMLBook.getPath(), false);
                DefaultListeners.getOnStarClick(getActivity()).onResultRecive(fileMeta, null);
                if (AppDB.get().isStarFolder(ldir)) {
                    starIconDir.setImageResource(R.drawable.star_1);
                } else {
                    starIconDir.setImageResource(R.drawable.star_2);
                }
                TintUtil.setTintImageWithAlpha(starIconDir, getActivity() instanceof MainTabs2 ? TintUtil.getColorInDayNighth() : TintUtil.getColorInDayNighthBook());
            }
        });

    }

    private void deleteFolderPopup(final Activity a, final String path) {

        AlertDialogs.showOkDialog(a, getString(R.string.delete_the_directory_all_the_files_in_the_directory_), new Runnable() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void run() {



            }
        });


    }

    public void onGridList() {
        onGridList(AppState.get().broseMode, onListGrid, searchAdapter, null);

    }

    private void popupMenu(final ImageView onGridList) {
        MyPopupMenu p = new MyPopupMenu(getActivity(), onGridList);
        PopupHelper.addPROIcon(p, getActivity());

        List<Integer> names = Arrays.asList(R.string.list, R.string.compact, R.string.grid, R.string.cover);
        final List<Integer> icons = Arrays.asList(R.drawable.glyphicons_114_justify, R.drawable.glyphicons_114_justify_compact, R.drawable.glyphicons_156_show_big_thumbnails, R.drawable.glyphicons_157_show_thumbnails);
        final List<Integer> actions = Arrays.asList(AppState.MODE_LIST, AppState.MODE_LIST_COMPACT, AppState.MODE_GRID, AppState.MODE_COVERS);

        for (int i = 0; i < names.size(); i++) {
            final int index = i;
            p.getMenu().add(names.get(i)).setIcon(icons.get(i)).setOnMenuItemClickListener(new OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AppState.get().broseMode = actions.get(index);
                    onGridList.setImageResource(icons.get(index));
                    onGridList();
                    return false;
                }
            });
        }

        p.show();

    }

    @Override
    public boolean isBackPressed() {
        return onBackAction();
    }

    public void setOnPositiveAction(ResultResponse<String> onPositiveAction) {
        this.onPositiveAction = onPositiveAction;
    }

    public void setOnCloseAction(ResultResponse<String> onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    @Override
    public void notifyFragment() {
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
            sortOrder.setVisibility(TxtUtils.visibleIf(AppState.get().isVisibleSorting));
        }

    }

    @Override
    public void resetFragment() {
        onGridList();
        populate();
    }

}
