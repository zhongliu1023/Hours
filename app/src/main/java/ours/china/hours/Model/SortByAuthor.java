package ours.china.hours.Model;

import java.util.Comparator;

public class SortByAuthor implements Comparator<Book> {
    @Override
    public int compare(Book book, Book book1) {
        return book1.author.compareTo(book.author);
    }
}
