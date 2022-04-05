import javax.swing.*;

/**
 * Main program that displays a bouncing balls simulation.
 */
public class BouncingBalls {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Bouncing Balls");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the simulation and add it to the main frame.
        BouncingBallsSimulation simulation = new BouncingBallsSimulation(1000,1000, 1000, 4.f, 0.2f);
        frame.add(simulation);
        simulation.setVisible(true);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        // Start the simulation.
        simulation.start();
    }
}
