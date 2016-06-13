package ca.uqac.viallet.benet.sma_carpool.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ca.uqac.viallet.benet.sma_carpool.R;
import ca.uqac.viallet.benet.sma_carpool.agent.CarpoolFindAgent;
import jade.core.AID;

public class SearchResultActivity extends AppCompatActivity {

    public static CarpoolFindAgent agentToFollow;

    private ArrayList<String> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
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

        if (agentToFollow != null) {
            for (AID agent : agentToFollow.getInterestedOfferAgents()) {
                results.add(agent.getName());
            }

            final GridView gridView = (GridView) findViewById(R.id.search_result);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, results) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    int color = 0x00FFFFFF; // Transparent
                    if (agentToFollow.getSelectedOfferAgent() != null && position == results.indexOf(agentToFollow.getSelectedOfferAgent().getName().toString())) {
                        color = 0xFF0000FF; // Opaque Blue
                    }

                    view.setBackgroundColor(color);

                    return view;
                }
            };

            assert gridView != null;
            gridView.setAdapter(adapter);
        }

        findViewById(R.id.myResearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SearchResultActivity.this, MyResearchActivity.class);
                startActivity(myIntent);
            }
        });
    }

}
