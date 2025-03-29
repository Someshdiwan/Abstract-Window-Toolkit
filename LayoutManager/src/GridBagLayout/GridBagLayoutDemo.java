package GridBagLayout;

import java.awt.*;

class MyFrame extends Frame {
    Button b1, b2, b3, b4;

    public MyFrame() {
        super("GridBagLayout Demo");

        // Set the layout manager to GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create buttons
        b1 = new Button("Button 1");
        b2 = new Button("Button 2");
        b3 = new Button("Button 3");
        b4 = new Button("Button 4");

        // Configure constraints and add b1 at (0,0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // default width
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5; // horizontal space distribution
        add(b1, gbc);

        // Configure constraints and add b2 at (1,0)
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(b2, gbc);

        // Configure constraints and add b3 at (0,1) spanning two columns
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(b3, gbc);

        // Reset grid width for next component and add b4 at (0,2)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(b4, gbc);
    }
}

public class GridBagLayoutDemo {
    public static void main(String[] args) {
        MyFrame f = new MyFrame();
        f.setSize(500, 500);
        f.setVisible(true);
    }
}