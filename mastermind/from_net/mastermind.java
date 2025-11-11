import java.awt.*;
import java.lang.Math;

public final class mastermind extends java.applet.Applet
{
	int i,j,k;      //some ubsurd ints  //really bad OO
	int pointX,pointY,pickX=-1,pickY=-1,dragX=-1,dragY=-1;

	int dotMap[],pointMap[],secretCol[];  //arrays for the pegs
                                            //initialized in init
	static int shadX[]={0,8,142,142,136,136,0},  //shading variables
		shadY[]={242,246,246,10,0,242,242};
	int selectedColor=0,changedCol;

	int pickedDot, changedGrfx, currentRow;
        //                      the current guess row
	boolean drawBoard=true, gameGoing=false;
	boolean pickable[]={true,true,true,true,true,true};
	boolean finished;
	Color lights[],darks[],darkGray;   //used for the pegs
    Color brown = new Color(128,94,2);
	Image buffer,peg;       //images
	Graphics bufg,pegg;     //graphics
	Math m;

	public void init()
	{
		dotMap = new int[32];  // Holds colored pin info
		pointMap = new int[32];  // Holds B&W score pin info
		secretCol = new int[4];  // Holds computer's choice of colors
		lights = new Color[6];  // arrays of colors for drawing
		darks = new Color[6];

		darkGray=new Color(60,60,60);  ///used for shading
		darks[0]=new Color(0,0,255); // Blue
		lights[0]=new Color(128,128,255);
		darks[1]=new Color(0,192,0); // Green
		lights[1]=new Color(64,255,64);
		darks[2]=new Color(255,0,0); // Red
		lights[2]=new Color(255,128,128);
		darks[3]=new Color(255,192,0); // Yellow
		lights[3]=new Color(255,255,70);
		darks[4]=new Color(210,150,0); // Orange
		lights[4]=new Color(245,185,0);
		darks[5]=new Color(255,0,255); // Purple
		lights[5]=new Color(255,128,255);

		buffer=createImage(250,250); // Prepare drawing buffers
		bufg=buffer.getGraphics();
		peg=createImage(48,40);  // Used for drawing peg symbol
		pegg=peg.getGraphics();
		resize(250,250);   // resizes the applet area to look nice
		clearBoard();
	}

	public void clearBoard()  // Reset all variables and prepare for game
	{
		for (i=0;i<32;i++)   // unde pui piesele
		{
			dotMap[i]=-1;
			pointMap[i]=-1;
		}
		currentRow=0;
		// create the master
		for (i=0;i<4;i++)
		{
			secretCol[i]=-1;
			while (secretCol[i]<0)
			{
				k=(int)(m.random()*6);
				if (pickable[k])    // daca k este pickable
					secretCol[i]=k;
			}
		}
		repaint();
	}

// Various graphical elements

	public void fixBox(Graphics g,int x,int y,int w,int h,Color c)
	{
		g.setColor(c);
		g.fillRect(x,y,w,h);
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

	public void boardDot(Graphics g,int x,int y,int c)//draws a peg on the board
	{
		fixDisc(g,x+2,y+2,18,darkGray);
		fixDisc(g,x,y,18,darks[c]);
		fixDisc(g,x+1,y+1,14,lights[c]);
		fixDisc(g,x+3,y+3,6,Color.white);
	}
	public void boardHole(Graphics g,int x,int y) //draws the peg holes
	{
		fixDisc(g,x+5,y+5,7,Color.black);
		g.setColor(Color.lightGray);
		g.drawArc(x+4,y+4,9,9,-135,180);
	}
	public void pointDot(Graphics g,int x,int y,int c)//draws a score point
	{                                                   //whire or black
		fixDisc(g,x+2,y+2,10,Color.black);
		if (c==1)
		{
			fixDisc(g,x,y,10,darkGray);
			g.setColor(Color.white);
			g.drawArc(x+2,y+2,7,7,70,130);
		}
		else
		{
			fixDisc(g,x,y,10,Color.white);
			fixDisc(g,x+3,y+3,4,Color.lightGray);
			fixDisc(g,x+5,y+5,4,Color.white);
		}
	}

	public void pointHole(Graphics g,int x,int y)  /// draws a score hole
	{
		fixDisc(g,x+3,y+3,4,Color.black);
		g.setColor(Color.lightGray);
		g.drawArc(x+2,y+2,6,6,-135,180);
	}

	public void bigPeg(Graphics g, int c)
	{
		fixBox(pegg,0,0,48,40,Color.lightGray);
		fixDisc(pegg,0,0,40,darks[c]);
		fixDisc(pegg,2,2,29,lights[c]);
		fixDisc(pegg,7,6,8,Color.white);
		fixCircle(pegg,0,0,39,Color.black);
		fixBox(pegg,22,0,26,40,Color.lightGray);
		fixBox(pegg,22,15,26,10,darks[c]);
		fixBox(pegg,31,15,17,8,lights[c]);
		pegg.setColor(Color.black);
		pegg.drawLine(22,0,22,40);
		pegg.drawRect(22,15,25,10);
		g.drawImage(peg,188,46,this);
	}

	public boolean mouseDown(java.awt.Event evt, int x, int y)
	//what the mouse does
	//takes an event and x and y coordinates
	{
		int whichCol,i,j,k,l;

					// Peg placed?
		for (j=0;j<4;j++)  //if in the four posible holef for a per
			if (((x-(23+j*21))*(x-(23+j*21))+   /// how this if works is beyond me
			    (y-(219-currentRow*28))*(y-(219-currentRow*28)) < 100 )
				&& gameGoing)
			{
				pickedDot=currentRow*4+j;
				changedGrfx=0;
				drawBoard=false;
				repaint();
			}
					// Color selected?
		for (i=0;i<3;i++)   // for the six colors
			for (j=0;j<2;j++)
				if ((x-(185+j*40))*(x-(185+j*40))+
				(y-(115+i*40))*(y-(115+i*40))<225)
				{
					whichCol=i*2+j; // New color   // new color is selected
					if (gameGoing && pickable[whichCol])  //for placing the next peg
					{
						selectedColor=whichCol;
						changedGrfx=1;
						drawBoard=false;
						repaint();
					}
					//the chose color BS
					else if (!gameGoing) // Toggle color
					{
						changedCol=whichCol;
						pickable[changedCol]=!pickable[changedCol]; //inverse t/f f/t
						l=0;    //one of those dork non oo variables
						for (k=0;k<6;k++)   // see how many pickables there are
							if (pickable[k])
								l++;
						if (l<2) // if less then 2 then... we cannot have this so change it back
							pickable[changedCol]=!pickable[changedCol];
							///and do not redraw the applet
						else
						{
							changedGrfx=2;    //?????
							drawBoard=false; ///?????
							repaint();
						}
					}
				}
					// Start or End clicked?
		if ((x>174)&&(x<235)&&(y>217)&&(y<236))
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
		}
		return false;
	}

	public void checkDots(Graphics g)  // Check the pegs
	{
		boolean temp=true;
		int holdDots[];
		int rightColor=0,rightPlace=0;

		for (j=0;j<4;j++)
			temp=(temp && (dotMap[currentRow*4+j]>=0));

		if (temp)		// Guess complete?
		{
			holdDots=new int[4];
			for (j=0;j<4;j++)
				holdDots[j]=dotMap[currentRow*4+j];

			for (i=0;i<4;i++)	// Award white pegs
				for (j=0;j<4;j++)
					if (holdDots[j]==secretCol[i])
					{
						pointMap[currentRow*4+rightColor++]=0;
						holdDots[j]=-1;
						j=4;
					}
			for (j=0;j<4;j++)	// And black ones
			{
				if (secretCol[j]==dotMap[currentRow*4+j])
					rightPlace++;
			}
			for (j=0;j<rightPlace;j++)
				pointMap[currentRow*4+j]=1;

						// Display pegs
			for (j=0;j<2;j++)
				for (k=0;k<2;k++)
					if (pointMap[currentRow*4+j*2+k]>=0)
						pointDot(g,101+k*12,
						206-currentRow*28+j*12,pointMap[currentRow*4+j*2+k]);

			if (rightPlace==4)	// All correct?
			{
				g.setColor(Color.black);
				g.drawString("BRAVO",160,20);
				currentRow=8;
			}
			currentRow++;
			if (currentRow>7)	// Game over?
			{
				if (rightPlace<4) // Didn't make it?
				{
					g.setColor(Color.black);
					g.drawString("PROSTULE",188,15);
					for (j=0;j<4;j++)
						boardDot(g,163+j*21,20,secretCol[j]);
				}
				gameGoing=false;
				g.setColor(Color.gray);
				g.fill3DRect(175,218,60,18,true);
				g.setColor(Color.white);
				g.drawString("Begin",191,231);
			}
		}
	}

	public void paint(Graphics g)
	{
		bufg.setColor(Color.lightGray); // Clear offcreen image buffer
		bufg.fillRect(0,0,250,250);
					// Draw the board itself
		bufg.setColor(brown);
		for (i=0;i<5;i++)
			bufg.fill3DRect(i,i,136-2*i,242-2*i,true);
		for (i=0;i<8;i++)
		{
			bufg.draw3DRect(10,10+i*28,88,25,false);
			bufg.draw3DRect(100,10+i*28,25,25,false);
		}
		bufg.fillPolygon(shadX,shadY,7);

		for (i=0;i<8;i++)
		{
					// Draw colored pins or just holes
			for (j=0;j<4;j++)
				if (dotMap[i*4+j]<0)
					boardHole(bufg,13+j*21,209-i*28);
				else
					boardDot(bufg,13+j*21,209-i*28,dotMap[i*4+j]);

					// Draw the score pins or holes
			for (j=0;j<2;j++)
				for (k=0;k<2;k++)
					if (pointMap[i*4+j*2+k]<0)
						pointHole(bufg,101+k*12,206-i*28+j*12);
					else
						pointDot(bufg,101+k*12,
						206-i*28+j*12,pointMap[i*4+j*2+k]);
		}

					// Draw the right field
		bufg.setColor(Color.gray);
		bufg.fill3DRect(160,92,90,151,true);
		bufg.fill3DRect(161,93,88,149,true);
		bufg.setColor(Color.gray);
		bufg.fill3DRect(175,218,60,18,true);
		bufg.setColor(Color.white);
		if (gameGoing)//
			bufg.drawString("End",195,231);
		else
			bufg.drawString("Begin",191,231);
		for (i=0;i<3;i++)
			for (j=0;j<2;j++)
			{
				fixDisc(bufg,169+j*40,99+i*40,32,Color.black);
				fixDisc(bufg,170+j*40,100+i*40,30,darks[i*2+j]);
				fixDisc(bufg,171+j*40,101+i*40,25,lights[i*2+j]);
				fixDisc(bufg,175+j*40,105+i*40,7,Color.white);
			//	if (pickable[i*2+j])
				//	fixCircle(bufg,168+j*40,98+i*40,34,Color.white);
			}
		bigPeg(bufg,selectedColor);
		g.drawImage(buffer,0,0,this); // Copy image buffer to screen
	}

	public void update(Graphics g)
	{
		if (drawBoard)
			paint(g);
		else
		{				// Process small changes
			switch(changedGrfx)
			{
				case 0: // Peg placed in hole?
					i=pickedDot/4;
					j=pickedDot-i*4;
					dotMap[pickedDot]=selectedColor;
					boardDot(g,13+j*21,209-i*28,selectedColor);
					checkDots(g);
					break;

				case 1:  // New color selected?
					bigPeg(g,selectedColor);
					break;

				case 2: // Color availability toggled?
					i=changedCol/2;
					j=changedCol-i*2;
					if (pickable[changedCol])
						fixCircle(g,168+j*40,98+i*40,34,Color.white);
					else
						fixCircle(g,168+j*40,98+i*40,34,Color.gray);
					break;

				case 3: // Given up?
					g.setColor(Color.gray);
					g.fill3DRect(175,218,60,18,true);
					g.setColor(Color.white);
					g.drawString("Begin",186,231);
					break;
				default:
					break;
			}
			drawBoard=true;
		}
	}
}

