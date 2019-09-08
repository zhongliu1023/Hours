package ours.china.hours.BookLib.artifex.mupdf.fitz;

public final class DisplayListDevice extends NativeDevice
{
	private static native long newNative(DisplayList list);

	public DisplayListDevice(DisplayList list) {
		super(newNative(list));
	}
}
