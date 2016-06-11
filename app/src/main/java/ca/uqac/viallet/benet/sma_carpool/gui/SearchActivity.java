package ca.uqac.viallet.benet.sma_carpool.gui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import ca.uqac.viallet.benet.sma_carpool.R;

public class SearchActivity extends AppCompatActivity {

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    private Vector departure_coord = new Vector(2);
    private Vector arrival_coord = new Vector(2);
    private int step = 1;
    private CharSequence confirmedResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final GridView gridView = (GridView) findViewById(R.id.map);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);

        assert gridView != null;
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                /* Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
                */
                // récupération des information de l'endroit selectionné
                int i = position / gridView.getNumColumns();
                int j = position % gridView.getNumColumns();
                Log.i("MAP-", "Position: " + i + "," + j);
                TextView resume = (TextView) findViewById(R.id.resume);
                if (step == 1) {
                    departure_coord.add(i);
                    departure_coord.add(j);
                    resume.setText("Départ de " + ((TextView) v).getText() + "(" + i + "," + j + ")");
                }
                else if (step == 2) {
                    arrival_coord.add(i);
                    arrival_coord.add(j);
                    resume.setText(confirmedResume + "\nArrivée à " + ((TextView) v).getText() + "(" + i + "," + j + ")");
                }
            }
        });

        Button confirmButton = (Button) findViewById(R.id.confirm_button);
        assert confirmButton != null;
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmedResume = ((TextView) findViewById(R.id.resume)).getText();
                step++;
            }
        });

        Button acceptButton = (Button) findViewById(R.id.accept_button);
        assert acceptButton != null;
        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (step == 3) {
                    //TODO: next activity
                }
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        assert resetButton != null;
        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                step = 1;
                confirmedResume = "";
            }
        });
    }

}
