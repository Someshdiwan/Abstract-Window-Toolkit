package SecondWay;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class  MyFrame extends Frame {
    TextField tf;
    Button b;

    int count = 0;

    MyFrame() {
        super("Event Demo");

        tf = new TextField("0", 20);
        b = new Button("Click Button for Increase the count");

        setLayout(new FlowLayout());

        add(tf);
        add(b);

        b.addActionListener(new MyListenrer()); //Anonymous Object Of Anonymous class.
    }

    class MyListenrer implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            count++;
            tf.setText(String.valueOf(count)); //Convert count into the String
            //This is a Listener.
        }
    }
}

public class InnerClass {
    public static void main(String[] args)
    {
        MyFrame f=new MyFrame();
        f.setSize(500,500);
        f.setVisible(true);
    }
}