
/**
 * The model for radar scan and accumulator
 * 
 * @author @AllenLin
 * @version 12/14/2014
 */
public class Radar
{

    // 2D boolean array for the currentScan 
    private boolean[][] currentScan;
    
    // a 2D int array for accumulating all the potential difference vectors
    private int[][] potentialD;

    // location of the monster
    private int monsterLocationRow;
    private int monsterLocationCol;



    // probability that a cell will trigger a false detection (must be >= 0 and < 1)
    private double noiseFraction;

    // number of scans of the radar since construction
    private int numScans; 
    
    // horizontal displacement of the monster
    private int dx;
    
    // vertical displacement of the monster
    private int dy; 

    /**
     * Constructor for objects of class Radar
     * 
     * @param   rows    the number of rows in the radar grid
     * @param   cols    the number of columns in the radar grid
     * @param   dx      the horizontal displacement of the monster
     * @param   dy      the vertical displacement of the monster
     * @param   mRow    the initial row of the monster
     * @param   mCol    the initial col of the monster
     */
    public Radar (int rows, int cols,int dx, int dy, int  mRow, int mCol)
    {
        // initialize instance variables
        this.dx = dx;
        this.dy = dy;
        
        this.monsterLocationRow = mRow;
        this.monsterLocationCol = mCol;

        currentScan = new boolean[rows][cols]; // elements will be set to false

        potentialD = new int[11][11]; // elements will be set to 0

        numScans= 0;
        
        // zero the current scan grid and constructs the initial scan(pattern)
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                currentScan[row][col] = false;
            }
        }
        injectNoise(); 
        setMonsterLocation();
        
        // zero the potential difference table
        for(int row = 0; row < potentialD.length; row++)
        {
            for(int col = 0; col < potentialD[0].length; col++)
            {
                potentialD[row][col] = 0;
            }
        }

    }
    
    /**
     * Performs a scan of the radar. Noise is injected into the grid. Monster's location is updated Accordingly. 
     * use a boolean 2D array of prevScan to keep track of the previous scan for comparing purposes. 
     * Record all the potential difference into the potentialD array. 
     */
    
    public void scan()
    {

        // constructs a 2D array call prevScan
        boolean [][] prevScan = new boolean[100][100]; 
        
        //make prevScan a copy of the currentScan
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                if (currentScan[row][col]==true)
                {
                    prevScan[row][col]=true;
                }
            }
        }
        
        //zero the currentScan 
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                currentScan[row][col] = false;
            }
        }
        
        // inject noise to the currentScan and update monster's location. 
        injectNoise();
        setMonsterLocation();
        
        // compare every true points of the two arrays. 
        // if the difference fall into the range(-5 and 5), record the vector in the potentialD array.  
        for(int row = 0; row < prevScan.length; row++)
        {
            for(int col = 0; col < prevScan[0].length; col++)
            {
                if (prevScan[row][col] == true)
                {
                    for(int row2 = 0; row2 < currentScan.length; row2++)
                    {
                        for(int col2 = 0; col2 < currentScan[0].length; col2++)
                        {
                            if (currentScan[row2][col2] == true )
                            {
                                int changeX= col2 - col;
                                int changeY= row2 - row;
                                

                                if (Math.abs(changeX)<= 5 && Math.abs(changeY)<= 5)
                                {
                                    int x= changeX + 5;      
                                    int y = changeY + 5; 
                                    this.potentialD[y][x]++;
                                }                     
                            }
                        }
                    }
                }
            }        
        }
        
    } 

    // find the largest number in potentialD array
    // and display the represented dx and dy on the screen. 
    public void foundLargest()
    {
        
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

        foundDx-=5;
        foundDy-=5;
        System.out.println("Dx: "+ foundDx+" Dy:"+foundDy);
    }

    /**
     * Sets the location of the monster and increments the numScans 
     * updates monster's location based on inputted dx and dy 
     * and set the position true in the currentScan array
     */
    
    public void setMonsterLocation()
    {
        if (monsterLocationRow + dy <= 100 && monsterLocationCol + dx <=100 &&
            monsterLocationRow + dy >= 0 && monsterLocationCol + dx >= 0)
        {
            this.monsterLocationRow += this.dy;
            this.monsterLocationCol += this.dx;
            currentScan[monsterLocationRow][monsterLocationCol]=true;
            numScans++ ; 
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