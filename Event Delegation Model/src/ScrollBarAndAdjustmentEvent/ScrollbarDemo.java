package ScrollBarAndAdjustmentEvent;

import java.awt.*;
import java.awt.event.*;

class MyFrame extends Frame implements AdjustmentListener //Its functional interfaces because it have single method only.
{
    Scrollbar red,green,blue;
    TextField tf;

    public MyFrame() {
        super("Scroll bar demo");

        red = new Scrollbar(Scrollbar.HORIZONTAL, 0, 20, 0, 255);
        blue = new Scrollbar(Scrollbar.HORIZONTAL, 0, 20, 0, 255);
        green = new Scrollbar(Scrollbar.HORIZONTAL, 0, 20, 0, 255);
        tf = new TextField(20);

        tf.setBounds(50, 50, 300, 50);
        red.setBounds(50, 150, 300, 30);
        blue.setBounds(50, 200, 300, 30);
        green.setBounds(50, 250, 300, 30);

        setLayout(null);
        add(tf);
        add(red);
        add(blue);
        add(green);

        //Adjustment Listener.
        red.addAdjustmentListener(this);
        blue.addAdjustmentListener(this);
        green.addAdjustmentListener(this);
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        //Setting background color. using get method and showing it on Text Filed.
        tf.setBackground(new Color(red.getValue(),blue.getValue(),green.getValue()));
    }
}
public class ScrollbarDemo {
    public static void main(String[] args) {
        MyFrame f=new MyFrame();
        f.setSize(500,500);
        f.setVisible(true);
        // TODO code application logic here
    }
}