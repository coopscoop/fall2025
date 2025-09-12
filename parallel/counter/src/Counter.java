import java.util.ArrayList;

public class Counter implements Runnable {

    int variable;

    public void run() {
        variable++;
        String name = Thread.currentThread().getName();
        System.out.println(name + " has value: " + variable);
    }

    public static void main(String args[]) {
        Counter count = new Counter();
        count.variable = 0;
        ArrayList<Thread> threads = new ArrayList<>();
        for(int i = 0; i < 10; i++)
        {
            Thread thread = new Thread(count);
            thread.setName(String.valueOf(i));
            threads.add(thread);
        }
        for(int i = 0; i < 10; i++)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            threads.get(i).start();
        }
    }
}
