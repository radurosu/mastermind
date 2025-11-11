import java.io.*;
import java.lang.*;
import java.util.*;


public class depthSearch{
    int compareCounter =0;
    int counter =0;
    int exits = 0;
    int colors;  //  the number of colors
    int positions;  //  the number of pieces in a code
    int posibleResponses;

    int high_level =0;
    int totalCodes = 2;

    int codeSetMinimum =1;
    int codeList[];   //the code list i.e. all possible code for
                            // this m and n == pow(m*n);

    mmCode codeSet[];   // an array of codes;

    mmCode sp1, sp2, sp3;

    Vector codeSetVector;
  //  Vector spVector;
    private Random random =  new Random();
    //-----------------------------------------------------------------

    public depthSearch(int col, int pos)
    {
        colors = col;
        positions = pos;
        posibleResponses = (positions*(positions+3))/2;//optimiser

        totalCodes = pow(colors, positions);
        codeList = new int[totalCodes];
        codeSet = new mmCode[totalCodes];
        codeSetVector = new Vector(totalCodes+10);

        initializeCodeLists();  //inits codeList & codeSet
 //       randomizeCodeList();
        makeCodeSet();          //transforms numbers to mm codes
        setTuplets();
 //       printCodeSet();
 //       testCompareFunction();

        loadVector();  // puts that codes from the codeset into the initial vector.
      //  printVector(codeSetVector); //testing function
     // the big recursive function
        codeSetMinimum = getMinimumOfSet1(codeSetVector,colors, positions, 1);

        System.err.println("For "+ colors + " colors and " +
                    positions + " positions there are " + totalCodes +
                    " totalCodes \n and the minimum number of guesses is: "+
                    codeSetMinimum+"\ncompareCounter: " + compareCounter +"\ncounter: " + counter + "\nexits:" +exits
                    + "\n high level: " + high_level);
     }

    public int pow(int base, int power){//returns the base to the power specified
        int result = base;

        if(power == 0)
            return 1;
        else
            return pow(base, power -1)*base;
    }

    public void initializeCodeLists(){
        for(int h =0; h<totalCodes; h++)
            codeSet[h] = new mmCode(positions, colors);
        //inits codeList & codeSet
        for(int i = 0; i < totalCodes; i++){
            codeList[i] = i;
            for(int j =0; j < positions; j++)
                codeSet[i].code[j] = 10;
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
        compareCounter++;
        int pos = a.getPositions();
        int col = a.getColors();
        mmResult thisResult = new mmResult();
        mmCode tmp = new mmCode(pos,col);
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
 /*****----------**********------------*************------------**********
    the deep search algorithm follows
    ******************************************************************/
    public int getMinimumOfSet1(Vector theSet, int colors, int positions, int level){
        level =0;
        Date x = new Date();
        System.out.println(x);
 //this function is called in order to get some output
        Vector thisSet = theSet;
        int vectorSize = thisSet.size();
        int arrayOfSums[];
 //printVector(thisSet);
        int beginPoint = 0;
        int stopPoint = pow(colors,positions -1);
 //stopPoint = 9;
        arrayOfSums = new int[stopPoint];


        for(int i = beginPoint; i<stopPoint; i++){
            if(verifyUniquenesOf(i, (mmCode)thisSet.elementAt(i), thisSet)){
                mmCode tmp = (mmCode)thisSet.elementAt(i);
                tmp.print();
                arrayOfSums[i] = getSumOf((mmCode)thisSet.elementAt(i), thisSet, colors, positions, level );
            }
        }

        for(int i = 0; i<stopPoint; i++)
           System.err.print(arrayOfSums[i] + "  ");

        x = new Date();
        System.out.println("\n" + x);
        return getMinimumOf(arrayOfSums, stopPoint);
    }

    public int getMinimumOfSet(Vector theSet, int colors, int positions, int level){

        Vector thisSet = theSet;
        int vectorSize = thisSet.size();
        int arrayOfSums[] = new int[vectorSize];

        for(int i = 0; i< vectorSize; i++){
            counter++;
            arrayOfSums[i] = getSumOf((mmCode)thisSet.elementAt(i), thisSet, colors, positions, level);
            //optimization code
            //System.out.println(vectorSize+ " min: " + arrayOfSums[i]);
             if(vectorSize <= posibleResponses)
                if(arrayOfSums[i] == (vectorSize + (vectorSize -1))){
                    exits++;//
                    break;
                }
        }
        return getMinimumOf(arrayOfSums, vectorSize);
    }

    public int getSumOf(mmCode thisCode, Vector remaingCodes, int colors, int positions, int level)
    {   level++;
        if(level>high_level){
            high_level = level;
          }

        int sum = 1; // the sum to be returned

        Vector resultTable[][] = new Vector[positions+1][positions+1];//vectors of sub brances
        int vectorSize = remaingCodes.size();

        mmResult currentResult = new mmResult();

        for(int i =0; i < vectorSize; i++){
            //compare each thisCode to the rest and store the thisCode we compared
            // against in the vector of its result.
            currentResult.blacks =0;  //set blacks and  whites to zero
            currentResult.whites =0;

            currentResult = compareCodes(thisCode, (mmCode)remaingCodes.elementAt(i));

            if(resultTable[currentResult.blacks][currentResult.whites] == null)
                resultTable[currentResult.blacks][currentResult.whites] = new Vector();

            resultTable[currentResult.blacks][currentResult.whites].addElement(/*mmCode*/remaingCodes.elementAt(i));
        }
  for(int whites = 0 ; whites <= positions; whites++)
        {
            white_loop:
            for(int blacks = 0; blacks <positions; blacks++)//notice we are skiping all blacks
            {                                //that is the guess itself and we are not intrested in it
                if(blacks+whites <= positions){
                    if(resultTable[blacks][whites] == null){
                         //sum+= 0;
                        continue white_loop;
                    }
                    int size = resultTable[blacks][whites].size();
                    if(size < 7){
                        sum += (size + (size -1) + size);
                        continue white_loop;
                    }

                    else{
                     int min=getMinimumOfSet(resultTable[blacks][whites],colors,positions, level);
                        sum += (min + resultTable[blacks][whites].size());//this was the trick
                    }
                }
            }
        }
        return sum;
    }

    public int getMinimumOf(int array[], int size){
        int min = 100000;// just a humangous number
        for(int i =0;  i < size; i++){
            if(array[i] == 0) continue;
            if(array[i] < min)
                min = array[i];
        }
        return min;
    }


/*****************************************************************************
deep search algorithm ends
*****************************************************************************////


/***********************************************************************
begin optimization code
**********************************************************************/

    public void setTuplets(){
        for(int i =0; i < totalCodes; i++){
            codeSet[i].setCodeTuplets();
        }
    }


    public boolean verifyUniquenesOf(int codeNumber, mmCode thisCode, Vector otherCodes){
        int i;
        boolean result;
        for(i = 0; i<codeNumber; i++){
            result = compareTuplets(thisCode, (mmCode)otherCodes.elementAt(i));
            if(result == true)//if they are equal
                return false;
        }
        return true;
    }

    public boolean compareTuplets(mmCode a, mmCode b){
        int pos = a.getPositions();

        for(int i = 0 ; i <= pos; i++)
            if(a.nTuplets[i] != b.nTuplets[i])
                return false;

        return true;
    }




/***************************************************************************
end optimization code
***************************************************************************/

    public static void main(String[] args){
        if(args.length != 2){
            System.out.println("please give 2 arguments");
            System.exit(0);
        }
        depthSearch mySearch =
            new depthSearch(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
    }
}


