package ours.china.hours.BookLib.foobnix.pdf.search.activity;

import android.graphics.Matrix;
import android.util.SparseArray;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.Model.TextWordWithType;

import org.ebookdroid.core.codec.PageLink;
import org.ebookdroid.droids.mupdf.codec.TextWord;

import java.util.ArrayList;
import java.util.List;

public class PageImageState {
    private static final int M9 = 9;

    private final static PageImageState instance = new PageImageState();

    private SparseArray<List<TextWord>> selectedWords = new SparseArray<List<TextWord>>();
    private SparseArray<List<TextWordWithType>> selectedNotes = new SparseArray<List<TextWordWithType>>();
    public final SparseArray<TextWord[][]> pagesText = new SparseArray<TextWord[][]>();
    public final SparseArray<List<PageLink>> pagesLinks = new SparseArray<List<PageLink>>();
    private Matrix matrix = new Matrix();

    public static volatile int currentPage = 0;

    public boolean isAutoFit = true;
    public boolean isShowCuttingLine = false;

    public boolean needAutoFit = false;

    public static PageImageState get() {
        return instance;
    }

    public void clearResouces() {
        selectedWords.clear();
        selectedNotes.clear();
        pagesText.clear();
        pagesLinks.clear();
    }

    public List<TextWord> getSelectedWords(int page) {
        return selectedWords.get(page);
    }

    public List<TextWordWithType> getSelectedNotes(int page) {
        return selectedNotes.get(page);
    }

    public void putWords(int page, List<TextWord> words) {
        selectedWords.put(page, words);
    }
    public void putNotes(int page, List<TextWordWithType> words) {
        selectedNotes.put(page, words);
    }

    public void addWord(int page, TextWord word) {
        List<TextWord> list = selectedWords.get(page);
        if (list == null) {
            list = new ArrayList<TextWord>();
            selectedWords.put(page, list);
        }
        list.add(word);
    }

    public void addNotes(int page, TextWord word, int type) {
        List<TextWordWithType> list = selectedNotes.get(page);
        if (list == null) {
            list = new ArrayList<TextWordWithType>();
            selectedNotes.put(page, list);
        }
        TextWordWithType textWordWithType = new TextWordWithType();
        textWordWithType.textWord = word;
        textWordWithType.type = type;
        list.add(textWordWithType);
    }
    public void cleanSelectedWords() {
        selectedWords.clear();
    }

    public void cleanSelectedNotes() {
        selectedNotes.clear();
    }
    public void cleanSelectedWordsWithoutPage(int page) {
        SparseArray<List<TextWordWithType>> tmpSelectedWords = selectedNotes.clone();
        for (int i = 0; i < selectedNotes.size(); i ++){
            int getPage = selectedNotes.keyAt(i);
            if (getPage != page){
                tmpSelectedWords.removeAt(i);
            }
        }
        selectedNotes = tmpSelectedWords.clone();
    }
    public boolean hasSelectedWords() {
        return selectedWords.size() > 0;
    }

    public boolean hasSelectedNotes() {
        return selectedNotes.size() > 0;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public String getMatrixAsString() {
        return fromMatrix(matrix);
    }

    public static Matrix fromString(String value) {
        Matrix matrix = new Matrix();
        if (TxtUtils.isEmpty(value)) {
            return matrix;
        }

        try {
            float[] values = new float[M9];
            String[] split = value.split(",");
            for (int i = 0; i < M9; i++) {
                values[i] = Float.valueOf(split[i]);
            }
            matrix.setValues(values);
        } catch (Exception e) {
            LOG.e(e);
        }
        return matrix;
    }

    public static String fromMatrix(Matrix matrix) {
        float[] values = new float[M9];
        matrix.getValues(values);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < M9; i++) {
            out.append(values[i]);
            if (i != M9 - 1) {
                out.append(",");
            }
        }
        return out.toString();

    }

}
