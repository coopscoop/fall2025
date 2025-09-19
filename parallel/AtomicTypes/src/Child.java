public class Child implements Runnable {

    private static final int totalNumberOfBlocks = 100_000_000;
    public static int numberOfBlocks = 0;
    public static volatile boolean finished = false;

    @Override
    public void run() {

        // int localBlocks = 0;
        for (int i = 0; i < totalNumberOfBlocks / 2; i++) {
            // increment(); // ~750ms
            synchronized (this.getClass()) { // ~700ms
                numberOfBlocks++;
            }
            // System.out.println(name + " sees total as: " + numberOfBlocks);
        }
        // numberOfBlocks += localBlocks;
    }

    public synchronized static void increment(){
        numberOfBlocks++;
    }

    public static void main(String[] args) {
        Thread childOne = new Thread(new Child());
        Thread childTwo = new Thread(new Child());
        // Thread childR = new Thread(new ChildReader());

        Long st = System.currentTimeMillis();

        childOne.start();
        childTwo.start();
        // childR.start();

        try {
            childOne.join();
            childTwo.join();
            finished = true;
            // childR.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("run time = " + (System.currentTimeMillis() - st) + "ms");
        System.out.printf("Number of blocks picked up: %,d ", numberOfBlocks);
    }
}
