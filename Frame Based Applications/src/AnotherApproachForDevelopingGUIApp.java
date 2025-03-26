import java.awt.*;

/**
 * This class demonstrates the best way to create a GUI.
 *
 * @see <a href="file:///C:/Users/somes/Downloads/Abstract-Window-Toolkit/Frame%20Based%20Applications/src/BestWayToCreateGUI.java">Open BestWayToCreateGUI.java (Local Path)</a>
 **/

class MyFrame extends Frame
{
    //Create a references.
    Label l;
    TextField tf;
    Button b;

    //Creating constructor MyFrame.
    public MyFrame()
    {
        //Setting the title of frame. super means frames constructor will be called.
        super("My App");

        //Inside constructor, we create objects and add all of them into Frame.
        setLayout(new FlowLayout());
        l  =  new Label("Name");
        tf =  new TextField(20);
        b  =  new Button("OK");

        //Add into the frame.
        add(l);
        add(tf);
        add(b);
    }
}

public class AnotherApproachForDevelopingGUIApp {
    public static void main(String[] args) {
        MyFrame f = new MyFrame();

        f.setSize(400,400);
        f.setVisible(true);
    }
}

//Creating separate class is for when we are creating a large size app.
//If you create small app you can check this out. BestWayToCreateGUI.java
//Another way to write above code. added check code in the file.