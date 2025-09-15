import java.util.concurrent.atomic.AtomicInteger;

public class Child implements Runnable {

    // private int numberOfBlocks = 0;
    // private static volatile boolean pickUp = true;
    private AtomicInteger numberOfBlocks = new AtomicInteger(0);
    private static boolean pickUp = true;

    @Override
    public void run() {

        String name = Thread.currentThread().getName();

        while (pickUp) {
            // do some stuff
            numberOfBlocks.incrementAndGet();
            // System.out.println(name + " has " + this.numberOfBlocks + " blocks");
        }
        System.out.printf(name + " is finished and has %,d\n", numberOfBlocks.get());
    }

    public static void main(String[] args) throws InterruptedException {

        Child c = new Child();

        Thread c1 = new Thread(c);
        Thread c2 = new Thread(c);
        Thread c3 = new Thread(c);

        c1.start();
        c2.start();
        c3.start();

        Thread.sleep(1000);
        pickUp = false;

        c1.join();
        c2.join();
        c3.join();
    }
}
