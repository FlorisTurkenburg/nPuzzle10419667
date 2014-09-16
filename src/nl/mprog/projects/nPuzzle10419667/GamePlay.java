/* Author: Floris Turkenburg
 * Email: sk8_floris@hotmail.com
 * UvANetID: 10419667 
 * 
 * 
 * 
 */

package nl.mprog.projects.nPuzzle10419667;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class GamePlay extends ActionBarActivity {
    private Handler mHandler = new Handler();
    int puzzleSize;
    int numTiles;
    int moves = 0;
    Bitmap[] tiles;

    /*
     * public int[] tilePos = { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
     */

    // This array links the position of the tile in the screen (the index) with
    // the number of the tile bitmap (the value corresponds with the index in Bitmap[] tiles).
    public int[] tilePos = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 14
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String puzzleName;

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
        } else if (difficulty.equals("medium")) {
            puzzleSize = 4;
        } else if (difficulty.equals("hard")) {
            puzzleSize = 5;
        } else {
            puzzleSize = 4;
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
            showPreview();

            mHandler.postDelayed(new Runnable() {
                public void run() {
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
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createTiles(Integer id) {
        tiles = new Bitmap[numTiles];

        MemoryInfo memInfo = new MemoryInfo();
        ActivityManager actMan = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        actMan.getMemoryInfo(memInfo);
        Log.i("GamePlay", "Memory available before tiles: " + memInfo.availMem);
        Bitmap puzzle = BitmapMethods.decodeSampledBitmapFromResource(getResources(), id, 200, 200);
        // Bitmap puzzle = BitmapFactory.decodeResource(getResources(), (int) id);
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
        tiles[numTiles - 1] = null;
        actMan.getMemoryInfo(memInfo);
        Log.i("GamePlay", "Memory available after tiles: " + memInfo.availMem);

        puzzle.recycle();
        puzzle = null;
        actMan.getMemoryInfo(memInfo);
        Log.i("GamePlay", "Memory available after puzzle.recycle(): " + memInfo.availMem);

    }

    public void showPreview() {

        setContentView(R.layout.puzzle_preview);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns(puzzleSize);
        gridview.setAdapter(new ImageAdapter(this, tiles));

        return;

    }

    public void playGame() {

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
                Toast.makeText(GamePlay.this, "You Clicked at " + position, Toast.LENGTH_SHORT)
                        .show();

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
        int indexEmpty = 0;
        // Search for the 1d index of the empty tile (= the last tile = n*n-1)
        for (int i = 0; i < numTiles; i++) {
            if (tilePos[i] == numTiles - 1) {
                indexEmpty = i;
                break;
            }
        }

        // Convert the 1d positions to 2d coordinates
        int tileX = position % puzzleSize;
        int tileY = (int) position / puzzleSize;

        int emptyX = indexEmpty % puzzleSize;
        int emptyY = (int) indexEmpty / puzzleSize;

        int xDiff = tileX - emptyX;
        int yDiff = tileY - emptyY;

        if ((xDiff == 0 && Math.abs(yDiff) == 1) || (Math.abs(xDiff) == 1 && yDiff == 0)) {
            int temp = tilePos[indexEmpty];
            tilePos[indexEmpty] = tilePos[position];
            tilePos[position] = temp;

            moves++;

            return true;
        }

        return false;

    }

    public boolean checkIfSolved() {

        for (int i = 0; i < numTiles; i++) {
            if (i != tilePos[i]) {
                return false;
            }
        }

        return true;
    }

    // Overrides the back button to open the menu instead of returning to the previous activity.
    @Override
    public void onBackPressed() {
        openOptionsMenu();
    }

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

    @Override
    protected void onStop() {
        super.onStop();

        for (int i = 0; i < numTiles - 1; i++) {
            tiles[i].recycle();
            tiles[i] = null;
        }
        tiles = null;

    }
}
