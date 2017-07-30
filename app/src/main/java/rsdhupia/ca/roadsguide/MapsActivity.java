package rsdhupia.ca.roadsguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<Restriction> restrictionsList;
    ArrayList<Restriction> restrictionsSelected;
    private double currentLatitude;
    private double currentLongitude;

    CheckBox majorCheck;
    CheckBox moderateCheck;
    CheckBox minorCheck;

    EditText locEditText;
    String adrSearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        restrictionsList = new ArrayList<>();


        locEditText = (EditText) findViewById(R.id.editText_address);
        adrSearched = "";

        // Initialize CheckBoxes
        majorCheck = (CheckBox) findViewById(R.id.checkBox4);
        moderateCheck = (CheckBox) findViewById(R.id.checkBox5);
        minorCheck = (CheckBox) findViewById(R.id.checkBox6);


        setUpMapIfNeeded();
        new AccessWebServiceTask().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_restrictions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            // Home Menu Selection
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            return true;
        }
        else if ( id == R.id.action_about ) {
            // Instantiate Alert Dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.about_body).setTitle(R.string.about_title);

            // set positive button: Yes message
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
        else if ( id == R.id.action_help) {
            // Help Menu Selection
            Intent intent = new Intent(this, HelpActivity.class);
            this.startActivity(intent);
            return true;
        }
        else if ( id == R.id.action_search) {
            if(restrictionsSelected != null) {
                if (restrictionsSelected.size() > 0) {
                    // Passing list of objects to new activity through Bundle and Intent
                    // http://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(this, SearchResultsList.class);
                    bundle.putSerializable("Restrictions", restrictionsSelected);
                    intent.putExtras(bundle);
                    intent.putExtra("location", adrSearched );
                    this.startActivity(intent);
                }
            }
            else {
                Intent intent = new Intent(this, SearchRestrictionsActivity.class);
                this.startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /* Method called when search button is clicked. EditText can have an address to search
       or can be blank. If left blank, area around current location will be searched for
       restrictions
    */
    public void onMapSearch(View view) {
        Log.d("BUTTON", "MapsActivity onMapSearch()");
        mMap.setMyLocationEnabled(false);
        mMap.clear();
        restrictionsSelected = new ArrayList<>();

        // Hide Keyboard: http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Fetch text value
        adrSearched = locEditText.getText().toString();
        Log.d("EditText", adrSearched);

        List<Address> addresses = null;
        Address address = null;

        double latitude;
        double longitude;

        if (!adrSearched.equals("")) {

            Geocoder geocoder = new Geocoder(this);

            try {
                addresses = geocoder.getFromLocationName(adrSearched, 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            address = addresses.get(0);
            latitude = address.getLatitude();
            longitude = address.getLongitude();
            Log.d("BUTTON", "onMapSearch() Lat: " + latitude);
        }
        else
        {
            latitude = currentLatitude;
            longitude = currentLongitude;
        }

        Log.d("BUTTON", "onMapSearch(): " + restrictionsList.size());
            if(restrictionsList != null && restrictionsList.size() > 0 )
            {
                for (int i = 0; i < restrictionsList.size(); i++)
                {

                    Log.d("BUTTON", "onMapSearch() LOOP");
                    final Restriction tempRestric = restrictionsList.get(i);
                    float[] results = new float[1];


                    Location.distanceBetween(latitude, longitude, tempRestric.getLatitude(), tempRestric.getLongitude(), results);
                    float distanceInMeters = results[0];
                    ArrayList<Marker> markers = new ArrayList<>();
                    Marker marker = null;


                    // Restrictions within 3Km
                    if (distanceInMeters < 3000)
                    {
                        Log.d("Distance", String.valueOf(i) + ") " + String.valueOf(distanceInMeters) + " " + tempRestric.getRoadAffected() + "; Major: " + majorCheck.isChecked() + "; Moderate: " +  moderateCheck.isChecked() +  "; Minor: " + minorCheck.isChecked());
                        LatLng coordinates = new LatLng( tempRestric.getLatitude(), tempRestric.getLongitude());
                        if( tempRestric.getImpact().equalsIgnoreCase("Major") && majorCheck.isChecked() ) {
                            Log.d("Distance", "Major");
                            marker = mMap.addMarker(new MarkerOptions().position(coordinates).title(tempRestric.getRoadAffected()).snippet(tempRestric.getRoadAffected()).icon(BitmapDescriptorFactory.fromResource(R.drawable.major)));
                            restrictionsSelected.add(tempRestric);
                        }
                        else if( tempRestric.getImpact().equalsIgnoreCase("Moderate") && moderateCheck.isChecked()) {
                            Log.d("Distance", "Moderate");
                            marker = mMap.addMarker(new MarkerOptions().position(coordinates).title(tempRestric.getRoadAffected()).snippet(tempRestric.getRoadAffected()).icon(BitmapDescriptorFactory.fromResource(R.drawable.moderate)));
                            restrictionsSelected.add(tempRestric);
                        }
                        else if( tempRestric.getImpact().equalsIgnoreCase("Minor") && minorCheck.isChecked() ) {
                                Log.d("Distance", "Minor");
                                marker = mMap.addMarker(new MarkerOptions().position(coordinates).title(tempRestric.getRoadAffected()).snippet(tempRestric.getRoadAffected()).icon(BitmapDescriptorFactory.fromResource(R.drawable.minor)));
                                restrictionsSelected.add(tempRestric);
                        }

                        Log.d("LIST", String.valueOf(restrictionsSelected.size()));

                        Log.d("Distance", tempRestric.getRoadAffected());

                        // Setting a custom info window adapter for the google map
                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            // Use default InfoWindow frame
                            @Override
                            public View getInfoWindow(Marker arg0) {
                                Log.d("Adapter", "getInfoWindow");
                                return null;
                            }

                            // Defines the contents of the InfoWindow
                            @Override
                            public View getInfoContents(Marker arg0) {
                                Log.d("Adapter", String.valueOf(arg0.getPosition().latitude));
                                // Getting view from the layout file info_window_layout
                                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                                Restriction temp = new Restriction();
                                // Getting the position from the marker
                                double latitude = arg0.getPosition().latitude;
                                double longitude = arg0.getPosition().longitude;

                                for (int i = 0; i < restrictionsSelected.size(); i++) {
                                    if (restrictionsSelected.get(i).getLatitude() == latitude && restrictionsSelected.get(i).getLongitude() == longitude) {
                                        temp = restrictionsSelected.get(i);
                                    }
                                }

                                // Getting reference to the TextView to set latitude
                                TextView tv2 = (TextView) v.findViewById(R.id.textView2);
                                TextView tv3 = (TextView) v.findViewById(R.id.textView3);
                                TextView tv4 = (TextView) v.findViewById(R.id.textView4);
                                TextView tv5 = (TextView) v.findViewById(R.id.textView5);
                                TextView tv7 = (TextView) v.findViewById(R.id.textView7);
                                TextView tv8 = (TextView) v.findViewById(R.id.textView8);
                                TextView tv9 = (TextView) v.findViewById(R.id.textView9);
                                TextView tv10 = (TextView) v.findViewById(R.id.textView10);

                                Log.d("Adapter", temp.getRoadAffected());
                                tv2.setText(temp.getRoadAffected());
                                tv3.setText(temp.getDescription());
                                tv4.setText(temp.getWorkZone());
                                tv5.setText(temp.getRoadClass());
                                tv7.setText(temp.getTimestampStart());
                                tv8.setText(temp.getTimestampEnd());
                                tv9.setText(temp.getPlanned());
                                tv10.setText(temp.getImpact());

                                // Returning the view containing InfoWindow contents
                                return v;

                            }
                        });

                        markers.add(marker);

                        /*
                            Change camera position when marker is clicked to fit info window
                            http://stackoverflow.com/questions/16764002/how-to-center-the-camera-so-that-marker-is-at-the-bottom-of-screen-google-map/16764140#16764140
                         */
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                Log.d("EVENT", String.valueOf(marker.getPosition().latitude));
                                int yMatrix = 200, xMatrix = 40;

                                DisplayMetrics metrics1 = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(metrics1);
                                switch (metrics1.densityDpi) {
                                    case DisplayMetrics.DENSITY_LOW:
                                        yMatrix = 80;
                                        xMatrix = 20;
                                        break;
                                    case DisplayMetrics.DENSITY_MEDIUM:
                                        yMatrix = 100;
                                        xMatrix = 25;
                                        break;
                                    case DisplayMetrics.DENSITY_HIGH:
                                        yMatrix = 150;
                                        xMatrix = 30;
                                        break;
                                    case DisplayMetrics.DENSITY_XHIGH:
                                        yMatrix = 200;
                                        xMatrix = 40;
                                        break;
                                    case DisplayMetrics.DENSITY_XXHIGH:
                                        yMatrix = 200;
                                        xMatrix = 50;
                                        break;
                                }

                                Projection projection = mMap.getProjection();
                                LatLng latLng = marker.getPosition();
                                Point point = projection.toScreenLocation(latLng);
                                Point point2 = new Point(point.x + xMatrix, point.y - yMatrix);

                                LatLng point3 = projection.fromScreenLocation(point2);
                                CameraUpdate zoom1 = CameraUpdateFactory.newLatLng(point3);
                                mMap.animateCamera(zoom1);

                                if( !(marker.getTitle().equalsIgnoreCase("Searched Location")) )
                                    marker.showInfoWindow();

                                return true;
                            }
                        });

                    }
                }
            }
            LatLng latLng = new LatLng(latitude, longitude);
            // Set Marker
            mMap.addMarker(new MarkerOptions().position(latLng).title("Searched Location").snippet("Hello").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            // Focus camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));



    }

    // Go to current location whenever button is clicked
    public void goToCurrent(View view) {
        setUpMap();
    }

    private void setUpMapIfNeeded() {
       // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    // Sets map location
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.clear();
        mMap.setMyLocationEnabled(true);                    // Current Location
        if (mMap != null) {


            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub

                    LatLng latLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    currentLatitude = arg0.getLatitude();
                    currentLongitude = arg0.getLongitude();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("It's Me!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    // Focus camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)) ;
                }
            });

        }

    }

    // Open Http Connection : code from MyNetworkingText example
    private InputStream OpenHttpConnection(String urlString)
            throws IOException {
        Log.d("METHOD", "OpenHttpConnection()");
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private ArrayList<Restriction> GetRestrictions() {

        Log.d("METHOD", "GetRestrictions");
        InputStream in = null;
        ArrayList<Restriction> conditions = new ArrayList<>();
        Restriction restric = null;
        String text = "";

        try {
            in = OpenHttpConnection("http://www1.toronto.ca/transportation/roadrestrictions/RoadRestrictions.xml");
            XmlPullParserFactory factory = null;
            XmlPullParser parser = null;

            try {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                parser = factory.newPullParser();

                parser.setInput(in, null);
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    String tagName = parser.getName();

                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (tagName.equalsIgnoreCase("Closure")) {
                                restric = new Restriction();
                            }
                            break;

                        case XmlPullParser.TEXT: {
                            text = parser.getText();
                        }
                        break;
                        case XmlPullParser.END_TAG:

                            if (tagName.equalsIgnoreCase("Closure")) {
                                if( restric != null ) {
                                    if (restric.getRoadClass().equalsIgnoreCase("Major Arterial") || restric.getRoadClass().equalsIgnoreCase("Expressway") || restric.getSeverity().equalsIgnoreCase("High"))
                                        restric.setImpact("Major");
                                    else if (restric.getRoadClass().equalsIgnoreCase("Minor Arterial") || restric.getRoadClass().equalsIgnoreCase("Collector"))
                                        restric.setImpact("Moderate");
                                    else
                                        restric.setImpact("Minor");
                                }
                                conditions.add(restric);
                            }
                            if( text.charAt(0)!='\n' ) {
                                if (tagName.equalsIgnoreCase("Id"))
                                    restric.setId(text);
                                else if (tagName.equalsIgnoreCase("Road"))
                                    restric.setRoadAffected(text);
                                else if (tagName.equalsIgnoreCase("Name"))
                                    restric.setWorkZone(text);
                                else if (tagName.equalsIgnoreCase("District"))
                                    restric.setDistrict(text);
                                else if (tagName.equalsIgnoreCase("Latitude"))
                                    restric.setLatitude(Double.parseDouble(text));
                                else if (tagName.equalsIgnoreCase("Longitude"))
                                    restric.setLongitude(Double.parseDouble(text));
                                else if (tagName.equalsIgnoreCase("RoadClass"))
                                    restric.setRoadClass(text);
                                else if (tagName.equalsIgnoreCase("Planned"))
                                    restric.setPlanned(Integer.parseInt(text));
                                else if (tagName.equalsIgnoreCase("SeverityOverride"))
                                    restric.setSeverity(Integer.parseInt(text));
                                else if (tagName.equalsIgnoreCase("Source"))
                                    restric.setSource(text);
                                else if (tagName.equalsIgnoreCase("LastUpdated"))
                                    restric.setTimestampLastUpdated(Long.parseLong(text));
                                else if (tagName.equalsIgnoreCase("StartTime"))
                                    restric.setTimestampStart(Long.parseLong(text));
                                else if (tagName.equalsIgnoreCase("EndTime"))
                                    restric.setTimestampEnd(Long.parseLong(text));
                                else if (tagName.equalsIgnoreCase("WorkPeriod"))
                                    restric.setWorkPeriod(text);
                                else if (tagName.equalsIgnoreCase("Expired"))
                                    restric.setExpired(Integer.parseInt(text));
                                else if (tagName.equalsIgnoreCase("Signing"))
                                    restric.setSigning(text);
                                else if (tagName.equalsIgnoreCase("Notification"))
                                    restric.setNotification(text);
                                else if (tagName.equalsIgnoreCase("WorkEventType"))
                                    restric.setWorkEventType(text);
                                else if (tagName.equalsIgnoreCase("Contractor"))
                                    restric.setContractor(text);
                                else if (tagName.equalsIgnoreCase("PermitType"))
                                    restric.setPermitType(text);
                                else if (tagName.equalsIgnoreCase("Description"))
                                    restric.setDescription(text);
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return conditions;
    }
    /****************** ASYNC TASK *******************/
    private class AccessWebServiceTask extends AsyncTask<Void, Void, ArrayList<Restriction>> {

        @Override
        protected ArrayList<Restriction> doInBackground(Void...params) {
            Log.d("ASYNC TASK", "MapsActivity: doInBg()");
            return GetRestrictions();
        }

        @Override
        protected void onPostExecute(ArrayList<Restriction> restrictions) {
            Log.d("ASYNC TASK", "MapsActivity: onPostExec()");
            restrictionsList = restrictions;
            Log.d("ASYNC TASK", "MapsActivity: onPostExec(): " + restrictionsList.size());

            Intent intent = getIntent();
            if( intent != null) {
                Log.d("INTENT", "getExtra()");
                String searched = intent.getStringExtra("location");

                if( searched != null ) {
                    if( !searched.equalsIgnoreCase("") ) {
                        locEditText.setText(searched);
                        majorCheck.setChecked(intent.getBooleanExtra("majorCB", true));
                        moderateCheck.setChecked(intent.getBooleanExtra("modCB", true));
                        minorCheck.setChecked(intent.getBooleanExtra("minorCB", true));
                        View view = getCurrentFocus();

                        Log.d("onCreate()", "MapsActivity: RestricList Size: " + restrictionsList.size());
                        onMapSearch(view);
                    }
                }
            }
        }
    }

    /**************** / ASYNC TASK *****************/


}
