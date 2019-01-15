package com.mobileaviationtools.nav_fly.Route.HeightMap;


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
    private RoutePoints routePoints;
    private ImageButton closeBtn;
    private TextView heightMapNameText;
    private ConstraintLayout heightMapBaseLayout;
    public HeightMapFragmentEvents heightMapFragmentEvents;
    public ImageView heightMapImage;
    public Integer imageWidth;
    public Integer imageHeight;

    public void setupHeightMap(final GlobalVars vars, HeightMapFragmentEvents heightMapFragmentEvents, boolean parseFromService)
    {
        this.vars = vars;

        this.heightMapFragmentEvents = heightMapFragmentEvents;

        imageHeight = heightMapImage.getMeasuredHeight();
        imageWidth = heightMapImage.getMeasuredWidth();

        this.vars.route.setupRoutePoints(new RoutePoints.RoutePointsEvents() {
            @Override
            public void OnPointsLoaded(RoutePoints routePoints) {
                final Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                draw(canvas, routePoints);

                HeightMapFragment.this.vars.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        heightMapImage.setImageBitmap(bitmap);
                    }
                });

                // Test to check the routepoints..
                //vars.route.drawRoutePoints();

            }
        }, parseFromService);
    }

    private void draw(Canvas canvas, RoutePoints routePoints)
    {
        double maxAltitude = routePoints.getMaxAltitude().altitude;
        double maxAltitudePlus = maxAltitude + (maxAltitude * .15);
        double minElevation = routePoints.getMinElevation().elevation;

        drawHeightLines(minElevation, maxAltitudePlus, canvas);

        //double addX = (double)imageWidth / (double)routePoints.size();

        Paint pLine = new Paint();
        pLine.setColor(Color.GREEN);
        pLine.setStrokeWidth(10);

        double startX = 0;
        double startY = calcYFrom(routePoints.get(0).elevation, minElevation, maxAltitudePlus);
        for (ExtCoordinate c: routePoints) {
            double addX = (double)imageWidth / (double)routePoints.size();
            double nextX = startX + addX;
            double nextY = calcYFrom(c.elevation, minElevation, maxAltitudePlus);
            canvas.drawLine((float)startX, (float)startY, (float)nextX, (float)nextY, pLine);
            startX = nextX;
            startY = nextY;
        }

        pLine.setColor(Color.BLUE);

        startX = 0;
        startY = calcYFrom(routePoints.get(0).altitude, minElevation, maxAltitudePlus);
        for (ExtCoordinate c: routePoints) {
            double addX = (c.distanceToNext_meter==0) ? 0 :
                    ((double)imageWidth / (double)routePoints.getTotalDistance_meter()) * c.distanceToNext_meter;
            double nextX = startX + addX;
            double nextY = calcYFrom(c.altitude, minElevation, maxAltitudePlus);
            canvas.drawLine((float)startX, (float)startY, (float)nextX, (float)nextY, pLine);
            startX = nextX;
            startY = nextY;
        }
    }

    private void drawHeightLines(double minElevation, double maxAltitude, Canvas canvas)
    {
        double drawAltitude = 0;
        Paint p = new Paint();
        p.setStrokeWidth(2);
        p.setTextSize(20);

        while (drawAltitude<maxAltitude)
        {
            double Y = calcYFrom(drawAltitude, minElevation, maxAltitude);
            p.setColor(Color.GRAY);
            canvas.drawLine(0, (float)Y, (float)imageWidth, (float)Y, p);
            p.setColor(Color.BLACK);
            canvas.drawText(Double.toString(drawAltitude), 10, (float)Y-2, p);
            drawAltitude = drawAltitude + 500;
        }

        double Y = calcYFrom(drawAltitude, minElevation, maxAltitude);
        p.setColor(Color.GRAY);
        canvas.drawLine(0, (float)Y, (float)imageWidth, (float)Y, p);
        p.setColor(Color.BLACK);
        canvas.drawText(Double.toString(drawAltitude), 10, (float)Y-2, p);

    }

    private double calcYFrom(double value, double min, double max)
    {
        double size = max - min;
        double factor = (imageHeight) / size;
        double retValue = factor * value;
        return imageHeight - (retValue + 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view =  inflater.inflate(R.layout.fragment_height_map, container, false);

        heightMapBaseLayout = (ConstraintLayout) view.findViewById(R.id.heightMapBaseLayout);

        heightMapImage = (ImageView) view.findViewById(R.id.heightMapImageView);

        heightMapNameText = (TextView) view.findViewById(R.id.heightmapNameText);
        closeBtn = (ImageButton) view.findViewById(R.id.closeHeightMapDialogBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heightMapBaseLayout.setVisibility(View.INVISIBLE);
                if (heightMapFragmentEvents != null) heightMapFragmentEvents.OnCloseBtnClicked();
            }
        });

        heightMapBaseLayout.setVisibility(View.INVISIBLE);

        return view;
    }

    public void setVisibility(int visibility)
    {
        heightMapBaseLayout.setVisibility(visibility);
    }
}
