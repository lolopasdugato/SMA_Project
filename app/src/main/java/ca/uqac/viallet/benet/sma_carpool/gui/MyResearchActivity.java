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
import android.widget.GridView;

import java.util.ArrayList;

import ca.uqac.viallet.benet.sma_carpool.R;
import ca.uqac.viallet.benet.sma_carpool.agent.CarpoolFindAgent;

public class MyResearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_research);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ArrayList<String> researches = new ArrayList<String>();

        for (CarpoolFindAgent fAgent : MainMenu.findAgents) {
            researches.add(fAgent.toString());
        }
        Log.i("MyResearchActivity", "researches size: " + researches.size());

        final GridView gridView = (GridView) findViewById(R.id.researchesGrid);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, researches);

        assert gridView != null;
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchResultActivity.agentToFollow = MainMenu.findAgents.get(position);
                Intent myIntent = new Intent(MyResearchActivity.this, SearchResultActivity.class);
                startActivity(myIntent);
            }
        });
    }

}
