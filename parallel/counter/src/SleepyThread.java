public class SleepyThread extends Thread{
    @Override
    public void run() {
        System.out.println("RUN: Entered run method...");
        /* sleepy state is ________________________ RUNNABLE*/
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("RUN: SleepyThread has been interrupted...");
            /* sleepy state is ________________________ RUNNABLE*/
        }
        System.out.println("RUN: Going back to sleep.....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // SLEEPY STATE SHOULD BE RUNNABLE

        System.out.println("RUN: Run method exiting...");
    }
}