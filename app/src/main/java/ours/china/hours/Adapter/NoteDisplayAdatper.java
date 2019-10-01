package ours.china.hours.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import at.stefl.svm.object.action.LineAction;
import ours.china.hours.Activity.Personality.UpdateinforActivity;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.pdf.info.BookmarksData;
import ours.china.hours.BookLib.foobnix.pdf.info.OutlineHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalBookReadingActivity;
import ours.china.hours.Common.Interfaces.NoteItemInterface;
import ours.china.hours.Dialog.NoteDialog;
import ours.china.hours.R;

public class NoteDisplayAdatper extends RecyclerView.Adapter<NoteDisplayAdatper.NoteViewHolder> implements NoteDialog.OnAddNoteListener{
    private static String TAG = "NoteDisplayAdapter";

    private Context context;
    private ArrayList<AppBookmark> objects = new ArrayList<AppBookmark>();

    private DocumentController controller;

    NoteDialog noteDialog;
    int focusPosition = 0;
    NoteViewHolder focusHolder;

    OnCopyListiner onCopyListiner;
    NoteItemInterface noteItemClickListener;

    public NoteDisplayAdatper(Context context, ArrayList<AppBookmark> objects, DocumentController controller, OnCopyListiner listner, NoteItemInterface noteItemClickListener) {
        this.context = context;
        this.objects = objects;
        this.controller = controller;
        this.onCopyListiner = listner;
        this.noteItemClickListener = noteItemClickListener;
//        this.onRefresh = onRefresh;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        AppBookmark one = objects.get(position);
        String pageNumber = TxtUtils.deltaPage(one.getPage(controller.getPageCount()));
        OutlineHelper.Info info = OutlineHelper.getForamtingInfo(controller, false);
        String totalPageCount = info.textPage;

        Log.i(TAG, "pageNumber => " + pageNumber + "totalPageCount => " + totalPageCount);
        holder.txtPageNumber.setText(context.getString(R.string.note_page, pageNumber, totalPageCount));

        if (one.text.equals("")) {
            holder.txtColorNoteContent.setVisibility(View.VISIBLE);
            holder.txtColorNoteContent.setText(one.note);
            holder.txtNoteContent.setVisibility(View.GONE);
            holder.imgNoteColorIcon.setImageResource(R.drawable.note_note_icon);

            holder.secondSeperator.setVisibility(View.GONE);
            holder.imgNoteAddOrUpdate.setImageResource(R.drawable.note_modify_icon);
//            holder.txtNoteAddOrUpdate.setText("修改笔记");

        } else if (one.note.equals("")) {
            holder.txtColorNoteContent.setVisibility(View.VISIBLE);
            holder.txtNoteContent.setVisibility(View.GONE);

            holder.txtColorNoteContent.setText(one.text);

            holder.noteLayout.setVisibility(View.VISIBLE);
            holder.secondSeperator.setVisibility(View.VISIBLE);
            holder.imgNoteAddOrUpdate.setImageResource(R.drawable.note_note_icon);

            holder.secondSeperator.setVisibility(View.GONE);
            holder.txtNoteAddOrUpdate.setText("添加笔记");

            holder.imgNoteColorIcon.setImageResource(R.drawable.read_orange_icon);
            if (one.type >= 0){
                switch (one.type){
                    case 0:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_yellow_icon);
                        break;
                    case 1:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_orange_icon);
                        break;
                    case 2:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_blue_icon);
                        break;
                    case 3:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_pink_icon);
                        break;
                    case 4:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.note_note_icon);
                        break;
                    default:
                        break;
                }
            }
        } else {
            holder.txtColorNoteContent.setVisibility(View.VISIBLE);
            holder.txtColorNoteContent.setText(one.text);

            holder.txtNoteContent.setVisibility(View.VISIBLE);
            holder.txtNoteContent.setText(one.note);

            holder.noteLayout.setVisibility(View.VISIBLE);
            holder.secondSeperator.setVisibility(View.VISIBLE);
            holder.imgNoteAddOrUpdate.setImageResource(R.drawable.note_modify_icon);

            holder.secondSeperator.setVisibility(View.VISIBLE);
            holder.txtNoteAddOrUpdate.setText("修改笔记");

            holder.imgNoteColorIcon.setImageResource(R.drawable.read_orange_icon);
            if (one.type >= 0){
                switch (one.type){
                    case 0:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_yellow_icon);
                        break;
                    case 1:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_orange_icon);
                        break;
                    case 2:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_blue_icon);
                        break;
                    case 3:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_pink_icon);
                        break;
                    case 4:
                        holder.imgNoteColorIcon.setImageResource(R.drawable.note_note_icon);
                        break;
                    default:
                        break;
                }
            }
        }

        holder.titleText.setText(one.subTitle);
        holder.imgNoteMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = mInflater.inflate(R.layout.more_note, null);

                final TextView menu_delete = (TextView) layout.findViewById(R.id.menu_delete);
                final TextView menu_copy = (TextView) layout.findViewById(R.id.menu_copy);
                final TextView menu_share = (TextView) layout.findViewById(R.id.menu_share);
                final ImageView menu_mark_1 = (ImageView) layout.findViewById(R.id.menu_mark_1);
                final ImageView menu_mark_2 = (ImageView) layout.findViewById(R.id.menu_mark_2);
                final ImageView menu_mark_3 = (ImageView) layout.findViewById(R.id.menu_mark_3);
                final ImageView menu_mark_4 = (ImageView) layout.findViewById(R.id.menu_mark_4);

                if (one.text.equals("")) {
                    menu_mark_1.setVisibility(View.GONE);
                    menu_mark_2.setVisibility(View.GONE);
                    menu_mark_3.setVisibility(View.GONE);
                    menu_mark_4.setVisibility(View.GONE);
                }

                layout.measure(View.MeasureSpec.UNSPECIFIED,
                        View.MeasureSpec.UNSPECIFIED);
                PopupWindow mDropdown = new PopupWindow(layout, FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,true);
                Drawable background = context.getResources().getDrawable(android.R.drawable.editbox_dropdown_light_frame);
                mDropdown.setBackgroundDrawable(background);
                mDropdown.showAsDropDown(view, 5, 5);

                menu_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDropdown.dismiss();
                        new AlertView.Builder().setContext(context).setTitle(context.getString(R.string.app_name))
                                .setMessage(context.getString(R.string.delete_note))
                                .setDestructive(context.getString(R.string.cancel))
                                .setOthers(new String[]{context.getString(R.string.confirm)})
                                .setStyle(AlertView.Style.Alert).setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 1){
                                    BookmarksData.get().remove(one);
                                    objects.remove(focusPosition);
                                    notifyDataSetChanged();
                                }
                            }
                        }).build().show();
                    }
                });
                menu_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDropdown.dismiss();
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(one.text);
                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", one.text);
                            clipboard.setPrimaryClip(clip);
                        }
                        onCopyListiner.onCopyNote();
                    }
                });
                menu_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDropdown.dismiss();
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String txt = one.note;
                        intent.putExtra(Intent.EXTRA_TEXT, txt);
                        context.startActivity(Intent.createChooser(intent,"Share"));
                    }
                });
                menu_mark_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDropdown.dismiss();
                        one.type = 0;
                        BookmarksData.get().update(one);
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_yellow_icon);
                    }
                });
                menu_mark_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDropdown.dismiss();
                        one.type = 1;
                        BookmarksData.get().update(one);
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_orange_icon);
                    }
                });
                menu_mark_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDropdown.dismiss();
                        one.type = 2;
                        BookmarksData.get().update(one);
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_blue_icon);
                    }
                });
                menu_mark_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDropdown.dismiss();
                        one.type = 3;
                        BookmarksData.get().update(one);
                        holder.imgNoteColorIcon.setImageResource(R.drawable.read_pink_icon);
                    }
                });
            }
        });

        holder.noteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusPosition = position;
                noteDialog = new NoteDialog(context, R.style.AppTheme_Alert, "添加笔记", one.note, NoteDisplayAdatper.this);
                noteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                noteDialog.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteItemClickListener.onClickNoteItem(one, pageNumber);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public void addNote(String str) {
        AppBookmark one = objects.get(focusPosition);
        one.note = str;

        BookmarksData.get().update(one);
        notifyDataSetChanged();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        ImageView imgNoteColorIcon;
        ImageView imgNoteAddOrUpdate;
        ImageView imgNoteMore;
        TextView txtColorNoteContent;
        TextView txtNoteContent;
        TextView txtNoteAddOrUpdate;
        TextView txtPageNumber;
        TextView titleText;
        TextView subTitleTxt;
        TextView secondSeperator;

        LinearLayout noteLayout;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            imgNoteColorIcon = itemView.findViewById(R.id.imgNoteColorIcon);
            imgNoteAddOrUpdate = itemView.findViewById(R.id.imgNoteAddOrUpdate);
            imgNoteMore = itemView.findViewById(R.id.imgNoteMore);
            txtColorNoteContent = itemView.findViewById(R.id.txtColorNoteContent);
            txtNoteContent = itemView.findViewById(R.id.txtNoteContent);
            txtNoteAddOrUpdate = itemView.findViewById(R.id.txtNoteAddOrUpdate);
            txtPageNumber = itemView.findViewById(R.id.txtPageNumber);
            titleText = itemView.findViewById(R.id.titleText);
            subTitleTxt = itemView.findViewById(R.id.subTitleTxt);

            noteLayout = itemView.findViewById(R.id.noteLayout);
            secondSeperator = itemView.findViewById(R.id.secondSeperator);
        }
    }

    public interface OnCopyListiner{
        void onCopyNote();
    }

}
