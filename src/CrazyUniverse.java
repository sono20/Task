import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.List;

public class CrazyUniverse extends JPanel implements ActionListener {
    private List<Particle> particles = new ArrayList<>();
    private Random rand = new Random();
    private Timer timer;
    private int mouseX, mouseY;
    private boolean mousePressed = false;

    class Particle {
        double x, y, vx, vy, size;
        float hue;
        Color color;

        Particle(double x, double y) {
            this.x = x; this.y = y;
            double angle = rand.nextDouble() * Math.PI * 2;
            double speed = rand.nextDouble() * 5 + 2;
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed;
            this.size = rand.nextDouble() * 15 + 5;
            this.hue = rand.nextFloat();
            this.color = Color.getHSBColor(hue, 1f, 1f);
        }

        void update() {
            x += vx; y += vy;
            vy += 0.15;

            if (x < 0 || x > getWidth()) vx *= -0.9;
            if (y > getHeight()) { y = getHeight(); vy *= -0.85; }
            if (y < 0) vy *= -0.9;

            x = Math.max(0, Math.min(getWidth(), x));
            y = Math.max(0, Math.min(getHeight(), y));

            hue += 0.002f;
            if (hue > 1) hue = 0;
            color = Color.getHSBColor(hue, 1f, 1f);
        }

        void draw(Graphics2D g) {
            g.setColor(color);
            g.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);

            for (Particle p : particles) {
                double dist = Math.hypot(x - p.x, y - p.y);
                if (dist < 100 && dist > 0) {
                    g.setColor(new Color(255, 255, 255, (int)(50 * (1 - dist/100))));
                    g.drawLine((int)x, (int)y, (int)p.x, (int)p.y);
                }
            }
        }
    }

    public CrazyUniverse() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        timer = new Timer(16, this);
        timer.start();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mousePressed = true; }
            public void mouseReleased(MouseEvent e) { mousePressed = false; }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
            public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (mousePressed) {
            for (int i = 0; i < 3; i++) {
                particles.add(new Particle(mouseX + rand.nextInt(20) - 10,
                        mouseY + rand.nextInt(20) - 10));
            }
        }

        if (particles.size() > 200) particles.subList(0, 50).clear();

        particles.forEach(Particle::update);
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        particles.forEach(p -> p.draw(g2));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("🌌 Crazy Particle Universe 🌌");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new CrazyUniverse());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}