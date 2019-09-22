package ours.china.hours.BookLib.foobnix.pdf.search.activity.msg;

import ours.china.hours.Common.ColorCollection;

public class MsgChangePaintWordsColor {

    private ColorCollection colorPickValue;

    public MsgChangePaintWordsColor(ColorCollection colorPickValue) {
        this.colorPickValue = colorPickValue;
    }

    public ColorCollection getColorPickValue() {
        return colorPickValue;
    }
}
