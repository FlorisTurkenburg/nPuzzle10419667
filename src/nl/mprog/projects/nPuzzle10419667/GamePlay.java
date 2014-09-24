/* 
 * Author: Floris Turkenburg
 * Email: sk8_floris@hotmail.com
 * UvANetID: 10419667 
 * 
 * 
 * 
 */

package nl.mprog.projects.nPuzzle10419667;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Random;

public class GamePlay extends ActionBarActivity {
    private Handler mHandler = new Handler();
    int puzzleSize;
    int numTiles;
    int moves = 0;
    Bitmap[] tiles;
    String puzzleName;
    int indexEmptyTile = 0;
    int emptyTileX = 0;
    int emptyTileY = 0;

    // This array links the position of the tile in the screen (the index) with
    // the number of the tile bitmap (the value corresponds with an index in Bitmap[] tiles).
    public int[] tilePos;

    public int[] tilePosEasy = {
            8, 7, 6,
            5, 4, 3,
            2, 1, 0
    };

    public int[] tilePosMed = {
            15, 14, 13, 12,
            11, 10, 9, 8,
            7, 6, 5, 4,
            3, 2, 0, 1
    };

    public int[] tilePosHard = {
            24, 23, 22, 21, 20,
            19, 18, 17, 16, 15,
            14, 13, 12, 11, 10,
            9, 8, 7, 6, 5,
            4, 3, 2, 1, 0
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        Intent intent = getIntent();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);

        String difficulty = sharedPref.getString(getString(R.string.pref_difficulty), "medium");
        boolean resume = sharedPref.getBoolean(getString(R.string.game_open), false);

        if (resume) {
            puzzleName = sharedPref.getString(getString(R.string.puzzle_name), "puzzle_0");
        } else {
            puzzleName = intent.getStringExtra(ImageSelection.EXTRA_PUZZLENAME);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.puzzle_name), puzzleName);
            editor.commit();
        }

        Integer id = (Integer) getResources().getIdentifier(puzzleName, "drawable",
                getPackageName());

        if (difficulty.equals("easy")) {
            puzzleSize = 3;
            tilePos = tilePosEasy;
        } else if (difficulty.equals("medium")) {
            puzzleSize = 4;
            tilePos = tilePosMed;
        } else if (difficulty.equals("hard")) {
            puzzleSize = 5;
            tilePos = tilePosHard;
        } else {
            puzzleSize = 4;
            tilePos = tilePosMed;
        }

        numTiles = puzzleSize * puzzleSize;

        createTiles(id);

        if (resume) {
            // load the tile positions and number of moves
            for (int i = 0; i < numTiles; i++) {
                tilePos[i] = sharedPref.getInt("tile" + i, i);
            }

            moves = sharedPref.getInt(getString(R.string.num_moves), 0);
            playGame();

        } else {
            actionBar.hide();
            showPreview();

            mHandler.postDelayed(new Runnable() {
                public void run() {
                    findRandomSolvablePuzzle();
                    actionBar.show();
                    playGame();
                }
            }, 3000);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.quit) {
            Intent intent = new Intent(GamePlay.this, ImageSelection.class);
            intent.putExtra("fromGamePlay", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.difficulty) {
            openChangeDifficulty();
        } else if (id == R.id.reset) {
            newGame();

        }
        return super.onOptionsItemSelected(item);
    }

    public void createTiles(Integer id) {
        tiles = new Bitmap[numTiles];

        Bitmap puzzle = BitmapMethods.decodeSampledBitmapFromResource(getResources(), id, 200, 200);
        int width = puzzle.getWidth();
        int height = puzzle.getHeight();

        int tileWidth = width / puzzleSize;
        int tileHeight = height / puzzleSize;

        int tile = 0;
        for (int j = 0; j < puzzleSize; j++) {
            for (int i = 0; i < puzzleSize; i++) {
                tiles[tile] = Bitmap.createBitmap(puzzle, i * tileWidth, j * tileHeight, tileWidth,
                        tileHeight);
                tile++;
            }
        }
        tiles[numTiles - 1] = Bitmap.createBitmap(tileWidth, tileHeight, Config.ALPHA_8);

        puzzle.recycle();
        puzzle = null;

    }

    // This method is implemented so the empty tile only has to be found once in the beginning.
    // These coordinates will be updated if the tile moves (in checkIfNeighbouringEmpty()).
    public void initEmptyTilePos() {
        // Search for the 1D index of the empty tile (= the last tile = n*n-1)
        for (int i = 0; i < numTiles; i++) {
            if (tilePos[i] == numTiles - 1) {
                indexEmptyTile = i;
                break;
            }
        }

        // Calculate the 2D coordinates.
        emptyTileX = indexEmptyTile % puzzleSize;
        emptyTileY = (int) indexEmptyTile / puzzleSize;

    }

    public void showPreview() {

        setContentView(R.layout.puzzle_preview);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns(puzzleSize);
        gridview.setAdapter(new ImageAdapter(this, tiles));

        return;

    }

    public void playGame() {
        initEmptyTilePos();

        final Bitmap[] newTiles = new Bitmap[numTiles];

        // Create a new array with the tile bitmaps on the index of the position where they should
        // be drawn.
        for (int i = 0; i < numTiles; i++) {
            newTiles[i] = tiles[tilePos[i]];
        }

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.game_open), true);
        editor.commit();

        setContentView(R.layout.activity_game_play);
        final TextView text = (TextView) findViewById(R.id.moves);
        text.setText("Moves: " + moves);
        final GridView gridview = (GridView) findViewById(R.id.gamegridview);
        gridview.setNumColumns(puzzleSize);
        gridview.setAdapter(new ImageAdapter(this, newTiles));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (checkIfNeighbouringEmpty(position)) {
                    text.setText("Moves: " + moves);
                    for (int i = 0; i < numTiles; i++) {
                        newTiles[i] = tiles[tilePos[i]];
                    }
                    gridview.setAdapter(new ImageAdapter(GamePlay.this, newTiles));

                    if (checkIfSolved()) {
                        for (int i = 0; i < numTiles - 1; i++) {
                            newTiles[i].recycle();
                            newTiles[i] = null;

                        }

                        Intent intent = new Intent(GamePlay.this, YouWon.class);
                        startActivity(intent);

                    }
                }

            }
        });

    }

    public boolean checkIfNeighbouringEmpty(int position) {

        // Convert the 1D positions to 2D coordinates
        int tileX = position % puzzleSize;
        int tileY = (int) position / puzzleSize;

        int xDiff = tileX - emptyTileX;
        int yDiff = tileY - emptyTileY;

        // If they are neighbours, swap the two tiles, update the empty tile coordinates, and
        // increment the number of moves, then return true.
        if ((xDiff == 0 && Math.abs(yDiff) == 1) || (Math.abs(xDiff) == 1 && yDiff == 0)) {
            int temp = tilePos[indexEmptyTile];
            tilePos[indexEmptyTile] = tilePos[position];
            tilePos[position] = temp;

            indexEmptyTile = position;
            emptyTileX = tileX;
            emptyTileY = tileY;

            moves++;

            return true;
        }

        return false;

    }

    // The puzzle is solved if all the values on the indices are the same number as the indices
    // themselves, in the tilePos array.
    public boolean checkIfSolved() {

        for (int i = 0; i < numTiles; i++) {
            if (i != tilePos[i]) {
                return false;
            }
        }

        return true;
    }

    int newDifficulty;

    public void openChangeDifficulty() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GamePlay.this);
        // Add the buttons
        builder.setTitle(R.string.change_difficulty);

        final SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);

        final int oldDifficulty;
        String difficulty = sharedPref.getString(getString(R.string.pref_difficulty), "medium");
        switch (difficulty) {
            case "easy":
                oldDifficulty = 0;
                break;
            case "medium":
                oldDifficulty = 1;
                break;
            case "hard":
                oldDifficulty = 2;
                break;
            default:
                oldDifficulty = 1;
        }
        newDifficulty = oldDifficulty;
        builder.setSingleChoiceItems(R.array.difficulties, oldDifficulty,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        newDifficulty = which;
                    }
                });

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Save changes button
                // The difficulty only needs to be saved and the game needs to be restarted if the
                // difficulty has changed.
                if (newDifficulty != oldDifficulty) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    switch (newDifficulty) {
                        case 0:
                            editor.putString(getString(R.string.pref_difficulty), "easy");
                            break;
                        case 1:
                            editor.putString(getString(R.string.pref_difficulty), "medium");
                            break;
                        case 2:
                            editor.putString(getString(R.string.pref_difficulty), "hard");
                            break;
                    }
                    editor.commit();

                    newGame();

                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void newGame() {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.game_open), false);
        editor.commit();

        Intent intent = new Intent(GamePlay.this, GamePlay.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ImageSelection.EXTRA_PUZZLENAME, puzzleName);
        startActivity(intent);
    }

    // Randomly permute the tile positions, this method is based on the method found on:
    // http://www.java2s.com/Code/Java/Development-Class/Randomlypermutestheelementsofthespecifiedarray.htm
    public void permute(Random random) {
        for (int i = numTiles; --i > 0;) {
            int pos = random.nextInt(i);
            int temp = tilePos[pos];
            tilePos[pos] = tilePos[i];
            tilePos[i] = temp;
        }
    }

    public int countInversions() {
        int inversions = 0;
        for (int i = 0; i < numTiles; i++) {
            // If it is the empty tile, store the index and skip checking for this tile.
            if (tilePos[i] == numTiles - 1) {
                indexEmptyTile = i;
                i++;
            }
            for (int t = i + 1; t < numTiles; t++) {
                if (tilePos[i] > tilePos[t] && tilePos[i] != (numTiles - 1)) {
                    inversions++;
                }
            }
        }

        return inversions;
    }

    // This method finds a solvable puzzle by permuting the tile positions randomly and then checks
    // if this permutation is solvable following the rules from this website:
    // http://www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html
    // Once a solvable puzzle is found, the while loop stops, if not, it continues till it has found
    // a solvable one.
    public void findRandomSolvablePuzzle() {
        int inversions;
        boolean solvable = false;
        while (!solvable) {
            permute(new Random());
            inversions = countInversions();
            if (puzzleSize % 2 == 1) {
                solvable = (inversions % 2 == 0);
            } else {
                int emptyRow = puzzleSize - ((int) indexEmptyTile / puzzleSize);
                if (emptyRow % 2 == 1) {
                    solvable = (inversions % 2 == 0);
                } else {
                    solvable = (inversions % 2 == 1);
                }
            }
        }

    }

    // Overrides the back button to open the menu instead of returning to the previous activity.
    @Override
    public void onBackPressed() {
        openOptionsMenu();
    }

    // Save the state.
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int i = 0; i < numTiles; i++) {
            editor.putInt("tile" + i, tilePos[i]);
        }

        editor.putInt(getString(R.string.num_moves), moves);
        editor.commit();
    }

    // If destroyed recycle the bitmaps
    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (int i = 0; i < numTiles - 1; i++) {
            tiles[i].recycle();
            tiles[i] = null;
        }
        tiles = null;

    }
}
