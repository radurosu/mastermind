import java.io.*;
import java.util.Date;
public class mmsolver{

   // boolean tried[][][][] = new boolean[6][6][6][6];

    int board[][] = new int[6][10];
    int guess[] = {0,0,0,0,0,0};

    int numOfGuesses = 0;
    double totalGuesses = 0.0;
    int guessTotalTable[] ={0,0,0,0,0,0,0,0,0};
    int guessNumber =0;

    boolean solved = false;
    int theMaster[] = {0, 0, 0, 0};//the master in array format
    int masterCopy[] = {0,0,0,0};
    int masterInt  =0;  //the master in interger format
    String masterString = "0000";//the master in string format

    public String outputString =" \n";
    DataInputStream in=new DataInputStream(System.in);

    public static void main(String[] args)throws java.io.IOException{
        System.out.println("Welcome to the MasterMind solver!");
        mmsolver solver = new mmsolver();
        solver.runSolver();
    }//end main

    public mmsolver(){  //constructor

    }
    public mmsolver(String inputMaster){
        masterString = inputMaster;}

    public void runSolver()//throws java.io.IOException
    {
          Date theTime = new Date();
          // for(int k = 0; k< 1000; k++){
            if(!getMaster())
                return;
   //         System.out.println("You entered: " + theMaster[0]+ theMaster[1]+ theMaster[2]+ theMaster[3]);

            int temp = 0;
            for(int i=0; i<6; i++)
                for(int j = 0; j<10; j++)
                board[i][j] = temp;
            //printBoard();

              solved = false;
              solve();
          /*    totalGuesses += guessNumber;
              guessTotalTable[guessNumber]++;

          double average = 0.0;
          average = totalGuesses/1000.0;
          System.out.println(" We ran 1000 times.");
          System.out.println("The average # of guesses: " + average);
          for(int l = 2; l< 9; l++)
              System.out.println(l + " Guesses: " + guessTotalTable[l]);
          Date theNewTime = new Date();
          //int totalTime =0;

         // totalTime = theNewTime.getMinutes() - theTime.getMinutes();
          //System.out.println("It took us: " + totalTime + " seconds.");
          System.out.println("Work brgun at: " + theTime.toString());
          System.out.println("We are finished at: " + theNewTime.toString());*/
    }
    public void solve()
    {
        outputString += ("To solve your master \nthe computer did the");
        outputString += ("\nfollowing:\n\t              B W\n");
        guessNumber = 1;
        int x = new RandomIntGenerator(1, 6).draw();
        int y = new RandomIntGenerator(1, 6).draw();
        guess[0] =x;
        guess[1] =x;
        guess[2] =y;
        guess[3] =y;
            GuessVMaster();

            for(int i = 0; i < 4; i++)
                board[i][guessNumber] = guess[i];
            board[4][guessNumber] = guess[4];
            board[5][guessNumber] = guess[5];


        outputString += ("Guess#:" + guessNumber+ "::");
            for(int i = 0; i < 4; i++)
                outputString += (board[i][guessNumber] + " ");
        outputString += ( "  " + board[4][guessNumber] + "  " + board[5][guessNumber]);
        outputString += ("\n");

        guessNumber++;
        while(!solved)
        {

            makeNewGuess();
           // System.out.print(". " );
            if(!verifyGuess())//if guess is not ok
                continue; //make an other guess
            //we have an ok guess ... register it
            GuessVMaster();

            for(int i = 0; i < 4; i++)
                board[i][guessNumber] = guess[i];
            board[4][guessNumber] = guess[4];
            board[5][guessNumber] = guess[5];

            outputString += ("Guess#:" + guessNumber+ "::");

            for(int i =0; i < 4; i++)
                outputString += (board[i][guessNumber] + " ");
            outputString += ( "  " + board[4][guessNumber] + "  " + board[5][guessNumber]);
            outputString += ("\n");
            if(guess[4] == 4)
                solved = true;
            else
                guessNumber++;//going to the next guess

        }//end while
        /*
        if(guessNumber > 6){
            System.out.println(" Ooopps we got a 7");
            printBoard();
        }*/
    }


    public void makeNewGuess(){
        for (int i=0; i<4; i++)
		    guess[i] = new RandomIntGenerator(1, 6).draw();
		//System.out.println(" " + guess.theGuess[0] + " " + guess.theGuess[1] + " " + guess.theGuess[2] + " " + guess.theGuess[3] );

    }
    public boolean verifyGuess(){

        int rowNumber = 1;
        int rowCopy[]= {0,0,0,0,0,0};
        int guessCopy[] = {0,0,0,0,0,0};

        while(rowNumber < guessNumber){// for every previous row
            // make a copy of that row and a copy of the guess

        //    System.out.print(rowNumber+ " ");

            for(int i = 0; i<4; i++){
                rowCopy[i] = board[i][rowNumber];
                guessCopy[i] = guess[i];
            }

            //check the blacks
            rowCopy[4] = 0;
            for(int i = 0; i < 4; i++)
                if(rowCopy[i] == guessCopy[i]){
                    guessCopy[i] = -1;//used
                    rowCopy[i] = -2;
                    rowCopy[4]++;
                }

            //check whites
            rowCopy[5] = 0;
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 4; j++)
                    if(guessCopy[i] == rowCopy[j]){
                        guessCopy[i]= -3;
                        rowCopy[j]=-4;
                        rowCopy[5]++;
                        break;
                    }
            if((rowCopy[4] != board[4][rowNumber]) ||
                (rowCopy[5] != board[5][rowNumber]))
                return false;
            // else ..that is if this row is ok with our guess go to  the next
            rowNumber++;
        }

        return true;
    }

    public void GuessVMaster(){
        guess[4]=0;
        guess[5]=0;
        int guessCopy[] = {0,0,0,0,0,0};
        for(int i = 0 ; i< 4 ; i++){
           guessCopy[i] = guess[i];
           masterCopy[i] = theMaster[i];}

        for(int i = 0; i < 4; i++){
            if(guessCopy[i] == masterCopy[i]){
                guessCopy[i] = -1;//used
                masterCopy[i] = -2;
                guess[4]++;
            }
        }

        for(int i =0; i < 4; i++)
            for(int j = 0; j <4; j++){
                if(guessCopy[j] == masterCopy[i]){
                    guessCopy[j] = -1; //used
                    masterCopy[i] = -2;
                    guess[5]++;
                    break;
                }
            }
    }


    public void printBoard(){
        System.out.println("\n The computer did this: ");
        for(int i = 1; i<= guessNumber; i++){
            for(int j = 0; j < 6; j++)
                System.out.print(board[j][i] + " ");
            System.out.println();
            }
    }

    public boolean getMaster()/*throws java.io.IOException*/{
       input_loop: while(true){
           // System.out.println("\nValid digits are 1..6, a master has four digits");
           // System.out.print("Please enter your master: ");

            //in.readLine();
            try{
                masterInt = Integer.valueOf(masterString).intValue();
            }
            catch(Exception NumberFormatExeption)
            {   outputString +=(" Bummer...sorry ...\nwe only take numbers!");
                return false;
                //continue;
                }
            theMaster[0] = masterInt/1000;
            masterInt = masterInt- theMaster[0]*1000;
            theMaster[1] = masterInt/100;
            masterInt = masterInt- theMaster[1]*100;
            theMaster[2] = masterInt/10;
            theMaster[3] = masterInt- theMaster[2]*10;
            for(int i =0 ; i<4 ; i++)
                if(theMaster[i] ==0 || theMaster[i]>6){
                    outputString += ("Your master was \nimproper.");
                    return false;
              //  continue input_loop;
            }
            //everything is fine
            break;
        }//end while
       /* for(int i =0; i <4; i++)
            theMaster[i]=new RandomIntGenerator(1, 6).draw();
         */
         return true;
        }
}


