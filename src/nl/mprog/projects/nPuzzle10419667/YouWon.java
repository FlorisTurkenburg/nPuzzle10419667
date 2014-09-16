
package nl.mprog.projects.nPuzzle10419667;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class YouWon extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_won);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);

        String puzzleName = sharedPref.getString(getString(R.string.puzzle_name), "puzzle_0");
        int id = getResources().getIdentifier(puzzleName, "drawable", getPackageName());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = (int) (metrics.heightPixels * 0.5);
        int width = (int) (metrics.widthPixels * 0.5);

        ImageView image = (ImageView) findViewById(R.id.puzzleComplete);
        image.setImageBitmap(BitmapMethods.decodeSampledBitmapFromResource(getResources(), id,
                width, height));
        // image.setImageResource(id);

        int moves = sharedPref.getInt(getString(R.string.num_moves), 0);
        TextView moveText = (TextView) findViewById(R.id.finalMoves);
        moveText.setText("Moves: " + moves);

        // Clear the saved game but save the difficulty. gameOpen != true because it is deleted by
        // editor.clear().
        String difficulty = sharedPref.getString(getString(R.string.pref_difficulty), "medium");

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString(getString(R.string.pref_difficulty), difficulty);
        editor.commit();
    }

    // Return to the ImageSelect activity and clear the back stack.
    public void newGame(View view) {

        Intent intent = new Intent(YouWon.this, ImageSelection.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    // Disable the back-button.
    @Override
    public void onBackPressed() {

    }

    // If the app is inproperly closed while in the You Won screen, delete the saved game anyway.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);
        String difficulty = sharedPref.getString(getString(R.string.pref_difficulty), "medium");

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString(getString(R.string.pref_difficulty), difficulty);
        editor.commit();
    }

}
