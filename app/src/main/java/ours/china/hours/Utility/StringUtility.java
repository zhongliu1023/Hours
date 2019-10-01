package ours.china.hours.Utility;

public class StringUtility {

    public static int count(final String string, final String substring)
    {
        int count = 0;
        int idx = 0;

        while ((idx = string.indexOf(substring, idx)) != -1)
        {
            idx++;
            count++;
        }

        return count;
    }

    public static int count(final String string, final char c)
    {
        return count(string, String.valueOf(c));
    }

}
