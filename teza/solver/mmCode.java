public final class mmCode extends Object{

    public int code[];      //the code itself
    public int positions;   //the number of positions
    public int colors;


    int i, j, k;

    public mmCode(int col, int pos){
        positions = pos;
        colors = col;
        code = new int[positions];
        for( i =0; i < positions; i++){
            code[i]=(-1);
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

        System.out.print("\n");
    }



}



