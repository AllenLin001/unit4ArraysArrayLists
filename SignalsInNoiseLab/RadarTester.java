
/**
 * Test two cases of the monster dx dy detecting function.
 * one for positive dx and dy the other for negative dx and dy. 
 * 
 * @Allen Lin 
 * @12/23/2014
 */
public class RadarTester
{
    public static void main(String[] args)
    {
        Radar case1 = new Radar(100,100,5,4,0,0);// rows, cols, dx ,dy ,mRow, mCol
        for (int i=0; i<10;i++)// 10 scans to build the potentialD 
        {
            case1.scan();
        }
        case1.setNoiseFraction(.01);// set noiseFraction to .01
        case1.foundLargest();// display the found dx and dy
        System.out.println("expected: 5,4");
        
        Radar case2=new Radar(100,100,-2,-3,100,100);// rows, cols, dx ,dy ,mRow, mCol
        for (int i=0; i<10;i++)
        {
            case2.scan();
        }
        case2.setNoiseFraction(.01);
        case2.foundLargest();
        System.out.println("expected: -2,-3");
        
    }
    
}
