package rsdhupia.ca.roadsguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SearchRestrictionsActivity extends AppCompatActivity {

    private ArrayList<Restriction> restrictionsList;
    ArrayList<Restriction> restrictionsSelected;
    ArrayList<String> names;
    Spinner durationSpinner;
    CheckBox majorCheck;
    CheckBox moderateCheck;
    CheckBox minorCheck;
    ImageButton searchButton;
    Context context;
    AutoCompleteTextView locEditText;
    ArrayAdapter<String> arrayAdapter;

    private String spinnerContent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restrictions);

        new AccessWebServiceTask().execute();

        restrictionsList = new ArrayList<>();
        context = this;

        // Initialize widgets
        searchButton = (ImageButton)findViewById(R.id.imageButton_search);
        majorCheck = (CheckBox) findViewById(R.id.checkBox);
        moderateCheck = (CheckBox) findViewById(R.id.checkBox2);
        minorCheck = (CheckBox) findViewById(R.id.checkBox3);
        durationSpinner = (Spinner) findViewById(R.id.spinner_search);

        // Run background tasks: get all Restrictions from url
        new AccessWebServiceTask().execute();

        // Get dropdown item selected
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerContent = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerContent = getResources().getStringArray(R.array.durations)[0];
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String adrSearched = locEditText.getText().toString();

                if(!adrSearched.equalsIgnoreCase("")) {
                    searchRestrictionsList();

                    // Passing list of objects to new activity through Bundle and Intent
                    // http://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, SearchResultsList.class);
                    bundle.putSerializable("Restrictions", restrictionsSelected);
                    intent.putExtras(bundle);
                    intent.putExtra("location", adrSearched);
                    intent.putExtra("majorCB", majorCheck.isChecked());
                    intent.putExtra("modCB", moderateCheck.isChecked());
                    intent.putExtra("minorCB", minorCheck.isChecked());
                    context.startActivity(intent);
                }
            }
        });



    }

    public void searchRestrictionsList() {


        // Code for converting and comparing timestamps
        // http://stackoverflow.com/questions/6850874/how-to-create-a-java-date-object-of-midnight-today-and-midnight-tomorrow
        // today
        Calendar date = new GregorianCalendar();

        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Long timestamp = date.getTimeInMillis();
        Log.d("TIMESTAMP", "Current: " + String.valueOf(timestamp));
        Log.d("SPINNER", "Search for: " + spinnerContent);

        Long maxDuration, minDuration;
        if(spinnerContent.equalsIgnoreCase(getResources().getStringArray(R.array.durations)[0]))
        {
            minDuration = timestamp;
            maxDuration = timestamp + ( 24 * 60 * 60 * 1000);
            Log.d("TIMESTAMP", "TodayENd: " + String.valueOf(maxDuration));
        }
        else if (spinnerContent.equalsIgnoreCase(getResources().getStringArray(R.array.durations)[1]))
        {
            minDuration = timestamp + ( 24 * 60 * 60 * 1000);
            maxDuration = timestamp + ( 48 * 60 * 60 * 1000);
            Log.d("TIMESTAMP", "TomENd: " + String.valueOf(maxDuration));
        }
        else
        {
            minDuration = timestamp;
            maxDuration = timestamp + ( 7 * 24 * 60 * 60 * 1000);
            Log.d("TIMESTAMP", "WeekENd: " + String.valueOf(maxDuration));
        }


        restrictionsSelected = new ArrayList<>();
        locEditText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        String adrSearched = locEditText.getText().toString();
        Log.d("MultiEditText", adrSearched);

        List<Address> addresses = null;
        Address address = null;

        double latitude = 0;
        double longitude = 0;

        if (!adrSearched.equals("")) {

            Geocoder geocoder = new Geocoder(this);

            try {
                addresses = geocoder.getFromLocationName(adrSearched, 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            if( addresses != null ) {
                address = addresses.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();
            }
            else {
                Toast.makeText(context, "Try a differenent location", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(context, "Enter a valid location", Toast.LENGTH_LONG).show();
        }


        if(restrictionsList != null && restrictionsList.size() > 0 )
        {
            for (int i = 0; i < restrictionsList.size(); i++)
            {
                final Restriction tempRestric = restrictionsList.get(i);
                float[] results = new float[1];


                Location.distanceBetween(latitude, longitude, tempRestric.getLatitude(), tempRestric.getLongitude(), results);
                float distanceInMeters = results[0];

                // Restrictions within 3Km
                if ( (distanceInMeters < 3000) && (tempRestric.getTimeStampStartLong() <= maxDuration ) && (tempRestric.getTimeStampEndLong() >= minDuration))
                {
                    Log.d("Distance", String.valueOf(i) + ") " + String.valueOf(distanceInMeters) + " " + tempRestric.getRoadAffected() + "; Major: " + majorCheck.isChecked() + "; Moderate: " + moderateCheck.isChecked() + "; Minor: " + minorCheck.isChecked());

                    if( tempRestric.getImpact().equalsIgnoreCase("Major") && majorCheck.isChecked() ) {
                        Log.d("Distance", "Major");
                        restrictionsSelected.add(tempRestric);
                    }
                    else if( tempRestric.getImpact().equalsIgnoreCase("Moderate") && moderateCheck.isChecked()) {
                        Log.d("Distance", "Moderate");
                        restrictionsSelected.add(tempRestric);
                    }
                    else if( tempRestric.getImpact().equalsIgnoreCase("Minor") && minorCheck.isChecked() ) {
                            Log.d("Distance", "Minor");
                            restrictionsSelected.add(tempRestric);

                    }

                    Log.d("LIST", String.valueOf(restrictionsSelected.size()));

                    Log.d("Distance", tempRestric.getRoadAffected());


                }
            }
        }


    }

    /************* BACKGROUND TASKS *******************/

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
        names = new ArrayList<>();
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
                                if (restric.getRoadAffected() != null && (!restric.getRoadAffected().equalsIgnoreCase("")) ) {
                                    names.add(restric.getRoadAffected() + ", Toronto");
                                }
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
    /************* / BACKGROUND TASKS *******************/

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
            /* http://www.tutorialspoint.com/android/android_auto_complete.htm */
            locEditText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
            arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, names);

            // Set the adapter
            locEditText.setAdapter(arrayAdapter);
            locEditText.setThreshold(2);


        }
    }

    /**************** / ASYNC TASK *****************/



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
        else if ( id == R.id.action_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
