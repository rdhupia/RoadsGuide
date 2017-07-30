package rsdhupia.ca.roadsguide;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Button Search Restrictions clicked
    public void openSearchForm(View view) {
        Intent intent = new Intent(this, SearchRestrictionsActivity.class);
        this.startActivity(intent);

    }

    public void openMapView(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
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

        return super.onOptionsItemSelected(item);
    }
}
