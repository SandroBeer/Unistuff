import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * Implements a bouncing ball simulation.
 */
public class BouncingBallsSimulation extends Component implements Runnable {
	int m = 10;               // m is the size of one side of the two dimensional hash table
    LinkedList<Ball> balls;   // List of balls.
    Image img;                // Image to display balls.
    int w, h;                 // Width an height of image.
    Graphics2D gi;            // Graphics object to draw balls.
    float r;                  // Radius of balls.
    int n;                    // Number of balls.
    Thread thread;            // Thread that runs simulation loop.

    /**
     * Initializes the simulation.
     *
     * @param w width of simulation window.
     * @param h height of simulation window.
     * @param n number of balls.
     * @param r radius of balls.
     * @param v initial velocity of balls.
     */
    public BouncingBallsSimulation(int w, int h, int n, float r, float v)
    {
        this.r = r;
        this.n = n;
        this.w = w;
        this.h = h;

        // Initialize balls by distributing them randomly.
        balls = new LinkedList<Ball>();
        for(int i=0; i<n; i++)
        {
            float vx = 2*(float)Math.random()-1;
            float vy = 2*(float)Math.random()-1;
            float tmp = (float)Math.sqrt((double)vx*vx+vy*vy);
            vx = vx/tmp*v;
            vy = vy/tmp*v;
            int color = (i < n / 100 ) ? 1 : 0;
            balls.add(new Ball(r+(float)Math.random()*(w-2*r), r+(float)Math.random()*(h-2*r), vx, vy, r, color));
        }

    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }

    /**
     * Paint the window that displays the simulation. This method is called
     * automatically by the Java window system as a response to the call to
     * repaint() in the run() method below.
     */
    public void paint(Graphics g)
    {
        gi.clearRect(0, 0, w, h);

        Iterator<Ball> it = balls.iterator();
        while(it.hasNext())
        {
            Ball ball = it.next();
            gi.setColor(ball.color == 1 ? Color.red : Color.black);
            gi.fill(new Ellipse2D.Float(ball.x-r, ball.y-r, 2*r, 2*r));
        }

        g.drawImage(img, 0, 0, null);
    }

    /**
     * Starts the simulation loop.
     */
    public void start()
    {
        img = createImage(w, h);
        gi = (Graphics2D)img.getGraphics();
        gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        thread = new Thread(this);
        thread.run();
    }

    /**
     * The simulation loop.
     */
    public void run()
    {
    	// hashTable is an ArrayList that contains m ArrayLists which contain m LinkedLists of Balls
    	// for each simulation step, we can add all balls to the corresponding LinkedList in this two dimensional hashTable
    	ArrayList<ArrayList<LinkedList<Ball>>> hashTable = new ArrayList<ArrayList<LinkedList<Ball>>>();
    	
    	// in this for-loop, we will add m ArrayLists to our hashTable
    	for (int i = 0; i < this.m; i++) {
    		hashTable.add(new ArrayList<LinkedList<Ball>>());
    	}
    	
    	// in this for-loop, we will add m LinkedList (that contains balls) to all of our m ArrayLists
    	for (int i = 0; i < this.m; i++) {
    		for (int j = 0; j < this.m; j++) {
    			hashTable.get(i).add(new LinkedList<Ball>());
    		}
    	}
    	
        // Set up timer.
        int c = 0;
        Timer timer = new Timer();
        timer.reset();

        // Loop forever (or until the user closes the main window).
        while(true)
        {
        	// here we clear all the LinkedLists in order to have an empty hashTable for every other simulation step
        	for (int i = 0; i < this.m; i++) {
        		for (int j = 0; j < this.m; j++) {
        			hashTable.get(i).get(j).clear();
        		}
        	}
        	
            // Run one simulation step.
            Iterator<Ball> it = balls.iterator();
            
            // in the next few lines, we iterate over all balls and put them in the corresponding cell of the hashTable
            while(it.hasNext()) {
            	Ball ballToHash = it.next();
            	int ballX = (int)Math.floor(ballToHash.x * m / w);
            	int ballY = (int)Math.floor(ballToHash.y * m / h);
        	
            	hashTable.get(ballX).get(ballY).add(ballToHash);
            }
            // now the hashTable is ready for this simulation step
            // we have to reassign the iterator in order to iterate over all balls
            it = balls.iterator();
            
            // Iterate over all balls.
            while(it.hasNext())
            {
                Ball ball = it.next();

                // Move the ball.
                ball.move();

                // Handle collisions with boundaries.
                if(ball.doesCollide((float)w,0.f,-1.f,0.f))
                    ball.resolveCollision((float)w,0.f,-1.f,0.f);
                if(ball.doesCollide(0.f,0.f,1.f,0.f))
                    ball.resolveCollision(0.f,0.f,1.f,0.f);
                if(ball.doesCollide(0.f,(float)h,0.f,-1.f))
                    ball.resolveCollision(0.f,(float)h,0.f,-1.f);
                if(ball.doesCollide(0.f,0.f,0.f,1.f))
                    ball.resolveCollision(0.f,0.f,0.f,1.f);
                
                // handle collisions with other balls (hashVersion)
                int ballX = (int)Math.floor(ball.x * m / w);
            	int ballY = (int)Math.floor(ball.y * m / h);
            	
            	// here we assign a few variables that we can join all LinkedLists of neighboring cells later
            	int xmin = ballX - 1, xmax = ballX + 1, ymin = ballY - 1, ymax = ballY + 1;
            	
            	// treat cases where ball is either on the edge or in a corner
            	if (ballX == 0) {
            		xmin = ballX;
            	}
            	else if (ballX == m - 1) {
            		xmax = ballX;
            	}
            	if (ballY == 0) {
            		ymin = ballY;
            	}
            	else if (ballY == m - 1) {
            		ymax = ballY;
            	}
            	
            	// possibleCollisions is a LinkedList of Balls that contains the cell of the ball which is being iterated over
            	// and as well all neighboring cells
            	LinkedList<Ball> possibleCollisions = new LinkedList<Ball>();
            	for (int i = xmin; i < xmax + 1; i++) {
            		for (int j = ymin; j < ymax + 1; j++) {
            			possibleCollisions.addAll(hashTable.get(i).get(j));
            		}
            	}
            	
            	Iterator<Ball> ballIt = possibleCollisions.iterator();
            	// in this while-loop we iterate over all all balls in the LinkeList possibleCollisions
            	while (ballIt.hasNext()) {
            		Ball ballToTest = ballIt.next();
            		// we don't want to test a collision of a ball with itself
            		if(ballToTest == ball) {
            			continue;
            		}
            		else {
            			if(ball.doesCollide(ballToTest)) {
            				ball.resolveCollision(ballToTest);
            			}
            		}
            	}
            	// in the next few lines, you can find the old version of how collisions are treated with other balls
            	
            	/*
                // Handle collisions with other balls.
                Iterator<Ball> it2 = balls.iterator();
                Ball ball2 = it2.next();
                while(ball2 != ball)
                {
                    if(ball.doesCollide(ball2))
                        ball.resolveCollision(ball2);
                    ball2 = it2.next();
                }*/
            }

            // Trigger update of display.
            repaint();

            // Print time per simulation step.
            c++;
            if(c==10)
            {
                System.out.printf("Timer per simulation step: %fms\n", (float)timer.timeElapsed()/(float)c);
                timer.reset();
                c = 0;
            }
        }
    }
}
