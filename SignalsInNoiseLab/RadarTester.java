
/**
 * Write a description of class RadarTester here.
 * 
 * @AllenLin 
 * @12/20/14
 */
public class RadarTester
{
    public static void main(String[] args)
    {
        Radar checking = new Radar(100,100,5,5,0,0);
        checking.scan();
        checking.setNoiseFraction(.01);
        checking.foundLargest();
        System.out.println("expected: 5,5");
        
    }
    
}
