import java.util.concurrent.atomic.AtomicBoolean;

public class RopeCrossing {
    private AtomicBoolean inUse = new AtomicBoolean(false);
    private String name;
    
    public RopeCrossing(String number) {
        this.name = number;
    }

    public String getName() {
        return name;
    }

    /**
     * Is the rope currently in use
     * @return
     */
    public boolean isInUse() {
        return inUse.get();
    }
    
    /**
     * Set the rope to be in use or not
     * @param inUse
     */
    public void setInUse(boolean inUse) {
        this.inUse = new AtomicBoolean(inUse);
    }
}
