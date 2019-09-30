package ours.china.hours.Model;

import java.util.Comparator;

public class SortByTitle implements Comparator<Book> {
    @Override
    public int compare(Book book, Book book1) {
        return book.bookName.compareTo(book1.bookName);
    }
}
