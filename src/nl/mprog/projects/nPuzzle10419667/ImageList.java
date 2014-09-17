/* 
 * Author: Floris Turkenburg
 * Email: sk8_floris@hotmail.com
 * UvANetID: 10419667 
 */

package nl.mprog.projects.nPuzzle10419667;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class ImageList extends ArrayAdapter<Integer> {
    private final Activity context;


    private final Integer[] imageId;

    public ImageList(Activity context, Integer[] imageId) {
        super(context, R.layout.image_list, imageId);
        this.context = context;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View elementView = inflater.inflate(R.layout.image_list, null, true);
        ImageView imageView = (ImageView) elementView.findViewById(R.id.image);
        imageView.setImageBitmap(BitmapMethods.decodeSampledBitmapFromResource(
                context.getResources(), (int) imageId[position], 200, 200));
        return elementView;
    }

}
