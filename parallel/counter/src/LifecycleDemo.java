public class LifecycleDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread sleepy = new SleepyThread();
        System.out.println("MAIN: Creating the sleepy thread...");
        System.out.println("\tMain Sleepy State: " + sleepy.getState());
        /* sleepy state is ________________________ NEW */

        sleepy.start();
        System.out.println("MAIN: Starting the sleepy thread...");
        System.out.println("\tMain Sleepy State: " + sleepy.getState());
        /* sleepy state is ________________________ RUNNABLE*/

        Thread.sleep(500);
        System.out.println("MAIN: Main thread waited 500 milliseconds...");
        System.out.println("\tMain Sleepy State: " + sleepy.getState());
        /* sleepy state is ________________________ */

        System.out.println("Wake up the sleeping thread....");
        sleepy.interrupt();
        Thread.sleep(500);
        System.out.println("MAIN: Main thread waited 500 milliseconds...");
        System.out.println("\tMain Sleepy State: " + sleepy.getState());
        /* sleepy state is ________________________ */
        /* main thread state is ________________________ */

        sleepy.join();
        System.out.println("MAIN: Main thread is completed....");
        System.out.println("\tMain Sleepy State: " + sleepy.getState());
        /* sleepy state is ________________________ */
        /* main thread state is ________________________ */
    }
}