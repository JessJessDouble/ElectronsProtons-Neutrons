import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

class EPN {

    Vector position, velocity;
    double diameter, gravity, mass;
    Color color;
    double elasticity;
    double charge;

    public EPN(Vector position, Vector velocity, Color color, double diameter, double gravity, double elasticity, double charge) {
        this.position = position;
        this.velocity = velocity;
        this.color = color;
        this.diameter = diameter;
        this.gravity = gravity;
        this.elasticity = elasticity;
        switch ((int)charge) {
            case 1:
                this.mass = 1;
                break;
            case -1:
                this.mass = 0.000544662309;
                this.diameter = diameter-2;
                break;
            default:
                this.mass = 1.008;
                break;
        }
        this.charge = charge;
    }

    public void force(EPN p) {
        if((p.charge+charge)==(-2)||(p.charge==-1&&charge==1)||(p.charge+charge)==2){
        this.collide(p);
        Vector direction = p.position.subtract(position).normalize();
        double distance = position.distance(p.position);
        double distancesq = Math.max(distance * distance, 10); // Avoid division by zero
        double CForce = 1 * (charge * p.charge) / distancesq;
        Vector force = direction.scale(CForce);
        Vector Accel = force.scale(-1 / mass);
        Vector AccelP = force.scale(1 / p.mass);
        double maxAccel = 100; // Maximum allowable acceleration
        if (Accel.magnitude() > maxAccel) {
            Accel = Accel.normalize().scale(maxAccel);
        }
        if (AccelP.magnitude() > maxAccel) {
            AccelP = AccelP.normalize().scale(maxAccel);
        }
        velocity = velocity.add(Accel);
        p.velocity = p.velocity.add(AccelP);
    }else if (p.charge+charge!=-1 ) {
        this.collide(p);
        Vector direction = p.position.subtract(position).normalize();
        double distance = position.distance(p.position);
        double distancesq = Math.max(distance * distance, 10); // Avoid division by zero
        double CForce = 1000 / (distancesq*distancesq);
        Vector force = direction.scale(CForce);
        Vector Accel = force.scale(1 / mass);
        Vector AccelP = force.scale(-1 / p.mass);
        double maxAccel = 1000; // Maximum allowable acceleration
        if (Accel.magnitude() > maxAccel) {
            Accel = Accel.normalize().scale(maxAccel);
        }
        if (AccelP.magnitude() > maxAccel) {
            AccelP = AccelP.normalize().scale(maxAccel);
        }
        velocity = velocity.add(Accel);
        p.velocity = p.velocity.add(AccelP);
    
    }
}

    public void show(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) position.x, (int) position.y, (int) diameter, (int) diameter);
    }

    public void gravityShift() {
        if (velocity.magnitude() > 100) {
            velocity.scale(100 / velocity.magnitude());
        }
        velocity.y += gravity;
        position = position.add(velocity);
    }

    public void collide(EPN p) {
        double distance = position.distance(p.position);
        if(distance ==0){
            position.x+=5;
        }
        if (distance < diameter) {
            // Resolve collision
            Vector normal = position.subtract(p.position).normalize();
            Vector relativeVelocity = velocity.subtract(p.velocity);
            double speed = relativeVelocity.dot(normal);
            if (speed > 0) {
                return;
            }

            double impulse = (2 * speed) / (mass + p.mass);
            velocity = velocity.subtract(normal.scale(impulse * p.mass));
            p.velocity = p.velocity.add(normal.scale(impulse * mass));

            // Separate overlapping particles
            double overlap = diameter - distance;
            position = position.add(normal.scale(overlap / 2));
            p.position = p.position.subtract(normal.scale(overlap / 2 + 1e-3));
        }
    }

    public void update(int width, int height, ArrayList<EPN> epn) {
        gravityShift();
        // Bounce off walls
        if (position.x < 0 || position.x + diameter > width) {
            velocity.x *= -elasticity;
            position.x = Math.max(0, Math.min(position.x, width - diameter));
        }

        if (position.y < 0 || position.y + diameter > height) {
            velocity.y *= -elasticity;
            position.y = Math.max(0, Math.min(position.y, height - diameter));
        }

        // Check for collisions with other particles
        for (EPN p : epn) {
            if (p != this) {
                this.collide(p);
            }
        }
        for (EPN p : epn) {
            if (p != this) {
                this.force(p);
            }
        }
    }
}

class Vector {

    double x, y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector subtract(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    public Vector scale(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector normalize() {
        double mag = magnitude();
        return new Vector(x / mag, y / mag);
    }

    public double distance(Vector v) {
        return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
    }
}

class EPNCanvas extends JPanel {

    static ArrayList<EPN> epn = new ArrayList<>();
    double gravity = 0.098 * 0;
    double elasticity = .1;
    double vx = 0;
    double vy = 0;
    static double charge = 0;
    Color color;

    public EPNCanvas() {
        Timer timer = new Timer(5, e -> {
            for (EPN p : epn) {
                p.update(getWidth(), getHeight(), epn);
            }
            repaint();
        });
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                spawn();
                Vector position = new Vector(e.getX(), e.getY());
                // Vector velocity = new Vector(random.nextDouble() * 8 - 4, random.nextDouble() * 8 - 4);
                Vector velocity = new Vector(2 * Math.cos(vx) * 0, 2 * Math.sin(vy) * 0);
                if (Keyss.E == charge) {
                    color = Color.BLUE;
                } else if (Keyss.P == charge) {
                    color = Color.RED;
                } else {
                    color = Color.GRAY;
                }

                epn.add(new EPN(position, velocity, color, 10, gravity, elasticity, charge));
                vx += 0.1;
                vy += 0.1;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (EPN p : epn) {
            p.show(g2d);
        }
    }
    public void spawn(){
        for (int i = 0; i < 10; i++) {
            Random rand = new Random();
                double rand1 = rand.nextDouble(getHeight());
                double rand2 = rand.nextDouble(getWidth());
                double rand3 = rand.nextDouble(Math.PI);
                double rand4 = rand.nextDouble(Math.PI);
                Vector position = new Vector(rand2 , rand1);
                    // Vector velocity = new Vector(random.nextDouble() * 8 - 4, random.nextDouble() * 8 - 4);
                    Vector velocity = new Vector(2 * Math.cos(rand3) * 0, 2 * Math.sin(rand4) * 0);
                    if (Keyss.E == charge) {
                        color = Color.BLUE;
                    } else if (Keyss.P == charge) {
                        color = Color.RED;
                    } else {
                        color = Color.GRAY;
                    }
    
                    epn.add(new EPN(position, velocity, color, 10, 0, .1, EPNCanvas.charge));
        }}
    }
    
    class Keyss extends JPanel implements KeyListener {
    
        public static double E = -1;
        public static double P = 1;
        public static double N = 0;
    
        public Keyss() {
            this.setFocusable(true);
            this.requestFocusInWindow();
        }
    
        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_E) {
                EPNCanvas.charge = E;
                System.out.println("E");
            }
            if (code == KeyEvent.VK_P) {
                EPNCanvas.charge = P;
                System.out.println("P");
    
            }
            if (code == KeyEvent.VK_N) {
                EPNCanvas.charge = N;
                System.out.println("N");
    
            }if (code == KeyEvent.VK_Q) {
                // EPNSimulation.canvas.spawn();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}

public class EPNSimulation {

    public static void main(String[] args) {
        JFrame frame = new JFrame("EPN Simulation");
        EPNCanvas canvas = new EPNCanvas();
        Keyss panel = new Keyss();
        frame.add(canvas);
        frame.setSize(1990/2, 1160/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(panel);
        frame.setResizable(false);
    }
}
