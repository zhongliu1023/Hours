package ours.china.hours.BookLib.artifex.mupdf.fitz.android;

import android.graphics.Bitmap;

import ours.china.hours.BookLib.artifex.mupdf.fitz.Context;
import ours.china.hours.BookLib.artifex.mupdf.fitz.Image;

public final class AndroidImage extends Image
{
	static {
		Context.init();
	}

	private native long newAndroidImageFromBitmap(Bitmap bitmap, long mask);

	public AndroidImage(Bitmap bitmap, AndroidImage mask)
	{
		super(0);
		pointer = newAndroidImageFromBitmap(bitmap, mask.pointer);
	}
}
