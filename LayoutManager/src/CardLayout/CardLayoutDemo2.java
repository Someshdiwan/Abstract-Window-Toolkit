package CardLayout;


import java.awt.*;
import java.awt.event.*;

class MyCardFrame extends Frame implements ActionListener {
    CardLayout cardLayout;
    Panel cardPanel;
    Button nextButton, previousButton;

    public MyCardFrame() {
        super("CardLayout Demo");

        // Create a CardLayout and set it for the card panel.
        cardLayout = new CardLayout();
        cardPanel = new Panel();
        cardPanel.setLayout(cardLayout);

        // Create several cards (panels) with distinct backgrounds or content.
        Panel card1 = new Panel();
        card1.setBackground(Color.RED);
        card1.add(new Label("Card 1"));

        Panel card2 = new Panel();
        card2.setBackground(Color.GREEN);
        card2.add(new Label("Card 2"));

        Panel card3 = new Panel();
        card3.setBackground(Color.BLUE);
        card3.add(new Label("Card 3"));

        // Add cards to the cardPanel with a unique string identifier.
        cardPanel.add(card1, "Card1");
        cardPanel.add(card2, "Card2");
        cardPanel.add(card3, "Card3");

        // Create navigation buttons.
        nextButton = new Button("Next");
        previousButton = new Button("Previous");

        // Register action listeners for navigation.
        nextButton.addActionListener(this);
        previousButton.addActionListener(this);

        // Set the Frame layout and add components.
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);

        // Create a panel to hold the navigation buttons.
        Panel controlPanel = new Panel();
        controlPanel.add(previousButton);
        controlPanel.add(nextButton);
        add(controlPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Navigate cards based on button clicks.
        if (e.getSource() == nextButton) {
            cardLayout.next(cardPanel);
        } else if (e.getSource() == previousButton) {
            cardLayout.previous(cardPanel);
        }
    }

    public static void main(String[] args) {
        new MyCardFrame();
    }
}
