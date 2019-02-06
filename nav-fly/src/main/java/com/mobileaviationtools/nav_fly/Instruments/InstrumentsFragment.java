package com.mobileaviationtools.nav_fly.Instruments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstrumentsFragment extends Fragment {

    public InstrumentsFragment() {
        // Required empty public constructor

        this.instrumentsVisible = true;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initInstruments();
    }

    private boolean instrumentsVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instruments, container, false);



        return view;
    }

    public Integer ToggleVisibility()
    {
        Integer visibility = (this.instrumentsVisible)? View.GONE : View.VISIBLE;
        return SetVisibility(visibility);
    }

    public Integer SetVisibility(Integer Visibility)
    {
        this.instrumentsVisible = (Visibility==View.VISIBLE);
        getView().setVisibility(Visibility);
        return Visibility;
    }

    public void setLocation(FspLocation location)
    {
        if (instrumentsVisible) {
            setCompass(location.getBearing());
            setAirspeed(location.getSpeed());
            setAltimeter(location.getAltitude());
            setVsi(location.verticalSpeed);
            setTurnCoordinator(location.turnCoordination[0], location.turnCoordination[1]);
            setHorizon(location.horizon[0], location.horizon[1]);
        }
    }

    private void initInstruments()
    {
        if (instrumentsVisible) {
            setCompass(90);
            setAirspeed(0);
            setAltimeter(0);
            setVsi(0);
            setTurnCoordinator(0, 0);
            setHorizon(0, 0);
        }
    }

    private void setCompass(double Heading)
    {
        if (instrumentsVisible) {
            CompassView c = (CompassView) getView().findViewById(R.id.compassView);
            c.setHeading((float) Heading);
        }
    }

    private void setAirspeed(double Speed)
    {
        if (instrumentsVisible)
        {
            AirspeedView s = (AirspeedView) getView().findViewById(R.id.airspeedView);
            s.setSpeed((float) Speed);
        }
    }

    private void setAltimeter(double height)
    {
        if (instrumentsVisible) {
            AltimeterView a = (AltimeterView) getView().findViewById(R.id.altimeterView);
            a.setHeight((float)height);
        }
    }

    private void setVsi(double speed)
    {
        if (instrumentsVisible) {
            VerticalSpeedIndicatorView v = (VerticalSpeedIndicatorView) getView().findViewById(R.id.vsiView);
            v.setSpeed((float)speed);
        }
    }

    private void setTurnCoordinator(double turn, double ball)
    {
        if (instrumentsVisible) {
            TurnCoordinatorView t = (TurnCoordinatorView) getView().findViewById(R.id.turnCoordinatorView);
            t.setTurnCoordinator((float)turn, (float)ball);
        }
    }

    private void setHorizon(double bank, double pitch)
    {
        if (instrumentsVisible) {
            HorizonView h = (HorizonView) getView().findViewById(R.id.horizonView);
            h.setHorizon((float)bank, (float)pitch);
        }
    }
}
