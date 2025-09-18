/***
 * @author Stephen Adams
 * @version 202535.001
 */

import java.util.EnumMap;
import java.util.Map;

public class Scorecard implements Cloneable {
    /**
     * Enum representing all available score boxes in Yahtzee, assuming a 6 sided die.
     *
     * Includes upper section (U1-U6), and lower section categories such as
     * three-of-a-kind (TK), four-of-a-kind (FK), full house (FH), small straight (SS),
     * large straight (LS), Yahtzee (Y), Yahtzee bonus (YB), and chance (C).
     *
     * WARNING: the scorecard code is dependent on the oridinal order of these boxes,
     * with the upper scorecard appearing first and in numeric order, followed by the lower card.
     */
    public enum Boxes { U1, U2, U3, U4, U5, U6, TK, FK, FH, SS, LS, Y, YB, C  }

    private final EnumMap<Boxes, Integer> upperScore;
    private final EnumMap<Boxes, Integer> lowerScore;

    private final YahtzeeStrategy player_strategy;

    public Scorecard( EnumMap<Boxes, Integer> upper, EnumMap<Boxes, Integer> lower, YahtzeeStrategy strategy ) {
        this( strategy ); // technically a bug, strategy does not support clone() and can be overwritten by this referecne.
        for (Boxes box : Boxes.values() ) {
            upperScore.put( box, upper.get(box) );
            lowerScore.put( box, lower.get(box) );
        }
    }

    public Scorecard( YahtzeeStrategy player ) {
        upperScore = new EnumMap<>(Boxes.class);
        lowerScore = new EnumMap<>(Boxes.class);

        for ( Boxes box : Boxes.values() ) {
            upperScore.put( box, 0 );
            lowerScore.put( box, 0 );
        }

        player_strategy = player;
    }

    public boolean setScore( Boxes box, Die[] dice ) {
        return setScore( box, dice, true);
    }
    public boolean setScore( Boxes box, Die[] dice, boolean verify ) {
        // Do not allow scoring yahtzee bonus directly.
        if ( box == Boxes.YB ) box=Boxes.Y;

        try {
            if ( isBoxFilled( box ) && box != Boxes.Y )
                throw new IllegalStateException( "Illegal state: box "+box+" is already filled");
            else if ( isBoxScratched(box) )
                throw new IllegalStateException( "Illegal state: box "+box+" is already scratched");

            int score = calculateScore( box, dice, verify );

            Yahtzee.debugWrite( "Scoring: " + score + " in " + box + "[" + Die.showDice(dice) + "]");

            if ( isUpperSection( box ) ) {
                upperScore.put(box, score);
            } else if ( isLowerSection( box )) {
                if ( box == Boxes.Y ) {
                    // If Yahtzee is filled, bonus rules will apply.
                    if ( box == Boxes.Y && isBoxFilled( Boxes.Y ) ) {
                        // Yahtzee Bonus handling
                        lowerScore.put( Boxes.YB, lowerScore.get( Boxes.YB ) + 100 );

                        // --- Joker Scoring Rule ---
                        // If the corresponding upper box is still open, must score it.
                        int face = dice[0].getCurrentFace(); // all dice are the same in a Yahtzee
                        Boxes forcedUpper = Scorecard.Boxes.values()[face - 1]; // U1..U6 are the first 6

                        if (!isBoxFilled(forcedUpper) && !isBoxScratched(forcedUpper)) {
                            // Forced placement in the matching upper box
                            setScore(forcedUpper, dice, false);
                        } else {
                            // Joker Scoring Rule - must score points in box other than Yahtzee
                            box = player_strategy.joker_scoring(this, dice);
                            if ( box == null ) {
                                // must scratch
                                box = player_strategy.scratch_strategy( this );
                                scratch( box );
                            } else if ( isBoxFilled( box ) || isBoxScratched( box ) )
                                throw new IllegalStateException( "Illegal state: joker rule box " + box + " is already filled");
                            else
                                setScore( box, dice, false );
                        }
                    } else
                        lowerScore.put( Boxes.Y, 50);

                } else
                    lowerScore.put(box, score);
            } else
                throw new IllegalArgumentException("Invalid box: " + box);

        } catch ( Exception e ) {
            System.out.println( e );
            return false;
        }

        return true;
    }

    public void scratch( Boxes box ) {
        if ( isBoxScratched(box) )
            throw new IllegalStateException( "Cannot scratch box "+box+", already scratched");

        Yahtzee.debugWrite( "Scratching " + box );
        if ( isUpperSection(box) ) upperScore.put(box,-1);
        else if ( isLowerSection(box) ) lowerScore.put(box, -1);
        else
            throw new IllegalArgumentException("Invalid scratch box: " + box);
    }
    public int calculateScore( Boxes box, Die[] dice ) { return calculateScore( box, dice, true ); }

    public int calculateScore( Boxes box, Die[] dice, boolean verify_dice ) {
        // matching the dice to the box is often useful, but fails for Yahtzee Bonus Joker Scoring Rules.

        // assumes dice are pre-sorted in ascending order
        int score = 0;
        Map<Boxes, Boolean> has = Die.has( dice );

        // validate that the dice presented match the box requested
        if ( verify_dice && !Die.has( dice ).get(box) ) {
            System.out.println( "Warning: Dice provided [" + Die.showDice(dice) + "] do not match box requirements for " + box );
            return 0;
        }

        switch( box ) {
            case U1: for ( Die d : dice ) if ( d.getCurrentFace() == 1 ) score += 1; break;
            case U2: for ( Die d : dice ) if ( d.getCurrentFace() == 2 ) score += 2; break;
            case U3: for ( Die d : dice ) if ( d.getCurrentFace() == 3 ) score += 3; break;
            case U4: for ( Die d : dice ) if ( d.getCurrentFace() == 4 ) score += 4; break;
            case U5: for ( Die d : dice ) if ( d.getCurrentFace() == 5 ) score += 5; break;
            case U6: for ( Die d : dice ) if ( d.getCurrentFace() == 6 ) score += 6; break;
            // Note that for scoring yahtzee bonuses, we don't need to actually match the pattern.
            case TK: if ( !verify_dice || has.get(Boxes.TK) ) score = Die.sumDice( dice ); break;
            case FK: if ( !verify_dice || has.get(Boxes.FK) ) score = Die.sumDice( dice ); break;
            case FH: if ( !verify_dice || has.get(Boxes.FH) ) score = 25; break;
            case SS: if ( !verify_dice || has.get(Boxes.SS) ) score = 30; break;
            case LS: if ( !verify_dice || has.get(Boxes.LS) ) score = 40; break;
            case  Y:
                if ( isBoxFilled( Boxes.Y ) ) {
                    // score using Yahtzee Bonus and Joker Scoring Rules
                    score = 100;
                } else
                    score = 50;
                break;
            case  C: score = Die.sumDice( dice ); break;
        }
        return score;
    }

    // Calculate total upper score, including the bonus
    public int getUpperScore() {
        int total = 0;
        for (Boxes box : upperScore.keySet()) {
            // -1 is used as a sentinel to mean scratched, and should score as 0
            total += Math.max(upperScore.getOrDefault(box, 0),0);
        }
        // Apply upper section bonus if total >= 63
        if (total >= 63) {
            total += 35;
        }
        return total;
    }

    // Calculate total lower score, including Yahtzee bonuses
    public int getLowerScore() {
        int total = 0;
        for (Boxes box : lowerScore.keySet()) {
            // -1 is used as a sentinel to mean scratched, and should score as 0
            total += Math.max(lowerScore.getOrDefault(box, 0),0);
        }
        return total;
    }

    public int getScore() { return getUpperScore()+getLowerScore();}

    // This method returns the score from a specified box.
    public int getBoxScore( Boxes box ) {
        if ( isUpperSection( box ) ) {
            return upperScore.get(box);
        } else if ( isLowerSection( box ) )
            return lowerScore.get(box);
        else
            throw new IllegalArgumentException( "Box "+box+" not found in scorecard" );
    }

    // This method returns the difference between the expectation of scoring 3 of a kind in each upper box to achieve the bonus.
    // The total has the currently scored values subtracted off. Note that unscored boxes are not factored into this calculation at all, scratched boxes are coutned here as 0.
    public int getUpperPlusMinus() {
        int plus_minus = 0;
        Boxes box = null;
        int score=0, expected_score=0;

        for ( int i = 1; i <=6; i++ ) {
            switch (i) {
                case 1: box = Boxes.U1; break;
                case 2: box = Boxes.U2; break;
                case 3: box = Boxes.U3; break;
                case 4: box = Boxes.U4; break;
                case 5: box = Boxes.U5; break;
                case 6: box = Boxes.U6;
            }

            if ( isBoxScratched( box ) ) {
                score = 0;
                expected_score = 3*i;
            } else if ( isBoxFilled( box )) {
                score = upperScore.get(box);
                expected_score = 3*i;
            } else {
                score = 0;
                expected_score = 0;
            }
            plus_minus = expected_score - score;
        }

        return plus_minus;
    }

    // Check if a box is filled
    public boolean isBoxFilled(Boxes box) {
        if (isUpperSection(box)) {
            return upperScore.getOrDefault(box, 0) > 0;
        } else {
            return lowerScore.getOrDefault(box, 0) > 0;
        }
    }

    public boolean isBoxScratched(Boxes box) {
        if ( isUpperSection(box) ) {
            return upperScore.getOrDefault(box, 0) == -1;
        } else if ( isLowerSection(box) ) {
            return lowerScore.getOrDefault(box, 0) == -1;
        } else
            throw new IllegalArgumentException("Invalid box: " + box);
    }

    // Helper methods to check if a box belongs to upper or lower section
    private boolean isUpperSection(Boxes box) {
        return box.ordinal() <= Boxes.U6.ordinal();
    }

    private boolean isLowerSection(Boxes box) {
        return box.ordinal() >= Boxes.TK.ordinal();
    }

    /**
     * Generates a string representation of the scorecard,
     * showing both the upper and lower sections, as well as the total score.
     *
     * @return a formatted string showing the scorecard and total scores
     */
    @Override
    public String toString() {
        StringBuilder card = new StringBuilder();
        card.append("U{");

        // Append the upper section scores
        for (Boxes box : Boxes.values()) {
            if (box.ordinal() <= Boxes.U6.ordinal()) {
                int score = upperScore.getOrDefault(box, 0);
                card.append(box.name()).append(":").append(score).append(",");
            }
        }

        // Append the bonus score if upper section is at least 63
        if (getUpperScore() >= 63) {
            card.append("Bonus:35");
        } else {
            card.append("Bonus:0");
        }
        card.append(",SUB:").append(getUpperScore()).append("},L{");

        // Append the lower section scores
        card.append(String.format("3K:%d,4K:%d,FH:%d,SS:%d,LS:%d,Y:%d,YB:%d,C:%d,SUB:%d}",
                lowerScore.getOrDefault(Boxes.TK, 0),
                lowerScore.getOrDefault(Boxes.FK, 0),
                lowerScore.getOrDefault(Boxes.FH, 0),
                lowerScore.getOrDefault(Boxes.SS, 0),
                lowerScore.getOrDefault(Boxes.LS, 0),
                lowerScore.getOrDefault(Boxes.Y, 0),
                lowerScore.getOrDefault(Boxes.YB, 0),
                lowerScore.getOrDefault(Boxes.C, 0),
                getLowerScore()));

        // Append total game score
        card.append(",T{Score:").append(getUpperScore() + getLowerScore()).append("}");

        return card.toString();
    }

    public Scorecard clone() {
        return new Scorecard( upperScore, lowerScore, player_strategy );
    }
}
