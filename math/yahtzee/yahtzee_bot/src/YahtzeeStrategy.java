/*
* Starter code for the COMP 10185 Yahtzee Strategy Assignment
*
The operation took 10.52 seconds.
Iterations: 1000000			Min Score: 21		Max Score: 442		Average Score: 112.56
Games>150: 11.14%	Games>200: 0.75%
*
*/
import java.util.Arrays;
import java.util.Map;

public class YahtzeeStrategy implements Cloneable {
    // Before performing large numbers of sims, set this to false.
    // Printing to the screen is relatively slow and will cause your game to under perform.
    //final boolean _STRAT_DEBUG_ = true;
    final boolean _STRAT_DEBUG_ = false;

    public void debugWrite( String str ) {
        if ( _STRAT_DEBUG_ )
            System.out.println( str );
    }

    // Return an array of booleans where true means keep the die in that position.
    // Returning keep in all positions from first_roll_strategy or second_roll_strategy will cause
    // the game to immediately move to the scoring phase, ending the turn.

    private void keep_all( boolean[] keep ) {
        for( int i = 0; i < keep.length; i++ ) keep[i] = true;
    }

    private boolean is_open( Scorecard score, Scorecard.Boxes box ) {
        return !score.isBoxFilled( box ) && !score.isBoxScratched( box );
    }

    public boolean[] first_roll_strategy(int[] dice, Scorecard scorecard) {
        boolean[] keep = new boolean[5];

        debugWrite( "First roll: " + Arrays.toString(dice) );

        // CHANGE THIS STRATEGY TO SUIT YOURSELF
        // THIS STRATEGY KEEPS YAHTZEES, LARGE STRAIGHTS AND THAT'S IT.

        Map<Scorecard.Boxes, Boolean> thisRollHas = Die.has( dice );

        // does the roll have a yahtzee?
        if (thisRollHas.get(Scorecard.Boxes.Y)) {
            // note that set score can fail if the pattern doesn't actually match up or
            // if the box is already filled.  Not a problem with yahtzee but maybe for
            // other patterns it is a problem.
            keep_all( keep );
        } else

        // does the roll have a large straight?
        if (thisRollHas.get(Scorecard.Boxes.LS) && is_open( scorecard, Scorecard.Boxes.LS ) ) {
            keep_all( keep );
        } else {
            // DO NOT SORT THE ROLL ARRAY - the order is significant!!
            // Since it is easier to reason with sorted arrays, we clone the
            // roll and work off a temporary copy.
            int[] tempRoll = new int[dice.length];
            for ( int i = 0; i < dice.length; i++  ) tempRoll[i] = dice[i];
            Arrays.sort(tempRoll);

            // If we have a 3 of a kind or 4 of a kind, roll for yahtzee otherwise roll all 5 dice
            // Don't have to check for the box being scratched or filled because this won't trigger
            // scoring the roll.
            if ( thisRollHas.get(Scorecard.Boxes.FK) || thisRollHas.get(Scorecard.Boxes.TK)) {
                // if there is a 3 or 4 of a kind, the middle die is always
                // part of the pattern, keep any die that matches it
                for (int i = 0; i < dice.length; i++)
                    if (dice[i] == tempRoll[2]) keep[i] = true;

                    // AAABC, ABBBC, ABCCC .... AAAAB, ABBBB ...
            }
        }

        debugWrite( "Keeping: " + Arrays.toString( keep ) );
        return keep;
    }

    public boolean[] second_roll_strategy(int[] dice, Scorecard scorecard) {
        debugWrite( "Second roll: " + Arrays.toString(dice) );

        // this is probably not a great idea. Might want to rewrite this.
        // you must recompute your "kept dice" each phase, they are not carried forward.
        return first_roll_strategy( dice, scorecard );
    }

    public Scorecard.Boxes scoring_phase_strategy(int[] dice, Scorecard scorecard) {
        debugWrite( "Final roll: " + Arrays.toString(dice) );
        Scorecard.Boxes box = null; // returning null will trigger the scratch routine.

        // Priority is set by ordering.
        Map<Scorecard.Boxes, Boolean> thisRollHas = Die.has( dice );

        // does the roll have a yahtzee? don't test YB directly.
        if ( thisRollHas.get(Scorecard.Boxes.Y ) )
            return Scorecard.Boxes.Y;

        if ( thisRollHas.get(Scorecard.Boxes.LS) && is_open( scorecard, Scorecard.Boxes.LS ) )
            return Scorecard.Boxes.LS;

        if ( thisRollHas.get(Scorecard.Boxes.FK) && is_open( scorecard, Scorecard.Boxes.FK ) ) {
            return Scorecard.Boxes.FK;
        }

        if ( thisRollHas.get(Scorecard.Boxes.TK) && is_open( scorecard, Scorecard.Boxes.TK ) ) {
            return Scorecard.Boxes.TK;
        }

        if ( thisRollHas.get(Scorecard.Boxes.U1) && is_open( scorecard, Scorecard.Boxes.U1 ) ) return Scorecard.Boxes.U1;
        if ( thisRollHas.get(Scorecard.Boxes.U2) && is_open( scorecard, Scorecard.Boxes.U2 ) ) return Scorecard.Boxes.U2;
        if ( thisRollHas.get(Scorecard.Boxes.U3) && is_open( scorecard, Scorecard.Boxes.U3 ) ) return Scorecard.Boxes.U3;
        if ( thisRollHas.get(Scorecard.Boxes.U4) && is_open( scorecard, Scorecard.Boxes.U4 ) ) return Scorecard.Boxes.U4;
        if ( thisRollHas.get(Scorecard.Boxes.U5) && is_open( scorecard, Scorecard.Boxes.U5 ) ) return Scorecard.Boxes.U5;
        if ( thisRollHas.get(Scorecard.Boxes.U6) && is_open( scorecard, Scorecard.Boxes.U6 ) ) return Scorecard.Boxes.U6;

        if ( thisRollHas.get(Scorecard.Boxes.FH) && is_open( scorecard, Scorecard.Boxes.FH ) ) return Scorecard.Boxes.FH;
        if ( thisRollHas.get(Scorecard.Boxes.SS) && is_open( scorecard, Scorecard.Boxes.SS ) ) return Scorecard.Boxes.SS;
        if ( thisRollHas.get(Scorecard.Boxes.C) && is_open( scorecard, Scorecard.Boxes.C  ) ) return Scorecard.Boxes.C;

        return null;
    }

    public Scorecard.Boxes joker_scoring( Scorecard score, Die[] dice ) {
        // You may change the order of jokerScoring as you wish.

        Scorecard.Boxes box = null;

        // Joker Rules: Score the total of all 5 dice in the appropriate upper section box (enforced by Scorecard)
        //              If the appropriate upper section box is still open, Scorecard will place it automatically —
        //              this method is only consulted once that box is unavailable.

        //              If that box has already been filled in, score as follows in any
        //              lower section box: 3K, 4K, FH, SS, LS, C as normal rules.

        // These can be re-ordered or additional logic placed here to improve the score.
        if ( !score.isBoxFilled( Scorecard.Boxes.TK ) && !score.isBoxScratched( Scorecard.Boxes.TK )) return Scorecard.Boxes.TK;
        if ( !score.isBoxFilled( Scorecard.Boxes.FK ) && !score.isBoxScratched( Scorecard.Boxes.FK )) return Scorecard.Boxes.FK;
        if ( !score.isBoxFilled( Scorecard.Boxes.FH ) && !score.isBoxScratched( Scorecard.Boxes.FH )) return Scorecard.Boxes.FH;
        if ( !score.isBoxFilled( Scorecard.Boxes.SS ) && !score.isBoxScratched( Scorecard.Boxes.SS )) return Scorecard.Boxes.SS;
        if ( !score.isBoxFilled( Scorecard.Boxes.LS ) && !score.isBoxScratched( Scorecard.Boxes.LS )) return Scorecard.Boxes.LS;
        if ( !score.isBoxFilled( Scorecard.Boxes.C  ) && !score.isBoxScratched( Scorecard.Boxes.C )) return Scorecard.Boxes.C;

        //              If the appropriate upper section box is filled in, and all lower section
        //              boxes are filled in, you must enter a zero in any open upper section box.
        switch( dice[0].getCurrentFace() ) {
            case 1: box = Scorecard.Boxes.U1; break;
            case 2: box = Scorecard.Boxes.U2; break;
            case 3: box = Scorecard.Boxes.U3; break;
            case 4: box = Scorecard.Boxes.U4; break;
            case 5: box = Scorecard.Boxes.U5; break;
            case 6: box = Scorecard.Boxes.U6; break;
        }

        if ( !score.isBoxFilled( box ) && !score.isBoxScratched( box ) ) return box;

        return null;
    }

    public Scorecard.Boxes scratch_strategy( Scorecard score ) {
        // Initial logic is quite trivial and should be replaced.

        // The loop goes from the top of the scorecard downward looking for a box to scratch.
        for ( Scorecard.Boxes box : Scorecard.Boxes.values() ) {
            if ( !score.isBoxFilled( box ) && !score.isBoxScratched( box ) )
                return box;
        }

        throw new IllegalStateException( "No available boxes to scratch found");
    }

}