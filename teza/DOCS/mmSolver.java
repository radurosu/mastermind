import java.io.*;
import java.lang.*;
import java.util.*;


public class mmSolver{

    public int counter =0;
    public int colors;          //the number of colors
    public int positions;       //the number of pieces in a code

    public int totalCodes = 2;

    public int codeSetMinimum =1;
    public int codeList[];     //the code list i.e. all possible code for
                               // this m and n == pow(m*n);
    public mmCode codeSet[];   // an array of codes;

    public mmCode master;
    public mmCode guessList[];      //List of previous codes
    public mmResult resultList[];   //List of previous results
    public int guessIndex = 0; //specifies where in the guess list we are geting the next guess
    public int guessNumber = 0;

    public Vector codeSetVector;

    private Random random =  new Random();
    //-----------------------------------------------------------------

    public mmSolver(int col, int pos)
    {
        colors = col;
        positions = pos;

        totalCodes = pow(colors, positions);
        codeList = new int[totalCodes];
        codeSet = new mmCode[totalCodes];
        codeSetVector = new Vector(totalCodes+10);

        master = new mmCode(colors, positions);
        guessList = new mmCode[20];
        resultList  = new mmResult[20];

        initializeCodeLists();  //inits codeList & codeSet
        randomizeCodeList();
        makeCodeSet();          //transforms numbers to mm codes

        setMaster();
 //       printCodeSet();
 //       testCompareFunction();

    //    loadVector();  // puts that codes from the codeset into the initial vector.
      //  printVector(codeSetVector); //testing function
     }

    public int pow(int base, int power){//returns the base to the power specified
        int result = base;

        if(power == 0)
            return 1;
        else
            return pow(base, power -1)*base;
    }

    public void initializeCodeLists(){
        int i, j, h;
        for(h =0; h<totalCodes; h++)
            codeSet[h] = new mmCode(colors, positions);
        //inits codeList & codeSet

        for(i = 0; i < totalCodes; i++){
            codeList[i] = i;
            for(j =0; j < positions; j++)
                codeSet[i].code[j] = 10;
        }
        for(i= 0; i < 20; i++){
            guessList[i] = new mmCode(colors, positions);
            resultList[i] = new mmResult();
        }


    }

    public void randomizeCodeList(){
        int index;
        int temp;
        for(int next =0; next < totalCodes -1; next++){

                index = rand(next, totalCodes);
                temp = codeList[next];
                codeList[next] = codeList[index];
                codeList[index] = temp;
        }
    }

    public void makeCodeSet(){
        // a bitch function that transforms numbers such as 1 to 1111111 or
        //2 to 112
        int codeListCopy[] = new int[totalCodes];
        for(int c =0; c < totalCodes; c++)
            codeListCopy[c] = codeList[c];

        for(int i =0; i < totalCodes; i++){ //don't try to figure this out!
            for(int j =0; j<positions; j++){
               // System.out.println("j: " + j + " divisor: " + pow(colors, positions - (j+1)));
                codeSet[i].code[j] = codeListCopy[i]/pow(colors, positions - (j+1));
                codeListCopy[i] = codeListCopy[i] - codeSet[i].code[j]*pow(colors, positions - (j+1));
            }

            for(int j =0; j<positions; j++)
                codeSet[i].code[j]++;
        }
    }

    public int rand(int min, int max){
        int r = random.nextInt();
        if(r<0)
            r = -r;
        return min + r%(max-min);
    }

    public void printCodeSet(){

        for(int i =0; i < totalCodes; i++){
            for(int j =0; j < positions; j++)
                System.out.print(codeSet[i].code[j] + " ");
            System.out.print("\n");
        }
        System.out.println("\n" + totalCodes);
    }

    public void testCompareFunction(){//was used in testing
        mmResult result = new mmResult();
        codeSet[0].print();
        codeSet[1].print();
        result = compareCodes(codeSet[0], codeSet[1]);
        result.print();
    }

    public mmResult compareCodes(mmCode a, mmCode b){
        int pos = a.getPositions();
        int col = a.getColors();
        mmResult thisResult = new mmResult();
        mmCode tmp = new mmCode(col, pos);
        int i;
        int j;

        for (i = 0; i < pos; i++){
            if (a.code[i] == b.code[i])
                thisResult.blacks++;
        }

        for (i = 0; i < pos; i++)
            tmp.code[i] = b.code[i];

        for (i = 0; i < pos; i++)
        {
            for (j = 0; j < pos; j++)
                if (a.code[i] == tmp.code[j])
                    break;

            if (j < pos)
            {
                thisResult.whites++;
                tmp.code[j] = -1;
            }
        }

        thisResult.whites -= thisResult.blacks;

        return thisResult;
    }

    public void loadVector(){
        for(int i =0; i<totalCodes; i++)
        {
            codeSetVector.addElement(codeSet[i]);
        }
        codeSetVector.trimToSize();
    }

    public void printVector(Vector myVector){
        int vSize = myVector.size();

        for(int i =0; i < vSize; i++){
            mmCode thisCode = (mmCode)myVector.elementAt(i);
            thisCode.print();
        }
        System.out.println("  ");
    }

    public void setMaster(){

        int intMaster = rand(0, totalCodes);

            for(int j =0; j<positions; j++){
               // System.out.println("j: " + j + " divisor: " + pow(colors, positions - (j+1)));
                master.code[j] = intMaster/pow(colors, positions - (j+1));
                intMaster = intMaster - master.code[j]*pow(colors, positions - (j+1));
            }

            for(int j =0; j<positions; j++)
                master.code[j]++;

         //System.out.println("Master is:");
        // master.print();
    }

    public boolean verifyGuess( mmCode guess,int guessNum){
        int rowNumber = 1;
        mmResult thisResult;

        while(rowNumber < guessNum){
            thisResult = compareCodes(guessList[rowNumber], guess);
            if((thisResult.blacks == resultList[rowNumber].blacks) &&
                (thisResult.whites == resultList[rowNumber].whites)){
                    rowNumber++;
                    continue;
                }
            else
                return false;
        }
        return true;
    }
    public mmCode getGuess(){
        return codeSet[guessIndex++];
    }

    public boolean setGuess(){
        guessNumber++;
        mmCode code;
        while(true){
            code = getGuess();
            if(verifyGuess(code, guessNumber))
                break;
        }
        guessList[guessNumber] = code;
        resultList[guessNumber] = compareCodes(code, master);

        if(resultList[guessNumber].blacks == positions)
            return true;

        return false;
    }

    public int solve(){

        while(!setGuess()){}
        return guessNumber;
    }

    public static void main(String args[]){
        int i;
        int runs = 1000;
        int guessSum = 0;
        int resultList[] = new int[20];
        for(i = 0; i < 20; i++)
            resultList[i] =0;

        for(i =0; i <runs; i++){
            mmSolver mySolver = new mmSolver(6, 4);
            resultList[mySolver.solve()]++;
        }
        for(i =1; i<20; i++)
            if(resultList[i] != 0){
                System.out.println(i + " Guesses: " +resultList[i]);
                guessSum += resultList[i]*i;
            }
        System.out.println("Average: " + (float)guessSum/runs);

    }
}
