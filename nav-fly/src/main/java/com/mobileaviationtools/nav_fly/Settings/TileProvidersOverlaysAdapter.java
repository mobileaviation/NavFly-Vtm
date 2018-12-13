package com.mobileaviationtools.nav_fly.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviders;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Settings.Providers.OnlineTileProviderSet;

import org.w3c.dom.Text;

import java.util.List;

public class TileProvidersOverlaysAdapter extends BaseAdapter {
    public TileProvidersOverlaysAdapter(List<OnlineTileProviderSet> providers)
    {
        this.providerSets = providers;
    }

    private List<OnlineTileProviderSet> providerSets;
    private View view;

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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.onlinetile_providers_item, parent, false);

        final OnlineTileProviderSet onlineTileProviderSet = providerSets.get(position);

        TextView nameText = (TextView) view.findViewById(R.id.onlineTileProviderNameText);
        nameText.setText(onlineTileProviderSet.getName());

        CheckBox activeBox = (CheckBox) view.findViewById(R.id.onlineTileProviderActiveCheckbpx);
        activeBox.setChecked(onlineTileProviderSet.active);
        activeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onlineTileProviderSet.active = isChecked;
                onlineTileProviderSet.FireChangeEvent(OnlineTileProviderSet.ChangeType.active);
            }
        });

        CheckBox cacheBox = (CheckBox) view.findViewById(R.id.onlineTileProviderCacheCheckBox);
        cacheBox.setChecked(onlineTileProviderSet.cache);
        cacheBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onlineTileProviderSet.cache = isChecked;
                onlineTileProviderSet.FireChangeEvent(OnlineTileProviderSet.ChangeType.cache);
            }
        });

        return view;
    }
}
