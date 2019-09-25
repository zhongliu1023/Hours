package ours.china.hours.Model;

public class QueryBook {
    public enum OrderBy {
        PUBLISHDATE("publishDate", 0),
        BOOKNAME("bookName", 1),
        AUTH0R("author", 2);

        private String stringValue;
        private int intValue;
        private OrderBy(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString(){
            return stringValue;
        }
    }
    public enum Order {
        ASC("asc", 0),
        DESC("desc", 1);

        private String stringValue;
        private int intValue;
        private Order(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString(){
            return stringValue;
        }
    }
    public enum Category {
        ALL("all", 0),
        RECOMMEND("recommend", 1),
        ZHIREN("zhiren", 2),
        RENWEN("renwen", 3),
        WENXIE("wenxie", 4),
        ATEENTION("attention", 5),
        DOWNLOADED("downloaded", 6);

        private String stringValue;
        private int intValue;
        private Category(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString(){
            return stringValue;
        }
    }

    public enum BookAction {
        NONE("none", 0),
        SELECTTION("selection", 1);

        private String stringValue;
        private int intValue;

        private BookAction(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString(){
            return stringValue;
        }
    }

    public enum BookAttention {
        NONE("none", 0),
        ATTENTION("selection", 1);

        private String stringValue;
        private int intValue;

        private BookAttention(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString(){
            return stringValue;
        }

    }
}
