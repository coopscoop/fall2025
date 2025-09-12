
public class Child2 implements Runnable {
    private static int numberOfBlocks = 0;
    private static final int totalNumberOfBlocks = 100_000;

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i=0; i<totalNumberOfBlocks/2; i++){
            numberOfBlocks++;
//            System.out.println(name + " sees total as: " + numberOfBlocks);
        }
        System.out.printf("%s sees total as: %,d\n", name, numberOfBlocks);
    }

    public static void main(String[] args) {
        Thread childOne = new Thread(new Child2());
        Thread childTwo = new Thread(new Child2());

        childOne.start();
        childTwo.start();

        try {
            childOne.join();
            childTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Number of blocks picked up: " + numberOfBlocks);
    }
}
