import mmResult;
import java.util.Date;

public class mmTester
{
  public int POSITIONS = 1;
  public int COLORS = 2;
  public int GUESSES = 20;
  public final static int N = 1000;

  public static void main(String args[])
  {
    mmTester thisTest = new mmTester();
    thisTest.run_tests();
  }

  public void run_tests(){


    Date nowTime = new Date();
    long longSeed = nowTime.getTime();

    COLORS = 2;
    POSITIONS =1;
    outer_loop:
    for(COLORS = 6 ; COLORS <= 12; COLORS++)
    {
        inter_loop:
            for(POSITIONS = 1; POSITIONS < 8; POSITIONS++){

            Date beginTime = new Date();

            long sum_g = 0;
            long sum_gg = 0;
            int max_g = 0;

            int resultSum[] = new int[GUESSES];
            for(int i =0; i < GUESSES ; i++)
                resultSum[i] = 0;

            System.out.println("\nColors:    " + COLORS + " \nPositions: "
                                    + POSITIONS + "\nRuns:      "+ N );
            System.err.println(COLORS+ "::" + POSITIONS );

            for (int i = 0; i < N; i++)
            {
     // System.err.print(i);
              int g = do_test();
              sum_g += g;
              sum_gg += g * g;
              if (max_g < g)
	               max_g = g;

        	  resultSum[g]++;
        }

            double avg_g = ((double)sum_g) / N;
            double dev_g = Math.sqrt(((double)(N * sum_gg - sum_g * sum_g)) / (N - 1)) / N;
            System.out.println("Average: " + avg_g);
            System.out.println("Std.Dev: " + dev_g);
            System.out.println("Maximum: " + max_g);

   // for(int i = 1; i <= max_g; i++)
     //   System.out.println(i + " Guesses: " + resultSum[i]);

            Date endTime = new Date();

            long totalTime = endTime.getTime() - beginTime.getTime();
    /*int totalMinutes = endTime.getMinutes() - beginTime.getMinutes();
    int secs = endTime.getSeconds() - beginTime.getSeconds();
    if( secs < 0)
        secs += 60;
    System.out.println("It took us: " +
          totalTime/60000 + ":"+ (totalTime%=60000)/1000 + ". \n-------");
               // }*/
             System.out.println("Time: " +totalTime);

             }//end inter
    }//outter
  }//end main

  public int do_test()
  {
    mmSolver solver = new mmSolver(COLORS, POSITIONS);
    return solver.solve();
  }
}
