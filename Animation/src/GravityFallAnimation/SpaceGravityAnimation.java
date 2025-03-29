package GravityFallAnimation;

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
    double vx, vy; // Velocities in pixels per second
    double angle; // Rotation angle in degrees
    double angularVelocity; // Degrees per second
    int size; // Diameter or bounding box width
    Color color;
    private Random rand; // For randomizing reset properties

    Shape(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.angularVelocity = angularVelocity;
        this.size = size;
        this.color = color;
        this.rand = rand;
    }

    void move(ArrayList<Shape> shapes, int panelWidth, int panelHeight, double dt) {
        // Apply gravity (downward acceleration)
        double gravity = 500; // pixels per second squared, adjust as needed
        vy += gravity * dt;

        // Update position
        x += vx * dt;
        y += vy * dt;
        angle += angularVelocity * dt;

        // Wrap around horizontally (left and right edges)
        if (x < 0) x += panelWidth;
        else if (x > panelWidth) x -= panelWidth;

        // Reset to top when shape falls below the panel
        if (y > panelHeight) {
            y = -size / 2.0; // Start just above the top
            x = size / 2.0 + rand.nextDouble() * (panelWidth - size); // Random x within bounds
            vy = 0; // Reset vertical velocity
            vx = rand.nextDouble() * 100 - 50; // Random horizontal velocity (-50 to 50)
            // Angular velocity remains unchanged for visual interest
        }

        // Check collisions with other shapes
        for (Shape other : shapes) {
            if (other != this && checkCollision(other)) {
                resolveCollision(other);
            }
        }
    }

    boolean checkCollision(Shape other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (this.size + other.size) / 2.0;
    }

    void resolveCollision(Shape other) {
        // Simple elastic collision resolution (swap velocities)
        double tempVx = this.vx;
        double tempVy = this.vy;
        this.vx = other.vx;
        this.vy = other.vy;
        other.vx = tempVx;
        other.vy = tempVy;
    }

    abstract void draw(Graphics2D g2d);
}

class Circle extends Shape {
    Circle(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
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
    Triangle(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
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
    Square(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
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
    Pentagon(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
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
            int size = 20 + rand.nextInt(41); // Size between 20 and 60
            double x = size / 2.0 + rand.nextDouble() * (500 - size); // Random x within panel
            double y = -size / 2.0; // Start above the top
            double vx = rand.nextDouble() * 100 - 50; // Random horizontal velocity (-50 to 50)
            double vy = 0; // Initial vertical velocity (gravity will accelerate)
            double angle = rand.nextDouble() * 360;
            double angularVelocity = rand.nextDouble() * 180 - 90;
            Color color = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);
            int type = rand.nextInt(4);
            switch (type) {
                case 0:
                    shapes.add(new Circle(x, y, vx, vy, angle, angularVelocity, size, color, rand));
                    break;
                case 1:
                    shapes.add(new Triangle(x, y, vx, vy, angle, angularVelocity, size, color, rand));
                    break;
                case 2:
                    shapes.add(new Square(x, y, vx, vy, angle, angularVelocity, size, color, rand));
                    break;
                case 3:
                    shapes.add(new Pentagon(x, y, vx, vy, angle, angularVelocity, size, color, rand));
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
            JFrame frame = new JFrame("Waterfall Shapes Simulation");
            AnimationPanel panel = new AnimationPanel();
            frame.add(panel);
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}