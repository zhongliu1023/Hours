package ours.china.hours.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;


public class CustomRectView extends View {

    private Rect rectangle;
    private Paint paint;

    public CustomRectView(Context context, int X1, int Y1, int X2, int Y2) {
        super(context);

        rectangle = new Rect(X1, Y1, X2, Y2);

        paint = new Paint();
        paint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(rectangle, paint);
    }
}
