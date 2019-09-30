package ours.china.hours.Model;

import java.util.Comparator;

public class SortByPublishDate implements Comparator<Book> {
    @Override
    public int compare(Book book, Book book1) {
        return book.publishDate.compareTo(book1.publishDate);
    }
}
