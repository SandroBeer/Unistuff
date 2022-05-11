import java.awt.*;
import java.awt.image.*;
import java.io.File;

import javax.imageio.ImageIO;

public class SeamCarving {

    /**
     * Computes a table that, for each pixel, stores the smallest cost for
     * a vertical seam that goes through that pixel. Uses dynamic programming
     * by stepping through all rows from top to bottom of the image. The cost
     * for introducing a seam at each pixel is determined using the energy()
     * method below.
     *
     * @param img the input image
     * @return the table storing the costs
     */
    public static float[][] computeCosts(BufferedImage img)
    {
        int width = img.getWidth();
        int height = img.getHeight();
        var costs = new float[width][height];

        // initialize the first row with the corresponding values of the energy function
        for (int x = 0; x < width; x++) {
        	costs[x][0] = energy(img, x, 0);
        }
        
        // calculate the rest of the table with dynamic programming
        for (int y = 1; y < height; y++) {
        	for (int x = 0; x < width; x++) {
        		if (x == 0) {
        			costs[x][y] = energy(img, x, y) + Math.min(costs[x][y - 1], costs[x + 1][y - 1]);
        		}
        		else if (x == width - 1) {
        			costs[x][y] = energy(img, x, y) + Math.min(costs[x - 1][y - 1], costs[x][y - 1]);
        		}
        		else {
        			costs[x][y] = energy(img, x, y) + Math.min(costs[x - 1][y - 1], Math.min(costs[x][y - 1], costs[x + 1][y - 1]));
        		}
        	}
        }
        
        return costs;
    }

    /**
     * Reconstructs a vertical seam from the cost table. A vertical seam is stored
     * as an array where element y in the array stores an index seam[y], which indicates
     * that in row y the seam goes through column seam[y].
     *
     * @param costs the cost table
     * @param width of the cost table
     * @param height of the cost table
     * @return the seam
     */
    public static int[] computeSeam(float[][] costs, int width, int height)
    {
        var seam = new int[height];
        float minimum = costs[0][height - 1];
        int row_index = 0;
        
        // choose the pixel with the lowest energy from the last row of the table
        for (int x = 1; x < width; x++) {
        	if (costs[x][height - 1] < minimum) {
        		minimum = costs[x][height - 1];
        		row_index = x;
        	}
        }
        seam[height - 1] = row_index;
        
        for (int y = height - 2; y > -1; y--) {
        	if (row_index == 0) {
        		if (costs[row_index][y] < costs[row_index + 1][y]) {
        			seam[y] = row_index;
        		}
        		else {
        			seam[y] = row_index + 1;
        			row_index++;
        		}
        	}
        	else if (row_index == width - 1) {
        		if (costs[row_index - 1][y] < costs[row_index][y]) {
        			seam[y] = row_index - 1;
        			row_index--;
        		}
        		else {
        			seam[y] = row_index;
        		}
        	}
        	else {
        		minimum = Math.min(costs[row_index - 1][y], Math.min(costs[row_index][y], costs[row_index + 1][y]));
        		if (minimum == costs[row_index - 1][y]) {
        			seam[y] = row_index - 1;
        			row_index--;
        		}
        		else if (minimum == costs[row_index][y]) {
        			seam[y] = row_index;
        		}
        		else {
        			seam[y] = row_index + 1;
        			row_index++;
        		}
        	}
        }

        return seam;
    }

    /**
     * Removes a vertical seam from the image. The seam is an array that stores
     * for each row y in the image the index of the column where the seam lies.
     * The resulting image after removing the seam has one column less than
     * the original (its width is reduced by one).
     *
     * @param img the input image
     * @param seam the seam to be removed
     * @return the new image
     */
    public static BufferedImage removeSeam(BufferedImage img, int[] seam)
    {
        int width, height;
        width = img.getWidth();
        height = img.getHeight();

        // The width of the new image is reduced by one
        BufferedImage newImg = new BufferedImage(width-1, height, BufferedImage.TYPE_INT_RGB);

        // For all rows in the image
        for(int y=0; y<height; y++)
        {
            // Copy columns up to seam
            for(int x=0; x<seam[y]; x++)
            {
                newImg.setRGB(x, y, img.getRGB(x,y));
            }
            // Skip seam and copy the rest of the columns
            for(int x=seam[y]; x<width-1; x++)
            {
                newImg.setRGB(x, y, img.getRGB(x+1,y));
            }
        }
        return newImg;
    }

    /**
     * Computes the energy of a pixel in the image. This energy is used as the cost
     * for introducing a seam at this pixel. The energy here approximates the sum
     * of the absolute values of the first derivatives of the image in x and y direction.
     *
     * @param img the input image
     * @param x x-coordinate of the pixel
     * @param y y-coordinate of the pixel
     * @return energy of the pixel
     */
    public static float energy(BufferedImage img, int x, int y)
    {
        int width = img.getWidth();
        int height = img.getHeight();

        if(x<0 || x>=width || y<0 || y>=height)
            return 0.f;

        float c0[],c1[];
        c0 = new float[3];
        c1 = new float[3];
        float didx = 0.f;
        float didy = 0.f;

        if(x+1<width)
        {
            c0[0] = (float)(img.getRGB(x, y) & 0xFF);
            c0[1] = (float)((img.getRGB(x, y) >> 8) & 0xFF);
            c0[2] = (float)((img.getRGB(x, y) >> 16) & 0xFF);

            c1[0] = (float)(img.getRGB(x+1, y) & 0xFF);
            c1[1] = (float)((img.getRGB(x+1, y) >> 8) & 0xFF);
            c1[2] = (float)((img.getRGB(x+1, y) >> 16) & 0xFF);
        } else
        {
            c0[0] = (float)(img.getRGB(x-1, y) & 0xFF);
            c0[1] = (float)((img.getRGB(x-1, y) >> 8) & 0xFF);
            c0[2] = (float)((img.getRGB(x-1, y) >> 16) & 0xFF);

            c1[0] = (float)(img.getRGB(x, y) & 0xFF);
            c1[1] = (float)((img.getRGB(x, y) >> 8) & 0xFF);
            c1[2] = (float)((img.getRGB(x, y) >> 16) & 0xFF);
        }

        for(int i=0; i<3; i++)
        {
            didx += (float)((c1[i]-c0[i])*(c1[i]-c0[i]));
        }
        didx = (float)Math.sqrt(didx);

        if(y+1<height)
        {
            c0[0] = (float)(img.getRGB(x, y) & 0xFF);
            c0[1] = (float)((img.getRGB(x, y) >> 8) & 0xFF);
            c0[2] = (float)((img.getRGB(x, y) >> 16) & 0xFF);

            c1[0] = (float)(img.getRGB(x, y+1) & 0xFF);
            c1[1] = (float)((img.getRGB(x, y+1) >> 8) & 0xFF);
            c1[2] = (float)((img.getRGB(x, y+1) >> 16) & 0xFF);
        } else
        {
            c0[0] = (float)(img.getRGB(x, y-1) & 0xFF);
            c0[1] = (float)((img.getRGB(x, y-1) >> 8) & 0xFF);
            c0[2] = (float)((img.getRGB(x, y-1) >> 16) & 0xFF);

            c1[0] = (float)(img.getRGB(x, y) & 0xFF);
            c1[1] = (float)((img.getRGB(x, y) >> 8) & 0xFF);
            c1[2] = (float)((img.getRGB(x, y) >> 16) & 0xFF);
        }

        for(int i=0; i<3; i++)
        {
            didy += (float)((c1[i]-c0[i])*(c1[i]-c0[i]));
        }
        didy = (float)Math.sqrt(didy);

        return didx+didy;
    }

    public static void main(String[] args) {

        BufferedImage img;
        float costs[][];
        int seam[];

        try {
            img = ImageIO.read(new File("test3.jpg"));
        } catch(Exception e)
        {
            System.out.printf("Could not read image file!\n");
            return;
        }

        for(int n=0; n<=200; n++)
        {
            int width = img.getWidth();
            int height = img.getHeight();

            // Compute costs for seams.
            costs = computeCosts(img);
            // Extract seam with lowest cost.
            seam = computeSeam(costs, width, height);
            // Remove the seam from the image.
            img = removeSeam(img, seam);

            if (n % 50 == 0) {
                try {
                    ImageIO.write(img, "png", new File("result_" + Integer.toString(n) + ".png"));
                } catch(Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

    }
}
