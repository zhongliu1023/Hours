package org.ebookdroid.droids;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.ext.CacheZipUtils;
import ours.china.hours.BookLib.foobnix.ext.EpubExtractor;
import ours.china.hours.BookLib.foobnix.ext.FooterNote;
import ours.china.hours.BookLib.foobnix.ext.MobiExtract;
import ours.china.hours.BookLib.foobnix.model.AppTemp;
import ours.china.hours.BookLib.foobnix.pdf.info.JsonHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.model.BookCSS;

import org.ebookdroid.core.codec.CodecDocument;
import org.ebookdroid.droids.mupdf.codec.MuPdfDocument;
import org.ebookdroid.droids.mupdf.codec.PdfContext;

import java.io.File;
import java.util.Map;

public class MobiContext extends PdfContext {

    String fileNameEpub = null;

    public int originalHashCode;
    File cacheFile;

    @Override
    public File getCacheFileName(String fileName) {
        originalHashCode = (fileName + BookCSS.get().isAutoHypens + AppTemp.get().hypenLang).hashCode();
        cacheFile = new File(CacheZipUtils.CACHE_BOOK_DIR, originalHashCode + "" + originalHashCode + ".epub");
        return cacheFile;
    }

    @Override
    public CodecDocument openDocumentInner(String fileName, String password) {

        LOG.d("Context", "MobiContext", fileName);

        if (cacheFile.isFile()) {
            fileNameEpub = cacheFile.getPath();
            LOG.d("Context", "MobiContext cache", fileNameEpub);

        } else {
            try {
                int outName = BookCSS.get().isAutoHypens ? "temp".hashCode() : originalHashCode;
                FooterNote extract = MobiExtract.extract(fileName, CacheZipUtils.CACHE_BOOK_DIR.getPath(), outName + "");
                fileNameEpub = extract.path;
                if (BookCSS.get().isAutoHypens) {

                    EpubExtractor.proccessHypens(fileNameEpub, cacheFile.getPath(), null);
                    fileNameEpub = cacheFile.getPath();
                }
                LOG.d("Context", "MobiContext extract", fileNameEpub);

            } catch (Exception e) {
                LOG.e(e);
            }
        }

        final MuPdfDocument muPdfDocument = new MuPdfDocument(this, MuPdfDocument.FORMAT_PDF, fileNameEpub, password);

        final File jsonFile = new File(cacheFile + ".json");
        if (jsonFile.isFile()) {
            muPdfDocument.setFootNotes(JsonHelper.fileToMap(jsonFile));
            LOG.d("Load notes from file", jsonFile);
        } else {

            new Thread() {
                @Override
                public void run() {
                    Map<String, String> notes = null;
                    try {
                        notes = EpubExtractor.get().getFooterNotes(fileNameEpub);
                        LOG.d("new file name", fileNameEpub);
                        muPdfDocument.setFootNotes(notes);

                        JsonHelper.mapToFile(jsonFile, notes);
                        LOG.d("save notes to file", jsonFile);

                        removeTempFiles();

                    } catch (Exception e) {
                        LOG.e(e);
                    }
                }

                ;
            }.start();
        }


        return muPdfDocument;
    }

}
