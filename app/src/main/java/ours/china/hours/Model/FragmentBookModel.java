package ours.china.hours.Model;

public class FragmentBookModel {
    Book book = new Book();
    String selectedState;

    public FragmentBookModel(Book book, String selectedState) {
        this.book = book;
        this.selectedState = selectedState;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(String selectedState) {
        this.selectedState = selectedState;
    }
}
