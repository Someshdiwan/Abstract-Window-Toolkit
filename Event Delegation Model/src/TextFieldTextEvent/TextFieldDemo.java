package TextFieldTextEvent;

import java.awt.*;
import java.awt.event.*;

class MyFrame extends Frame
{
    //Need two Labels
    Label l1,l2;
    TextField tf;

    MyFrame()
    {
        super("Text Field");
        l1=new Label("No Text Is Entered Yet");
        l2=new Label("Enter a key. and it is not yet hit");
        tf=new TextField(20);
        tf.setEchoChar('*');

        Handler h = new Handler();

        tf.addTextListener(h);
        tf.addActionListener(h);

        setLayout(new FlowLayout());
        add(l1);
        add(tf);
        add(l2);

    }
    class Handler implements TextListener,ActionListener
    {
        @Override
        public void textValueChanged(TextEvent e)
        {
            l1.setText(tf.getText());
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            l2.setText(tf.getText());
        }
    }
}

public class TextFieldDemo {
    public static void main(String[] args) {
       /*Two Events handel here.
        Text Event(For enter a text) and Action Event(Displaying a action).
        */

        MyFrame f = new MyFrame();
        f.setSize(400,400);
        f.setVisible(true);
    }
}