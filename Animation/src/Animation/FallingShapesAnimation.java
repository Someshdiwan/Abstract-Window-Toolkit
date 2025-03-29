package Animation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

abstract class Shape {
    int x, y, speed;
    Color color;
    boolean stopped = false;

    Shape(int x, int y, int speed, Color color) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.color = color;
    }

    void move(ArrayList<Shape> shapes, int panelHeight) {
        if (!stopped) {
            y += speed;
            if (y + 40 >= panelHeight) {
                y = panelHeight - 40;
                stopped = true;
            }
            for (Shape other : shapes) {
                if (other != this && checkCollision(other)) {
                    stopped = true;
                    break;
                }
            }
        }
    }

    boolean checkCollision(Shape other) {
        return Math.abs(this.y + 40 - other.y) < 5 && Math.abs(this.x - other.x) < 40;
    }

    abstract void draw(Graphics g);
}

class Circle extends Shape {
    int diameter;

    Circle(int x, int y, int speed, int diameter, Color color) {
        super(x, y, speed, color);
        this.diameter = diameter;
    }

    void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, diameter, diameter);
    }
}

class Triangle extends Shape {
    int size;

    Triangle(int x, int y, int speed, int size, Color color) {
        super(x, y, speed, color);
        this.size = size;
    }

    void draw(Graphics g) {
        g.setColor(color);
        int[] xPoints = {x, x + size / 2, x - size / 2};
        int[] yPoints = {y, y + size, y + size};
        g.fillPolygon(xPoints, yPoints, 3);
    }
}

class AnimationPanel extends JPanel implements Runnable {
    ArrayList<Shape> shapes;
    Random rand = new Random();

    AnimationPanel() {
        shapes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (rand.nextBoolean()) {
                shapes.add(new Circle(rand.nextInt(450), rand.nextInt(50), rand.nextInt(3) + 1, 40, Color.RED));
            } else {
                shapes.add(new Triangle(rand.nextInt(450), rand.nextInt(50), rand.nextInt(3) + 1, 40, Color.BLUE));
            }
        }
        Thread thread = new Thread(this);
        thread.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }

    public void run() {
        while (true) {
            for (Shape shape : shapes) {
                shape.move(shapes, getHeight());
            }
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class FallingShapesAnimation {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Falling Shapes Collision Animation");
        AnimationPanel panel = new AnimationPanel();
        frame.add(panel);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
