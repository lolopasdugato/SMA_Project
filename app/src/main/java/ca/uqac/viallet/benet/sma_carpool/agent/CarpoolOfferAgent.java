package ca.uqac.viallet.benet.sma_carpool.agent;

import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;

import ca.uqac.viallet.benet.sma_carpool.utils.Coordinate;
import ca.uqac.viallet.benet.sma_carpool.utils.Trip;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CarpoolOfferAgent extends Agent {

    // Desired route
    private Coordinate departure;
    private Coordinate arrival;

    // The additional distance accepted by the driver
    private float detour;

    // The trips that the driver offers
    private ArrayList<Trip> offers;

    // The GUI by means of which the user can add books in the catalogue
    //private BookSellerGui myGui;

    // Agent initialization
    protected void setup() {
        // Create the catalogue
        offers = new ArrayList<Trip>();

        // Register the book-selling service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("carpool-offering");
        sd.setName("JADE-carpool");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add the behaviour serving queries from agents
        addBehaviour(new OfferRequestsServer());

        // Add the behaviour serving booking orders from agents
        addBehaviour(new BookOrdersServer());
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Printout a dismissal message
        Log.i("OFFERAG","Seller-agent "+getAID().getName()+" terminating.");
    }

    /**
     This is invoked by the GUI when the user offers a new trip
     */
    public void updateCatalogue(final Trip trip) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                offers.add(trip);
                Log.i("OFFERAG",trip.toString()+" inserted into catalogue. %detour = "+detour);
            }
        } );
    }

    /**
     Inner class OfferRequestsServer.
     This is the behaviour used by Book-seller agents to serve incoming requests
     for offer from buyer agents.
     If the requested book is in the local catalogue the seller agent replies
     with a PROPOSE message specifying the price. Otherwise a REFUSE message is
     sent back.
     */
    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {/*
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // CFP Message received. Process it
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();

       //         Integer price = (Integer) offers.get(title);
                if (price != null) {
                    // The requested book is available for sale. Reply with the price
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(String.valueOf(price.intValue()));
                }
                else {
                    // The requested book is NOT available for sale.
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            }
            else {
                block();
            } */
        }
    }  // End of inner class OfferRequestsServer

    /**
     Inner class PurchaseOrdersServer.
     This is the behaviour used by Book-seller agents to serve incoming
     offer acceptances (i.e. purchase orders) from buyer agents.
     The seller agent removes the purchased book from its catalogue
     and replies with an INFORM message to notify the buyer that the
     purchase has been sucesfully completed.
     */
    private class BookOrdersServer extends CyclicBehaviour {
        public void action() {
        /*    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();

                Integer price = (Integer) catalogue.remove(title);
                if (price != null) {
                    reply.setPerformative(ACLMessage.INFORM);
                    System.out.println(title+" sold to agent "+msg.getSender().getName());
                }
                else {
                    // The requested book has been sold to another buyer in the meanwhile .
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            }
            else {
                block();
            } */
        }
    }  // End of inner class OfferRequestsServer
}