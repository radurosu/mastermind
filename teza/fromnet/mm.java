import java.util.*;
import java.lang.*;
import mm_result;

public final class mm
{
    private int positions;
    private int colors;
    private int attempts;
    private int attempt;
    private int my_value[];
    private int counter[];
    private int value[][];
    private int guess[][];
    private int result_black[];
    private int result_total[];
    private int tmp[];
    private Random rand;

    public mm(int pos, int col, int att)
    {
        int i;
        int j;
        int k;
        int min;
        int mink;
        Date d = new Date();

        rand = new Random(d.getTime());

        positions = pos;
        colors = col;
        attempts = att;
        my_value = new int[positions];
        counter = new int[positions];
        value = new int[positions][colors];
        guess = new int[attempts][positions];
        result_black = new int[attempts];
        result_total = new int[attempts];
        tmp = new int[positions];
        attempt = 0;

        for (i = 0; i < positions; i++)
        {
            my_value[i] = rnd(0, colors - 1);
            counter[i] = 0;

            // fill value[i] array with the integers from 0 to colors-1 in random order
            for (j = 0; j < colors; j++)
                value[i][j] = -rnd(1, 100 * colors);
            for (j = 0; j < colors; j++)
            {
                min = 0;
                mink = 0;
                for (k = 0; k < colors; k++)
                {
                    if (value[i][k] < min)
                    {
                        min = value[i][k];
                        mink = k;
                    }
                }
                value[i][mink] = j;
            }
        }
    }

    public mm(int pos, int col, int att, long seed)
    {
        int i;
        int j;
        int k;
        int min;
        int mink;
        Date d = new Date();

        rand = new Random(seed);

        positions = pos;
        colors = col;
        attempts = att;
        my_value = new int[positions];
        counter = new int[positions];
        value = new int[positions][colors];
        guess = new int[attempts][positions];
        result_black = new int[attempts];
        result_total = new int[attempts];
        tmp = new int[positions];
        attempt = 0;

        for (i = 0; i < positions; i++)
        {
            my_value[i] = rnd(0, colors /*- 1*/);
            counter[i] = 0;

            // fill value[i] array with the integers from 0 to colors-1 in random order
            for (j = 0; j < colors; j++)
                value[i][j] = -rnd(1, 100 * colors);
            for (j = 0; j < colors; j++)
            {
                min = 0;
                mink = 0;
                for (k = 0; k < colors; k++)
                {
                    if (value[i][k] < min)
                    {
                        min = value[i][k];
                        mink = k;
                    }
                }
                value[i][mink] = j;
            }
        }
    }

    public int [] get_guess() throws Exception
    {
        int i;
        int p;

        while ((p = test()) < positions)
        {
            counter[p]++;
            for (i = p + 1; i < positions; i++)
                counter[i] = 0;
             while (counter[p] == colors)
            {
                if (p == 0)
                    error("No solution possible");
                counter[p] = 0;
                p--;
                counter[p]++;
            }
        }

        return guess[attempt];
    }

    public void set_result(mm_result res)
    {
        result_black[attempt] = res.black;
        result_total[attempt] = res.white + res.black;
        attempt++;
    }

    public mm_result set_guess(int att[])
    {
        int i;
        int j;
        mm_result res = new mm_result();

        res.black = 0;
        res.white = 0;

        for (i = 0; i < positions; i++)
        {
            if (att[i] == my_value[i])
                res.black++;
        }

        for (i = 0; i < positions; i++)
            tmp[i] = my_value[i];
        for (i = 0; i < positions; i++)
        {
            for (j = 0; j < positions; j++)
                if (att[i] == tmp[j])
                    break;

            if (j < positions)
            {
                res.white++;
                tmp[j] = -1;
            }
        }

        res.white -= res.black;

        return res;
    }

    public void print_code()
    {
	for (int i = 0; i < positions; i++)
	    System.out.print(my_value[i] + " ");
	System.out.println();
    }

    private void error(String msg) throws Exception
    {
        throw new Exception(msg);
    }

    private int rnd(int min, int max)
    {
        int r = rand.nextInt();
        if (r < 0)
            r = -r;
        return min + r % (max - min);
    }

    private int test()
    {
        int i;
        int j;
        int g;
        int black;
        int total;
        int p = positions;

        for (i = 0; i < positions; i++)
            guess[attempt][i] = value[i][counter[i]];

        for (g = 0; g < attempt; g++)
        {
            // test black

            black = 0;
            for (i = 0; i < p; i++)
            {
                if (guess[attempt][i] == guess[g][i])
                {
                    black++;
                    if (black > result_black[g])
                        p = i;
                }
                else
                {
                    if (positions - i - 1 < result_black[g] - black)
                        p = i;
                }
            }

            // test total

            total = 0;
            for (i = 0; i < positions; i++)
                tmp[i] = guess[g][i];
            for (i = 0; i < p; i++)
            {
                for (j = 0; j < positions; j++)
                    if (guess[attempt][i] == tmp[j])
                        break;

                if (j < positions)
                {
                    total++;
                    tmp[j] = -1;
                    if (total > result_total[g])
                        p = i;
                }
                else
                {
                    if (positions - i - 1 < result_total[g] - total)
                        p = i;
                }
            }
        }

        return p;
    }

};

