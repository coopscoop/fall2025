import java.util.Random;

public class Racer implements Runnable {
    private boolean prepared = false;
    private String name;
    private boolean stopped = false;
    private boolean crossing = false;
    private RopeCrossing rope;

     /**
     * Constructor
     * @param name
     */
    public Racer(String name) {
        this.name = name;
    }

    /**
     * Is the racer prepared
     * 
     * @return
     */
    public boolean isPrepared() {
        return prepared;
    }

    /**
     * Is the racer currently crossing
     * @return
     */
    public boolean isCrossing() {
        return crossing;
    }
    /**
     * Set the crossing status of the racer
     * @param crossing
     */
    public void setCrossing(boolean crossing) {
        this.crossing = crossing;
    }

    /**
     * Get the racer's name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Wait an appropriate amount of time for the racer to cross, avg time = 1.5 sec
     * 
     * @param rope RopeCrossing object
     * @param racerName name of the racer
     * @param direction direction, always == 1 for A1, 1 or 2 for A2
     */
    public void cross(RopeCrossing rope, String racerName, int direction) {
        Random random = new Random();
        int low = 1000;
        int high = 2000;
        int waitTime = random.nextInt(high - low) + low;
        String dir = direction == 1 ? "Forward " : "Return ";
        System.out.println("> " + name + " started crossing " + dir + rope.getName());
        try {
            rope.setInUse(true);
            Thread.sleep(waitTime);
            rope.setInUse(false);
        } catch (Exception e) {
        }
        System.out.println("# " + racerName + " finished crossing " + dir + rope.getName() + " in "
                + waitTime + " ms");
    }

    /**
     * Wait an appropriate amount of time for the racer to setup, average time = 1
     * sec
     * 
     * @param rName - racer name
     */
    public void setup(String rName) {
        Random random = new Random();
        int low = 800;
        int high = 1200;
        int waitTime = random.nextInt(high - low) + low;
        try {
            Thread.sleep(waitTime);
            prepared = true;
        } catch (Exception e) {
        }
        System.out.println(rName + " ready to cross - " + waitTime + " ms for gear setup");
    }

    /**
     * Set the rope to be used by the racer
     * @param rope
     */
    public void setRope(RopeCrossing rope) {
        this.rope = rope;
    }

    /**
     * Run method for the thread
     */
    @Override
    public void run() {
        setup(name);
    }

}
