public class RandomIntGenerator
{   public  RandomIntGenerator(int l, int h)
    {   low = l;
        high = h;
    }

    public int draw()
    {
       int r =low + (int)((high - low + 1) * nextRandom());
       if(r>high) r = high;
       return r;
    }


    private static double nextRandom()
    {   int pos = (int)(java.lang.Math.random() * BUFFER_SIZE);
        if (pos == BUFFER_SIZE) pos = BUFFER_SIZE -1;
        double r = buffer[pos];
        buffer[pos] = java.lang.Math.random();
        return r;
    }

    private static final int BUFFER_SIZE = 101;
    private static double[] buffer = new double[BUFFER_SIZE];
    static // initialization of static data
    {
        int i;
        for ( i =0 ; i < BUFFER_SIZE; i++)
            buffer[i] = java.lang.Math.random();
    }

    private int low;
    private int high;

}