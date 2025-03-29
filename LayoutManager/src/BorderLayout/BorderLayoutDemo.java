package BorderLayout;

import java.awt.*;

class MyFrame extends Frame {
    Button b1, b2, b3, b4, b5, b6;

    public MyFrame() {
        super("BorderLayout Demo");

        b1 = new Button("One");
        b2 = new Button("Two");
        b3 = new Button("Three");
        b4 = new Button("Four");
        b5 = new Button("Five");
        b6 = new Button("Six");

        // Set the layout manager to BorderLayout
        setLayout(new BorderLayout());

        // Add buttons using proper BorderLayout constraints
        add(b1, BorderLayout.NORTH);
        add(b3, BorderLayout.SOUTH);
        add(b4, BorderLayout.WEST);
        add(b5, BorderLayout.CENTER);

        Panel p = new Panel();
        p.setLayout(new GridLayout(3, 1));
        p.add(new Button("Monday"));
        p.add(new Button("Tuesday"));
        p.add(new Button("Wednesday"));

        add(p, BorderLayout.EAST);

        // Optionally, if you want to display b2 or b6,
        // you can add them to one of the regions or embed them in another panel.
        // For example, adding b2 to the south panel:
        // Panel southPanel = new Panel(new FlowLayout());
        // southPanel.add(b2);
        // southPanel.add(b3);
        // remove(b3); // Remove the previous b3 addition if needed
        // add(southPanel, BorderLayout.SOUTH);
    }
}

public class BorderLayoutDemo {
    public static void main(String[] args) {
        MyFrame f = new MyFrame();
        f.setSize(500, 500);
        f.setVisible(true);
    }
}
