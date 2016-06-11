package ca.uqac.viallet.benet.sma_carpool.agent;

import android.graphics.Point;
import android.util.Log;

import ca.uqac.viallet.benet.sma_carpool.utils.Coordinate;
import ca.uqac.viallet.benet.sma_carpool.utils.Trip;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CarpoolFindAgent extends Agent {

    // Desired route
    private Trip trip;

    // The list of known carpool offer agents
    private AID[] offerAgents;

    // Agent initialization
    protected void setup() {
        Log.i("FINDAG", "Find-agent "+getAID().getName()+" is ready.");
        // Get the coordinates as a start-up argument
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            trip = new Trip(new Coordinate((int) args[0], (int) args[1]),
                    new Coordinate((int) args[2], (int) args[3]));


            Log.i("FINDAG", "Trip : " + trip.toString());

            // Add a TickerBehaviour that schedules a request to agents every minute
            addBehaviour(new TickerBehaviour(this, 60000) {
                protected void onTick() {
                    // Update the list of seller agents
                    Log.i("FINDAG", "Trying to find a carpool");
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("carpool-offering");
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        offerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            offerAgents[i] = result[i].getName();
                        }
                    }
                    catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    // Perform the request
                    myAgent.addBehaviour(new RequestPerformer());
                }
            } );
        }
        else {
            // Make the agent terminate
            Log.i("FINDAG", "Missing arguments");
            doDelete();
        }
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Printout a dismissal message
        Log.i("FINDAG", "Find-Agent "+getAID().getName()+" terminating.");
    }

    /**
     Inner class RequestPerformer.
     This is the behaviour used by Book-buyer agents to request seller
     agents the target book.
     */
    private class RequestPerformer extends Behaviour {
        private AID bestOffer; // The agent who provides the best offer
        private int bestPrice;  // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < offerAgents.length; ++i) {
                        cfp.addReceiver(offerAgents[i]);
                    }
                    cfp.setContent(trip.toString());
                    cfp.setConversationId("carpool-proposal");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("carpool-proposal"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
                            int price = Integer.parseInt(reply.getContent());
                            if (bestOffer == null || price < bestPrice) {
                                // This is the best offer at present
                                bestPrice = price;
                                bestOffer = reply.getSender();
                            }
                        }
                        repliesCnt++;
                        if (repliesCnt >= offerAgents.length) {
                            // We received all replies
                            step = 2;
                        }
                    }
                    else {
                        block();
                    }
                    break;
                case 2:
                    // Send the purchase order to the seller that provided the best offer
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver(bestOffer);
                    order.setContent(trip.toString());
                    order.setConversationId("carpool-proposal");
                    order.setReplyWith("order"+System.currentTimeMillis());
                    myAgent.send(order);
                    // Prepare the template to get the purchase order reply
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("carpool-proposal"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 3;
                    break;
                case 3:
                    // Receive the purchase order reply
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Purchase order reply received
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // Purchase successful. We can terminate
                            myAgent.doDelete();
                        }
                        else {
                            System.out.println("Attempt failed: refused offer.");
                        }

                        step = 4;
                    }
                    else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            if (step == 2 && bestOffer == null) {
                Log.i("FINDAG", "Attempt failed: no carpool mathcing");
            }
            return ((step == 2 && bestOffer == null) || step == 4);
        }
    }  // End of inner class RequestPerformer
}
