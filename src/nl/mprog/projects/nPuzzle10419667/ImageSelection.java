/* Author: Floris Turkenburg
 * Email: sk8_floris@hotmail.com
 * UvANetID: 10419667 
 */

package nl.mprog.projects.nPuzzle10419667;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ImageSelection extends Activity {
    public final static String PUZZLENAME = "puzzle";

    public final static String DIFFICULTY = "medium";

    ListView list;

    Integer[] imageId;

    String[] web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if a game is open:
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        boolean gameOpen = sharedPref.getBoolean(getString(R.string.game_open), false);
        
        if (gameOpen) {
            // Resume the game
            Intent intent = new Intent(ImageSelection.this, GamePlay.class);
            startActivity(intent);
            
        } else {            
            setContentView(R.layout.activity_image_selection);
            
            // Check how many images there are available
            int i = 0;
            while (0 != getResources().getIdentifier("puzzle_" + i, "drawable", getPackageName())) {
                i++;
            }
            
            // Store the drawable ID's in the imageId array
            imageId = new Integer[i];
            web = new String[i];
            for (int j = 0; j < i; j++) {
                imageId[j] = getResources().getIdentifier("puzzle_" + j, "drawable", getPackageName());
            }
            
            // Make the image list
            ImageList adapter = new ImageList(ImageSelection.this, web, imageId);
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = getResources().getResourceEntryName(imageId[+position]);
                    Toast.makeText(ImageSelection.this, "You Clicked at " + name, Toast.LENGTH_SHORT)
                    .show();
                    Intent intent = new Intent(ImageSelection.this, GamePlay.class);
                    intent.putExtra(PUZZLENAME, name);
                    intent.putExtra(DIFFICULTY, "medium");
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_selection, menu);
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

}
