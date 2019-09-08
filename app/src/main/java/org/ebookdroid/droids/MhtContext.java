package org.ebookdroid.droids;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.ext.CacheZipUtils;
import ours.china.hours.BookLib.foobnix.ext.FooterNote;
import ours.china.hours.BookLib.foobnix.ext.HtmlExtractor;

import org.ebookdroid.core.codec.CodecDocument;
import org.ebookdroid.droids.mupdf.codec.MuPdfDocument;
import org.ebookdroid.droids.mupdf.codec.PdfContext;

import java.util.Map;

public class MhtContext extends PdfContext {

    @Override
    public CodecDocument openDocumentInner(String fileName, String password) {

        Map<String, String> notes = null;
        try {
            FooterNote extract = HtmlExtractor.extractMht(fileName, CacheZipUtils.CACHE_BOOK_DIR.getPath());
            fileName = extract.path;
            notes = extract.notes;
            LOG.d("new file name", fileName);
        } catch (Exception e) {
            LOG.e(e);
        }

        MuPdfDocument muPdfDocument = new MuPdfDocument(this, MuPdfDocument.FORMAT_PDF, fileName, password);
        muPdfDocument.setFootNotes(notes);
        return muPdfDocument;
    }

}
