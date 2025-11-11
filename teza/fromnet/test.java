import mm;
import mm_result;
import java.util.*;
import java.lang.Long;

public class test
{
  public final static int POSITIONS = 4;
  public final static int COLORS = 6;
  public final static int GUESSES = 10;
  public final static long N = 10;

  public static void main(String args[])
  {
    Date nowTime = new Date();       // create a seed for this test run
    long longSeed = nowTime.getTime();

    long sum_g = 0;
    long sum_gg = 0;
    long max_g = 0;
    long cnt_g[] = new long[GUESSES];
    for (int i = 0; i < GUESSES; i++)
      cnt_g[i] = 0;

    for (int i = 0; i < N; i++)
    {
      int g = do_test(longSeed + i);
      cnt_g[g - 1] += 1;
      sum_g += g;
      sum_gg += g * g;
      if (max_g < g)
	max_g = g;
    }

    double avg_g = ((double)sum_g) / N;
    double dev_g = Math.sqrt(((double)(N * sum_gg - sum_g * sum_g)) / (N - 1)) / N;
    System.out.println("Average: " + avg_g);
    System.out.println("Std.Dev: " + dev_g);
    System.out.println("Maximum: " + max_g);
    for (int i = 0; i < GUESSES; i++)
      System.out.println((i + 1) + " guesses: " + cnt_g[i]);
  }

  public static int do_test(long seed)
  {
    mm logic = new mm(POSITIONS, COLORS, GUESSES, seed);

    for (int i = 0; i < GUESSES; i++)
    {
      try
      {
	mm_result result = logic.set_guess(logic.get_guess());
	if (result.black == POSITIONS)
	  return i + 1;
        logic.set_result(result);
      }
      catch (Exception e)
      {
	e.printStackTrace();
      }
    }
    return GUESSES;
  }
}
