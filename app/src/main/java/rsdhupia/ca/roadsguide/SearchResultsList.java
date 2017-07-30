package rsdhupia.ca.roadsguide;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultsList extends AppCompatActivity {

    ListView listView;
    CustomAdapter adapter;
    String searchedStr;
    boolean isMajorChecked;
    boolean isModChecked;
    boolean isMinorChecked;
    public Restriction selectedRestriction;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_list);

        context = this;
        listView = (ListView) findViewById(R.id.listView);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final ArrayList<Restriction> restrictionsReceived = (ArrayList<Restriction>)bundle.getSerializable("Restrictions");
        searchedStr = intent.getStringExtra("location");
        isMajorChecked = intent.getBooleanExtra("majorCB", true);
        isModChecked = intent.getBooleanExtra("modCB", true);
        isMinorChecked = intent.getBooleanExtra("minorCB", true);
        TextView searchedLocation = (TextView) findViewById(R.id.textView_searchedLocation);
        if(searchedStr.equalsIgnoreCase("") || searchedStr.equalsIgnoreCase(" "))
            searchedStr = "Current Location";
        searchedLocation.setText(searchedStr);

        adapter = new CustomAdapter(this, restrictionsReceived);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("RESTRICTIONS", restrictionsReceived.size() + "; Position: " + position );
                selectedRestriction = new Restriction();
                if(restrictionsReceived != null)
                    selectedRestriction = restrictionsReceived.get(position);

                Log.d("SELECTED", selectedRestriction.getRoadAffected());

                Intent intentDetail = new Intent(context, ClosureDetailsActivity.class);
                intentDetail.putExtra("Selected", selectedRestriction);
                context.startActivity(intentDetail);
            }
        });
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
            Intent intent = new Intent(this, SearchRestrictionsActivity.class);
            this.startActivity(intent);
            return true;
        }
        else if ( id == R.id.action_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("location", searchedStr);
            intent.putExtra("majorCB", isMajorChecked);
            intent.putExtra("modCB", isModChecked);
            intent.putExtra("minorCB", isMinorChecked);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
