package nl.mprog.projects.nPuzzle10419667;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageList extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] web;
	private final Integer[] imageId;
	
	public ImageList(Activity context, String[] web, Integer[] imageId) {
		super(context, R.layout.image_list, web);
		this.context = context;
		this.web = web;
		this.imageId = imageId;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View elementView= inflater.inflate(R.layout.image_list, null, true);
		TextView txtTitle = (TextView) elementView.findViewById(R.id.txt);
		ImageView imageView = (ImageView) elementView.findViewById(R.id.image);
		txtTitle.setText(web[position]);
		imageView.setImageResource(imageId[position]);
		return elementView;
	}
}