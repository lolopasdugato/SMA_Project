package ca.uqac.viallet.benet.sma_carpool.gui;

import android.content.Intent;
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
import android.widget.TextView;

import java.util.Vector;

import ca.uqac.viallet.benet.sma_carpool.R;
import ca.uqac.viallet.benet.sma_carpool.agent.CarpoolFindAgent;
import ca.uqac.viallet.benet.sma_carpool.container.Container;
import ca.uqac.viallet.benet.sma_carpool.utils.Coordinate;

public class RideActivity extends AppCompatActivity {

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    private Coordinate departure_coord;
    private Coordinate arrival_coord;
    private int step = 1;
    private CharSequence confirmedResume = new String("Résumé");

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
                // récupération des information de l'endroit selectionné
                int i = position / gridView.getNumColumns();
                int j = position % gridView.getNumColumns();
                Log.i("MAP-", "Position: " + i + "," + j);
                TextView resume = (TextView) findViewById(R.id.resume);
                if (step == 1) {
                    departure_coord = new Coordinate(i, j);
                    resume.setText("Départ de " + ((TextView) v).getText() + "(" + i + "," + j + ")");
                }
                else if (step == 2) {
                    arrival_coord = new Coordinate(i, j);
                    resume.setText(confirmedResume + "\nArrivée à " + ((TextView) v).getText() + "(" + i + "," + j + ")");
                }
            }
        });

        final Button confirmButton = (Button) findViewById(R.id.confirm_button);
        assert confirmButton != null;
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CharSequence currentText = ((TextView) findViewById(R.id.resume)).getText();
                Log.i("txt:s", "curr" + currentText);
                Log.i("txt:s", "conf" + confirmedResume);
                if(currentText.toString().compareToIgnoreCase(confirmedResume.toString()) != 0) {
                    confirmedResume = currentText;
                    ((TextView) findViewById(R.id.instructions)).setText("Choisissez votre destination");
                    step++;
                }
            }
        });

        Button acceptButton = (Button) findViewById(R.id.accept_button);
        assert acceptButton != null;
        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (step == 3) {
                    if(MainMenu.SEARCHING) {
                        Container.getInstance().startCarpool("search" + MainMenu.findAgents.size(), CarpoolFindAgent.class.getName()
                                , new Object[] {departure_coord.x,departure_coord.y, arrival_coord.x,arrival_coord.y}, MainMenu.MainMenuLink);
                        Intent myIntent = new Intent(RideActivity.this, SearchResultActivity.class);
                        startActivity(myIntent);
                    } else {
                        Intent myIntent = new Intent(RideActivity.this, ProposalRestrictionActivity.class);
                        myIntent.putExtra("departure_x", departure_coord.x);
                        myIntent.putExtra("departure_y", departure_coord.y);
                        myIntent.putExtra("arrival_x", arrival_coord.x);
                        myIntent.putExtra("arrival_y", arrival_coord.y);
                        startActivity(myIntent);
                    }
                }
            }
        });

        Button resetButton = (Button) findViewById(R.id.reset_button);
        assert resetButton != null;
        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                step = 1;
                confirmedResume = "Résumé";
                ((TextView) findViewById(R.id.instructions)).setText("Choisissez votre position actuelle");
            }
        });
    }

}
