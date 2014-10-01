Design Document
===============
ImageSelectionActivity.java:
-----
  - LaunchActivity
  - Should check if there is a saved game
    + Yes: resume the game (in GamePlay.java)
    + No: Retrieve the images from the drawable Resources, and loads scaled down versions of these images into a custom ListView (ImageList.java). Then starts a ClickListener for these images. When an image is clicked, starts the GamePlay activity with the image name and stored difficulty in the Intent.
  - If this activity is started by GamePlayActivity.java (i.e. a game is being quit and the user wants to either quit the game or start a new puzzle), the ImageList is also displayed.

ImageList.java:
------
  This class extends the ArrayAdapter for Integers to make a custom ArrayAdapter which can work with ImageViews. It is here that the ListView gets filled with the scaled down images (using the methods from BitmapMethods.java, which are taken from the Android developer website).
  
GamePlayActivity.java:
------
  Get the intent, if there is a saved game, this game is loaded. The difficulty is retrieved from the SharedPreferences and the corresponding puzzle size is set. The puzzle name is either retrieved from the intent, or from the SharedPreferences, depending on if a new game was started, or a previous game needs to be resumed. Then the createTiles method is called with the image ID. This method sets the Bitmap array with the bitmaps of the tiles, with the last tile being empty (fully transparent pixels). If a new game is started, the method showPreview is called. The Bitmap array of the tiles is used to fill the Gridview which represents the preview. The GridView uses a custom adapter to fill the grid with images (ImageAdapter.java).
  After 3 seconds, the view with the preview gets replaced by the view of the game.
  The game contains an onItemClickListener for the tiles and if the clicked tile neighbours the empty space, these two are being swapped. The way the tiles and their current positions and original positions are being used and mapped, is explained later on.
  The menu is displayed in the ActionBar with 3 icons/buttons:
  - Reset puzzle; The current game is aborted and a new game is started, using the same image and difficulty.
  - Difficulty; An AlertDialog appears with radio buttons for the three difficulties, if after selecting a different difficulty, the "Save Changes"-button is pressed, the game gets reset, using the same image and the new difficulty. If the "Cancel"-button is pressed, the AlertDialog disappears and the game is resumed.
  - Quit Game; This saves the current state of the game and returns the user to the ImageSelection screen, if here the "back"-button is pressed, the app closes. If a new image is selected, the previous saved game gets deleted and a new game is started.  

During the GamePlay activity, the "menu"-button and "back"-button have no function, if, however, the ActionBar can not be displayed (maybe in older versions), both buttons will open the OptionsMenu.  
After every move that is made, the program checks if the puzzle is solved, and the "moves"-counter is increased by 1. The game state is saved onPause(), i.e. whenever the GamePlay activity leaves the foreground. If the puzzle is solved, the YouWon activity is started.

YouWonActivity.java:
-------
This class displays the message: "You have won!" and the original image of the puzzle, followed by the number of moves taken, and a "New Game"-button. This starts up the ImageSelection screen and deletes the saved game (since it is completed).


Saving the state:
--------
The information about the app is saved using SharedPreferences, this includes:
  - difficulty
  - gameOpen
  - puzzleName
  - numMoves
  - tile positions

Difficulty is saved whenever it is changed (changeDifficulty screen).
gameOpen indicates wether the app should open the ImageSelection on startup or the GamePlay, and if the GamePlay should show the preview or not.
puzzleName, numMoves and tile positions are needed to resume the game, these are cleared if a new game is selected or a game is completed.

Mapping and saving the tiles:
---------
To save the tile positions and to display the tiles in the game, three arrays are used. The first array is made when GamePlayActivity starts, and contains the Bitmaps of the tiles on the index that corresponds with the position of the tile in the original image (index 0 corresponds with the upper left corner). The second array maps the index of the tile with the position where it should be placed in the game, example: secondArray[0] = 13, means that the 13th tile in the original image, is to be placed in position 0 (upper left corner). Finally, the third array uses the values of the second array, to put the bitmaps from the first array on its index where it is placed in the game.  
The second array is the array that is saved in SharedPreferences, it is however, saved in separate values, since it is not possible to save an entire array for one key.
