public final class Peg
{
    public static final int ROSU  = 1;
    public static final int GALBEN = 2;
    public static final int ALBASTRU = 3;
    public static final int VERDE  = 4;
    public static final int NEGRU = 5;
    public static final int ALB = 6;

    public int value;

    public Peg (int temp)
    {
        value = temp;
    }

    public void assignValue(int thevalue)
    {
         value = thevalue;
    }
    public String toString()
    // converts to a string the nume
    {   String v ;
        if ( value == ROSU) v ="R";
            else if  (value == GALBEN ) v = "G";
            else if  (value == ALBASTRU) v = "B";
            else if (value == VERDE) v = "V";
            else if (value == NEGRU) v = "N";
            else   v = "A";
        return v;

    }


}
