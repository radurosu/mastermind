// this version of MMS  is an MMS tester ... it runs the program automaticaly
// without user input
//
// last edited on 8/5/97.
// tryed the 1123 first guess.
// second guess hardcoded
import java.io.*;
import java.lang.*;
import java.util.Date;

public class mmsTester{
 int runs = 10000;
    boolean rangeing = true;
    int guessList[] = new int[1296]; //we will be starding at the one index

    int guessListIndex = 0; //holds the index in guessList where to get the next guess

    int board[][] = new int[6][10]; // the whole mastermind board 10 rows
                                    // and 6 collums 4 for guess 2 for response.
    int guess[] = {0,0,0,0,0,0};
    int intGuess = 0;

    int numOfGuesses = 0;
    double totalGuesses = 0.0;
    int guessTotalTable[] ={0,0,0,0,0,0,0,0,0};
    int guessNumber =0;

    boolean solved = false;
    int theMaster[] = {0,0,0,0};//the master in array format
    int masterCopy[] = {0,0,0,0};
    //******************************************************///////
    //begin datastructures for opitimization processes.
    int remainingGuessList[][][] = new int[9][277][4];

    int bestGuessList[][] = new int[1296][4];

    int posibleResponseTable[][] = new int[5][5];

    // int masterInt  =0;  //the master in interger format
    // String masterString = "0000";//the master in string format

    //  DataInputStream in=new DataInputStream(System.in);

    public static void main(String[] args)throws java.io.IOException{
        System.out.println("Welcome to the MasterMind solver!");
        mmsTester solver = new mmsTester();
        solver.runSolver();
    }//end main

    public mmsTester(){
        if(rangeing)
            System.out.println("with range");
        else
            System.out.println("with expected");
        //initialize the guessList
        for(int i = 0; i<1296; i++)
           guessList[i] = i;

        mixGuessList();
/*
        for(int i = 0; i< 9; i++)
            for(int j =0; j<277; j++)
                for(int k =0; k<4; k++)
                    remainingGuessList[i][j][k] = -1;

        for(int j =0; j<1296; j++)
            for(int k =1; k<4; k++)
                bestGuessList[j][k] = -1;

        for(int j =0; j<1296; j++)
            bestGuessList[j][0] = j;

        for(int i = 0; i< 5; i++)
            for(int j =0; j<5; j++)
                posibleResponseTable[i][j] = 0;
  */
    } // constructor

    public void runSolver() throws java.io.IOException{
        ///executes the solver for a set number of times on random codes
          Date theTime = new Date();

           for(int l = 0; l< runs; l++){
              if(l%100 ==0)
                 System.err.println(l);
              getMaster();

              //clearRemaingGuessList

            for(int i = 0; i< 9; i++)
                for(int j =0; j<277; j++)
                    for(int k =0; k<4; k++)
                        remainingGuessList[i][j][k] = -1;


              solved = false;
              solve();
              totalGuesses += guessNumber;
              guessTotalTable[guessNumber]++;
          }

          double average = 0.0;
          average = totalGuesses/(new Integer(runs).doubleValue());
          System.out.println(" We ran " + runs + " times.");
          System.out.println(" The average # of guesses: " + average);
          for(int l = 1; l< 9; l++)
              System.out.println(l + " Guesses: " + guessTotalTable[l]);
          Date theNewTime = new Date();
          //int totalTime =0;

         // totalTime = theNewTime.getMinutes() - theTime.getMinutes();
          //System.out.println("It took us: " + totalTime + " seconds.");
          System.out.println("Work begun at:      " + theTime.toString());
          System.out.println("We are finished at: " + theNewTime.toString());
    }

    public void solve()
    {
        int temp = 0;
        //initialize the mastermind board
        for(int i=0; i<6; i++)
           for(int j = 0; j<10; j++)
              board[i][j] = temp;

        mixGuessList(); //suffle the guess list.
        guessListIndex =0;  // the guess number we are at (non official)

        guessNumber = 1;
        makeFirstGuess();
        if(board[4][1] == 4)
            solved = true;
        else{
            guessNumber++; //2

            makeSecondGuess();

            if(board[4][2] == 4)
                solved = true;
            else
                guessNumber++;//3
       }

        while(!solved)
        {
           // System.out.println("------------");
  /*          buildRemainingGuessList(guessNumber);
           //System.out.println("eval");
            evalRemainingGuesses(guessNumber);

            int bestGuessNumber = choseNewGuess(guessNumber);

            evalGuesses(guessNumber);
            int bestGuessNumber = choseNewGuess();

            if(!(verifyGuess(remainingGuessList[guessNumber][bestGuessNumber][0])))
               { System.err.println("GOT ONE"); //make an other guess
               }
            //we have an ok guess ... register it
*/

// standard algoritm
            int newGuess =0;
            while(true){
                newGuess = makeNewGuess();
           //     System.out.print(". "+newGuess);
                if(verifyGuess(newGuess))
                    break;
            }


            GuessVMaster();

            for(int i = 0; i < 4; i++)
                board[i][guessNumber] = guess[i];
            board[4][guessNumber] = guess[4];//blacks
            board[5][guessNumber] = guess[5];//whites

            if(guess[4] == 4)
                solved = true;
            else
                guessNumber++;//going to the next guess
        }//end while
      //  printBoard();
    //  System.out.println("the master was: " + theMaster[0]+theMaster[1]+theMaster[2]+theMaster[3]);

        if(guessNumber > 6){
            System.out.println(" Ooopps we got a "+ guessNumber);
            printBoard();
        }
    }

    public void mixGuessList(){

        for(int next = 0; next < 1295; next++){
            int r = new RandomIntGenerator(next, 1295).draw();
            int temp = guessList[next];
            guessList[next]=guessList[r];
            guessList[r] = temp;
        }
        guessListIndex =0;
    }
/*
    public void buildRemainingGuessList(int guessNum){
        int index =0; //holds the count of possible guesses found.
        int newGuess =0;
        int oldGuessListIndex = guessListIndex;
        guessListIndex =0;
        if(guessNum == 3){
            while(guessListIndex < 1296)  //build the guesslist
            {
                intGuess =makeNewGuess();
                if(verifyGuess(intGuess)){
                    remainingGuessList[guessNum][index][0] = intGuess;
                    index++;
                }
            }
        }
        else{
            int newIndex =0;//used for the new list
            int oldIndex =0;//used for the old list
            while(remainingGuessList[guessNum-1][oldIndex][0] != -1)
            {    //if the guess still verifies
                if(verifyGuess(remainingGuessList[guessNum-1][oldIndex][0])){
                    remainingGuessList[guessNum][newIndex][0] =
                             remainingGuessList[guessNum-1][oldIndex][0];
                    newIndex++;
                }
                oldIndex++;
            }
        }
        guessListIndex = oldGuessListIndex;
        // ===============
        System.out.println("Remaining possibilities at" + guessNumber);
        int newIndex =0;
        while(remainingGuessList[guessNum][newIndex][0] != -1){
            System.out.print(" " +remainingGuessList[guessNum][newIndex][0]);
            newIndex++;
        }
    }
*/
    public void evalGuesses(int guessNum){
        int count = 0;

        for(int i = 0; ; i++){
            if(remainingGuessList[guessNum][i][0] != -1)
                count++;
            else
                break;
        }

        for(int index =0; index<1296; index++){
            //clear the table
            for(int i =0; i< 5; i++)
                for(int j=0; j<5; j++)
                    posibleResponseTable[i][j] =0;

            for(int i =0; i < count; i++)//compare guess to all others
                compare(bestGuessList[index][0], remainingGuessList[guessNum][i][0]);
                        //this number              //all remaining numbers
                   // compute the guess quality variables
            int totalResponses =0;
            int multiplyQuality = 1;
            int squaredQuality = 0;
            for(int i =0; i< 5; i++)
                for(int j=0; j<5; j++)
                    if(posibleResponseTable[i][j] > 0){
                        totalResponses++;
                        multiplyQuality = multiplyQuality * posibleResponseTable[i][j];
                        squaredQuality = (squaredQuality +
                                            ((posibleResponseTable[i][j])*(posibleResponseTable[i][j])));
                    }
            bestGuessList[index][1] = totalResponses;
            bestGuessList[index][2] = multiplyQuality;
            bestGuessList[index][3] = squaredQuality;

            index++;// go to next guess
        }
    }

    public void evalRemainingGuesses(int guessNum){
        int index = 0;
        int count = 0;

        for(int i = 0; ; i++){
            if(remainingGuessList[guessNum][i][0] != -1)
                count++;
            else
                break;
        }
       // System.out.println(count);
        while(index < count){// for all remaining guesses
            //clear the table
            for(int i =0; i< 5; i++)
                for(int j=0; j<5; j++)
                    posibleResponseTable[i][j] =0;

            for(int i =0; i < count; i++)//compare guess to all others
                compare(remainingGuessList[guessNum][index][0], remainingGuessList[guessNum][i][0]);

            // compute the guess quality variables
            int totalResponses =0;
            int multiplyQuality = 1;
            int squaredQuality = 0;
            for(int i =0; i< 5; i++)
                for(int j=0; j<5; j++)
                    if(posibleResponseTable[i][j] > 0){
                        totalResponses++;
                        multiplyQuality = multiplyQuality * posibleResponseTable[i][j];
                        squaredQuality = (squaredQuality +
                                            ((posibleResponseTable[i][j])*(posibleResponseTable[i][j])));
                    }
            remainingGuessList[guessNum][index][1] = totalResponses;
            remainingGuessList[guessNum][index][2] = multiplyQuality;
            remainingGuessList[guessNum][index][3] = squaredQuality;

            index++;// go to next guess
        }
    }

    public int choseNewGuess(int guessNum){
        int index =0;
        int bestIndex = 0;  // the location of the best Guess so far

        int bestSum = 1000000000;
        if(rangeing)
            bestSum = 0;
        while(remainingGuessList[guessNum][index][0] >=0){
            ///range
           if(rangeing){
            if(remainingGuessList[guessNum][index][1] > bestSum){
                bestIndex = index ; // reset the best index     and sum
                bestSum = remainingGuessList[guessNum][bestIndex][1] ;
            }
            else if(remainingGuessList[guessNum][index][1] == bestSum){//if two sums
                if(remainingGuessList[guessNum][index][2] >         //are equal
                            remainingGuessList[guessNum][index][2] ){
                            bestIndex = index ; // no need to do the sum
                            }
            }
          }
          else{
           //expected
           if(remainingGuessList[guessNum][index][3] < bestSum){
                 bestIndex = index ; // reset the best index     and sum
                bestSum = remainingGuessList[guessNum][index][3] ;
            }
          }
            index++;
         //   if(remainingGuessList[guessNum][index][0] == -1)
           //     System.out.println("--------");
        }
      //  System.out.println("++++++++++++++");
        if(!verifyGuess(remainingGuessList[guessNumber][bestIndex][0])){

            System.out.print("TROUBLE\n guess number:"+ guessNumber +
                        "    best index: " + bestIndex + "\n");
           printRemainingGuessTable();
           printBoard();
           System.out.println("END");
           System.exit(0);}
        return bestIndex;
    }

    public int choseNewGuess(){//no input means we are looking at the biglist

        int bestIndex = 0;  // the location of the best Guess so far

        int bestSum = 1000000000;
        if(rangeing)
            bestSum = 0;
        for(int index =0; index <1296; index++){
            ///range
           if(rangeing){
            if(bestGuessList[index][1] > bestSum){
                bestIndex = index ; // reset the best index     and sum
                bestSum = bestGuessList[bestIndex][1] ;
            }
            else if(bestGuessList[index][1] == bestSum){//if two sums
                if(bestGuessList[index][2] >         //are equal
                            bestGuessList[index][2] ){
                            bestIndex = index ; // no need to do the sum
                            }
            }
          }
          else{
           //expected
           if(bestGuessList[index][3] < bestSum){
                 bestIndex = index ; // reset the best index     and sum
                bestSum = bestGuessList[index][3] ;
            }
          }

        }
      //  System.out.println("++++++++++++++");
        if(!verifyGuess(bestGuessList[bestIndex][0])){

            System.out.print("TROUBLE\n guess number:"+ guessNumber +
                        " best index: " + bestIndex + "\n");
        }
        return bestIndex;
    }

    public int makeNewGuess(){

        intGuess = guessList[guessListIndex];
        guessListIndex++;   //index up for next call
        return intGuess;
    }

    public void makeFirstGuess(){
        guessNumber = 1;

        guess[0] =1;
        guess[1] =1;
        guess[2] =2;
        guess[3] =3;

        GuessVMaster();

        for(int i = 0; i < 4; i++)
                board[i][guessNumber] = guess[i];
            board[4][guessNumber] = guess[4];
            board[5][guessNumber] = guess[5];
    }

    public void makeSecondGuess(){
        guessNumber = 2;
        guess[0] =0;
        guess[1] =0;
        guess[2] =0;
        guess[3] =0;

        if(board[4][1] == 0){
            if(board[5][1] == 4){
              guess[0] =2;
              guess[1] =3;
              guess[2] =1;
              guess[3] =1;
            }
            else if(board[5][1] == 3){
              guess[0] =2;
              guess[1] =3;
              guess[2] =4;
              guess[3] =5;
            }
            else if(board[5][1] == 2){
              guess[0] =2;
              guess[1] =4;
              guess[2] =3;
              guess[3] =4;
            }
            else if(board[5][1] == 1){
              guess[0] =2;
              guess[1] =4;
              guess[2] =4;
              guess[3] =5;
            }
            else if(board[5][1] == 0){
              guess[0] =4;
              guess[1] =4;
              guess[2] =5;
              guess[3] =6;
            }
        }
        else if(board[4][1] == 1){
            if(board[5][1] == 3){//4
              guess[0] =1;
              guess[1] =3;
              guess[2] =1;
              guess[3] =2;
            }
            else if(board[5][1] == 2){//84
              guess[0] =1;
              guess[1] =4;
              guess[2] =1;
              guess[3] =5;
            }
            else if(board[5][1] == 1){
              guess[0] =1;
              guess[1] =4;
              guess[2] =1;
              guess[3] =5;
            }
            else if(board[5][1] == 0){
              guess[0] =1;
              guess[1] =4;
              guess[2] =4;
              guess[3] =5;
            }
        }
        if(board[4][1] ==2){
            if(board[5][1] == 2){
              guess[0] =1;
              guess[1] =3;
              guess[2] =2;
              guess[3] =1;
            }
            else if(board[5][1] <= 1){
              guess[0] =1;
              guess[1] =2;
              guess[2] =4;
              guess[3] =5;
            }
        }
        if(board[4][1] == 3){

            guess[0] =1;
            guess[1] =2;
            guess[2] =4;
            guess[3] =5;
        }
        GuessVMaster();

        for(int i = 0; i < 4; i++)
                board[i][guessNumber] = guess[i];
            board[4][guessNumber] = guess[4];
            board[5][guessNumber] = guess[5];

    }

    public boolean verifyGuess(int thisGuess){
        //verifies that a guess is valid consideting the previous ones.
        //first converts the guess from number format to straight chars.
        guess[0] = thisGuess/216;
        thisGuess = thisGuess- guess[0]*216;
        guess[1] = thisGuess/36;
        thisGuess = thisGuess- guess[1]*36;
        guess[2] = thisGuess/6;
        guess[3] = thisGuess- guess[2]*6 + 1;

        guess[0] = guess[0] +1;
        guess[1] = guess[1] +1;
        guess[2] = guess[2] +1;


       //      System.out.println(guess[0] + " " + guess[1] + " " + guess[2] + " " + guess[3] );

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
        //compares guesss to a master
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


    public void compare(int intCodeX, int intCodeY){ //
        int codeX[] = new int[4];
        int codeY[] = new int[4];

        codeX[0] = intCodeX/216;
        intCodeX = intCodeX- codeX[0]*216;
        codeX[1] = intCodeX/36;
        intCodeX = intCodeX- codeX[1]*36;
        codeX[2] = intCodeX/6;
        codeX[3] = intCodeX- codeX[2]*6 + 1;

        codeX[0] = codeX[0] +1;
        codeX[1] = codeX[1] +1;
        codeX[2] = codeX[2] +1;
        //--
        codeY[0] = intCodeY/216;
        intCodeY = intCodeY- codeY[0]*216;
        codeY[1] = intCodeY/36;
        intCodeY = intCodeY- codeY[1]*36;
        codeY[2] = intCodeY/6;
        codeY[3] = intCodeY- codeY[2]*6 + 1;

        codeY[0] = codeY[0] +1;
        codeY[1] = codeY[1] +1;
        codeY[2] = codeY[2] +1;



        int blacks = 0;
        int whites = 0;

        int guessCopy[] = {0,0,0,0,0,0};

        for(int i = 0 ; i< 4 ; i++){
           guessCopy[i] = codeX[i];   ///cutting corners here.
           masterCopy[i] = codeY[i];}

        for(int i = 0; i < 4; i++){
            if(guessCopy[i] == masterCopy[i]){
                guessCopy[i] = -1;//used
                masterCopy[i] = -2;
                blacks++;
            }
        }

        for(int i =0; i < 4; i++)
            for(int j = 0; j <4; j++){
                if(guessCopy[j] == masterCopy[i]){
                    guessCopy[j] = -1; //used
                    masterCopy[i] = -2;
                    whites++;
                    break;
                }
            }
         posibleResponseTable[blacks][whites]+= 1;
    }

    public void printBoard(){
        System.out.println("\n The computer did this: ");
        for(int i = 1; i<= guessNumber; i++){
            for(int j = 0; j < 4; j++)
                System.out.print(board[j][i] + " ");
            System.out.print(" ");
            for(int j = 4; j<6;j++)
                System.out.print(board[j][i] + " ");
            System.out.println();
            }
    }

    public boolean getMaster()throws java.io.IOException{
    //get the master code
         for(int i =0; i <4; i++)
            theMaster[i]=new RandomIntGenerator(1, 6).draw();
         return true;

     /*      input_loop: while(true){
            System.out.println("\nValid digits are 1..6, a master has four digits");
            System.out.print("Please enter your master: ");

            masterString = "2233";//in.readLine();
            try{
                masterInt = Integer.valueOf(masterString).intValue();
            }
            catch(Exception NumberFormatExeption)
            {   System.out.println(" Bummer...sorry but we only take numbers!");
                continue;}
            theMaster[0] = masterInt/1000;
            masterInt = masterInt- theMaster[0]*1000;
            theMaster[1] = masterInt/100;
            masterInt = masterInt- theMaster[1]*100;
            theMaster[2] = masterInt/10;
            theMaster[3] = masterInt- theMaster[2]*10;
            for(int i =0 ; i<4 ; i++)
                if(theMaster[i] ==0 || theMaster[i]>6){
                System.out.println("Your master was improper.");
                continue input_loop;
            }
            //everything is fine
            break;
        }//end while  */

    }

    public void printRemainingGuessTable(){
           for(int i = 0; i< 9; i++){
                System.out.println("ROW " + i);
                for(int j =0; j<277; j++){
                       if( remainingGuessList[i][j][0] >= 0)
                         System.out.println(remainingGuessList[i][j][0]+ " " +
                            remainingGuessList[i][j][1]+ " "+
                            remainingGuessList[i][j][2]+ " "+
                            remainingGuessList[i][j][3]);
                }
           }
     System.out.println("the master was: " + theMaster[0]+theMaster[1]+theMaster[2]+theMaster[3]);
    }
}


