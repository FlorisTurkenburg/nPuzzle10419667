package nl.mprog.projects.nPuzzle10419667;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ImageSelection extends Activity {
	ListView list;
	
	String[] web = {
		"Image0",
		"Image1",
		"Image2",
		"Image3"
	};
	Integer[] imageId = {
		R.drawable.puzzle_0,
		R.drawable.puzzle_1,
		R.drawable.puzzle_2,
		R.drawable.puzzle_3
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_selection);
		
		// Make the image list
		ImageList adapter = new ImageList(ImageSelection.this, web, imageId);
		list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            Toast.makeText(ImageSelection.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
        	}
        });
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
