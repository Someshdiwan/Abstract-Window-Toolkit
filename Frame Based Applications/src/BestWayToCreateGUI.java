import java.awt.*;

public class BestWayToCreateGUI extends Frame {
    //Create a references.
    Label l;
    TextField tf;
    Button b;

    //Creating constructor MyFrame.
    public BestWayToCreateGUI()
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

    public static void main(String[] args) {
        BestWayToCreateGUI b = new BestWayToCreateGUI();
    }
}