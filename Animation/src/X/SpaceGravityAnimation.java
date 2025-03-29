package X;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract class Shape {
    double x, y; // Center of the shape
    double vx, vy;
    double angle; // Rotation angle in degrees
    double angularVelocity; // Degrees per second
    Color color;
    int size; // Diameter for circles, bounding box width for others

    Shape(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.angularVelocity = angularVelocity;
        this.size = size;
        this.color = color;
    }

    void move(ArrayList<Shape> shapes, int panelWidth, int panelHeight, double dt) {
        x += vx * dt;
        y += vy * dt;
        angle += angularVelocity * dt;

        // Bounce off walls
        double radius = size / 2.0;
        if (x - radius <= 0) {
            vx = -vx;
            x = radius;
        } else if (x + radius >= panelWidth) {
            vx = -vx;
            x = panelWidth - radius;
        }
        if (y - radius <= 0) {
            vy = -vy;
            y = radius;
        } else if (y + radius >= panelHeight) {
            vy = -vy;
            y = panelHeight - radius;
        }

        // Check collisions
        for (Shape other : shapes) {
            if (other != this && checkCollision(other)) {
                resolveCollision(other);
            }
        }
    }

    boolean checkCollision(Shape other) {
        double dx = Math.abs(this.x - other.x);
        double dy = Math.abs(this.y - other.y);
        double collisionDist = (this.size / 2.0 + other.size / 2.0);
        return dx < collisionDist && dy < collisionDist;
    }

    void resolveCollision(Shape other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return;

        double nx = dx / distance;
        double ny = dy / distance;

        double dvx = this.vx - other.vx;
        double dvy = this.vy - other.vy;
        double dot = dvx * nx + dvy * ny;

        if (dot > 0) return;

        double impulse = dot;
        this.vx -= impulse * nx;
        this.vy -= impulse * ny;
        other.vx += impulse * nx;
        other.vy += impulse * ny;

        double temp = this.angularVelocity;
        this.angularVelocity = other.angularVelocity;
        other.angularVelocity = temp;
    }

    abstract void draw(Graphics2D g2d);
}

class Circle extends Shape {
    Circle(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        double radius = size / 2.0;
        g2d.fillOval((int) -radius, (int) -radius, size, size);
        g2d.setColor(Color.WHITE);
        g2d.drawOval((int) -radius, (int) -radius, size, size);
        g2d.setTransform(old);
    }
}

class Triangle extends Shape {
    Triangle(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        double half = size / 2.0;
        int[] xPoints = {0, (int) half, (int) -half};
        int[] yPoints = {(int) -half, (int) half, (int) half};
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(Color.WHITE);
        g2d.drawPolygon(xPoints, yPoints, 3);
        g2d.setTransform(old);
    }
}

class Square extends Shape {
    Square(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        double half = size / 2.0;
        g2d.fillRect((int) -half, (int) -half, size, size);
        g2d.setColor(Color.WHITE);
        g2d.drawRect((int) -half, (int) -half, size, size);
        g2d.setTransform(old);
    }
}

class Pentagon extends Shape {
    Pentagon(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        double r = size / 2.0;
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];
        for (int k = 0; k < 5; k++) {
            double theta = 2 * Math.PI * k / 5 - Math.PI / 2; // Start from top
            xPoints[k] = (int) (r * Math.cos(theta));
            yPoints[k] = (int) (r * Math.sin(theta));
        }
        g2d.fillPolygon(xPoints, yPoints, 5);
        g2d.setColor(Color.WHITE);
        g2d.drawPolygon(xPoints, yPoints, 5);
        g2d.setTransform(old);
    }
}

class AnimationPanel extends JPanel {
    ArrayList<Shape> shapes;
    Random rand = new Random();
    ScheduledExecutorService executor;
    long lastTime;

    AnimationPanel() {
        setBackground(Color.BLACK);
        shapes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int size = 20 + rand.nextInt(41); // 20 to 60
            double x = size / 2.0 + rand.nextDouble() * (500 - size);
            double y = size / 2.0 + rand.nextDouble() * (500 - size);
            double vx = rand.nextDouble() * 200 - 100;
            double vy = rand.nextDouble() * 200 - 100;
            double angle = rand.nextDouble() * 360;
            double angularVelocity = rand.nextDouble() * 180 - 90;
            Color color = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);
            int type = rand.nextInt(4); // 0: Circle, 1: Triangle, 2: Square, 3: Pentagon
            switch (type) {
                case 0:
                    shapes.add(new Circle(x, y, vx, vy, angle, angularVelocity, size, color));
                    break;
                case 1:
                    shapes.add(new Triangle(x, y, vx, vy, angle, angularVelocity, size, color));
                    break;
                case 2:
                    shapes.add(new Square(x, y, vx, vy, angle, angularVelocity, size, color));
                    break;
                case 3:
                    shapes.add(new Pentagon(x, y, vx, vy, angle, angularVelocity, size, color));
                    break;
            }
        }
        lastTime = System.nanoTime();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            updateAnimation();
            repaint();
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    void updateAnimation() {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        for (Shape shape : shapes) {
            shape.move(shapes, panelWidth, panelHeight, dt);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Shape shape : shapes) {
            shape.draw(g2d);
        }
    }
}

public class SpaceGravityAnimation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Zero Gravity Shapes Collision");
            AnimationPanel panel = new AnimationPanel();
            frame.add(panel);
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}