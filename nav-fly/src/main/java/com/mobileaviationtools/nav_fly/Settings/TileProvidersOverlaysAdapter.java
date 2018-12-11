package com.mobileaviationtools.nav_fly.Settings;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviders;
import com.mobileaviationtools.nav_fly.Settings.Providers.OnlineTileProviderSet;

import java.util.List;

public class TileProvidersOverlaysAdapter extends BaseAdapter {
    public TileProvidersOverlaysAdapter(List<OnlineTileProviderSet> providers)
    {
        this.providerSets = providers;
    }

    private List<OnlineTileProviderSet> providerSets;

    @Override
    public int getCount() {
        return providerSets.size();
    }

    @Override
    public Object getItem(int position) {
        return providerSets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
