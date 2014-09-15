/* Author: Floris Turkenburg
 * Email: sk8_floris@hotmail.com
 * UvANetID: 10419667 
 * 
 * 
 * 
 */

package nl.mprog.projects.nPuzzle10419667;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class GamePlay extends ActionBarActivity {
    private Handler mHandler = new Handler();
    int puzzleSize;

    /*
     * public int[] tilePos = { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
     */

    // This array links the position of the tile in the screen (the index) with
    // the number of the tile bitmap (the value corresponds with the index in Bitmap[] tiles).
    public int[] tilePos = {
            0, 13, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 14, 15
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bitmap[] tiles;
        String puzzleName;

        Intent intent = getIntent();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);

        String difficulty = sharedPref.getString(getString(R.string.pref_difficulty), "medium");
        boolean resume = sharedPref.getBoolean(getString(R.string.game_open), false);

        if (resume) {
            puzzleName = sharedPref.getString(getString(R.string.puzzle_name), "puzzle_0");
        } else {
            puzzleName = intent.getStringExtra(ImageSelection.PUZZLENAME);
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

        tiles = createTiles(id);
        
        if (resume) {
            // load the tile positions and number of moves
            int n2 = puzzleSize * puzzleSize;
            for (int i = 0; i < n2; i++) {
                tilePos[i] = sharedPref.getInt("tile"+i, i);
            }
            playGame(tiles);
            
        } else {
            showPreview(tiles);

            mHandler.postDelayed(new Runnable() {
                public void run() {
                    playGame(tiles);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap[] createTiles(Integer id) {
        int n2 = puzzleSize * puzzleSize;
        Bitmap[] tiles = new Bitmap[n2];

        Bitmap puzzle = BitmapFactory.decodeResource(getResources(), (int) id);
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
        tiles[n2 - 1] = null;
        puzzle.recycle();

        return tiles;

    }

    public void showPreview(Bitmap[] tiles) {

        setContentView(R.layout.puzzle_preview);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns(puzzleSize);
        gridview.setAdapter(new ImageAdapter(this, tiles));

        return;

    }

    public void playGame(Bitmap[] tiles) {
        
        final Bitmap[] finalTiles = tiles;
        final int n2 = puzzleSize * puzzleSize;
        final Bitmap[] newTiles = new Bitmap[n2];

        // Create a new array with the tile bitmaps on the index of the position where they should
        // be drawn.
        for (int i = 0; i < n2; i++) {
            newTiles[i] = tiles[tilePos[i]];
        }
        
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.game_open), true);
        editor.commit();
        
        setContentView(R.layout.activity_game_play);
        final GridView gridview = (GridView) findViewById(R.id.gamegridview);
        gridview.setNumColumns(puzzleSize);
        gridview.setAdapter(new ImageAdapter(this, newTiles));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(GamePlay.this, "You Clicked at " + position, Toast.LENGTH_SHORT)
                        .show();

                Toast.makeText(GamePlay.this,
                        "Neighbouring? =" + checkIfNeighbouringEmpty(position), Toast.LENGTH_SHORT)
                        .show();
                for (int i = 0; i < n2; i++) {
                    newTiles[i] = finalTiles[tilePos[i]];
                }
                gridview.setAdapter(new ImageAdapter(GamePlay.this, newTiles));

            }
        });

    }

    public boolean checkIfNeighbouringEmpty(int position) {
        int n2 = puzzleSize * puzzleSize;
        int indexEmpty = 0;
        // Search for the 1d index of the empty tile (= the last tile = n*n-1)
        for (int i = 0; i < n2; i++) {
            if (tilePos[i] == n2 - 1) {
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
            
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            for (int i = 0; i < n2; i++) {
                editor.putInt("tile"+i, tilePos[i]);
            }
            editor.commit();
            return true;
        }

        return false;

    }
}
