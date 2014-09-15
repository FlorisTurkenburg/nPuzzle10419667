/* Author: Floris Turkenburg
 * Email: sk8_floris@hotmail.com
 * UvANetID: 10419667 
 * 
 * 
 * 
 */

package nl.mprog.projects.nPuzzle10419667;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

public class GamePlay extends ActionBarActivity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        Bitmap[] tiles = showPreview(intent);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                // doStuff();
                setContentView(R.layout.activity_game_play);
            }
        }, 3000);

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

    public Bitmap[] createTiles(Integer id, int n) {
        Bitmap[] tiles = new Bitmap[n * n];

        Bitmap puzzle = BitmapFactory.decodeResource(getResources(), (int) id);
        int width = puzzle.getWidth();
        int height = puzzle.getHeight();

        int tileWidth = width / n;
        int tileHeight = height / n;

        int tile = 0;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                tiles[tile] = Bitmap.createBitmap(puzzle, i * tileWidth, j * tileHeight, tileWidth,
                        tileHeight);
                tile++;
            }
        }
        tiles[n * n - 1] = null;
        puzzle.recycle();

        return tiles;

    }

    public Bitmap[] showPreview(Intent intent) {
        String puzzleName = intent.getStringExtra(ImageSelection.PUZZLENAME);
        String difficulty = intent.getStringExtra(ImageSelection.DIFFICULTY);
        Integer id = (Integer) getResources()
                .getIdentifier(puzzleName, "drawable", getPackageName());
        int n = 0;
        if (difficulty.equals("easy")) {
            n = 3;
        } else if (difficulty.equals("medium")) {
            n = 4;
        } else if (difficulty.equals("hard")) {
            n = 5;
        }

        Bitmap[] tiles = createTiles(id, n);

        setContentView(R.layout.puzzle_preview);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns(n);
        gridview.setAdapter(new ImageAdapter(this, tiles));

        return tiles;

    }
}
