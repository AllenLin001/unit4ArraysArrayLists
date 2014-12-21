
/**
 * The model for radar scan and accumulator
 * 
 * @author @AllenLin
 * @version 12/14/2014
 */
public class Radar
{

    // stores whether each cell triggered detection for the current scan and the next scan of the radar
    private boolean[][] currentScan;
    private boolean [] [] nextScan;

    // value of each cell is incremented for each scan in which that cell triggers detection 

    private int[][] potentialD;

    // location of the monster
    private int monsterLocationRow;
    private int monsterLocationCol;

    private int nextMonsterLocRow;
    private int nextMonsterLocCol;

    // probability that a cell will trigger a false detection (must be >= 0 and < 1)
    private double noiseFraction;

    // number of scans of the radar since construction
    private int numScans; 

    private int dx;
    private int dy; 

    /**
     * Constructor for objects of class Radar
     * 
     * @param   rows    the number of rows in the radar grid
     * @param   cols    the number of columns in the radar grid
     */
    public Radar (int rows, int cols,int dx, int dy, int  mRow, int mCol)
    {
        // initialize instance variables
        this.dx = dx;
        this.dy = dy;
        this.monsterLocationRow = mRow;
        this.monsterLocationCol = mCol;
        this.nextMonsterLocRow = 0;
        this.nextMonsterLocCol = 0;

        currentScan = new boolean[rows][cols]; // elements will be set to false

        nextScan= new boolean [rows][cols];

        int[][] potentialD = new int[2*Math.abs(dy)+1][2*Math.abs(dx)+1]; // elements will be set to 0

        numScans= 0;
        // zero the current scan grid
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                currentScan[row][col] = false;
            }
        }

        // create the next scan 
        for(int row = 0; row < nextScan.length; row++)
        {
            for(int col = 0; col < nextScan[0].length; col++)
            {
                nextScan[row][col] = false;
            }
        }

        for(int row = 0; row < potentialD.length; row++)
        {
            for(int col = 0; col < potentialD[0].length; col++)
            {
                potentialD[row][col] = 0;
            }
        }

    }
    /**
     * Performs a scan of the radar. Noise is injected into the grid and the accumulator is updated.
     * 
     */
    public void scan()
    {

        // inject noise into the grid
        injectNoise(); 
        setMonsterLocation();
        if (monsterLocationRow<100)
        {
            currentScan[monsterLocationRow][monsterLocationRow]=true;
            nextScan[nextMonsterLocRow][nextMonsterLocCol]=true;
        }
        // compare 

        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                if (currentScan[row][col] == true)
                {
                    for(int row2 = 0; row2 < nextScan.length; row2++)
                    {
                        for(int col2 = 0; col2 < nextScan[0].length; col2++)
                        {
                            if (nextScan[row2][col2] == true )
                            {
                                int changeX= col2 - col;
                                int changeY= row2 - row;
                                
                               
                                if (Math.abs(changeX)<=Math.abs(this.dx) 
                                    && Math.abs(changeY)<=Math.abs(this.dy))
                                {
                                    int x= changeX + Math.abs(this.dx) ;     
                                    int y = changeY + Math.abs(this.dy) ; 
                                    this.potentialD[y][x]++;
                                }                     
                            }
                        }
                    }
                }
            }        
        }
        // keep track of the total number of scans
        numScans++;
    } 

    // find the largest number of pair of dx and dy
    public int[] foundLargest()
    {
        int[] info = new int[2];
        int largest = potentialD[0][0]; 
        int foundDy= 0;
        int foundDx= 0;
        for(int row = 0; row < potentialD.length; row++)
        {
            for(int col = 0; col < potentialD[0].length; col++)
            {
                if (potentialD[row][col]>largest)
                {
                    largest=potentialD[row][col]; 
                    foundDx = col;
                    foundDy = row;
                }
            }
        }
        info[0]=foundDx;
        info[1]=foundDy;

        return info; 
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
        if (this.numScans == 0)
        {
            currentScan[monsterLocationRow][monsterLocationCol]=true;
        }
        else 
        {
            monsterLocationRow+=this.dy;
            monsterLocationCol+=this.dx;
            int checker1 = 100 - monsterLocationRow;
            int checker2 = 100 - monsterLocationCol;
            if (checker1 > this.dy && checker2 > this.dx)
            {
                nextMonsterLocRow = monsterLocationRow+this.dy;
                nextMonsterLocCol = monsterLocationCol+this.dx;
            }
        }
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


}