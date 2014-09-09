nPuzzle10419667
===============
This project's goal is to build an Android Application that lets the user play the n-puzzle game, also known as a sliding puzzle, which the choice of several different images/pictures and 3 difficulties.

List of features:
- 3 difficulties, 3x3, 4x4, 5x5. medium (4x4) is default
- image selection menu, including at least 3 images; must scale automatically with the amount of images included.
- upon starting game, show solved image for 3 seconds
- game play:
  + image split in n tiles
  + 1 empty spot
  + tap tiles neighbouring the empty spot to move them there
  + track number of moves
  + menu button containing:
    - reset (restart)
    - change difficulty
    - quit game
- you've won screen, showing number of moves, original image and "new game"-button
- default set to user's prefered difficulty 
- upon quiting, save game state:
  + image
  + number of moves
  + tile-position
  + difficulty
