import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.List;

public class MegaCrazyUniverse extends JPanel implements ActionListener {
    private List<Particle> particles = new ArrayList<>();
    private Random rand = new Random();
    private Timer timer;
    private int mouseX, mouseY;
    private boolean mousePressed = false;
    private int mode = 0;
    private boolean gravityOn = true;
    private boolean blackHoleActive = false;
    private boolean connectionLines = true;
    private String[] modes = {"RAINBOW", "FIRE", "ELECTRIC", "PLASMA", "GALAXY", "NEON"};
    private int fps = 0;
    private long lastTime = System.nanoTime();

    class Particle {
        double x, y, vx, vy, size, angle, spin, life, decay;
        float hue;
        int particleMode;
        Color[] trail = new Color[5];

        Particle(double x, double y, int mode) {
            this.x = x; this.y = y;
            this.particleMode = mode;
            double ang = rand.nextDouble() * Math.PI * 2;
            double speed = rand.nextDouble() * 8 + 3;
            this.vx = Math.cos(ang) * speed;
            this.vy = Math.sin(ang) * speed;
            this.size = rand.nextDouble() * 25 + 8;
            this.angle = ang;
            this.spin = rand.nextDouble() * 0.3 - 0.15;
            this.life = 1.0;
            this.decay = rand.nextDouble() * 0.008 + 0.003;
            this.hue = rand.nextFloat();
        }

        void update(int width, int height) {
            if (gravityOn) vy += 0.25;

            if (blackHoleActive) {
                double dx = width / 2.0 - x;
                double dy = height / 2.0 - y;
                double dist = Math.hypot(dx, dy);
                if (dist > 50) {
                    vx += dx / dist * 0.5;
                    vy += dy / dist * 0.5;
                }
            }

            x += vx; y += vy;
            angle += spin;

            if (x < 0 || x > width) {
                vx *= -0.8;
                x = Math.max(0, Math.min(width, x));
            }
            if (y < 0 || y > height) {
                vy *= -0.8;
                y = Math.max(0, Math.min(height, y));
            }

            life -= decay;
            hue += 0.003f;
            if (hue > 1) hue = 0;
        }

        Color getColor() {
            switch (particleMode) {
                case 1: return Color.getHSBColor(0.1f + rand.nextFloat() * 0.1f, 1f, 1f);
                case 2: return Color.getHSBColor(0.55f + rand.nextFloat() * 0.15f, 1f, 1f);
                case 3: return Color.getHSBColor(rand.nextFloat(), 1f, 1f);
                case 4: return Color.getHSBColor(0.7f + rand.nextFloat() * 0.2f, 0.8f, 1f);
                case 5: return new Color(0, (int)(255 * life), (int)(255 * life));
                default: return Color.getHSBColor(hue, 1f, 1f);
            }
        }

        void draw(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color c = getColor();
            RadialGradientPaint gradient = new RadialGradientPaint(
                    (float)x, (float)y, (float)size,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{
                            new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255 * life)),
                            new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(127 * life)),
                            new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)
                    }
            );

            g.setPaint(gradient);
            g.fillOval((int)(x - size), (int)(y - size), (int)(size * 2), (int)(size * 2));

            g.setColor(new Color(255, 255, 255, (int)(255 * life)));
            g.fillOval((int)(x - size * 0.3), (int)(y - size * 0.3),
                    (int)(size * 0.6), (int)(size * 0.6));

            // Efekt gwiezdny
            for (int i = 0; i < 4; i++) {
                double a = angle + i * Math.PI / 2;
                int x1 = (int)(x + Math.cos(a) * size * 1.5);
                int y1 = (int)(y + Math.sin(a) * size * 1.5);
                g.setStroke(new BasicStroke(2));
                g.setColor(new Color(255, 255, 255, (int)(100 * life)));
                g.drawLine((int)x, (int)y, x1, y1);
            }
        }
    }

    public MegaCrazyUniverse() {
        setPreferredSize(new Dimension(1400, 900));
        setBackground(Color.BLACK);
        timer = new Timer(16, this);
        timer.start();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                mouseX = e.getX();
                mouseY = e.getY();
            }
            public void mouseReleased(MouseEvent e) { mousePressed = false; }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE: changeMode(); break;
                    case KeyEvent.VK_G: gravityOn = !gravityOn; break;
                    case KeyEvent.VK_B: blackHoleActive = !blackHoleActive; break;
                    case KeyEvent.VK_E: explosion(); break;
                    case KeyEvent.VK_C: particles.clear(); break;
                    case KeyEvent.VK_L: connectionLines = !connectionLines; break;
                }
            }
        });
        setFocusable(true);
    }

    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        fps = (int)(1000000000.0 / (now - lastTime));
        lastTime = now;

        if (mousePressed) {
            for (int i = 0; i < 6; i++) {
                particles.add(new Particle(
                        mouseX + rand.nextInt(40) - 20,
                        mouseY + rand.nextInt(40) - 20,
                        mode
                ));
            }
        }

        if (particles.size() > 600) {
            particles.subList(0, 100).clear();
        }

        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update(getWidth(), getHeight());
            if (p.life <= 0) particles.remove(i);
        }

        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Efekt śladu
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Linie połączeń
        if (connectionLines) {
            for (int i = 0; i < particles.size(); i++) {
                for (int j = i + 1; j < particles.size(); j++) {
                    Particle p1 = particles.get(i);
                    Particle p2 = particles.get(j);
                    double dist = Math.hypot(p1.x - p2.x, p1.y - p2.y);
                    if (dist < 120) {
                        int alpha = (int)((1 - dist / 120) * 80);
                        g2.setColor(new Color(0, 255, 255, alpha));
                        g2.setStroke(new BasicStroke(2));
                        g2.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
                    }
                }
            }
        }

        // Cząstki
        particles.forEach(p -> p.draw(g2));

        // Czarna dziura wizualizacja
        if (blackHoleActive) {
            int cx = getWidth() / 2, cy = getHeight() / 2;
            for (int i = 5; i > 0; i--) {
                g2.setColor(new Color(138, 43, 226, 30 * i));
                g2.fillOval(cx - i * 20, cy - i * 20, i * 40, i * 40);
            }
        }

        // UI
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2.drawString("🌟 Particles: " + particles.size(), 20, 30);
        g2.drawString("💥 FPS: " + fps, 20, 55);
        g2.drawString("🎨 Mode: " + modes[mode], 20, 80);
        g2.drawString("🌍 Gravity: " + (gravityOn ? "ON" : "OFF"), 20, 105);
        g2.drawString("🕳️ Black Hole: " + (blackHoleActive ? "ON" : "OFF"), 20, 130);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g2.drawString("[SPACE] Mode | [G] Gravity | [B] Black Hole", 20, getHeight() - 60);
        g2.drawString("[E] Explosion | [C] Clear | [L] Lines", 20, getHeight() - 35);
        g2.drawString("[CLICK & DRAG] Create Particles", 20, getHeight() - 10);
    }

    private void changeMode() {
        mode = (mode + 1) % modes.length;
    }

    private void explosion() {
        int cx = getWidth() / 2, cy = getHeight() / 2;
        for (int i = 0; i < 150; i++) {
            particles.add(new Particle(cx, cy, mode));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🌌 MEGA CRAZY UNIVERSE 🌌");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MegaCrazyUniverse());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}