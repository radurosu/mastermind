
import java.awt.*;

public class FarmGUI extends java.applet.Applet
{
    TextField fname=new TextField(10);
    TextField lname=new TextField(10);
    TextField street=new TextField(20);
    TextField city=new TextField(10);
    TextField state=new TextField(2);
    TextField zip=new TextField(5);
    Choice horse=new Choice();
    Button submit=new Button("Submit");
    TextArea output=new TextArea(5,25);


    public void init()
    {
        setBackground(new Color(0,133,63));
        Panel fnamePn=new Panel();
        Panel lnamePn=new Panel();
        Panel streetPn=new Panel();
        Panel cityPn=new Panel();
        Panel statePn=new Panel();
        Panel zipPn=new Panel();
        Panel horsePn=new Panel();
        Panel submitPn=new Panel();
        Panel outputPn=new Panel();
        //input fields
        fnamePn.add(new Label("First name:"));
        fnamePn.add(fname);
        lnamePn.add(new Label("Last name:"));
        lnamePn.add(lname);
        streetPn.add(new Label("Street:"));
        streetPn.add(street);
        cityPn.add(new Label("City:"));
        cityPn.add(city);
        statePn.add(new Label("State:"));
        statePn.add(state);
        zipPn.add(new Label("Zip:"));
        zipPn.add(zip);
        //choice
        horsePn.add(new Label("Preferred horse:"));
        horse.addItem("Draft");
        horse.addItem("Riding");
        horse.addItem("Racing");
        horsePn.add(horse);
        //button
        submitPn.add(submit);
        //output
        outputPn.add(output);
        output.setEditable(false);
        //final layout
        setLayout(new GridLayout(1,2));
        Panel leftPn=new Panel();
        Panel rightPn=new Panel();
        Panel hrsSbmtPn=new Panel();
        leftPn.setLayout(new GridLayout(6,1));
        leftPn.add(fnamePn);
        leftPn.add(lnamePn);
        leftPn.add(streetPn);
        leftPn.add(cityPn);
        leftPn.add(statePn);
        leftPn.add(zipPn);
        hrsSbmtPn.add(horsePn);
        hrsSbmtPn.add(submitPn);
        rightPn.setLayout(new GridLayout(2,2));
        rightPn.add(hrsSbmtPn);
        rightPn.add(outputPn);
        add(leftPn);
        add(rightPn);
    }

    public boolean action(Event evt,Object arg)
    {
        if(evt.target instanceof Button)
        {
            output.setText(fname.getText()+" "+lname.getText()+"\n"+
                           street.getText()+"\n"+
                           city.getText()+","+state.getText()+" "+
                           zip.getText()+"\n"+"\n"+
                           "Preferred horse: "+horse.getSelectedItem());
            return true;
        }
        return false;
    }
}
