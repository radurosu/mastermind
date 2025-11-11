public final class mmCode extends Object{

    public int code[];      //the code itself
    public int positions;   //the number of positions
    public int colors;
    public int nTuplets[];    //desctiption of the composition of the code.

    private boolean temp[];  // used for the creation of nplets

    int i, j, k;

    public mmCode(int pos, int col){
        positions = pos;
        colors = col;
        code = new int[positions];
        nTuplets = new int[positions+1];
        temp = new boolean[positions];
        for( i =0; i < positions; i++){
            code[i]=(-1);
            nTuplets[i]=0;
            temp[i]= true;
        }
        nTuplets[positions]= 0;
    }

    public void setCodeTuplets(){

         int npletSum = 0;
        for(j = 0; j <positions; j++){
            if(temp[j]){
                npletSum = 1;
                temp[j] = false;  //set this space as being used
                for( k = j+1; k<positions; k++){
                    if(temp[k])
                        if(code[j] == code[k]){
                            temp[k] = false;
                            npletSum++;
                        }
                }
                nTuplets[npletSum]++;
            }
        }
    }

    public int getColors(){
        return colors;
    }
    public int getPositions(){
        return positions;
    }

    public void print(){
        int i;
        for( i =0; i< positions; i++)
            System.out.print(code[i]+ " ");
        System.out.print("\t");
        for( i = 1; i<= positions; i++)
            System.out.print(nTuplets[i]+ " ");
        System.out.print("\n");
    }



}



