package nl.mprog.projects.nPuzzle10419667;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class GamePlay extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String puzzleName = intent.getStringExtra(ImageSelection.PUZZLENAME);
		String difficulty = intent.getStringExtra(ImageSelection.DIFFICULTY);
		Integer id = (Integer) getResources().getIdentifier(puzzleName, "drawable", getPackageName());
		int n = 0;
		if(difficulty.equals("easy")) {
			n = 3;
		} 
		else if(difficulty.equals("medium")) {
			n = 4;
		}
		else if(difficulty.equals("hard")) {
			n = 5;
		}
		
		Bitmap[] tiles = createTiles(id, n);
		
		setContentView(R.layout.activity_game_play);
		ImageView imageView = (ImageView) findViewById(R.id.firstTile);
		imageView.setImageBitmap(tiles[0]);
		imageView = (ImageView) findViewById(R.id.secondTile);
		imageView.setImageBitmap(tiles[1]);
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
		Bitmap[] tiles = new Bitmap[n*n];
		
		Bitmap puzzle = BitmapFactory.decodeResource(getResources(), (int) id);
		int width = puzzle.getWidth();
		int height = puzzle.getHeight();
		
		int tileWidth = width / n;
		int tileHeight = height / n;
		
		int tile = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tiles[tile] = Bitmap.createBitmap(puzzle, i*tileWidth, j*tileHeight, tileWidth, tileHeight);
				tile++;
			}
		}
		
		return tiles;
		
	}
}
