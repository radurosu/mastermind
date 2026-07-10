import java.io.*;
import java.util.Date;
public class MMSAsolver{

    int tried[][][][] = new int[6][6][6][6];
    boolean trouble = false;

    public int board[][] = new int[6][10];//10 just for safety we only need 8
    int guess[] = {0,0,0,0,0,0};

/*    int numOfGuesses = 0;
    double totalGuesses = 0.0;
    int guessTotalTable[] ={0,0,0,0,0,0,0,0,0};
*/
    int guessNumber =0;

    boolean solved = false;
    int theMaster[] = {0, 0, 0, 0};//the master in array format
    int masterCopy[] = {0,0,0,0};
    int masterInt  =0;  //the master in interger format
    String masterString = "0000";//the master in string format

    DataInputStream in=new DataInputStream(System.in);

    MMSAsolver(){  //constructor
            int temp = 0;
            for(int i=0; i<6; i++)
                for(int j = 0; j<10; j++)
                board[i][j] = temp;
            for(int i = 0; i< 6;i++)
                for(int j = 0; j < 6; j++)
                    for(int k =0; k<6; k++)
                        for(int l =0;l<6;l++)
                            tried[i][j][k][l] = temp;
    }
    public void mmsolver(){  //constructor
        int temp = 0;
        for(int i=0; i<6; i++)
           for(int j = 0; j<10; j++)
              board[i][j] = temp;

        for(int i = 0; i< 6;i++)
            for(int j = 0; j < 6; j++)
                for(int k =0; k<6; k++)
                    for(int l =0;l<6;l++)
                        tried[i][j][k][l] = temp;

    }


    public void firstGuess(){
        guessNumber = 1;
        int x = new RandomIntGenerator(1, 6).draw();
        int y =x;
        while(y==x){
            y=new RandomIntGenerator(1, 6).draw();
        }
        guess[0] =x;
        guess[1] =x;
        guess[2] =y;
        guess[3] =y;

        for(int i = 0; i < 4; i++)
            board[i][guessNumber] = guess[i];

        tried[x-1][x-1][y-1][y-1]=1;

    }

    public void nextGuess(){
        int tries = 0;
        guessNumber++;
        boolean valid = false;
        while(true){//get a new guess
           tries++;
           if(tries%10000 == 0){// secrity code if user messed up
               if(checkForProblem()){
                   for(int i = 0; i < 4; i++) //copy the new guess
                        board[i][guessNumber] = 10;
                   return;
               }
           }

           makeNewGuess();
           if(!verifyGuess())
                continue;
           else
                break;
        }
        for(int i = 0; i < 4; i++) //copy the new guess
           board[i][guessNumber] = guess[i];
    }


    public void makeNewGuess(){
        for (int i=0; i<4; i++)
		    guess[i] = new RandomIntGenerator(1, 6).draw();
    }

    public boolean verifyGuess(){

        int rowNumber = 1;
        int rowCopy[]= {0,0,0,0,0,0};
        int guessCopy[] = {0,0,0,0,0,0};

        if(tried[guess[0]-1][guess[1]-1][guess[2]-1][guess[3]-1] == 1)
            return false;//if we have done this one before get an other one
        else
            tried[guess[0]-1][guess[1]-1][guess[2]-1][guess[3]-1] = 1;

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

    public boolean checkForProblem(){

        for(int i = 0; i< 6;i++)
           for(int j = 0;j<6;j++)
              for(int k =0; k<6;k++)
                 for(int l =0;l<6;l++) //if any location has a 0 it has no
                    if(tried[i][j][k][l] == 0)//been tried so
                        return false;           //we have not tried everything
        return true;
    }

}


