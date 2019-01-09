package com.mobileaviationtools.nav_fly.Route;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import org.oscim.layers.vector.geometries.Drawable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeightMapFragment extends Fragment {
    public interface HeightMapFragmentEvents
    {
        public void OnCloseBtnClicked();
    }

    public HeightMapFragment() {
        // Required empty public constructor
    }

    private View view;
    private GlobalVars vars;
    private ImageButton closeBtn;
    private TextView heightMapNameText;
    private ConstraintLayout heightMapBaseLayout;
    public HeightMapFragmentEvents heightMapFragmentEvents;
    public ImageView heightMapImage;

    public void setupHeightMap(GlobalVars vars, HeightMapFragmentEvents heightMapFragmentEvents)
    {
        this.vars = vars;
        this.heightMapFragmentEvents = heightMapFragmentEvents;

        int height = heightMapImage.getMeasuredHeight();
        int width = heightMapImage.getMeasuredWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        draw(canvas, width, height);

        heightMapImage.setImageBitmap(bitmap);
    }

    private void draw(Canvas canvas, int width, int height)
    {
        Paint pText = new Paint();
        pText.setColor(Color.BLACK);
        pText.setStrokeWidth(10);

        canvas.drawLine(0,height-20, width, height-20, pText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view =  inflater.inflate(R.layout.fragment_height_map, container, false);

        heightMapBaseLayout = (ConstraintLayout) view.findViewById(R.id.heightMapBaseLayout);
        heightMapBaseLayout.setVisibility(View.GONE);

        heightMapImage = (ImageView) view.findViewById(R.id.heightMapImageView);

        heightMapNameText = (TextView) view.findViewById(R.id.heightmapNameText);
        closeBtn = (ImageButton) view.findViewById(R.id.closeHeightMapDialogBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heightMapBaseLayout.setVisibility(View.GONE);
                if (heightMapFragmentEvents != null) heightMapFragmentEvents.OnCloseBtnClicked();
            }
        });

        return view;
    }

    public void setVisibility(int visibility)
    {
        heightMapBaseLayout.setVisibility(visibility);
    }
}
