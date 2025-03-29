package FlowLayout;

import java.awt.*;

class MyFrame extends Frame
{
    Button b1, b2, b3,b4,b5,b6;

    public MyFrame()
    {
        super("Flow Layout");

        b1=new Button("One");
        b2=new Button("Two");
        b3=new Button("Three");
        b4=new Button("Four");
        b5=new Button("Five");
        b6=new Button("Six");

        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.RIGHT);
        fl.setHgap(100);
        setLayout(fl);

        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        add(b6);
    }
}
public class FlowLayoutDemo {
    public static void main(String[] args) {
        MyFrame f=new MyFrame();
        f.setSize(500,500);
        f.setVisible(true);
    }
}