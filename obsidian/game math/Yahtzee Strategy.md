## 5 general blocks
- first roll logic
- second roll logic
- third roll logic
- scoring strategy
- scratching strategy

## Roll logic
all roll logic will depend on the scoring strategy, then fall back to the scratch strategy depending if it's required

## Scoring logic
the general idea is to create a probability map and attempt the most likely roll and attempt that

to pick what roll i want to attempt, attribute a score to it, highest score gets attempted.

first thoughts on the score would be the ratio of `points / probability`

> [!NOTE]
> in the upper section ones-sixes would be the average of each category, i.e. 2.5 for ones, 5 for twos, 7.5 for threes etc.

one catch to this is joker scoring. once a yahtzee is scored and the box is filled, then follow the following joker scoring rules:
1. 

## Scratch logic
scratch logic is a fallback if i don't get a roll or a satisfactory roll. for the sake of higher average runs and less low score vs super high score games take the least likely box to tick off

i.e. yahtzee has a ~1/47000 so that's the first to go, then long run, short run, etc

other option is to go for a high roller game and flip the logic so the lowest average points per block gets scratched first