package ca.uqac.viallet.benet.sma_carpool.agent;

import android.util.Log;

import ca.uqac.viallet.benet.sma_carpool.gui.MainMenu;
import ca.uqac.viallet.benet.sma_carpool.utils.Coordinate;
import ca.uqac.viallet.benet.sma_carpool.utils.Trip;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class CarpoolOfferAgent extends Agent {

    private Logger logger = Logger.getJADELogger(this.getClass().getName());

    // Proposed route
    private Trip trip;

    // The GUI by means of which the user can add books in the catalogue
    //private BookSellerGui myGui;

    // Agent initialization
    protected void setup() {
        Log.i("OFFDAG", "Offer-agent "+getAID().getName()+" is ready.");
        // Get the coordinates as a start-up argument
        Object[] args = getArguments();
        if (args != null && args.length == 6) {
            trip = new Trip(new Coordinate((int) args[0], (int) args[1]),
                    new Coordinate((int) args[2], (int) args[3]),
                    (float) args[4], (float) args[5]);

            MainMenu.offerAgents.add(this);

            // Register the carpool service in the yellow pages
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(this.getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("carpool-offering");
            sd.setName("JADE-carpool");
            dfd.addServices(sd);
            try {
                DFService.register(this, dfd);
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

            // Add the behaviour serving queries from agents
            addBehaviour(new OfferRequestsServer());

            // Add the behaviour serving booking orders from agents
            addBehaviour(new BookOrdersServer());
        }
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
     Inner class OfferRequestsServer.
     This is the behaviour used by Book-seller agents to serve incoming requests
     for offer from buyer agents.
     If the requested book is in the local catalogue the seller agent replies
     with a PROPOSE message specifying the price. Otherwise a REFUSE message is
     sent back.
     */
    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            Log.i("OFFAG", "Coucou");
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // CFP Message received. Process it
                //String title = msg.getContent();
                String[] tripSearch = msg.getContent().split(" ");
                Coordinate departure = new Coordinate(tripSearch[0]);
                Coordinate arrival = new Coordinate(tripSearch[1]);

                ACLMessage reply = msg.createReply();

                float detour = trip.detourLength(departure, arrival)/trip.routeLength()*100;
                Log.i("OFFAG", "Deviation :" + detour);
                if (detour-100 <= trip.getDetour()) {
                    // The requested trip is available
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(trip.toString());
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
            }
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