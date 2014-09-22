/* 
 * Author: Floris Turkenburg
 * Email: sk8_floris@hotmail.com
 * UvANetID: 10419667 
 */

package nl.mprog.projects.nPuzzle10419667;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ImageSelection extends Activity {
    public final static String EXTRA_PUZZLENAME = "nl.mprog.projects.nPuzzle10419667.PUZZLENAME";

    ListView list;

    Integer[] imageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        boolean fromExit = receivedIntent.getBooleanExtra("fromGamePlay", false);

        // Check if a game is open:
        final SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        boolean gameOpen = sharedPref.getBoolean(getString(R.string.game_open), false);

        if (gameOpen && !fromExit) {
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
            for (int j = 0; j < i; j++) {
                imageId[j] = getResources().getIdentifier("puzzle_" + j, "drawable",
                        getPackageName());
            }

            // Make the image list
            ImageList adapter = new ImageList(ImageSelection.this, imageId);
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = getResources().getResourceEntryName(imageId[+position]);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.game_open), false);
                    editor.commit();

                    Intent intent = new Intent(ImageSelection.this, GamePlay.class);
                    intent.putExtra(EXTRA_PUZZLENAME, name);
                    startActivity(intent);
                }
            });
        }
    }
}
