package com.mobileaviationtools.nav_fly.Route.HeightMap;

import android.graphics.Bitmap;
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
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeightMapFragment extends Fragment {
    private enum HeightMapType
    {
        routeHeightMap,
        trackHeightMap
    }

    public interface HeightMapFragmentEvents
    {
        public void OnCloseBtnClicked();
    }

    public HeightMapFragment() {
        // Required empty public constructor
    }

    private String TAG = "HeightMapFragment";

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
    private HeightMapType heightMapType;
    private RouteHeightMapBitmap routeHeightMapBitmap;

    private Bitmap heightMapBitmap;
    private double startIndex;
    private double endIndex;

    public void setupTrackHeightMap(GlobalVars vars, Long trackId)
    {
        this.vars = vars;
        heightMapType = HeightMapType.trackHeightMap;

        imageHeight = heightMapImage.getMeasuredHeight();
        imageWidth = heightMapImage.getMeasuredWidth();

        final Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        TrackPoints trackPoints = new TrackPoints(this.vars);
        trackPoints.SetupPoints(trackId, null);

        TrackHeightMapBitmap trackHeightMapBitmap = new TrackHeightMapBitmap(bitmap, this.vars);
        trackHeightMapBitmap.drawTrack(trackPoints);
        heightMapImage.setImageBitmap(bitmap);

        heightMapNameText.setText(trackPoints.getTrackLogName());
    }

    public void setupRouteHeightMap(final GlobalVars vars, HeightMapFragmentEvents heightMapFragmentEvents, boolean parseFromService)
    {
        this.vars = vars;
        heightMapType = HeightMapType.routeHeightMap;

        this.heightMapFragmentEvents = heightMapFragmentEvents;

        imageHeight = heightMapImage.getMeasuredHeight();
        imageWidth = heightMapImage.getMeasuredWidth();

        this.vars.route.setupRoutePoints(new RoutePoints.RoutePointsEvents() {
            @Override
            public void OnPointsLoaded(RoutePoints routePoints) {
                final Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
                routeHeightMapBitmap = new RouteHeightMapBitmap(bitmap, vars);
                routeHeightMapBitmap.drawRoute(routePoints);

                HeightMapFragment.this.vars.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        heightMapBitmap = bitmap;
                        heightMapImage.setImageBitmap(heightMapBitmap);
                    }
                });

                // Test to check the routepoints..
                //vars.route.drawRoutePoints();
                HeightMapFragment.this.routePoints = routePoints;
                HeightMapFragment.this.startIndex = routePoints.getStartIndex();
                HeightMapFragment.this.endIndex = routePoints.getEndIndex();

            }
        }, parseFromService);

        heightMapNameText.setText(vars.route.name);
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

    public void setLocation(FspLocation location)
    {
        if (heightMapType == HeightMapType.routeHeightMap)
        {
            if (routePoints != null)
            {
                double index = routePoints.getIndexForLocation(location);
                //Log.i(TAG, "Index on route line: " + index);
//                double mapXpos =  (((double)imageWidth) / (endIndex-startIndex)) * index;
//                Log.i(TAG, "X Position on heightmap: " + Double.toString(mapXpos));
//
//                Canvas canvas = new Canvas(heightMapBitmap);
//                Paint pLine = new Paint();
//                pLine.setColor(Color.GREEN);
//                pLine.setStrokeWidth(Helpers.dpToPx(20));
//
//                canvas.drawCircle((float)mapXpos, 100f, 5f, pLine);
//                heightMapImage.setImageBitmap(heightMapBitmap);

                heightMapBitmap = routeHeightMapBitmap.drawPositionOnTop(index, startIndex, endIndex, location.getAltitude());
                heightMapImage.setImageBitmap(heightMapBitmap);

            }
        }
    }
}
