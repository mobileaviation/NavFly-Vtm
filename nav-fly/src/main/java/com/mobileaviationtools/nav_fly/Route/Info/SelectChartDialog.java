package com.mobileaviationtools.nav_fly.Route.Info;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.mobileaviationtools.nav_fly.R;

import java.io.File;

public class SelectChartDialog extends DialogFragment {
    public SelectChartDialog()
    {
        super();
    }

    public static SelectChartDialog getInstance(Context context)
    {
        SelectChartDialog settingsDialog = new SelectChartDialog();
        settingsDialog.context = context;
        return settingsDialog;
    }

    private Context context;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_assign_list, container);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialog);

        setup(view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 50);
        getDialog().getWindow().setBackgroundDrawable(inset);

        return view;
    }

    private void setup(View view)
    {
        final ListView filesList = (ListView) view.findViewById(R.id.list);
        ChartLoadItemAdapter chartLoadItemAdapter = new ChartLoadItemAdapter(getFiles(), context);
        filesList.setAdapter(chartLoadItemAdapter);
        filesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File selectedFile = (File)adapterView.getAdapter().getItem(i);
                ImageView chartImage = (ImageView) SelectChartDialog.this.getView().findViewById(R.id.chartImageView);
                if (selectedFile.getName().endsWith("png") || selectedFile.getName().endsWith("jpg"))
                {
                    Bitmap myBitmap = BitmapFactory.decodeFile(selectedFile.getAbsolutePath());
                    chartImage.setImageBitmap(myBitmap);
                }
            }
        });
    }

    private File[] getFiles()
    {
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return downloadFolder.listFiles(new InfoLayout.MyFileNameFilter(new String[]{".png", ".pdf", ".mbtiles", ".jpg"}));
    }
}
