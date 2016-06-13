package ca.uqac.viallet.benet.sma_carpool.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.uqac.viallet.benet.sma_carpool.R;
import ca.uqac.viallet.benet.sma_carpool.agent.CarpoolFindAgent;
import ca.uqac.viallet.benet.sma_carpool.agent.CarpoolOfferAgent;
import ca.uqac.viallet.benet.sma_carpool.container.Container;
import ca.uqac.viallet.benet.sma_carpool.utils.Coordinate;

public class ProposalRestrictionActivity extends AppCompatActivity {

    public static float PRICE = 0;
    public static float DEVIATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_restriction);
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

        Button acceptButton = (Button) findViewById(R.id.accept_button);
        assert acceptButton != null;
        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String price = ((EditText) findViewById(R.id.price)).getText().toString();
                String deviation = ((EditText) findViewById(R.id.deviation)).getText().toString();
                PRICE = Float.parseFloat(price);
                DEVIATION = Float.parseFloat(deviation);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    Coordinate departure_coord = new Coordinate(extras.getInt("departure_x"),
                            extras.getInt("departure_y"));
                    Coordinate arrival_coord = new Coordinate(extras.getInt("arrival_x"),
                            extras.getInt("arrival_y"));
                    Container.getInstance().startCarpool("offer" + MainMenu.offerAgents.size(), CarpoolOfferAgent.class.getName(),
                            new Object[] {departure_coord.x,departure_coord.y, arrival_coord.x,arrival_coord.y, DEVIATION, PRICE},
                            MainMenu.MainMenuLink);
                    Intent myIntent = new Intent(ProposalRestrictionActivity.this, ProposalCheckActivity.class);
                    startActivity(myIntent);
                }

                //TODO: Add proposal to the pool
            }
        });
    }

}
