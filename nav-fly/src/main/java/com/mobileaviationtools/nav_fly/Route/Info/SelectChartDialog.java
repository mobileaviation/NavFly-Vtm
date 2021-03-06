package com.mobileaviationtools.nav_fly.Route.Info;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.nav_fly.Classes.FileHelpers;
import com.mobileaviationtools.nav_fly.Classes.Kml;
import com.mobileaviationtools.nav_fly.Classes.MBTilePreview;
import com.mobileaviationtools.nav_fly.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import static com.mobileaviationtools.nav_fly.Menus.MenuItemType.loadCharts;

public class SelectChartDialog extends DialogFragment {
    public SelectChartDialog()
    {
        super();
    }

    public static SelectChartDialog getInstance(Context context, Airport selectedAirport)
    {
        SelectChartDialog settingsDialog = new SelectChartDialog();
        settingsDialog.context = context;
        settingsDialog.selectedAirport = selectedAirport;
        settingsDialog.basePath = context.getApplicationInfo().dataDir + "/charts/";
        return settingsDialog;
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener)
    {
        this.dismissListener = dismissListener;
    }
    private DialogInterface.OnDismissListener dismissListener;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dismissListener != null) dismissListener.onDismiss(dialog);
    }

    private Context context;
    private File selectedFile;
    private Airport selectedAirport;
    private View view;
    private String basePath;

    private EditText latSText;
    private EditText latNText;
    private EditText lonEText;
    private EditText lonWText;

    private Button saveBtn;
    private boolean saved;

    public boolean isSaved() {
        return saved;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
        saved = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.chart_assign_list, container);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialog);

        setup();
        setupButtons();
        setupLocationEditText();

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 50);
        getDialog().getWindow().setBackgroundDrawable(inset);

        return view;
    }

    private void setup()
    {
        final ListView filesList = (ListView) view.findViewById(R.id.list);
        ChartLoadItemAdapter chartLoadItemAdapter = new ChartLoadItemAdapter(getFiles(), context);
        filesList.setAdapter(chartLoadItemAdapter);
        filesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFile = (File)adapterView.getAdapter().getItem(i);
                ImageView chartImage = (ImageView) SelectChartDialog.this.getView().findViewById(R.id.chartImageView);
                LinearLayout nwLayout = (LinearLayout) SelectChartDialog.this.getView().findViewById(R.id.nwChartSelectLayout);
                LinearLayout seLayout = (LinearLayout) SelectChartDialog.this.getView().findViewById(R.id.seChartSelectLayout);

                if (selectedFile.getName().toLowerCase().endsWith("png") || selectedFile.getName().toLowerCase().endsWith("jpg"))
                {
                    saveBtn.setEnabled(chechChart());
                    Bitmap myBitmap = BitmapFactory.decodeFile(selectedFile.getAbsolutePath());
                    chartImage.setImageBitmap(myBitmap);
                    nwLayout.setVisibility(View.VISIBLE);
                    seLayout.setVisibility(View.VISIBLE);

                    String kmlFile = FileHelpers.replaceExtention(selectedFile.getAbsolutePath(),".kml");
                    if (new File(kmlFile).exists()) {
                        // Process kml file and set values in TextEdit boxes
                        processKmlFile(kmlFile);
                    }
                }

                if (selectedFile.getName().toLowerCase().endsWith("mbtiles"))
                {
                    nwLayout.setVisibility(View.GONE);
                    seLayout.setVisibility(View.GONE);
                    MBTilePreview preview = new MBTilePreview(selectedFile.getAbsolutePath());
                    Bitmap myBitmap = preview.GetBitmap();
                    chartImage.setImageBitmap(myBitmap);
                    saveBtn.setEnabled(true);
                }

                if (selectedFile.getName().toLowerCase().endsWith("pdf"))
                {
                    saveBtn.setEnabled(false);
                    chartImage.setImageDrawable(context.getResources().getDrawable(R.drawable.select_a_file));
                }

            }
        });
    }

    private void processKmlFile(String kmlFile)
    {
        try {
            Serializer serializer = new Persister();
            Kml resp = serializer.read(Kml.class, new File(kmlFile), false);

            latSText.setText(Double.toString(resp.groundOverlay.latLonBox.south));
            latNText.setText(Double.toString(resp.groundOverlay.latLonBox.north));
            lonEText.setText(Double.toString(resp.groundOverlay.latLonBox.east));
            lonWText.setText(Double.toString(resp.groundOverlay.latLonBox.west));
        }
        catch (Exception e)
        {
            int ii=0;
        }
    }

    private void setupButtons()
    {
        saveBtn = (Button) view.findViewById(R.id.saveChartAssignBtn);
        Button cancelBtn = (Button) view.findViewById(R.id.cancelChartAssignBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChart();
                saved = true;
                SelectChartDialog.this.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saved = false;
                SelectChartDialog.this.dismiss();
            }
        });
    }

    private void setupLocationEditText()
    {
        latSText = (EditText) view.findViewById(R.id.latitudeSText);
        latNText = (EditText) view.findViewById(R.id.latitudeNText);
        lonEText = (EditText) view.findViewById(R.id.longitudeEText);
        lonWText = (EditText) view.findViewById(R.id.longitudeWText);

        TextWatcher onLocEditChange = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean saveable = false;
                try {
                    saveable = chechChart();
                }
                catch (Exception ee)
                {
                    saveable = false;
                }

                if (selectedFile != null)
                    if (selectedFile.getName().toLowerCase().endsWith("mbtiles"))
                        saveable = true;

                saveBtn.setEnabled(saveable && (selectedFile != null));

            }
        };

        latNText.addTextChangedListener(onLocEditChange);
        latNText.addTextChangedListener(onLocEditChange);
        lonEText.addTextChangedListener(onLocEditChange);
        lonWText.addTextChangedListener(onLocEditChange);
    }

    private boolean chechChart()
    {
        try {
            Chart chart = new Chart();
            chart.latitude_deg_n = Double.parseDouble(latNText.getText().toString());
            chart.latitude_deg_s = Double.parseDouble(latSText.getText().toString());
            chart.longitude_deg_e = Double.parseDouble(lonEText.getText().toString());
            chart.longitude_deg_w = Double.parseDouble(lonWText.getText().toString());
            return chart.validate();
        }
        catch (Exception ee)
        {
            return false;
        }
    }

    private boolean setChart()
    {
        if (selectedFile != null)
        {
            if (selectedFile.getName().toLowerCase().endsWith("png") ||
                    selectedFile.getName().toLowerCase().endsWith("jpg"))
            {
                return setJpgPngChart();
            }
            if (selectedFile.getName().toLowerCase().endsWith("mbtiles"))
            {
                return setMbTilesChart();
            }

            return false;
        }
        return false;
    }

    private boolean setJpgPngChart()
    {
        try {
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] fileBytes = new byte[(int)selectedFile.length()];
            fis.read(fileBytes, 0, (int)selectedFile.length());
            Chart chart = new Chart();
            chart.filelocation = selectedFile.getAbsolutePath();
            chart.name = selectedFile.getName();
            chart.airport_ref = selectedAirport.id;
            chart.chart = fileBytes;
            chart.type = ChartType.getTypeByExtention(selectedFile);
            chart.latitude_deg_n = Double.parseDouble(latNText.getText().toString());
            chart.latitude_deg_s = Double.parseDouble(latSText.getText().toString());
            chart.longitude_deg_e = Double.parseDouble(lonEText.getText().toString());
            chart.longitude_deg_w = Double.parseDouble(lonWText.getText().toString());
            chart.active = false;

            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
            db.getCharts().InsertChart(chart);

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private boolean setMbTilesChart()
    {
        Chart chart = new Chart();
        chart.filelocation = basePath + selectedFile.getName();

        // Copy chart to "Charts" directory
        try {
            File dir = new File(basePath);
            if (!dir.exists()) dir.mkdir();
            FileHelpers.copy(selectedFile, new File(chart.filelocation));
        }
        catch (IOException ee)
        {
            return false;
        }

        chart.name = selectedFile.getName();
        chart.airport_ref = selectedAirport.id;
        chart.type = ChartType.mbtiles;
        chart.active = false;

        AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
        db.getCharts().InsertChart(chart);

        return true;
    }

    private File[] getFiles()
    {
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files =  downloadFolder.listFiles(new InfoLayout.MyFileNameFilter(new String[]{".png", ".pdf", ".mbtiles", ".jpg"}));
        return files;
    }
}
