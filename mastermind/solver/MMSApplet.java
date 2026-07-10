
//  Mastermind Solver Applet   - Radu Rosu July 6 97
//  this is a dummy user version of MMS.
//  a quik number version can be acced from the Mastermins Applet
//  Infinite credit to Karl Hörnel  .. his graphic ideas were used extensively by me


import java.awt.*;

public class MMSApplet extends java.applet.Applet
{
    MMSAsolver solver = new MMSAsolver();
    Image buffer,peg;       //images
	Graphics bufg,pegg;     //graphics

    boolean drawBoard = true;
    boolean gameGoing=false;
    boolean okButtonOn = false;
    boolean readyButtonOn = false;
    boolean masterDisplayed = false;
    boolean makingMaster = true;
    boolean masterReady = false;
    boolean computerPlaying = false;
    boolean solved = false;
    boolean problem = false;

    int guessBoard[][], pointBoard[][];
    int theMaster[] = {-1, -1, -1, -1};  //arrays for the pegs
                                            //initialized in construcor
    static int XPalletLoc = 220;
    static int YPalletLoc = 80;
    static int XBoardLoc = 5;
    static int YBoardLoc = 50;


	static int shadX[]={0,12,213,213,204,204,0},  //shading variables
		shadY[]={412,415,415,15,0,412,412};
    static int leftTriX[]={XBoardLoc+17,XBoardLoc+17,XBoardLoc+ 21};
    static int leftTriY[]={YBoardLoc+343, YBoardLoc+305,YBoardLoc+ 330};
    static int rightTriX[]={XBoardLoc+145, XBoardLoc+145, XBoardLoc+141};
    static int rightTriY[]={YBoardLoc+343, YBoardLoc+305,YBoardLoc+ 330};
    static int coverX[]= {XBoardLoc+17,XBoardLoc+ 21,XBoardLoc+ 141, XBoardLoc+145};
    static int coverY[] = {YBoardLoc+308,YBoardLoc+ 330, YBoardLoc+330,YBoardLoc+ 308};

    //static int shadeX[] = {XBoardLoc+15, XBoardLoc+19,XBoardLoc+ 141, XBoardLoc+145};
    //static int shadeY[] = {YBoardLoc+398,YBoardLoc+ 387,YBoardLoc+ 387,YBoardLoc+ 398};

    int selectedColor=0;
    int selPPegColor =0;

	int selectedHole,  currentRow;
    int selMasterHole;
	int changedGrfx = -1 ;

	int numOfBlacks =0;
    int numOfWhites = 0;
        //                      the current guess row

	Color lights[],darks[],darkGray;   //used for the pegs
    Color tableColor = new Color( 128, 166, 253);
    Color brown = new Color(128,90,2);
    Color darkRed = new Color(128, 0, 2);
    Color darkBrown = new Color( 94, 64 , 2);
    Color veryDarkBrown = new Color( 64, 34, 2);
    Color darkGreen = new Color(0,200,0);

//    movingPeg movingPeg = new movingPeg();

/*
   public static void main(String args[])
   {  Frame f = new MasterMind();
      f.resize(295, 500);
      f.show();
   }
*/
   public void init(){
        //super("MasterMind by Radu Rosu");

            //theMaster = new int[4];  // Holds computer's choice of colors
	        guessBoard = new int[4][7];  // Holds colored pin info
		    pointBoard = new int[4][7];  // Holds B&W score pin info
		    lights = new Color[8];  // arrays of colors for drawing
		    darks = new Color[8];

    		darkGray=new Color(60,60,60);  ///used for shading
	    	darks[0]=new Color(0,0,180); // Blue
		    lights[0]=new Color(0,0,255);
    		darks[1]=new Color(0,180,0); // Green
	    	lights[1]=new Color(34,255,34);
		    darks[2]=new Color(180,0,0); // Red
    		lights[2]=new Color(255,0,0);
	    	darks[3]=new Color(255,255,0); // Yellow
		    lights[3]=new Color(255,255,150);
    		darks[4]=new Color(240,150,0); // Orange
	    	lights[4]=new Color(245,185,0);
		    darks[5]=new Color(180,0,180); // magenta
    		lights[5]=new Color(230,0,230);
    		darks[6]=Color.white;
    		lights[6]=Color.lightGray;
    		darks[7]=new Color(0,0,0);//black
    		lights[7]=new Color(60,60,60);

    		buffer=createImage(290,420); // Prepare drawing buffers
		    bufg=buffer.getGraphics();
		    peg=createImage(48,40);  // Used for drawing peg symbol
		    pegg=peg.getGraphics();
            clearBoard();
    }



	public void clearBoard()  // Reset all variables and prepare for game
	{

		for (int i = 0; i < 7; i++)
		    for(int j = 0; j<4; j++){
		        guessBoard[j][i] = -1;
		        pointBoard[j][i] = -1;
		    }

		for (int i=0;i<4;i++)
		    theMaster[i]= -1;

        currentRow=0;
        masterReady = false;
        readyButtonOn = false;
        computerPlaying = false;
        makingMaster = true;
        numOfBlacks =0;
        numOfWhites =0;
        solved =false;
        problem =false;
        okButton(false);
		drawBoard = true;  // repaint the board to begin game
		repaint();
	}

 public void paint(Graphics g)
 {
		bufg.setColor(Color.darkGray); // Clear offcreen image buffer
		bufg.fillRect(0,0,300,420);
					// Draw the board itself
		paintBoard(bufg);
				// Draw the right field

	    paintPegPallet(bufg, XPalletLoc, YPalletLoc);
        solverButton(bufg);
        gameButton(bufg);

		g.drawImage(buffer,0,0,this); // Copy image buffer to screen
		drawBoard = false;

        buffer.flush();

        // draw non standdard stuff

//        if(masterDisplayed)
//            showMaster();
        if(okButtonOn)
            okButton(true);


    }
    public void paintBoard(Graphics boardBuffer)
    {
        boardBuffer.setColor(brown); //brown

		for (int i=0;i<5;i++)
			boardBuffer.fill3DRect(i+XBoardLoc,i+YBoardLoc,204-3*i,357-3*i,true);
		for (int i=0;i<7;i++)
		{
			boardBuffer.draw3DRect(15+XBoardLoc,15+i*42+YBoardLoc,132,38,false);// guees boxes
			boardBuffer.draw3DRect(150+XBoardLoc,15+i*42+YBoardLoc,38,38,false);//response boxes
		}

//boardBuffer.fillPolygon(shadX,shadY,7);
        boardBuffer.setColor(darkBrown);
        boardBuffer.fillRoundRect(151+XBoardLoc,310+YBoardLoc,33, 33, 15,13);
        boardBuffer.setColor(veryDarkBrown);

        boardBuffer.setFont( new Font("TimesRoman", Font.BOLD, 18));
        boardBuffer.drawString("OK",153+XBoardLoc, 332+YBoardLoc);


		for (int i=0;i<7;i++)
		{
					// Draw colored pins or just holes
			for (int j=0;j<4;j++){
				if (guessBoard[j][i]<0)
					boardHole(boardBuffer,j,i);
				else{
				    try{
    					boardPeg(boardBuffer,j,i,guessBoard[j][i]);
    				}
    				catch(Exception ArrayIndexOutOfBoundsException)
    				{   problem =true;
    				  }
    			}
            }
					// Draw the score pins or holes
			for (int j=0;j<2;j++)
				for (int k=0;k<2;k++){
					if (pointBoard[j*2+k][i]<0)
						pointHole(boardBuffer,k,i,j);
					else
						pointPeg(boardBuffer,k,i,j,pointBoard[j*2+k][i]);
				}
		}
		for (int j=0;j<4;j++){
		    if (theMaster[j]<0)
				boardHole(boardBuffer,j,-1);
			else
				boardPeg(boardBuffer,j,-1,theMaster[j]);
        }
		// now create the cover  ... made out of 4 elements
	    if(computerPlaying && (!solved)){

            boardBuffer.setColor(veryDarkBrown);
            boardBuffer.fillPolygon(leftTriX, leftTriY, 3);
            boardBuffer.fillPolygon(rightTriX, rightTriY, 3);
            boardBuffer.setColor(darkBrown);
            boardBuffer.fillPolygon(coverX,coverY,4);
            okButton(true);
            boardBuffer.setColor(new Color(150,150,150));
            boardBuffer.fillRect(4,4, 220,40);
            boardBuffer.setColor(veryDarkBrown);
            boardBuffer.setFont( new Font("TimesRoman", Font.BOLD, 12));
            boardBuffer.drawString("Please award black and white pegs",7,20);
            boardBuffer.drawString("press the OK button when finished.",7,35);
           // boardBuffer.setColor(Color.black);
           // boardBuffer.fillPolygon(shadeX, shadeY,4);
        }
        if(!computerPlaying){
            boardBuffer.drawString("press Ready when finished.",7,38);
	        boardBuffer.setFont( new Font("TimesRoman", Font.BOLD, 18));
            boardBuffer.setColor(new Color(150,150,150));
            boardBuffer.fillRect(4,4, 220,40);
            boardBuffer.setColor(veryDarkBrown);
            boardBuffer.drawString("Please select your code.",7,20);
            boardBuffer.drawString("click 'Ready' when finished.",7,40);
 	    }
		if(solved){
		    boardBuffer.setColor(new Color(150,150,150));
            boardBuffer.fillRect(4,4, 220,40);
            boardBuffer.setColor(veryDarkBrown);
            boardBuffer.setFont( new Font("TimesRoman", Font.BOLD, 12));
            boardBuffer.drawString("The computer has solved your code",7,20);
            boardBuffer.drawString("to play again press 'New'. ",7,35);
        }
        if(problem)
            dispProblem(boardBuffer);

    }
    public void dispProblem(Graphics g ){

        if(problem){
            g.setColor(new Color(150,150,150));
            g.fillRect(4,4, 220,40);
            g.setColor(veryDarkBrown);
            g.setFont( new Font("TimesRoman", Font.BOLD, 12));
            g.drawString("The computer thinks that you have",7,20);
            g.drawString("made a mistake...Please start again.",7,35);
        }
    }



    public void paintPegPallet(Graphics palletBuffer, int Xloc, int Yloc)
    {   //draw a nice pallet board
        //NOTE: this function can take the location of where to put the board.
        if(makingMaster){
            palletBuffer.setColor(brown);
	    	palletBuffer.fill3DRect(Xloc,Yloc,60,254,true);
            palletBuffer.setColor(Color.black);
            palletBuffer.drawRect(Xloc-1,Yloc-21, 61, 275);

            for(int color =0; color<6; color++){
                if(color == selectedColor)
                    selectedBigPeg(palletBuffer, Xloc+7, Yloc+2+42*color, color);
                else
                    bigPeg(palletBuffer, Xloc+7, Yloc+2+42*color, color);
            }
        }
        else{
            palletBuffer.setColor(darkGreen);
            palletBuffer.fill3DRect(Xloc,Yloc,60,90,true);
            palletBuffer.setColor(Color.black);
            palletBuffer.drawRect(Xloc-1,Yloc-21, 61, 90+21);
            if(selPPegColor == 0){
                bigPointPeg(palletBuffer,Xloc+10,Yloc+5, 1);//the 1 and 0
                selectedPointPeg(palletBuffer,Xloc+10,Yloc+50, 0);//here must keep
            }
            else{
                selectedPointPeg(palletBuffer,Xloc+10,Yloc+5, 1);//this order
                bigPointPeg(palletBuffer,Xloc+10,Yloc+50, 0);
            }


        }
        palletBuffer.setColor(Color.black);
        palletBuffer.drawLine(Xloc, Yloc-1, Xloc+60,Yloc-1);
        palletBuffer.drawLine(Xloc, Yloc-2, Xloc+60,Yloc-2);

    }

    public boolean handleEvent(Event event) {

	if (event.id == Event.WINDOW_DESTROY) {
	    hide();         // hide the Frame
//	    dispose();      // tell windowing system to free resources
	    System.exit(0); // exit
	    return true;
    	}

	return super.handleEvent(event);
    }
/*
    public boolean keyDown(Event event, int key){
        if(key == 10){
            if (okButtonOn){
            changedGrfx= 2;
            okButton(false);//reseet the ok button
		    drawBoard=false;
            repaint();
            return false;
		    }
		    else if(!gameGoing)
		    {
		        gameGoing = true;
		        clearBoard();
		    }

        }
        if(key ==32 && !gameGoing) {
		        gameGoing = true;
		        clearBoard();
		    }
		return false;
    }*/


    public void gameButton(Graphics g){
        //called just to paint
        int Xloc = XPalletLoc;
        int Yloc = YPalletLoc-20;

        String message = "Ready";
        //Graphics g = this.getGraphics();
        g.setFont( new Font("TimesRoman", Font.BOLD, 18));
        g.setColor(Color.gray);
		g.fill3DRect(Xloc,Yloc,60,18,true);

        if(computerPlaying){
            g.setColor(Color.red);
            message = "Quit";
        }
        if(solved){
            g.setColor(Color.green);
            message = "New";}
        else if(!readyButtonOn)
            g.setColor(Color.red);
        else if(readyButtonOn)
            g.setColor(Color.green);

	    g.drawString(message,Xloc+9,Yloc+14);
    }
    // the graphical stuff

    public void okButton(boolean on)
    {
        Graphics g = this.getGraphics();
        if(on){
            g.setColor(Color.green);
            okButtonOn = true;
        }
        else{
            g.setColor(darkRed);
            okButtonOn = false;
        }
        g.setFont( new Font("TimesRoman", Font.BOLD, 18));
        g.drawString("OK",153+XBoardLoc, 332+YBoardLoc);


    }

	public void fixBox(Graphics g,int x,int y,int w,int h,Color c)
	{
		g.setColor(c);
		g.fillRect(x,y,w,h);
	}

    public void drawDisc(Graphics diskBuffer, int j, int i, int color)
    {
        j -= 5;
        i -= 5;
        fixDisc(diskBuffer,j,i,32,Color.black);
		fixDisc(diskBuffer,j+1,i+1,30,darks[color]);
		fixDisc(diskBuffer,j+6,i+2,25,lights[color]);
		fixDisc(diskBuffer,j+6,i+6,7,Color.white);
    }

	public void fixDisc(Graphics g,int x,int y,int r,Color c)
	{

		g.setColor(c);
		g.fillOval(x,y,r,r);
	}

	public void fixCircle(Graphics g,int x,int y,int r,Color c)
	{
		g.setColor(c);
		g.drawOval(x,y,r,r);
	}

	public void boardPeg(Graphics g,int xin,int yin,int c)//draws a peg on the board
	{
	    //int x = 13+xin*21;
	   // int y = 209-yin*28;
	    int x = 22+XBoardLoc+xin*31;
	    int y = 273+YBoardLoc-yin*42;

		fixDisc(g,x+2,y+2,25,darkGray);//25 waas 18
		fixDisc(g,x,y,25,darks[c]);
		fixDisc(g,x+1,y+1,21,lights[c]);
		fixDisc(g,x+3,y+3,9,Color.white);
	}

	public void boardHole(Graphics g,int xin,int yin) //draws the peg holes
	{
	    int x = 23+XBoardLoc+xin*31;
	    int y = 274+YBoardLoc-yin*42;
		fixDisc(g,x+5,y+5,14,Color.black);
		g.setColor(Color.lightGray);
		g.drawArc(x+4,y+4,15,15,-135,180);
	}

	public void pointHole(Graphics g,int xin,int yin, int zin)  /// draws a score hole
	{
	    int x = 152+XBoardLoc+xin*18;
	    int y = 270+YBoardLoc-yin*42+zin*18;


		fixDisc(g,x+3,y+3,9,Color.black);
		g.setColor(Color.lightGray);
		g.drawArc(x+2,y+2,9,9,-135,180);
	}


	public void pointPeg(Graphics g,int xin,int yin, int zin,int c)//draws a score point
	{
	    //whire or black
	    int x = 152+XBoardLoc+xin*18;
	    int y = 269+YBoardLoc-yin*42+zin*18;

		fixDisc(g,x+2,y+2,15,Color.black);
		if (c==1)
		{
			fixDisc(g,x,y,14,darkGray);
			g.setColor(Color.white);
			g.drawArc(x+2,y+2,11,11,70,130);
		}
		else
		{
			fixDisc(g,x,y,14,Color.white);
			fixDisc(g,x+4,y+4,6,Color.lightGray);
			fixDisc(g,x+6,y+6,6,Color.white);
		}
	}

	public void bigPointPeg(Graphics g,int x,int y, int c)//draws a score point
	{
        c= c+6;
	    fixBox(pegg,0,0,48,40,darkGreen);
	    //whire or black

	    	fixDisc(pegg,0,0,30,darks[c]);
		    fixDisc(pegg,2,2,22,lights[c]);
    		fixDisc(pegg,7,6,6,Color.white);
	    	fixCircle(pegg,0,0,30,Color.black);
		    fixBox(pegg,15,0,17,31, darkGreen);
      		fixBox(pegg,15,12,23,8,darks[c]);
		    fixBox(pegg,15,12,17,6,lights[c]);
    	pegg.setColor(Color.black);
	   	pegg.drawLine(15,0,15,30);
	    pegg.drawRect(15,12,23,8);
   		g.drawImage(peg,x,y,this);
        peg.flush();
	}
    public void selectedPointPeg(Graphics g,int x,int y, int c)//draws a score point
	{//draws a point peg backwards
        c= c+6;
	    fixBox(pegg,0,0,48,40,darkGreen);
	    //whire or black

	    	fixDisc(pegg,13,0,30,darks[c]);
		    fixDisc(pegg,19,8,22,lights[c]);
    		fixDisc(pegg,32,18,6,Color.white);
	    	fixCircle(pegg,13,0,30,Color.black);
		    fixBox(pegg,2,0,27,31, darkGreen);
      		fixBox(pegg,5,12,23,8,darks[c]);
		    fixBox(pegg,11,12,17,6,lights[c]);
    	pegg.setColor(Color.black);
	   	pegg.drawLine(28,0,28,30);
	    pegg.drawRect(5,12,23,8);
   		g.drawImage(peg,x,y,this);
        peg.flush();
	}

	public void bigPeg(Graphics g, int Xloc, int Yloc, int c)
	{
		fixBox(pegg,0,0,48,40,brown);
		fixDisc(pegg,0,0,40,darks[c]);
		fixDisc(pegg,2,2,29,lights[c]);
		fixDisc(pegg,7,6,8,Color.white);
		fixCircle(pegg,0,0,39,Color.black);
		fixBox(pegg,22,0,26,40,brown);
		fixBox(pegg,22,15,26,10,darks[c]);
		fixBox(pegg,31,15,17,8,lights[c]);
		pegg.setColor(Color.black);
		pegg.drawLine(22,0,22,40);
		pegg.drawRect(22,15,25,10);
		g.drawImage(peg,Xloc,Yloc,this);
        peg.flush();
	}
	public void selectedBigPeg(Graphics g, int Xloc, int Yloc, int c)
	{
		fixBox(pegg,0,0,48,40,brown);
		fixDisc(pegg,8,0,40,darks[c]);
		fixDisc(pegg,14,11,29,lights[c]);
		fixDisc(pegg,32,25,8,Color.white);
		fixCircle(pegg,8,0,39,Color.black);
		fixBox(pegg,0,0,27,40,brown);
		fixBox(pegg,0,15,26,10,darks[c]);
		fixBox(pegg,9,15,17,8,lights[c]);
		pegg.setColor(Color.black);
		pegg.drawLine(26,0,26,44);
		pegg.drawRect(0,15,26,10);
		g.drawImage(peg,Xloc,Yloc,this);
        peg.flush();
	}


    public void solverButton(Graphics g){
        g.setColor(darkRed);
        g.fillRoundRect(XPalletLoc+5, YPalletLoc-76, 58,50, 30,30);
        g.setColor(Color.yellow);
        g.setFont( new Font("TimesRoman", Font.ITALIC, 11));
        g.drawString("Click here", XPalletLoc+11, YPalletLoc-64);
        g.drawString("for", XPalletLoc+25, YPalletLoc-54);
        g.drawString("Nerdy MMS", XPalletLoc+8, YPalletLoc-40);
    }

    public boolean mouseDown(Event evt, int x, int y)
	//what the mouse does
	//takes an event and x and y coordinates
	//in most cases, only a few variables are set the logic takes place
	//in the update function
	{

		int whichColor,i,j,k,l;

        this.showStatus("Mouse down at: " + evt.x +"," + evt.y);

        //placing a peg in the master?
		for (j=0;j<4;j++)  //if in the four posible holef for a per
			if ( makingMaster && ( x>15+XBoardLoc+31*j) && (x<50+XBoardLoc+31*j) &&
			    (y> YBoardLoc+310) && (y< YBoardLoc+345) )
			{
				selMasterHole=j;
				changedGrfx=0;//master placed a peg
				drawBoard=false;//update should process this
				repaint();//call update
				return false;
			}
        // Color selected?
		for (i=0;i<6;i++)   // for the six colors
		    if( (x > XPalletLoc+7) && (x < XPalletLoc + 55) &&
		        (y > YPalletLoc+2+42*i) && ( y < YPalletLoc+42+42*i))
		     	{
					whichColor=i; // New color   // new color is selected

					if (makingMaster)  //for placing the next peg
					{
						selectedColor=whichColor;
						changedGrfx=1;
						drawBoard=false;
						repaint();
						return false;
					}
				}

        // black or white selected
        if( (x>XPalletLoc) && (x<XPalletLoc+60) &&
            (y>YPalletLoc) && (y<YPalletLoc+45) && computerPlaying)
            {   //black selected
                selPPegColor =1;
                drawBoard = false;
                repaint();
                return false;
            }
        if( (x>XPalletLoc) && (x<XPalletLoc+60) &&
            (y>YPalletLoc+46) && (y<YPalletLoc+85) && computerPlaying)
            {   //white selected
                selPPegColor =0;
                drawBoard = false;
                repaint();
                return false;

            }

        // point peg placed....THIS is a bitch
        if((x>XBoardLoc+150) && (x<XBoardLoc+185)&& (y<308+YBoardLoc)
                                &&computerPlaying){
            if(x<XBoardLoc+167){

                if((y>(YBoardLoc+306-(currentRow+1)*41)) &&
                    (y<(YBoardLoc+306-(currentRow+1)*41+18)))
                    {  //upper left
                        if(pointBoard[0][currentRow]>=0)//reset caase
                            pointBoard[0][currentRow] = -1;
                        else
                            pointBoard[0][currentRow] = selPPegColor;
                    }
                else if((y>(YBoardLoc+306-(currentRow+1)*41+18) )&&
                    (y<(YBoardLoc+306-(currentRow+1)*41+38) ))
                    {   //lower left
                        if(pointBoard[2][currentRow]>=0)//reset caase
                            pointBoard[2][currentRow] = -1;
                        else
                            pointBoard[2][currentRow] = selPPegColor;
                    }
            }
            else{
                              if((y>(YBoardLoc+306-(currentRow+1)*41)) &&
                    (y<(YBoardLoc+306-(currentRow+1)*41+18)))
                    {  //upper left
                        if(pointBoard[1][currentRow]>=0)//reset caase
                            pointBoard[1][currentRow] = -1;
                        else
                            pointBoard[1][currentRow] = selPPegColor;
                    }
                else if((y>(YBoardLoc+306-(currentRow+1)*41+18) )&&
                    (y<(YBoardLoc+306-(currentRow+1)*41+38) ))
                    {   //lower left
                        if(pointBoard[3][currentRow]>=0)//reset caase
                            pointBoard[3][currentRow] = -1;
                        else
                            pointBoard[3][currentRow] = selPPegColor;
                    }
            }

            drawBoard=false;
            repaint();
            return false;
        }

        //  int selMasterHole;

	    //the stuff below must be made for a new ok button that is
	    //always active while the comuter plays.

        if ((x>151+XBoardLoc) && (x<184+XBoardLoc) &&
                (y>310+YBoardLoc) && (y<343+YBoardLoc) && okButtonOn)
		{//hit the OK button
		    changedGrfx= 2;
            okButton(false);//reseet the ok button
		    drawBoard=false;
		    if(!nextGuess()){
		        problem = true;
		        return false;}
            repaint();
            return false;
		}

	    // Ready or quit or new
		if ((x>XPalletLoc)&&(x< XPalletLoc+60)&&
		    (y<YPalletLoc)&&(y>YPalletLoc-20))
		{
			if (masterReady)
			{// begin the solving process
                computerPlaying = true;
                makingMaster = false;
                masterReady =false;
                okButton(true);
                changedGrfx = 3;
                drawBoard= false;
                firstGuess();
                repaint();
                return false;
			}
			else if(computerPlaying || solved)
			{//*****CLEAR BOARD***GET READDY for NEW GAME***///
			    clearBoard();
			    solved = false;
				changedGrfx=3;
				drawBoard=false;
				repaint();
		        return false;
		    }

		}
		if((x>(XPalletLoc+5)) && (x< XPalletLoc+63) &&
		    (y>YPalletLoc-76) && (y< YPalletLoc-26)) {
		    MMSGUI GUI = new MMSGUI("MasterMind Solver");
            GUI.resize(390,250);
            GUI.show();
            }


		drawBoard = false;
		changedGrfx = 4;
		repaint();
		return false;
	}

	public void update(Graphics g)
	{
	    int i =0;
	    int j =0;

	//System.out.println("in update....." + changedGrfx);

		if (drawBoard)
			paint(g);
		else
		{		// Process small changes
			switch(changedGrfx)
			{
				case 0: // Peg placed in hole?
					if(theMaster[selMasterHole] >=0){//taking off a peg
					    theMaster[selMasterHole] = -1;
					    readyButtonOn = false; // <-- here we actualy want the ready button
					    masterReady = false;
					}
					else{
					    theMaster[selMasterHole]= selectedColor;//assign the color to the master
					    boardPeg(g,selMasterHole,-1,selectedColor); //display the peg
					    boolean temp = true;
					    for (j=0;j<4;j++)  //if guess is complete
			                temp=(temp && (theMaster[j]>=0));
                        if(temp){
                            readyButtonOn = true; //ready button
                            masterReady = true;
                        }
                    }
                    break;

				case 3: // Given up?
//				    showMaster();
				   // okButtonOn = false;
				    okButton(false);
				    changedGrfx = 4;
					break;

				default:
					break;
			}//end switch
            paint(g);
		    drawBoard=true;
		}
	}

	public void firstGuess(){//first guess this seems to be the optimum
	    solver.mmsolver();  //reset the solver
	    solver.firstGuess();
        for(int i = 0; i < 4; i++){
            guessBoard[i][currentRow] = (solver.board[i][solver.guessNumber]-1);
        }
	}
	public boolean nextGuess(){
	    solver.board[4][solver.guessNumber] = getBlacks(currentRow);
	    solver.board[5][solver.guessNumber] = getWhites(currentRow);
	    currentRow++; //we are done with this row//go to nextone
	    this.showStatus("Blacks:" + solver.board[4][solver.guessNumber] +
	                 "   Whites:"+ solver.board[5][solver.guessNumber] );
        if(numOfBlacks == 4){
            solved =true;
            return true;
        }
	    solver.nextGuess();
	    for(int i = 0; i < 4; i++){
            guessBoard[i][currentRow] = (solver.board[i][solver.guessNumber]-1);
            //substract one because the solver works on 1-6 and this works on 0-5
        }
        if(guessBoard[2][currentRow] == 10)///this is the error code
        {   problem = true;
            computerPlaying = false;
            return false;
        }
        return true;
	}

	public int getBlacks(int row){// gets the number of blacks imputed by the user
        if(numOfBlacks==4) // erroor control hack
            return 4;
        numOfBlacks =0;
        for(int i = 0; i<4; i++){
            if(pointBoard[i][row]==1)
                numOfBlacks++;
        }
        return numOfBlacks;
    }

    public int getWhites(int row){// gets the number of blacks imputed by the user
        numOfWhites =0;
        for(int i = 0; i<4; i++){
            if(pointBoard[i][row]==0)
                numOfWhites++;
        }
        return numOfWhites;
    }

}
