package SpaceGravity;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract class Shape {
    // Using doubles for smooth movement and precise collision physics.
    double x, y;
    double vx, vy;
    double angle;            // current rotation angle in degrees
    double angularVelocity;  // degrees per second
    Color color;
    int size;                // general size for collision bounds (assumed square bounds)

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

    // dt in seconds
    void move(ArrayList<Shape> shapes, int panelWidth, int panelHeight, double dt) {
        // Update position with delta time.
        x += vx * dt;
        y += vy * dt;
        // Update rotation.
        angle += angularVelocity * dt;

        // Bounce off walls
        if (x <= 0 || x >= panelWidth - size) {
            vx = -vx;
            x = Math.max(0, Math.min(x, panelWidth - size));
        }
        if (y <= 0 || y >= panelHeight - size) {
            vy = -vy;
            y = Math.max(0, Math.min(y, panelHeight - size));
        }

        // Check collisions with other shapes and resolve
        for (Shape other : shapes) {
            if (other != this && checkCollision(other)) {
                resolveCollision(other);
            }
        }
    }

    // Basic bounding-box collision based on size.
    boolean checkCollision(Shape other) {
        return Math.abs(this.x - other.x) < size && Math.abs(this.y - other.y) < size;
    }

    // Elastic collision assuming equal masses.
    void resolveCollision(Shape other) {
        // Compute the normal vector from other to this.
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return; // Avoid division by zero

        double nx = dx / distance;
        double ny = dy / distance;

        // Relative velocity along the normal
        double dvx = this.vx - other.vx;
        double dvy = this.vy - other.vy;
        double dot = dvx * nx + dvy * ny;

        // Only resolve if objects are moving towards each other.
        if (dot > 0) return;

        // Compute impulse scalar (equal mass => impulse = dot)
        double impulse = dot;

        // Update velocities (subtracting impulse along normal)
        this.vx -= impulse * nx;
        this.vy -= impulse * ny;
        other.vx += impulse * nx;
        other.vy += impulse * ny;

        // Swap angular velocities for a rotational effect
        double temp = this.angularVelocity;
        this.angularVelocity = other.angularVelocity;
        other.angularVelocity = temp;
    }

    // Draw the shape using Graphics2D to apply rotation.
    abstract void draw(Graphics2D g2d);
}

class Circle extends Shape {
    Circle(double x, double y, double vx, double vy, double angle, double angularVelocity, int diameter, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, diameter, color);
    }

    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        // Rotate around the center of the circle.
        g2d.translate(x + size/2, y + size/2);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        g2d.fillOval(-size/2, -size/2, size, size);
        g2d.setTransform(old);
    }
}

class Triangle extends Shape {
    Triangle(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        // Rotate around the center of the triangle.
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        int half = size / 2;
        int[] xPoints = {0, half, -half};
        int[] yPoints = {-half, half, half};
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setTransform(old);
    }
}

class AnimationPanel extends JPanel {
    ArrayList<Shape> shapes;
    Random rand = new Random();
    ScheduledExecutorService executor;
    long lastTime;

    AnimationPanel() {
        shapes = new ArrayList<>();
        // Create 10 shapes with random initial positions, velocities, and rotation parameters.
        for (int i = 0; i < 10; i++) {
            double vx = rand.nextDouble() * 200 - 100; // pixels per second (-100 to 100)
            double vy = rand.nextDouble() * 200 - 100;
            double angularVelocity = rand.nextDouble() * 180 - 90; // degrees per second (-90 to 90)
            if (rand.nextBoolean()) {
                shapes.add(new Circle(rand.nextInt(450), rand.nextInt(450), vx, vy, rand.nextDouble() * 360, angularVelocity, 40, Color.RED));
            } else {
                shapes.add(new Triangle(rand.nextInt(450), rand.nextInt(450), vx, vy, rand.nextDouble() * 360, angularVelocity, 40, Color.BLUE));
            }
        }
        lastTime = System.nanoTime();
        // Use a ScheduledExecutorService to update animation at fixed intervals.
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            updateAnimation();
            repaint();
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    void updateAnimation() {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9; // convert nanoseconds to seconds
        lastTime = now;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        for (Shape shape : shapes) {
            shape.move(shapes, panelWidth, panelHeight, dt);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Use Graphics2D for better rendering (and to apply transforms)
        Graphics2D g2d = (Graphics2D) g;
        // Enable anti-aliasing for smoother rendering.
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
