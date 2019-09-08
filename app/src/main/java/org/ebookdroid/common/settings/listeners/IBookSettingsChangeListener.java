package org.ebookdroid.common.settings.listeners;

import ours.china.hours.BookLib.foobnix.model.AppBook;

public interface IBookSettingsChangeListener {

    void onBookSettingsChanged(AppBook oldSettings, AppBook newSettings, AppBook.Diff diff);

}
