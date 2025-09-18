public class ChildReader implements Runnable {
    @Override
    public void run() {
        while (!Child.finished) {
            // do something?
        }
        System.out.println("child reader sees num blocks: " + Child.numberOfBlocks);
        System.out.println("finished: " + Child.finished);
    }
}