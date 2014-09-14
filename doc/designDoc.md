Design Document
===============
ImageSelection.java:
-----
  - Launch Activity
  - Should check if there is a saved game
    + Yes: resume the game (in GamePlay.java)
    + No: Retrieve the images from the drawable Resources, and loads scaled down versions of these images into a custom ListView (ImageList.java). Then starts a ClickListener for these images. When an image is clicked, starts the GamePlay activity with the image name and stored difficulty in the Intent.

ImageList.java:
------
  This class extends the ArrayAdapter for String to make a custom ArrayAdapter which can work with ImageViews. It is here that the ListView gets filled with the scaled down images (using the methods from BitmapMethods.java, which are taken from the android developer website).
  
GamePlay.java:
------
  Get the intent, if there is a saved game, this game is loaded. If a new game is started, the method showPreview is called with the intent as argument. This method retrieves the image and the difficulty from the intent parameter and determines the puzzle size from the difficulty. Then the createTiles method is called with the image ID and puzzle size as arguments. This method returns a Bitmap array with the bitmaps of the tiles, with the last tile being empty (null).
  This Bitmap array is used to fill the Gridview which respresents the preview. The GridView uses a custom adapter to fill the grid with images (ImageAdapter.java).
  After setting this view, the showPreview method return the array with the tiles, which then can be used to create the game itself.
  After 3 seconds, the view with the preview gets replaced by the view of the game.
  The game contains an onItemClickListener for the tiles and if the clicked tile neighbours the empty space, these two are being swapped.
  If the menu button is pressed, a menu appears with three options:
  - Reset puzzle; The current game is aborted and a new game is started, using the same image and difficulty.
  - Difficulty; An overlay view appears with radio buttons for the three difficulties, if after selecting a different difficulty, the "Save Changes"-button is pressed, the game gets reset, using the same image and the new difficulty. If the "Cancel"-button is pressed, the overlay view disappears and the game is resumed.
  - Quit Game; This saves the current state of the game and returns the user to the ImageSelection screen, if the "back"-button is pressed, the app closes. If a new image is selected, the previous saved game gets deleted and a new game is started.
  After every move that is made, the program checks if the puzzle is solved, and the "moves"-counter is increased by 1, also the game state is saved. If the puzzle is solved, the YouWon activity is started.

YouWon.java
-------
This class displays the message: "You have won!" and the original image of the puzzle, followed by the number of moves taken, and a "New Game"-button. This starts up the ImageSelection screen and deletes the saved game (since it is completed).
