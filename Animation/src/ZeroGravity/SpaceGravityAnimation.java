package ZeroGravity;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

abstract class Shape {
    int x, y, dx, dy;
    Color color;

    Shape(int x, int y, int dx, int dy, Color color) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
    }

    void move(ArrayList<Shape> shapes, int panelWidth, int panelHeight) {
        x += dx;
        y += dy;

        if (x <= 0 || x >= panelWidth - 40) dx = -dx;
        if (y <= 0 || y >= panelHeight - 40) dy = -dy;

        for (Shape other : shapes) {
            if (other != this && checkCollision(other)) {
                bounceOff(other);
            }
        }
    }

    boolean checkCollision(Shape other) {
        return Math.abs(this.x - other.x) < 40 && Math.abs(this.y - other.y) < 40;
    }

    void bounceOff(Shape other) {
        int tempDx = this.dx;
        int tempDy = this.dy;
        this.dx = other.dx;
        this.dy = other.dy;
        other.dx = tempDx;
        other.dy = tempDy;
    }

    abstract void draw(Graphics g);
}

class Circle extends Shape {
    int diameter;

    Circle(int x, int y, int dx, int dy, int diameter, Color color) {
        super(x, y, dx, dy, color);
        this.diameter = diameter;
    }

    void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, diameter, diameter);
    }
}

class Triangle extends Shape {
    int size;

    Triangle(int x, int y, int dx, int dy, int size, Color color) {
        super(x, y, dx, dy, color);
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
            int dx = rand.nextInt(3) + 1;
            int dy = rand.nextInt(3) + 1;
            if (rand.nextBoolean()) {
                shapes.add(new Circle(rand.nextInt(450), rand.nextInt(450), dx, dy, 40, Color.RED));
            } else {
                shapes.add(new Triangle(rand.nextInt(450), rand.nextInt(450), dx, dy, 40, Color.BLUE));
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
                shape.move(shapes, getWidth(), getHeight());
            }
            repaint();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

public class SpaceGravityAnimation {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Zero Gravity Shapes Collision");
        AnimationPanel panel = new AnimationPanel();
        frame.add(panel);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
