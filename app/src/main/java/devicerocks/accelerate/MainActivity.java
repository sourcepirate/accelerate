package devicerocks.accelerate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;


public class MainActivity extends Activity implements LocationListener,
        OnClickListener {

    public LocationManager locationManager;
    private Button btnStart, btnStop, btnSave;

    private boolean started = false;

    public ArrayList<SpeedData>x = new ArrayList<SpeedData>();

    private RelativeLayout layout;

    private View mChart;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    @Override


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        this.layout = (RelativeLayout) findViewById(R.id.chart_container);

        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        this.btnStart = (Button) findViewById(R.id.btnStart);

        this.btnStop = (Button) findViewById(R.id.btnStop);

        this.btnSave = (Button) findViewById(R.id.btnSave);


        this.btnStart.setOnClickListener(this);

        this.btnStop.setOnClickListener(this);

        this.btnSave.setOnClickListener(this);

        this.btnStart.setEnabled(true);

        this.btnStop.setEnabled(false);

        if (this.x == null || this.x.size() == 0) {

            this.btnSave.setEnabled(false);

        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        this.client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override


    protected void onResume() {

        super.onResume();


    }


    @Override

    protected void onPause() {

        super.onPause();

        if (started == true) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            this.locationManager.removeUpdates(this);
        }
    }


    @Override


    public void onLocationChanged(Location location) {
        TextView txt = (TextView) this.findViewById(R.id.textView);
        Log.v("Location change", "triggered");
        System.out.println(this.started);

        if (this.started) {
            System.out.println(location);
            if (location == null) {
                txt.setText("-.- Km/hr");
                System.out.println(this.x);
            } else {
                double x_s = (location.getSpeed()) * 3.6f;

                txt.setText(x_s + " Km/hr");
                long timestamp = System.currentTimeMillis();

                SpeedData data = new SpeedData(timestamp, x_s);

                this.x.add(data);
            }

        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override


    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.btnStart:


                this.btnStart.setEnabled(false);

                this.btnStop.setEnabled(true);

                this.btnSave.setEnabled(false);


                // save prev data if available

                this.started = true;


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    return;
                }
                this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                this.onLocationChanged(null);
                long timestamp = System.currentTimeMillis();
                SpeedData data = new SpeedData(timestamp, 0);
                this.x.add(data);
                Log.v("inseting new data", data.toString());
                break;


            case R.id.btnStop:


                this.btnStart.setEnabled(true);

                this.btnStop.setEnabled(false);

                this.btnSave.setEnabled(true);

                this.started = false;

                this.locationManager.removeUpdates(this);

                this.layout.removeAllViews();

                openChart(this.x);


                // show data in chart

                break;


            case R.id.btnSave:


                break;


            default:

                break;


        }


    }


    private void openChart(ArrayList<SpeedData> x) {


        if (x != null || x.size() > 0) {


            long t = x.get(0).getTimestamp();

            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();


            XYSeries xSeries = new XYSeries("X");


            for (SpeedData data : x) {
                xSeries.add(data.getTimestamp() - t, data.getX());
            }


            dataset.addSeries(xSeries);


            XYSeriesRenderer xRenderer = new XYSeriesRenderer();

            xRenderer.setColor(Color.RED);

            xRenderer.setPointStyle(PointStyle.CIRCLE);

            xRenderer.setFillPoints(true);

            xRenderer.setLineWidth(1);

            xRenderer.setDisplayChartValues(false);


            XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

            multiRenderer.setXLabels(0);

            multiRenderer.setLabelsColor(Color.RED);

            multiRenderer.setChartTitle("t vs (x)");

            multiRenderer.setXTitle("Sensor Data");

            multiRenderer.setYTitle("Speed Km/hr");

            multiRenderer.setZoomButtonsVisible(true);


            for (int i = 0; i < x.size(); i++) {


                multiRenderer.addXTextLabel(i + 1, ""
                        + (x.get(i).getTimestamp() - t));


            }


            multiRenderer.addSeriesRenderer(xRenderer);


            // Getting a reference to LinearLayout of the MainActivity Layout

            // Creating a Line Chart

            this.mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
                    multiRenderer);


            // Adding the Line Chart to the LinearLayout

            this.layout.addView(this.mChart);


        }

    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://devicerocks.accelerate/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://devicerocks.accelerate/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
