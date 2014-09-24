# Deployment Instructions:
  # Clone repo  
  git clone git@github.com:FlorisTurkenburg/nPuzzle10419667.git FlorisTurkenburg
  
  # Import project into Eclipse  
  In eclipse: File -> Import -> General -> Existing Projects into Workspace -> Set the root directory to the FlorisTurkenburg directory -> Finish  
  
  # Include the Support Library (appcompat_v7)  
  Make sure you have the Android Support Library, else install it with the SDK Manager.  
  In Eclipse: Project -> Properties -> Android, add the appcompat_v7 to the libraries.
  
  # Run the app  
  Press ctrl+F11, or click Run -> Run and select the Android device or AVD on which you want to install the app.




nPuzzle10419667
===============
This project's goal is to build an Android Application that lets the user play the n-puzzle game, also known as a sliding puzzle, with the choice of several different images/pictures and 3 difficulties.

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


![Overview of the app](/doc/MockupOverview.png "Overview of the screens")
