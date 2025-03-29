import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class MyWindowAdapter extends WindowAdapter
{
    @Override
    public void windowClosing(WindowEvent we)
    {
        System.exit(0);
    }
}

class MyFrame extends Frame
{
    MyFrame()
    {
        super("Adapter Demo");
        addWindowListener(new MyWindowAdapter());
    }
}

public class AdapterClassDemo {
    public static void main(String[] args) {
        MyFrame f=new MyFrame();
        f.setSize(500,500);
        f.setVisible(true);
    }
}