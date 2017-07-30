package rsdhupia.ca.roadsguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ClosureDetailsActivity extends AppCompatActivity {

    Restriction chosenRestriction;
    TextView tvRA;
    TextView tvDesc;
    TextView tvWZ;
    TextView tvRC;
    TextView tvImp;
    TextView tvST;
    TextView tvET;
    TextView tvCT;
    TextView tvId;
    TextView tvDist;
    TextView tvLU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closure_details);

        Intent intent = getIntent();
        if( intent != null )
        {
            Log.d("INTENT", "onCreate()");

            // http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
            chosenRestriction = (Restriction) intent.getSerializableExtra("Selected");
        }

        tvRA = (TextView) findViewById(R.id.textView_frag_roadAffected);
        tvDesc = (TextView) findViewById(R.id.textView_frag_descrip);
        tvWZ = (TextView) findViewById(R.id.textViewWorkZoneFrag);
        tvRC = (TextView) findViewById(R.id.textViewRoadClassFrag);
        tvImp = (TextView) findViewById(R.id.textViewImpactFrag);
        tvST = (TextView) findViewById(R.id.textViewStartTimeFrag);
        tvET = (TextView) findViewById(R.id.textViewEndTimeFrag);
        tvCT = (TextView) findViewById(R.id.textViewClosureTypeFrag);
        tvId = (TextView) findViewById(R.id.textViewIdFrag);
        tvDist = (TextView) findViewById(R.id.textViewDistrictFrag);
        tvLU = (TextView) findViewById(R.id.textViewLastUpdatedFrag);

        tvRA.setText(chosenRestriction.getRoadAffected());
        tvDesc.setText(chosenRestriction.getDescription());
        tvWZ.setText(chosenRestriction.getWorkZone());
        tvRC.setText(chosenRestriction.getRoadClass());
        tvRC.setText(chosenRestriction.getRoadClass());
        tvImp.setText(chosenRestriction.getImpact());
        tvST.setText(chosenRestriction.getTimestampStart());
        tvET.setText(chosenRestriction.getTimestampEnd());
        tvCT.setText("Construction - " + (chosenRestriction.getPlanned()));
        tvId.setText(chosenRestriction.getId());
        tvDist.setText(chosenRestriction.getDistrict());
        tvLU.setText(chosenRestriction.getTimestampLastUpdated());

    }

    public void goToList(View view) {
        onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_closure_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
