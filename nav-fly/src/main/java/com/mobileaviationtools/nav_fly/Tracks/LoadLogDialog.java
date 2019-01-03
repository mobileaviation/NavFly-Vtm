package com.mobileaviationtools.nav_fly.Tracks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.mobileaviationtools.airnavdata.AirnavTracklogDatabase;
import com.mobileaviationtools.airnavdata.Entities.TrackLog;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import java.util.List;

public class LoadLogDialog {
    public interface LoadLog
    {
        public void LogSelected(Long TrackLogId);
    }


    private AlertDialog.Builder builder;

    public LoadLogDialog(GlobalVars vars, View view, final LoadLog loadLog)
    {
        builder = new AlertDialog.Builder(vars.context);
        builder.setTitle("Open Previous Flight Logs.");
        View viewInflated = LayoutInflater.from(vars.context).inflate(R.layout.list_input, (ViewGroup) view, false);
        final ListView logList = (ListView) viewInflated.findViewById(R.id.list);
        AirnavTracklogDatabase db = AirnavTracklogDatabase.getInstance(vars.context);
        final List<TrackLog> logs = db.getTrackLog().getTracklogs();
        TrackLogAdapter trackLogAdapter = new TrackLogAdapter(logs, vars);
        logList.setAdapter(trackLogAdapter);
        logList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                logList.setItemChecked(i, true);
            }
        });
        builder.setView(viewInflated);
        builder.setIcon(android.R.drawable.ic_input_get);
        builder.setMessage("Select Log to display!");
        builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int pos = logList.getCheckedItemPosition();

                if (loadLog != null) loadLog.LogSelected(logs.get(pos).id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
    }

    public void show()
    {
        builder.show();
    }
}
