/**
 * The Yahtzee class represents the logic for a Yahtzee game.
 * This class provides methods for rolling dice, tracking scores, 
 * and implementing Yahtzee-specific rules, including handling Yahtzee bonuses and score calculation.
 * 
 * It supports scoring both the upper and lower sections of the Yahtzee scorecard, 
 * rolling up to three times per turn, and applying scoring rules for different Yahtzee combinations.
 * 
 * @author Stephen Adams
 * @version 202535.000
 */
public class Yahtzee {
    private final Die[] dice;
    private final Scorecard scorecard;
    YahtzeeStrategy player = new YahtzeeStrategy();

    // Before performing large numbers of sims, set this to false.
    // Printing to the screen is relatively slow and will cause your game to under perform.
    //final static boolean _DEBUG_ = true;
    final static boolean _DEBUG_ = false;

    /**
     * Constructor for the Yahtzee class.
     * Initializes the game with 5 dice, each having 6 faces.
     * The turn phase starts at 0 and all score boxes are empty.
     */
    public Yahtzee( int number_of_die, int die_faces ) {
        dice = new Die[ number_of_die ];
        for( int i = 0; i < number_of_die; i++ ) dice[i] = new Die( die_faces );

        scorecard = new Scorecard( player );
    }

    public Yahtzee( ) {
        // Note that the current game is not generalized to support other numbers of die faces.
        // Specifically the score card enum would need to be adjusted to allow new "U#" style values
        // for the newly added faces.
        this( 5, 6 );
    }

    @Override
    public String toString(){
        return scorecard.toString();
    }

    public static void debugWrite( String str ) {
        if ( _DEBUG_ )
            System.out.println( str );
    }

    private static int[] faces(Die[] dice) {
        int[] f = new int[dice.length];
        for (int i = 0; i < dice.length; i++) f[i] = dice[i].getCurrentFace();
        return f;
    }

    public int play( ) {
        // Show Game State

        // Each game is exactly 13 turns.
        for (int turnNum = 1; turnNum <= 13; turnNum++) {
            debugWrite("Playing turn " + turnNum + ": ");
            debugWrite( "Scorecard: " + this.toString());

            // **** PHASE 1 ****

            boolean[] kept_dice = null;
            // have not yet rolled the dice, so let's do that.
            for (Die d : dice) d.roll();

            // you pick what dice you want to keep
            kept_dice = player.first_roll_strategy(faces(dice), scorecard.clone());

            // if you kept them all, then you don't want to roll anymore, let's score those dice
            boolean done = true;
            for (boolean b : kept_dice) done &= b;

            if (done) {
                scorecard.setScore(player.scoring_phase_strategy(faces(dice), scorecard.clone()), dice.clone());
                continue;
            }


            // **** PHASE 2 ****


            // time to reroll if we didn't score something, this time pay attention to which dice to keep
            for (int i = 0; i < dice.length; i++) if (!kept_dice[i]) dice[i].roll();

            // you pick what dice you want to keep
            kept_dice = player.second_roll_strategy(faces(dice), scorecard.clone());

            // if you kept them all, then you don't want to roll anymore, let's score those dice
            done = true;
            for (boolean b : kept_dice) done &= b;

            if (done) {
                scorecard.setScore(player.scoring_phase_strategy(faces(dice), scorecard.clone()), dice.clone());
                continue;
            }


            // **** PHASE 3 ****

            // time to reroll if we didn't score something, this time pay attention to which dice to keep
            for (int i = 0; i < dice.length; i++) if (!kept_dice[i]) dice[i].roll();

            Scorecard.Boxes target = player.scoring_phase_strategy(faces(dice), scorecard.clone());

            if ( target == null )
                scorecard.scratch( player.scratch_strategy(scorecard) );
            else
                scorecard.setScore(target, dice.clone());

        }
        debugWrite( "Final State: " + scorecard );
        return scorecard.getScore();
    }
}
