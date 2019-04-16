/**
 * Sources: Dr. Zhu and Daniel Davis
 * https://sites.google.com/a/uah.edu/pervasive-security-privacy/node-level/cryptographic-algorithms-using-java
 */

// Testing the efficiency/performance of algorithms
public class TimingTest {
    public static long stime = 0;  //Start Time
    public static long etime = 0;  //End Time

    public static void timing(String header){

        System.out.println("\nMemory available at " + header + " (Free/Total): ( " +
                Runtime.getRuntime().freeMemory() + " / " +
                Runtime.getRuntime().totalMemory() + " )");

        System.out.println("Time at " + header + ": " + (etime - stime) + "\n");

    }
}
