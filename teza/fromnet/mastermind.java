import java.awt.*;
import mm;
import mm_result;

public final class mastermind extends java.applet.Applet
{
    private int max_colors = 6;

    private int colors = 6;            // must be smaller than max_colors
    private int attempts = 10;
    private int positions = 4;

    private int i;
    private int j;
    private int dotMap[][];
    private int pointMap[][];
    private int pickedCol;
    private int pickedDot;
    private int changedGrfx;
    private int currentRow;
    private int p2;

    private boolean drawBoard;
    private boolean gameFinished;
    private boolean appletGuessing;

    private Color lights[];
    private Color darks[];
    private Color darkGray;
    private Color darkBrown;
    private Color lightBrown;
    private Color normalBrown;

    private Image buffer;
    private Graphics bufg;

    private mm mm_logic;

    public void init()
    {
        p2 = (positions + 2) / 2;

        dotMap = new int[attempts][positions];  // Holds colored pin info
        pointMap = new int[attempts][positions];  // Holds B&W score pin info
        lights = new Color[max_colors];
        darks = new Color[max_colors];

        darks[0] = new Color(0, 0, 255); // Blue
        lights[0] = new Color(128, 128, 255);
        darks[1] = new Color(0, 192, 0); // Green
        lights[1] = new Color(64, 255, 64);
        darks[2] = new Color(255, 0, 0); // Red
        lights[2] = new Color(255, 128, 128);
        darks[3] = new Color(255, 192, 0); // Yellow
        lights[3] = new Color(255, 255, 70);
        darks[4] = new Color(210, 150, 0); // Orange
        lights[4] = new Color(245, 185, 0);
        darks[5] = new Color(255, 0, 255); // Purple
        lights[5] = new Color(255, 128, 255);
        darks[6] = new Color(215, 215, 215); // White
        lights[6] = new Color(230, 230, 230);
        darks[7] = new Color(25, 25, 25); // Black
        lights[7] = new Color(100, 100, 100);
        darks[8] = new Color(100, 50, 25); // Brown
        lights[8] = new Color(180, 90, 45);

        darkGray = new Color(31, 31, 31);

        darkBrown = new Color(68, 40, 20);
        lightBrown = new Color(220, 150, 100);
        normalBrown = new Color(170, 100, 50);

        drawBoard = true;
        appletGuessing = false;
        gameFinished = true;

        buffer = createImage(positions * 21 + p2 * 12 + 64, attempts * 30 + 48); // Prepare drawing buffers
        bufg = buffer.getGraphics();
        resize(positions * 21 + p2 * 12 + 64, attempts * 30 + 48);
        clearBoard();
    }

    public void clearBoard()  // Reset all variables and prepare for game
    {
        int row;
        int pos;

        for (row = 0; row < attempts; row++)
        {
            for (pos = 0; pos < positions; pos++)
            {
                dotMap[row][pos] = -1;
                pointMap[row][pos] = -1;
            }
        }
        appletGuessing = !appletGuessing;
        gameFinished = false;
        currentRow = 0;
        mm_logic = new mm(positions, colors, attempts);
        if (appletGuessing)
            dotMap[currentRow] = get_guess();
        repaint();
    }

    public void fixBox(Graphics g, int x, int y, int w, int h, Color c)
    {
        g.setColor(c);
        g.fillRect(x, y, w, h);
    }
    public void fixDisc(Graphics g, int x, int y, int r, Color c)
    {
        g.setColor(c);
        g.fillOval(x, y, r, r);
    }
    public void fixCircle(Graphics g, int x, int y, int r, Color c)
    {
        g.setColor(c);
        g.drawOval(x, y, r, r);
    }

    public void selectDot(Graphics g, int col)
    {
        int x = positions * 21 + p2 * 12 + 31;
        int y = attempts * 30 - col * 21 - 16;

        fixDisc(g, x + 2, y + 2, 18, darkBrown);
        fixDisc(g, x, y, 18, darks[col]);
        fixDisc(g, x + 1, y + 1, 14, lights[col]);
        fixDisc(g, x + 3, y + 3, 6, Color.white);
    }
    public void boardDot(Graphics g, int row, int pos)
    {
        int x = pos * 21 + 13;
        int y = (attempts - row) * 30 - 17;
        int c = dotMap[row][pos];

        if (c < 0)
        {
            fixDisc(g, x + 5, y + 5, 7, Color.black);
            g.setColor(lightBrown);
            g.drawArc(x + 4, y + 4, 9, 9, -135, 180);
        }
        else
        {
            fixDisc(g, x + 2, y + 2, 18, darkBrown);
            fixDisc(g, x, y, 18, darks[c]);
            fixDisc(g, x + 1, y + 1, 14, lights[c]);
            fixDisc(g, x + 3, y + 3, 6, Color.white);
        }
    }
    public void pointDot(Graphics g, int row, int pos)
    {
        int x = (pos % p2) * 12 + positions * 21 + 17;
        int y = (attempts - row) * 30 + (pos / p2) * 12 - 18;
        int c = pointMap[row][pos];

        if (c == 1)
        {
            fixDisc(g, x + 2, y + 1, 10, darkBrown);
            fixDisc(g, x, y, 10, darkGray);
            g.setColor(Color.white);
            g.drawArc(x + 2, y + 2, 7, 7, 70, 130);
        }
        else if (c == 0)
        {
            fixDisc(g, x + 2, y + 1, 10, darkBrown);
            fixDisc(g, x, y, 10, Color.white);
            fixDisc(g, x + 3, y + 3, 4, Color.lightGray);
            fixDisc(g, x + 5, y + 5, 4, Color.white);
        }
        else
        {
            fixDisc(g, x + 2, y + 1, 10, normalBrown);
            fixDisc(g, x, y, 10, normalBrown);
            fixDisc(g, x + 3, y + 3, 4, Color.black);
            g.setColor(lightBrown);
            g.drawArc(x + 2, y + 2, 6, 6, -135, 180);
        }
    }

    private int sqrLen(int x, int y)
    {
        return x * x + y * y;
    }

    public boolean mouseUp(java.awt.Event evt, int x, int y)
    {
        int pos;

        if (!appletGuessing && !gameFinished && pickedCol >= 0)
        {
            // Peg placed?
            for (pos = 0; pos < positions; pos++)
            {
                if (sqrLen(x - (pos * 21 + 23), y - ((attempts - currentRow) * 30 - 7)) < 100 )
                {
                    pickedDot = pos;
                    changedGrfx = 0;
                    drawBoard = false;
                    repaint();
                }
            }
        }

        return false;
    }
    public boolean mouseDown(java.awt.Event evt, int x, int y)
    {
        int row;
        int pos;
        int col;

        if (!gameFinished)
        {
            if (appletGuessing)
            {
                for (pos = 0; pos < positions; pos++)
                {
                    int xx = (pos % p2) * 12 + positions * 21 + 22;
                    int yy = (attempts - currentRow) * 30 + (pos / p2) * 12 - 13;
                    if (sqrLen(x - xx, y - yy) < 25)
                    {
                        pickedDot = pos;
                        changedGrfx = 3;
                        drawBoard = false;
                        repaint();
                    }
                }
            }
            else
            {
                // Color selected?
                pickedCol = -1;

                // First try the already placed pegs
                for (row = 0; row < attempts; row++)
                    for (pos = 0; pos < positions; pos++)
                        if (sqrLen(x - (pos * 21 + 23), y - ((attempts - row) * 30 - 7)) < 100 )
                            pickedCol = dotMap[row][pos];;

                // Now try the peg store
                for (col = 0; col < colors; col++)
                    if (sqrLen(x - (positions * 21 + p2 * 12 + 41), y - (attempts * 30 - col * 21 - 6)) < 100)
                        pickedCol = col; // New color
            }
        }

        // Button clicked?
        if ((x > 10) && (x < positions * 21 + p2 * 12 + 55) && (y > attempts * 30 + 18) && (y < attempts * 30 + 38))
        {
            if (gameFinished)
            {
                clearBoard();
            }
            else if (appletGuessing)
            {
                changedGrfx = 2;
                drawBoard = false;
                repaint();
            }
            else
            {
                changedGrfx = 1;
                drawBoard = false;
                repaint();
            }
        }
        return false;
    }

    public void checkDots(Graphics g)  // Check the pegs
    {
        boolean temp=true;
        mm_result res;

        for (j = 0; j < positions; j++)
            temp=(temp && (dotMap[currentRow][j] >= 0));

        if (temp)		// Guess complete?
        {
            res = mm_logic.set_guess(dotMap[currentRow]);

            for (j = 0; j < res.black; j++)
                pointMap[currentRow][j]=1;
            for (j = res.black; j < res.black + res.white; j++)
                pointMap[currentRow][j]=0;

            // Display pegs
            for (j = 0; j < positions; j++)
                pointDot(g, currentRow, j);

            if (res.black == positions || currentRow == attempts - 1)	// Done?
            {
		mm_logic.print_code();
                gameFinished = true;
                g.setColor(normalBrown);
                g.fillRect(12, attempts * 30 + 20, positions * 21 + p2 * 12 + 41, 18);
                g.setColor(Color.white);
                g.drawString("New game", 20, attempts * 30 +32);
            }
            else
            {
                currentRow++;
            }
        }
    }

    public void checkPoints(Graphics g)  // Check the pegs
    {
        mm_result res = new mm_result();

        res.black = 0;
        res.white = 0;
        for (i = 0; i < positions; i++)
        {
            if (pointMap[currentRow][i] == 1)
                res.black++;
            else if (pointMap[currentRow][i] == 0)
                res.white++;
        }

        mm_logic.set_result(res);

        if (res.black == positions || currentRow == attempts - 1)    // Done?
        {
            gameFinished = true;
            g.setColor(normalBrown);
            g.fillRect(12, attempts * 30 + 20, positions * 21 + p2 * 12 + 51, 18);
            g.setColor(Color.white);
            g.drawString("New game", 20, attempts * 30 + 32);
        }
        else
        {
            currentRow++;
            dotMap[currentRow] = get_guess();
            for (i = 0; i < positions; i++)
                boardDot(g, currentRow, i);
        }
    }

    public void paint(Graphics g)
    {
        // Draw the board itself
        bufg.setColor(normalBrown);
        bufg.fill3DRect(0, 0, positions * 21 + p2 * 12 + 64, attempts * 30 + 48, true);
        for (i = 0; i < attempts; i++)
        {
            bufg.draw3DRect(10, i * 30 + 10, positions * 21 + 4, 25, false);
            bufg.draw3DRect(positions * 21 + 16, i * 30 + 10, p2 * 12 + 1, 25, false);
        }
        for (i = 0; i < attempts; i++)
        {
            for (j = 0; j < positions; j++)
            {
                boardDot(bufg, i, j);
                pointDot(bufg, i, j);
            }
        }

        // Draw the available pegs
        bufg.setColor(normalBrown);
        bufg.draw3DRect(positions * 21 + p2 * 12 + 26, 10, 28, attempts * 30 - 3, false);
        for (i = 0; i < colors; i++)
            selectDot(bufg, i);

        // Draw the button
        bufg.setColor(normalBrown);
        bufg.draw3DRect(10, attempts * 30 + 18, positions * 21 + p2 * 12 + 45, 20, true);
        bufg.setColor(Color.white);
        if (gameFinished)
            bufg.drawString("New game", 20, attempts * 30 + 32);
        else if (appletGuessing)
            bufg.drawString("Enter result", 20, attempts * 30 + 32);
        else
            bufg.drawString("Enter guess", 20, attempts * 30 + 32);

        g.drawImage(buffer, 0, 0, this); // Copy image buffer to screen
    }

    public void update(Graphics g)
    {
        if (drawBoard)
        {
            paint(g);
        }
        else
        {				// Process small changes
            switch(changedGrfx)
            {
            case 0: // Board peg placed in hole
                dotMap[currentRow][pickedDot] = pickedCol;
                boardDot(g, currentRow, pickedDot);
                break;

            case 1: // Get result
                checkDots(g);
                break;

            case 2: // Get next guess
                checkPoints(g);
                break;

            case 3: // Point peg placed in hole
                pointMap[currentRow][pickedDot]++;
                if (pointMap[currentRow][pickedDot] == 2)
                    pointMap[currentRow][pickedDot] = -1;
                pointDot(g, currentRow, pickedDot);
                break;

            default:
                break;
            }
            drawBoard=true;
        }
    }

    private int [] get_guess()
    {
        try
        {
            return mm_logic.get_guess();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            showStatus(e.getMessage());
            appletGuessing = false;
            clearBoard();
            return dotMap[1];
        }
    }
}
