package com.mobileaviationtools.nav_fly.Route.HeightMap;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;

import org.oscim.core.GeoPoint;

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
        public void OnLocationChanged(FspLocation location);
        public void OnTrackLoaded(TrackPoints points);
    }

    public interface HeightMapOnLocationChanged
    {
        public void OnLocationChanged(FspLocation location);
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
    private TrackPoints trackPoints;

    private Button dragtestBtn;

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
        trackPoints = new TrackPoints(this.vars);
        trackPoints.SetupPoints(trackId, null);

        TrackHeightMapBitmap trackHeightMapBitmap = new TrackHeightMapBitmap(bitmap, this.vars);
        trackHeightMapBitmap.drawTrack(trackPoints);
        heightMapImage.setImageBitmap(bitmap);

        heightMapNameText.setText(trackPoints.getTrackLogName());
        pointsCount = trackPoints.size();
        dragtestBtn.setVisibility(View.VISIBLE);

        if (heightMapFragmentEvents != null) heightMapFragmentEvents.OnTrackLoaded(trackPoints);
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
                pointsCount = routePoints.size();

            }
        }, parseFromService);

        heightMapNameText.setText(vars.route.name);
        dragtestBtn.setVisibility(View.GONE);
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

        dragtestBtn = (Button) view.findViewById(R.id.dragTestBtn);
        setupDragBtn();
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

                heightMapBitmap = routeHeightMapBitmap.drawPositionOnTop(index, startIndex, endIndex, location.getAltitude());
                heightMapImage.setImageBitmap(heightMapBitmap);
            }
        }
    }

    private int pointsCount;
    private Long cpos = 0l;
    private int[] loc;
    private void setupDragBtn()
    {
        dragtestBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    loc = new int[2];
                    heightMapImage.getLocationOnScreen(loc);
                }
                if (event.getAction()==MotionEvent.ACTION_MOVE) {
                    int max = heightMapImage.getWidth() - loc[0] -10;
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)v.getLayoutParams();
                    int l = Math.round(event.getRawX()) - loc[0] - 10;
                    if (l<0) l=0;
                    if (l>max) l=max;
                    params.leftMargin = l;
                    v.setLayoutParams(params);

                    Long dpos = Math.round(((double)l / (double)max) * (double)pointsCount);
                    Log.i(TAG, "Position in points: " + dpos.toString());
                    if (dpos != cpos) {
                        cpos = dpos;
                        ExtCoordinate c = trackPoints.get(dpos.intValue());
                        FspLocation f = new FspLocation(new GeoPoint(c.y, c.x), "TrackLog");
                        f.setAltitude(c.altitude);
                        f.setBearing((float)c.heading);
                        f.setSpeed((float)c.speed);

                        if (heightMapFragmentEvents != null)
                            heightMapFragmentEvents.OnLocationChanged(f);
                    }
                }

                return false;
            }
        });
    }
}
