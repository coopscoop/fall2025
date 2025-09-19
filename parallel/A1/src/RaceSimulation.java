import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class RaceSimulation {

    // general plan:
    // ropes and racers are both in arraylists
    // run prepare for each racer in own thread
    // when a racer is prepared, add to a queue
    // main thread checks queue, if a racer is in the queue, check for an open rope
    // if a rope is open, lock it, remove racer from queue, start crossing in own
    // thread
    // when crossing is done, unlock rope

    private static final int NUM_ROPES = 3;
    private static final int NUM_RACERS = 16;

    // 3 ropes and 16 racers
    private static ArrayList<RopeCrossing> ropes = new ArrayList<RopeCrossing>(NUM_ROPES);
    private static ArrayList<Racer> racers = new ArrayList<Racer>(NUM_RACERS);

    // queues for prepared racers and available ropes
    private static Queue<Racer> preparedRacers = new LinkedList<Racer>();
    private static Queue<RopeCrossing> availableRopes = new LinkedList<RopeCrossing>();

    public static void main(String[] args) {
        // incrementing numbers for naming
        int ropeNum = 1;
        int racerNum = 1;

        // counter for people crossed
        AtomicInteger peopleCrossed = new AtomicInteger(0);

        // fill ropes and racers arralists
        for (int i = 0; i < NUM_ROPES; i++) {
            ropes.add(new RopeCrossing(String.format("rope %d", ropeNum++)));
        }
        for (int i = 0; i < NUM_RACERS; i++) {
            racers.add(new Racer(String.format("racer %d", racerNum++)));
        }

        // for each racer, start a thread to prepare
        for (Racer racer: racers) {
            new Thread(racer).start();
        }

        while (true) {
            // if all racers have crossed, end simulation
            if (peopleCrossed.get() == NUM_RACERS) {
                System.out.println("All racers have crossed");
                break;
            }

            // check if any racers are prepared and add to prepared queue
            for (int i = 0; i < NUM_RACERS; i++) {
                if (racers.get(i).isPrepared() && !preparedRacers.contains(racers.get(i)) && !racers.get(i).isCrossing()) {
                    preparedRacers.add(racers.get(i));
                }
            }

            // check if any ropes are available and add to available queue
            for (int i = 0; i < NUM_ROPES; i++) {
                if (!ropes.get(i).isInUse() && !availableRopes.contains(ropes.get(i))) {
                    availableRopes.add(ropes.get(i));
                }
            }

            // debug print out queues
            // System.out.print("Prepared racers queue: ");
            // for (Racer r : preparedRacers) {
            //     System.out.print(r.getName() + " ");
            // }
            // System.out.println("");
            // System.out.print("Available ropes queue: ");
            // for (RopeCrossing r : availableRopes) {
            //     System.out.print(r.getName() + " ");
            // }
            // System.out.println("");

            // TODO: actually manage the crossing from the queues

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread interrupted");
                break;
            }
        }
    }
}