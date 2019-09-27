package ours.china.hours.Model;


public enum QueryRequest {
    ADD("add", 0),
    DELETE("delete", 1),
    SET("set", 2);

    private String stringValue;
    private int intValue;
    private QueryRequest(String toString, int value){
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString(){
        return stringValue;
    }
}

