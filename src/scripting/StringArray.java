package scripting;

public class StringArray implements Comparable<StringArray>
{

    private String[] strings;
    private int hashCode;


    public StringArray(String[] strings)
    {
        this.strings = strings;
    }


    public String get(int index)
    {
        return (strings != null && strings.length > index ? strings[index] : null);
    }


    @Override
    public int hashCode()
    {
        if (hashCode != 0)
        {
            for (int i = 0; i < strings.length; i++)
            {
                hashCode += strings[i].hashCode() * 13 * i;
            }
        }
        return (hashCode);
    }


    @Override
    public boolean equals(Object object)
    {
        return (object == this || (object instanceof StringArray && compareTo((StringArray) object) == 0));
    }


    @Override
    public int compareTo(StringArray stringArray)
    {
        int result = 0;
        if (stringArray != this)
        {
            for (int i = 0; i < stringArray.strings.length && i < strings.length; i++)
            {
                result = strings[i].compareTo(stringArray.strings[i]);
                if (result != 0)
                {
                    break;
                }
            }
            if (result == 0)
            {
                result = strings.length - stringArray.strings.length;
            }
        }
        return (result);
    }


    @Override
    public String toString()
    {
        String s = "";
        for (int i = 0; i < strings.length; i++)
        {
            s += strings[i] + ",";
        }
        return (strings.length > 0 && s.length() > 0 ? s.substring(0, s.length() - 1) : s);
    }


}