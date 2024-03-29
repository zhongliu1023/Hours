package ours.china.hours.BookLib.foobnix.pdf.info.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.widget.TextView;

import ours.china.hours.BookLib.foobnix.android.utils.Dips;

public class BorderTextView extends TextView {

    Paint paint = new Paint();
    {
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(Dips.dpToPx(1));
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Style.STROKE);
    }

    public BorderTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getHeight(), getWidth(), paint);
        super.onDraw(canvas);
    }


}
