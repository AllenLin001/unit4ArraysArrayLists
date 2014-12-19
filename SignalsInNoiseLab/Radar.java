
/**
 * The model for radar scan and accumulator
 * 
 * @author @AllenLin
 * @version 12/14/2014
 */
public class Trial
{
    private boolean[][] currentScan;
    private boolean [] [] nextScan;
    private int[][] potentialD;

    // initial location of the monster
    private int monsterLocationRow;
    private int monsterLocationCol;
    
    // monster's location on the next grid.
    private int nextMonsterLocRow;
    private int nextMonsterLocCol;

    // probability that a cell will trigger a false detection (must be >= 0 and < 1)
    private double noiseFraction;

    // number of scans of the radar since construction
    private int numScans; 

    // inputed monster's speed 
    private int dx;
    private int dy; 
    
    // found monster's speed for return  
    private int returnDx;
    private int returnDy;

    /**
     * Constructor for objects of class Radar
     * 
     * @param   rows    the number of rows in the radar grid
     * @param   cols    the number of columns in the radar grid
     * @param   
     */
    public Trial (int rows, int cols,int dx, int dy, int  mRow, int mCol)
    {
        // initialize instance variables
        this.dx = dx;
        this.dy = dy;
        this.monsterLocationRow = mRow;
        this.monsterLocationCol = mCol;
       
        currentScan = new boolean[rows][cols]; // elements will be set to false

        nextScan= new boolean [rows][cols];

        potentialD = new int[2*dy+1][2*dx+1]; // elements will be set to 0

        noiseFraction = 0.01;

        numScans= 0;

    }

    /**
     * Performs a scan of the radar. Noise is injected into the grid and the accumulator is updated.
     * 
     */
    public void scan()
    {
        // zero the current scan grid
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                currentScan[row][col] = false;
            }
        }

        // create the next scan 
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                nextScan[row][col] = false;
            }
        }

        // inject noise into the grid
        injectNoise();
        setMonsterLocation();

        // udpate the potentialD
        for(int row = 0; row < potentialD.length; row++)
        {
            for(int col = 0; col < potentialD[0].length; col++)
            {
                potentialD[row][col]=0;
            }
        }

        // compare 
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                if (currentScan[row][col] = true)
                {
                    for(int row2 = 0; row2 < nextScan.length; row2++)
                    {
                        for(int col2 = 0; col < nextScan[0].length; col2++)
                        {
                            if (nextScan[row2][col2]=true)
                            {
                                int x = 0; 
                                int y = 0;

                                int changeX= col2 - col;
                                int changeY= row2 - row;

                                if (changeX>=0)
                                {
                                    x= this.dx + changeX ;     
                                }

                                else if (changeX<0)
                                {
                                    x = this.dx - Math.abs(changeX);
                                }

                                if (changeY>=0)
                                {
                                    y = this.dy + changeY ; 
                                }

                                else if (changeY<0)
                                {
                                    y = this.dy - Math.abs(changeY);
                                }

                                this.potentialD[y][x]++;

                            }
                        }

                        // keep track of the total number of scans
                        numScans++;
                    }
                }
            }        
        }
        
        // find the largest number of pair of dx and dy
         for(int row = 0; row < potentialD.length; row++)
        {
            for(int col = 0; col < potentialD[0].length; col++)
            {
                int largest = potentialD[0][0];
                if (potentialD[row][col]>largest){largest = potentialD[row][col]; returnDx= col; returnDy = row;}
            }
        }
      
    }

    /**
     * Sets the location of the monster
     * 
     * @param   row     the row in which the monster is located
     * @param   col     the column in which the monster is located
     * @param   dy      the change in rows
     * @param   dx      the change in columns 
     * @pre row and col must be within the bounds of the radar grid
     */

    public void setMonsterLocation( )
    {
        //update monster's location  
        if (numScans >=1)
        {
            this.monsterLocationRow +=(numScans-1)*this.dy;
            this.monsterLocationCol +=(numScans-1)*this.dx;
            this.nextMonsterLocRow +=(numScans)*this.dy;
            this.nextMonsterLocCol +=(numScans)*this.dx;
            nextScan[nextMonsterLocRow][nextMonsterLocCol]= true; 
            currentScan[monsterLocationRow][monsterLocationCol]=true; 
        }

        else
        {
            currentScan[monsterLocationRow][monsterLocationCol]=true;
            nextScan[monsterLocationRow+dy][monsterLocationCol+dx]=true;
        }
    }

    /**
     * Sets the probability that a given cell will generate a false detection
     * 
     * @param   fraction    the probability that a given cell will generate a flase detection expressed
     *                      as a fraction (must be >= 0 and < 1)
     */
    public void setNoiseFraction(double fraction)
    {
        noiseFraction = fraction;
    }
    
    /**
     * 
     */
    public void reveal()
    {
        System.out.print("dx: "+this.returnDx);
        System.out.print("dy: "+this.returnDy);
    }
    
    /**
     * Returns true if the specified location in the radar grid triggered a detection.
     * 
     * @param   row     the row of the location to query for detection
     * @param   col     the column of the location to query for detection
     * @return true if the specified location in the radar grid triggered a detection
     */
    public boolean isDetected(int row, int col)
    {
        return currentScan[row][col];
    }

    /**
     * Returns the number of times that the specified location in the radar grid has triggered a
     *  detection since the constructor of the radar object.
     * 
     * @param   row     the row of the location to query for accumulated detections
     * @param   col     the column of the location to query for accumulated detections
     * @return the number of times that the specified location in the radar grid has
     *          triggered a detection since the constructor of the radar object
     */
    public int getPotentialDifferenceTable(int row, int col)
    {
        return potentialD[row][col];
    }

    /**
     * Returns the number of rows in the radar grid
     * 
     * @return the number of rows in the radar grid
     */
    public int getNumRows()
    {
        return currentScan.length;
    }

    /**
     * Returns the number of columns in the radar grid
     * 
     * @return the number of columns in the radar grid
     */
    public int getNumCols()
    {
        return currentScan[0].length;
    }

    /**
     * Returns the number of scans that have been performed since the radar object was constructed
     * 
     * @return the number of scans that have been performed since the radar object was constructed
     */
    public int getNumScans()
    {
        return numScans;
    }

    /**
     * Sets cells as falsely triggering detection based on the specified probability
     * 
     */
    private void injectNoise()
    {
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                // each cell has the specified probablily of being a false positive
                if(Math.random() < noiseFraction)
                {
                    currentScan[row][col] = true;
                }
            }
        }

        for(int row = 0; row < nextScan.length; row++)
        {
            for(int col = 0; col < nextScan[0].length; col++)
            {
                // each cell has the specified probablily of being a false positive
                if(Math.random() < noiseFraction)
                {
                    nextScan[row][col] = true;
                }
            }
        }
    }

}
