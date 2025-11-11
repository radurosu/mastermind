import mm;
import mm_result;
import java.util.Date;

public class big_test
{
  public int POSITIONS = 1;
  public int COLORS = 2;
  public final static int GUESSES = 20;
  public final static int N = 100;

  public static void main(String args[])
  {
    big_test thisTest = new big_test();
    thisTest.run_tests();
  }

  public void run_tests(){


    Date nowTime = new Date();
    long longSeed = nowTime.getTime();

    COLORS = 2;
    POSITIONS =1;
    outer_loop:
    for(COLORS = 2 ; COLORS <= 12; COLORS++)
    {
        inter_loop:
            for(POSITIONS = 9; POSITIONS < 12; POSITIONS++){
              //  System.out.println(java.lang.Math.pow(COLORS, POSITIONS));
             /*   if( java.lang.Math.pow(COLORS, POSITIONS) <   1000000)
                   continue inter_loop;
               // if(POSITIONS < COLORS*2)
                 //   continue inter_loop;
                if( java.lang.Math.pow(COLORS, POSITIONS) >  30000000){
                    continue outer_loop;
                     }
                else
                {

                System.out.println("------");
*/

            Date beginTime = new Date();

            long sum_g = 0;
            long sum_gg = 0;
            int max_g = 0;

            int resultSum[] = new int[GUESSES];
            for(int i =0; i < GUESSES ; i++)
                resultSum[i] = 0;

            System.out.println("Colors:    " + COLORS + " \nPositions: "
                                    + POSITIONS + "\nRuns:      "+ N + "\n");
            System.err.println(COLORS+ "::" + POSITIONS );

            for (int i = 0; i < N; i++)
            {
     // System.err.print(i);
              int g = do_test(longSeed+i);
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

  public int do_test(long testNum)
  {
    mm logic = new mm(POSITIONS, COLORS, GUESSES, testNum);

    for (int i = 0; i < GUESSES; i++)
    {
      try
      {
	        mm_result result = logic.set_guess(logic.get_guess());
            if (result.black == POSITIONS){
                logic.set_result(result);
            //   logic.printPath(i+1);//prints what the computer did
           // logic.print_code();
    	        return i + 1;
    	    }
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
