
//  Mastermind
//  A mastermind frame for any system that can run with the JDK
//  By Radu Rosu
//  Infinite credit to Karl Hörnel  .. his applet version of the same game was
// used extensively by me.
import java.applet.*;
import java.awt.*;
import java.awt.event.*;


public class MMApplet extends java.applet.Applet
{

    public static void main(String[] args) {
    Frame f = new Frame();                     // Create a window
    Applet a = new MMApplet();       // Create the applet panel
    f.add(a, "Center");                        // Add applet to window
    a.init();                                  // Initialize the applet
    f.setSize(300, 500);                       // Set the size of the window
    f.show();                                  // Make the window visible
    f.addWindowListener(new WindowAdapter() {  // Handle window close requests
      public void windowClosing(WindowEvent e) { System.exit(0); }
    });
    }

    Image buffer,peg, table;       //images
	Graphics bufg,pegg, tableg;     //graphics


    public boolean drawBoard = true;
    public boolean firstMouseClick = true;
    int pointX,pointY,pickX=-1,pickY=-1,dragX=-1,dragY=-1;

    int guessBoard[][], pointBoard[][],theMaster[];  //arrays for the pegs
                                            //initialized in construcor

    static int XPalletLocation = 220;
    static int YPalletLocation = 97;

	static int shadX[]={0,12,213,213,204,204,0},  //shading variables
		shadY[]={412,415,415,15,0,412,412};
    static int leftTriX[]={20,18, 18};
    static int leftTriY[]={17, 17, 51};
    static int rightTriX[]={140, 142, 142};
    static int rightTriY[]={17, 17, 51};
    static int coverX[]= {19, 19, 142, 141};
    static int coverY[] = {17, 51, 51, 17};
    static int shadeX[] = {16, 20, 142, 146};
    static int shadeY[] = {398, 387, 387, 398};
	int selectedColor=0,changedCol;

	int selectedHole,  currentRow;
	int changedGrfx = -1 ;
        //                      the current guess row
	boolean gameGoing=false;
    boolean okButtonOn = false;
    boolean masterDisplayed = false;

	Color lights[],darks[],darkGray;   //used for the pegs
    Color tableColor = new Color( 128, 166, 253);
    Color brown = new Color(128,90,2);
    Color darkRed = new Color(128, 0, 2);
    Color darkBrown = new Color( 94, 64 , 2);
    Color veryDarkBrown = new Color( 64, 34, 2);
    Color green = new Color(0,128,0);

    movingPeg movingPeg = new movingPeg();

/*
   public static void main(String args[])
   {  Frame f = new MMApplet();
      f.init();
      f.resize(295, 500);
      f.show();
   }
*/
   public void init(){
        //super("MasterMind by Radu Rosu");

            theMaster = new int[4];  // Holds computer's choice of colors
	        guessBoard = new int[4][8];  // Holds colored pin info
		    pointBoard = new int[4][8];  // Holds B&W score pin info
		    lights = new Color[6];  // arrays of colors for drawing
		    darks = new Color[6];

    		darkGray=new Color(60,60,60);  ///used for shading
	    	darks[0]=new Color(0,0,180); // Blue
		    lights[0]=new Color(0,0,255);
    		darks[1]=new Color(0,180,0); // Green
	    	lights[1]=new Color(34,255,34);
		    darks[2]=new Color(180,0,0); // Red
    		lights[2]=new Color(255,0,0);
	    	darks[3]=new Color(255,255,0); // Yellow
		    lights[3]=new Color(255,255,150);
    		darks[4]=new Color(200,150,0); // Orange
	    	lights[4]=new Color(245,185,0);
		    darks[5]=new Color(180,0,180); // magenta
    		lights[5]=new Color(230,0,230);

    		buffer=createImage(290,420); // Prepare drawing buffers
		    bufg=buffer.getGraphics();
		    peg=createImage(48,40);  // Used for drawing peg symbol
		    pegg=peg.getGraphics();
            clearBoard();
}



	public void clearBoard()  // Reset all variables and prepare for game
	{

		for (int i = 0; i < 8; i++)
		    for(int j = 0; j<4; j++){
		        guessBoard[j][i] = -1;
		        pointBoard[j][i] = -1;
		    }

		currentRow=0;
		// create the master
		for (int i=0;i<4;i++)
		{theMaster[i]= new RandomIntGenerator(0, 5).draw();}

        masterDisplayed = false;
        movingPeg.clear();
		drawBoard = true;  // repaint the board to begin game
		repaint();
	}

 public void paint(Graphics g)
 {
		bufg.setColor(new Color(30,50,50)); // Clear offcreen image buffer
		bufg.fillRect(0,0,300,430);
					// Draw the board itself
		paintBoard(bufg);
				// Draw the right field
	    paintPegPallet(bufg, XPalletLocation, YPalletLocation);

        if(movingPeg.isMoving())  // display the draging
            drawDisc(bufg, movingPeg.getXloc(), movingPeg.getYloc(), movingPeg.getColor());

      //  solverButton(bufg);
        gameOnButton(bufg, gameGoing);

		g.drawImage(buffer,0,0,this); // Copy image buffer to screen
		drawBoard = false;

        buffer.flush();

        // draw non standdard stuff

        if(masterDisplayed)
            showMaster();
        if(okButtonOn)
            okButton(true);


    }

    public void paintBoard(Graphics boardBuffer)
    {
        boardBuffer.setColor(brown); //brown

		for (int i=0;i<5;i++)
			boardBuffer.fill3DRect(i,i,204-3*i,412-3*i,true);
		for (int i=0;i<8;i++)
		{
			boardBuffer.draw3DRect(15,60+i*42,132,38,false);// guees boxes
			boardBuffer.draw3DRect(150,60+i*42,38,38,false);//response boxes
		}

		boardBuffer.fillPolygon(shadX,shadY,7);
		// now create the cover  ... made out of 4 elements
		boardBuffer.setColor(darkBrown);
        boardBuffer.fillPolygon(coverX,coverY,4);
       // boardBuffer.setColor(Color.black);
       // boardBuffer.fillPolygon(shadeX, shadeY,4);

        boardBuffer.setColor(veryDarkBrown);
        boardBuffer.fillPolygon(leftTriX, leftTriY, 3);
        boardBuffer.fillPolygon(rightTriX, rightTriY, 3);

        boardBuffer.fillRoundRect(150,15,38, 38, 15,13);
        boardBuffer.setColor(darkRed);

        boardBuffer.setFont( new Font("TimesRoman", Font.BOLD, 18));
        boardBuffer.drawString("OK",155, 40);


		for (int i=0;i<8;i++)
		{
					// Draw colored pins or just holes
			for (int j=0;j<4;j++){
				if (guessBoard[j][i]<0)
					boardHole(boardBuffer,j,i);
				else
					boardPeg(boardBuffer,j,i,guessBoard[j][i]);
            }
					// Draw the score pins or holes
			for (int j=0;j<2;j++)
				for (int k=0;k<2;k++){
					if (pointBoard[j*2+k][i]<0)
						pointHole(boardBuffer,k,i,j);
					else
						pointBeg(boardBuffer,k,i,j,pointBoard[j*2+k][i]);
				}
		}

    }

    public void paintPegPallet(Graphics palletBuffer, int Xloc, int Yloc)
    {   //draw a nice pallet board
        //NOTE: this function can take the location of where to put the board.

        palletBuffer.setColor(brown);
		palletBuffer.fill3DRect(Xloc,Yloc,60,254,true);
        palletBuffer.setColor(Color.black);
        palletBuffer.drawRect(Xloc-1,Yloc-21, 61, 275);
        palletBuffer.drawLine(Xloc, Yloc-1, Xloc+60,Yloc-1);
        palletBuffer.drawLine(Xloc, Yloc-2, Xloc+60,Yloc-2);

        for(int color =0; color<6; color++){
            if(color == selectedColor)
                selectedBigPeg(palletBuffer, Xloc+7, Yloc+2+42*color, color);
            else
                bigPeg(palletBuffer, Xloc+7, Yloc+2+42*color, color);
            }
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
    }

    public boolean mouseDown(Event evt, int x, int y)
	//what the mouse does
	//takes an event and x and y coordinates
	{

		   int whichColor,i,j,k,l;
        /*   Graphics g = this.getGraphics();
            g.setColor(Color.white);
            g.fillRect(50,350, 180, 20);
            g.setColor(Color.black);

           g.drawString("Mouse down at: " + evt.x +"," + evt.y, 50, 370);*/
            //placing a peg?
		for (j=0;j<4;j++)  //if in the four posible holef for a per
			if ( gameGoing && ( x>25+31*j) && (x<45+31*j) &&
			    (y< (91 +(currentRow*42))) && (y> (65 + (currentRow*42))) )
			{
				selectedHole=currentRow*4+j;
				changedGrfx=0;
				drawBoard=false;
				repaint();
				return false;
			}
					// Color selected?
		for (i=0;i<6;i++)   // for the six colors
		    if( (x > XPalletLocation+7) && (x < XPalletLocation + 55) &&
		        (y > YPalletLocation+2+42*i) && ( y < YPalletLocation+42+42*i))
		     	{
					whichColor=i; // New color   // new color is selected

					if (gameGoing)  //for placing the next peg
					{
						selectedColor=whichColor;
						changedGrfx=1;
						drawBoard=false;
						repaint();
						movingPeg.isMoving(true);
						movingPeg.homeLocation(x,y);
						movingPeg.newColor(whichColor);
						return false;
					}
					//the chose color BS

				}
        if ((x>150) && (x<190) && (y>16) && (y<56) && okButtonOn)
		{//hit the ok button
		    changedGrfx= 2;
            okButton(false);//reseet the ok button
		    drawBoard=false;
            repaint();
            return false;
		}

	    // Begin or end game
		if ((x>XPalletLocation)&&(x< XPalletLocation+60)&&
		    (y<YPalletLocation)&&(y>YPalletLocation-20))
		{
			gameGoing=!gameGoing;
			if (gameGoing)
			{
				clearBoard();
			}
			else
			{
				changedGrfx=3;
				drawBoard=false;
				repaint();
			}
			return false;
		}
		/* solver button?
		if ((x>(XPalletLocation+2))&&(x<( XPalletLocation+58))&&
		    (y<(YPalletLocation-30))&&(y>(YPalletLocation-80))){
		    MMSGUI GUI = new MMSGUI("MasterMind Solver");
            GUI.resize(390,250);
            GUI.show();
            }
*/
		drawBoard = false;
		changedGrfx = 4;
		repaint();
		return false;
	}

    public boolean mouseDrag(Event event, int x, int y)
    {
        //System.out.println(" in mouse drag at : " + x + "," + y);

        if(!(movingPeg.isMoving()))
            return false;
        //else
        if(x<XPalletLocation){
            movingPeg.newLocation(x,y);
            drawBoard= false;
            repaint();}
        return false;
    }

    public boolean mouseUp(Event event, int x , int y)
    {
      //  System.out.println(" in mouse up at : " + x + "," + y);
        if(!(movingPeg.isMoving()))
            return false;
        ///  if we are not draging

        // if we just draged the peg on the pallete
        if( (x>XPalletLocation) &&( x < XPalletLocation+60) &&
            (y>YPalletLocation)  && (y < YPalletLocation+254)){
            movingPeg.isMoving(false);
            drawBoard=false;
            repaint();
            return false;
            }


        for (int j=0;j<4;j++){  //if in the four posible holef for a per
		    if ( gameGoing && ( x>25+31*j) && (x<45+31*j) &&
			    (y< (91 +(currentRow*42))) && (y> (65 + (currentRow*42))) )

		    {
			    selectedHole=currentRow*4+j;
			    changedGrfx=5;
			    drawBoard=false;
			    movingPeg.isMoving(false);
			    repaint();
			    return false;
			}
        }

        // we put it in a wrong place
        // do a nice animation of it coming back

        int xIncrement =0;
        int yIncrement =0;

        xIncrement = (movingPeg.getXloc() - movingPeg.getHomeX())/10;
        yIncrement = (movingPeg.getYloc() - movingPeg.getHomeY())/10;
        //showStatus(" the increments are: " + xIncrement + "  "+yIncrement);

        Graphics g = this.getGraphics();

        for(int i = 0; i<10; i++){
            movingPeg.newLocation((movingPeg.getXloc()- xIncrement),
                                    (movingPeg.getYloc()- yIncrement));
            movingPeg.isMoving(true);
            paint(g);
        }
        movingPeg.isMoving(false);
        repaint();
        return false;
    }

    public void solverButton(Graphics g){
        g.setColor(darkRed);
        g.fillRoundRect(XPalletLocation+1, YPalletLocation-80, 58,50, 30,30);
        g.setColor(Color.yellow);
        g.setFont( new Font("TimesRoman", Font.ITALIC, 11));
        g.drawString("Click here", XPalletLocation+7, YPalletLocation-64);
        g.drawString("to see the", XPalletLocation+7, YPalletLocation-54);
        g.drawString("computer", XPalletLocation+7, YPalletLocation-44);
        g.drawString("play. ", XPalletLocation+17, YPalletLocation-32);
    }
    public void gameOnButton(Graphics g,boolean on){
        int Xloc = 220;
        int Yloc = YPalletLocation-20;

        String message = "Begin";
        //Graphics g = this.getGraphics();
        g.setFont( new Font("TimesRoman", Font.BOLD, 18));
        g.setColor(Color.gray);
		g.fill3DRect(Xloc,Yloc,60,18,true);

        if(on){
            gameGoing = true;
            g.setColor(Color.red);
            message = "End";
        }
        else{
            gameGoing = false;
            g.setColor(Color.green);
        }

	    g.drawString(message,Xloc+11,Yloc+15);
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
        g.drawString("OK",155, 40);
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
	    int x = 22+xin*31;
	    int y = /*314-*/66+yin*42;

		fixDisc(g,x+2,y+2,25,darkGray);//25 waas 18
		fixDisc(g,x,y,25,darks[c]);
		fixDisc(g,x+1,y+1,21,lights[c]);
		fixDisc(g,x+3,y+3,9,Color.white);
	}

	public void boardHole(Graphics g,int xin,int yin) //draws the peg holes
	{
	    int x = 23+xin*31;
	    int y = 66+yin*42;
		fixDisc(g,x+5,y+5,14,Color.black);
		g.setColor(Color.lightGray);
		g.drawArc(x+4,y+4,15,15,-135,180);
	}
	public void pointBeg(Graphics g,int xin,int yin, int zin,int c)//draws a score point
	{
	    //whire or black
	    int x = 152+xin*18;
	    int y = /*311-*/63+yin*42+zin*18;

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

	public void pointHole(Graphics g,int xin,int yin, int zin)  /// draws a score hole
	{
	    int x = 152+xin*18;
	    int y = /*311-*/63+yin*42+zin*18;

		fixDisc(g,x+3,y+3,9,Color.black);
		g.setColor(Color.lightGray);
		g.drawArc(x+2,y+2,9,9,-135,180);
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
					i=selectedHole/4;
					j=selectedHole- i*4;
					if(guessBoard[j][i] >= 0){// now you must take peg out first
					    guessBoard[j][i] = -1;
					   // okButtonOn = false;
                        okButton(false);
                        }
					else{
					    guessBoard[j][i]=selectedColor;
					    boardPeg(g,j,i,selectedColor);
                        boolean temp = true;
                        for (j=0;j<4;j++)  //if guess is complete
			                temp=(temp && (guessBoard[j][currentRow]>=0));
                        if(temp){
                          //  okButtonOn = true;
                            okButton(true);
                        }
                    }//end else
			//	    checkDots(g);

					break;

				case 1:  // New color selected?
					changedGrfx = 4;
					return;
					//break;
                case 2:
                    checkDots(g);
                    break;
				case 3: // Given up?
				    showMaster();
				   // okButtonOn = false;
				    okButton(false);
				    changedGrfx = 4;
					break;
			    case 5:  // peg draged over peg
			    	i=selectedHole/4;
					j=selectedHole- i*4;

				    guessBoard[j][i]=selectedColor;
				    boardPeg(g,j,i,selectedColor);
                    boolean temp = true;
                    for (j=0;j<4;j++)  //if guess is complete
                    temp=(temp && (guessBoard[j][currentRow]>=0));
                    if(temp){
                      // okButtonOn = true;
                       okButton(true);}
					break;
				default:
					break;
			}//end switch
            paint(g);
		    drawBoard=true;
		}
	}


    public void showMaster(){

            Graphics g =this.getGraphics();
            g.setColor(brown);
			g.fillRect(15,10, 131, 45 );
			for (int  j=0;j<4;j++){
			    int x = 23+j*31;
	            int y = 18;

		        fixDisc(g,x+2,y+2,25,darkGray);
		        fixDisc(g,x,y,25,darks[theMaster[j]]);
		        fixDisc(g,x+1,y+1,21,lights[theMaster[j]]);
		        fixDisc(g,x+3,y+3,9,Color.white);
            }
            gameGoing = false;
          //  gameOnButton(gameGoing);

			masterDisplayed = true;

	}

	public void checkDots(Graphics g)  // Check the pegs
	{// called by update
		boolean temp=true;
		int holdDots[];
		int rightColor=0,rightPlace=0;
        if (currentRow >=9)
            return;
		for (int j=0;j<4;j++)
			temp=(temp && (guessBoard[j][currentRow]>=0));

		if (temp)		// Guess complete?
		{
			holdDots=new int[4];
			for (int j=0;j<4;j++)
				holdDots[j]=guessBoard[j][currentRow];

			for (int i=0;i<4;i++)	// Award white pegs
				for (int j=0;j<4;j++)
					if (holdDots[j]==theMaster[i])
					{
						pointBoard[rightColor++][currentRow]=0;
						holdDots[j]=-1;
						j=4;
					}
			for (int j=0;j<4;j++)	// And black ones
			{
				if (theMaster[j]==guessBoard[j][currentRow])
					rightPlace++;
			}
			for (int j=0;j<rightPlace;j++)
				pointBoard[j][currentRow]=1;

						// Display pegs
			for (int j=0;j<2;j++)
				for (int k=0;k<2;k++)
					if (pointBoard[j*2+k][currentRow]>=0)
						pointBeg(g,k,currentRow,j,pointBoard[j*2+k][currentRow]);

			if (rightPlace==4)	// All correct?
			{
				//g.setColor(Color.black);
				//g.drawString("BRAVO",160,20);
				showMaster();
				drawBoard = false;
				masterDisplayed = true;
				currentRow=8;
			}
			currentRow++;
			if (currentRow>7)	// Game over?
			{
				if (rightPlace<4) // Didn't make it?
				{
					//g.setColor(Color.black);
				//	g.drawString("PROSTULE",160,15);
					showMaster();
				}
				gameGoing=false;

			}
		}
	}

}

//__________________________________
class movingPeg{

    int Xloc =0;
    int Yloc = 0;
    int pegColor =0;
    boolean moving = false;
    int XhomeLoc =0;
    int YhomeLoc = 0;

    public movingPeg(){
    }

    public int getXloc(){
        return Xloc;}

    public int getYloc(){
        return Yloc;}

    public int getHomeX(){
        return XhomeLoc;}
    public int getHomeY(){
        return YhomeLoc;}

    public int getColor(){
        return pegColor;}

    public boolean isMoving(){
        return moving;}

    public void isMoving(boolean input){
        moving = input;}

    public void homeLocation(int x, int y){
        XhomeLoc = x;
        Xloc = x;
        YhomeLoc = y;
        Yloc = y;
    }
    public void newLocation(int x, int y){
        Xloc = x;
        Yloc = y;
    }
    public void newColor(int newcolor){
        pegColor = newcolor;}

    public void clear(){
        this.homeLocation(0,0);
        pegColor = -1;
        moving =false;
    }
}

