package WaterFallAnimation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Abstract base class for all shapes
abstract class Shape {
    double x, y;             // Center position
    double vx, vy;           // Velocity components
    double angle;            // Rotation angle in degrees
    double angularVelocity;  // Rotation speed in degrees per second
    int size;                // Size of the shape (diameter or bounding box width)
    Color color;             // Shape color
    boolean settled = false; // Indicates if the shape has stopped moving

    Shape(double x, double y, double vx, double vy, double angle, double angularVelocity,
          int size, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.angularVelocity = angularVelocity;
        this.size = size;
        this.color = color;
    }

    // Update position and check for settling or collisions
    void move(List<Shape> movingShapes, int[] pileHeight, double dt, int panelWidth, int panelHeight) {
        if (settled) return; // Skip if settled

        // Apply gravity
        double gravity = 500; // pixels per second squared
        vy += gravity * dt;

        // Update position and rotation
        x += vx * dt;
        y += vy * dt;
        angle += angularVelocity * dt;

        // Wrap around horizontally
        if (x < 0) x += panelWidth;
        else if (x > panelWidth) x -= panelWidth;

        // Check if shape settles at the bottom
        double r = size / 2.0;
        if (y + r >= panelHeight) {
            settled = true;
            y = panelHeight - r; // Align bottom with panel bottom
            vx = 0;
            vy = 0;
            angularVelocity = 0;
            // Update pile height across shape's width
            int left = (int) Math.max(0, x - r);
            int right = (int) Math.min(pileHeight.length - 1, x + r);
            for (int ix = left; ix <= right; ix++) {
                double dx = ix - x;
                double dy = Math.sqrt(r * r - dx * dx);
                pileHeight[ix] = (int) Math.min(pileHeight[ix], y - dy);
            }
        } else {
            // Check collisions with other moving shapes within a synchronized block.
            synchronized (movingShapes) {
                for (Shape other : movingShapes) {
                    if (other != this && !other.settled && checkCollision(other)) {
                        resolveCollision(other);
                    }
                }
            }
        }
    }

    // Check for collision with another shape
    boolean checkCollision(Shape other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (this.size + other.size) / 2.0;
    }

    // Resolve collision by swapping velocities
    void resolveCollision(Shape other) {
        double tempVx = this.vx;
        double tempVy = this.vy;
        this.vx = other.vx;
        this.vy = other.vy;
        other.vx = tempVx;
        other.vy = tempVy;
    }

    // Abstract method for drawing the shape
    abstract void draw(Graphics2D g2d);
}

// Circle shape implementation
class Circle extends Shape {
    Circle(double x, double y, double vx, double vy, double angle, double angularVelocity,
           int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    @Override
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

// Triangle shape implementation
class Triangle extends Shape {
    Triangle(double x, double y, double vx, double vy, double angle, double angularVelocity,
             int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    @Override
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

// Square shape implementation
class Square extends Shape {
    Square(double x, double y, double vx, double vy, double angle, double angularVelocity,
           int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    @Override
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

// Pentagon shape implementation
class Pentagon extends Shape {
    Pentagon(double x, double y, double vx, double vy, double angle, double angularVelocity,
             int size, Color color) {
        super(x, y, vx, vy, angle, angularVelocity, size, color);
    }

    @Override
    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        double r = size / 2.0;
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];
        for (int k = 0; k < 5; k++) {
            double theta = 2 * Math.PI * k / 5 - Math.PI / 2;
            xPoints[k] = (int) (r * Math.cos(theta));
            yPoints[k] = (int) (r * Math.sin(theta));
        }
        g2d.fillPolygon(xPoints, yPoints, 5);
        g2d.setColor(Color.WHITE);
        g2d.drawPolygon(xPoints, yPoints, 5);
        g2d.setTransform(old);
    }
}

// Panel for animation and rendering
class AnimationPanel extends JPanel {
    // Use synchronized lists for safe concurrent access
    private List<Shape> movingShapes = Collections.synchronizedList(new ArrayList<>());
    private List<Shape> settledShapes = Collections.synchronizedList(new ArrayList<>());
    private int[] pileHeight; // Tracks height of settled pile
    private Random rand = new Random();
    private ScheduledExecutorService executor;
    private long lastTime;
    private long lastAddTime;
    private double addInterval = 0.2; // Add shape every 0.2 seconds

    AnimationPanel() {
        setBackground(Color.BLACK);
        int panelWidth = 500;
        int panelHeight = 500;
        pileHeight = new int[panelWidth];
        Arrays.fill(pileHeight, panelHeight); // Pile starts at bottom

        // Add a few initial shapes
        for (int i = 0; i < 5; i++) {
            addNewShape(panelWidth, panelHeight);
        }

        lastTime = System.nanoTime();
        lastAddTime = System.nanoTime();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            updateAnimation(panelWidth, panelHeight);
            repaint();
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    // Add a new shape at the top
    private void addNewShape(int panelWidth, int panelHeight) {
        int size = 20 + rand.nextInt(41); // 20 to 60 pixels
        double x = size / 2.0 + rand.nextDouble() * (panelWidth - size);
        double y = -size / 2.0; // Start above panel
        double vx = rand.nextDouble() * 100 - 50; // -50 to 50
        double vy = 0;
        double angle = rand.nextDouble() * 360;
        double angularVelocity = rand.nextDouble() * 180 - 90;
        Color color = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);

        Shape newShape;
        int type = rand.nextInt(4);
        switch (type) {
            case 0:
                newShape = new Circle(x, y, vx, vy, angle, angularVelocity, size, color);
                break;
            case 1:
                newShape = new Triangle(x, y, vx, vy, angle, angularVelocity, size, color);
                break;
            case 2:
                newShape = new Square(x, y, vx, vy, angle, angularVelocity, size, color);
                break;
            case 3:
                newShape = new Pentagon(x, y, vx, vy, angle, angularVelocity, size, color);
                break;
            default:
                newShape = new Circle(x, y, vx, vy, angle, angularVelocity, size, color);
        }
        movingShapes.add(newShape);
    }

    // Update the simulation
    void updateAnimation(int panelWidth, int panelHeight) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9; // Time step in seconds
        lastTime = now;

        // Add a new shape periodically
        if ((now - lastAddTime) / 1e9 > addInterval) {
            addNewShape(panelWidth, panelHeight);
            lastAddTime = now;
        }

        // Update moving shapes using an iterator for safe removal
        synchronized (movingShapes) {
            Iterator<Shape> iterator = movingShapes.iterator();
            while (iterator.hasNext()) {
                Shape shape = iterator.next();
                shape.move(movingShapes, pileHeight, dt, panelWidth, panelHeight);
                if (shape.settled) {
                    iterator.remove();
                    synchronized (settledShapes) {
                        settledShapes.add(shape);
                    }
                }
            }
        }
    }

    // Render the shapes
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw moving shapes
        synchronized (movingShapes) {
            for (Shape shape : movingShapes) {
                shape.draw(g2d);
            }
        }
        // Draw settled shapes
        synchronized (settledShapes) {
            for (Shape shape : settledShapes) {
                shape.draw(g2d);
            }
        }
    }
}

// Main class to run the simulation
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

/*
package WaterFall;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Abstract base class for all shapes
abstract class Shape {
    double x, y; // Center position
    double vx, vy; // Velocity components
    double angle; // Rotation angle in degrees
    double angularVelocity; // Rotation speed in degrees per second
    int size; // Size of the shape (diameter or bounding box width)
    Color color; // Shape color
    boolean settled = false; // Indicates if the shape has stopped moving
    private Random rand;

    // Constructor
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

    // Update position and check for settling or collisions
    void move(List<Shape> movingShapes, int[] pileHeight, double dt, int panelWidth, int panelHeight) {
        if (settled) return; // Skip if settled

        // Apply gravity
        double gravity = 500; // pixels per second squared
        vy += gravity * dt;

        // Update position and rotation
        x += vx * dt;
        y += vy * dt;
        angle += angularVelocity * dt;

        // Wrap around horizontally
        if (x < 0) x += panelWidth;
        else if (x > panelWidth) x -= panelWidth;

        // Check if shape settles at the bottom
        double r = size / 2.0;
        if (y + r >= panelHeight) {
            settled = true;
            y = panelHeight - r; // Align bottom with panel bottom
            vx = 0;
            vy = 0;
            angularVelocity = 0;
            // Update pile height across shape's width
            int left = (int) Math.max(0, x - r);
            int right = (int) Math.min(pileHeight.length - 1, x + r);
            for (int ix = left; ix <= right; ix++) {
                double dx = ix - x;
                double dy = Math.sqrt(r * r - dx * dx);
                pileHeight[ix] = (int) Math.min(pileHeight[ix], y - dy);
            }
        } else {
            // Check collisions with other moving shapes
            for (Shape other : movingShapes) {
                if (other != this && !other.settled && checkCollision(other)) {
                    resolveCollision(other);
                }
            }
        }
    }

    // Check for collision with another shape
    boolean checkCollision(Shape other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (this.size + other.size) / 2.0;
    }

    // Resolve collision by swapping velocities
    void resolveCollision(Shape other) {
        double tempVx = this.vx;
        double tempVy = this.vy;
        this.vx = other.vx;
        this.vy = other.vy;
        other.vx = tempVx;
        other.vy = tempVy;
    }

    // Abstract method for drawing the shape
    abstract void draw(Graphics2D g2d);
}

// Circle shape implementation
class Circle extends Shape {
    Circle(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
    }

    @Override
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

// Triangle shape implementation
class Triangle extends Shape {
    Triangle(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
    }

    @Override
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

// Square shape implementation
class Square extends Shape {
    Square(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
    }

    @Override
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

// Pentagon shape implementation
class Pentagon extends Shape {
    Pentagon(double x, double y, double vx, double vy, double angle, double angularVelocity, int size, Color color, Random rand) {
        super(x, y, vx, vy, angle, angularVelocity, size, color, rand);
    }

    @Override
    void draw(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angle));
        g2d.setColor(color);
        double r = size / 2.0;
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];
        for (int k = 0; k < 5; k++) {
            double theta = 2 * Math.PI * k / 5 - Math.PI / 2;
            xPoints[k] = (int) (r * Math.cos(theta));
            yPoints[k] = (int) (r * Math.sin(theta));
        }
        g2d.fillPolygon(xPoints, yPoints, 5);
        g2d.setColor(Color.WHITE);
        g2d.drawPolygon(xPoints, yPoints, 5);
        g2d.setTransform(old);
    }
}

// Panel for animation and rendering
class AnimationPanel extends JPanel {
    private List<Shape> movingShapes = new ArrayList<>();
    private List<Shape> settledShapes = new ArrayList<>();
    private int[] pileHeight; // Tracks height of settled pile
    private Random rand = new Random();
    private ScheduledExecutorService executor;
    private long lastTime;
    private long lastAddTime;
    private double addInterval = 0.2; // Add shape every 0.2 seconds

    AnimationPanel() {
        setBackground(Color.BLACK);
        int panelWidth = 500;
        int panelHeight = 500;
        pileHeight = new int[panelWidth];
        Arrays.fill(pileHeight, panelHeight); // Pile starts at bottom

        // Add initial shapes
        for (int i = 0; i < 5; i++) {
            addNewShape(panelWidth, panelHeight);
        }

        lastTime = System.nanoTime();
        lastAddTime = System.nanoTime();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            updateAnimation(panelWidth, panelHeight);
            repaint();
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    // Add a new shape at the top
    private void addNewShape(int panelWidth, int panelHeight) {
        int size = 20 + rand.nextInt(41); // 20 to 60 pixels
        double x = size / 2.0 + rand.nextDouble() * (panelWidth - size);
        double y = -size / 2.0; // Start above panel
        double vx = rand.nextDouble() * 100 - 50; // -50 to 50
        double vy = 0;
        double angle = rand.nextDouble() * 360;
        double angularVelocity = rand.nextDouble() * 180 - 90;
        Color color = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);

        int type = rand.nextInt(4);
        Shape newShape;
        switch (type) {
            case 0:
                newShape = new Circle(x, y, vx, vy, angle, angularVelocity, size, color, rand);
                break;
            case 1:
                newShape = new Triangle(x, y, vx, vy, angle, angularVelocity, size, color, rand);
                break;
            case 2:
                newShape = new Square(x, y, vx, vy, angle, angularVelocity, size, color, rand);
                break;
            case 3:
                newShape = new Pentagon(x, y, vx, vy, angle, angularVelocity, size, color, rand);
                break;
            default:
                newShape = new Circle(x, y, vx, vy, angle, angularVelocity, size, color, rand);
        }
        movingShapes.add(newShape);
    }

    // Update the simulation
    void updateAnimation(int panelWidth, int panelHeight) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9; // Time step in seconds
        lastTime = now;

        // Add new shape periodically
        if ((now - lastAddTime) / 1e9 > addInterval) {
            addNewShape(panelWidth, panelHeight);
            lastAddTime = now;
        }

        // Update moving shapes
        List<Shape> toSettle = new ArrayList<>();
        for (Shape shape : movingShapes) {
            shape.move(movingShapes, pileHeight, dt, panelWidth, panelHeight);
            if (shape.settled) {
                toSettle.add(shape);
            }
        }

        // Move settled shapes to settled list
        for (Shape shape : toSettle) {
            movingShapes.remove(shape);
            settledShapes.add(shape);
        }
    }

    // Render the shapes
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Shape shape : movingShapes) {
            shape.draw(g2d);
        }
        for (Shape shape : settledShapes) {
            shape.draw(g2d);
        }
    }
}

// Main class to run the simulation
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
}*/
