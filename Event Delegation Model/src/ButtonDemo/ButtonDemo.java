package ButtonDemo;

import java.awt.*;
import java.awt.event.*;

class MyFrame extends Frame implements ActionListener{
    int count =0;
    Label l;
    Button b;

    public MyFrame(){
        super("Button Demo");
        l=new Label("   "+count);
        b=new Button("Click");
        b.addActionListener(this);

        setLayout(new FlowLayout());
        add(l);
        add(b);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        l.setText(" "+count);
    }
}
public class ButtonDemo {
    public static void main(String[] args) {
        MyFrame m = new MyFrame();
        m.setSize(400,400);
        m.setVisible(true);
        m.setBackground(Color.orange);
    }
}