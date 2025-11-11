import java.awt.*;

public class MMSGUI  extends Frame
{

    TextField master=new TextField(4);
    Button submit=new Button("Submit");
    TextArea output=new TextArea(12,20);

    public static void main(String[] args){
        MMSGUI GUI = new MMSGUI("MMSolver Frame");
        GUI.resize(390,250);
        GUI.show();
    }

    MMSGUI(String title)
    {
        super(title);
        this.setLayout(new GridLayout(1,2));
        setBackground(new Color(60,100,60));
        Panel masterPl = new Panel();

        Panel outputPl=new Panel();


        masterPl.add(new Label("Valid digits 1:6"));
        masterPl.add(new Label("Enter a master here"));
        masterPl.add(master);
        //button
        masterPl.add(submit);
        //output
        outputPl.add(output);
        output.setEditable(true);
        //final layout
        setLayout(new GridLayout(1,2));
        Panel leftPl=new Panel();
        Panel rightPl=new Panel();
        leftPl.setLayout(new GridLayout(2,1));
        leftPl.add(masterPl);
       // leftPl.add(submitPl);

        rightPl.add(outputPl);
        setLayout(new GridLayout(1,2));
        add(leftPl);
        add(rightPl);

    }


    public boolean action(Event evt,Object arg) {
        if(evt.target instanceof Button)
        {
            mmsolver compsolver = new mmsolver(master.getText());
            compsolver.runSolver();
            output.setText(compsolver.outputString);
            return true;
        }
        return false;
    }

    public boolean handleEvent(Event e)
    {
  	    if (e.id==Event.WINDOW_DESTROY)
  	    {
	        System.exit(0);
	        return true;
	    }
	    return super.handleEvent(e);
    }

    public boolean keyDown(Event event, int key){
        if(key==10)
        {
            mmsolver compsolver = new mmsolver(master.getText());
            compsolver.runSolver();
            output.setText(compsolver.outputString);
            return true;
        }
        return false;
    }
}
