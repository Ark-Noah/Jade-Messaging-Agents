
import jade.core.Agent;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import jade.util.leap.*;
import jade.gui.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;

import java.util.Iterator;
import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

public class ReceiverAgent extends Agent {

	protected void setup() {

		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ReceiverAgent");
		sd.setName(getName());
		sd.setOwnership("ExampleReceiversOfJADE");
		sd.addOntologies("ReceiverAgent");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
			doDelete();
		}

		System.out.println("Receiver is Ready " + getAID().getLocalName());
		ReceiveMessage sm = new ReceiveMessage();
		addBehaviour(sm);
	}

	public class ReceiveMessage extends CyclicBehaviour {

		private String msgPerformative;
		private String msgContent;
		private String senderName;
		private String senderAddress;

		public void action() {
			// RECIEVE MESSAGE
			ACLMessage msg = receive();
			if (msg != null) {

				msgPerformative = msg.getPerformative(msg.getPerformative());
				msgContent = msg.getContent();
				senderName = msg.getSender().getName();
				senderAddress = msg.getSender().getAddressesArray().toString();

				System.out.println("***I Received a Message***" + "\n" + "The Sender Name is:" + senderName + "\n"
						+ "The Content of the Message is::> " + msgContent + "\n" + "::: And Performative is:: "
						+ msgPerformative);
				// Reply to the Message
				if (msgPerformative.equals("REQUEST")) {

					ACLMessage replyMessage = new ACLMessage(ACLMessage.INFORM);

					AID r = new AID();
					r.setName(senderName);
					r.addAddresses("http://10.3.160.75:7778/acc");
					replyMessage.addReceiver(r);
					replyMessage.setLanguage("English");
					replyMessage.setContent("Reply : " + msgContent.toString());
					send(replyMessage);
					System.out.println("****I Replied to::> " + senderName + "***");
					System.out.println("The Content of My Reply is:" + replyMessage.getContent());
					System.out.println("ooooooooooooooooooooooooooooooooooooooo");

				}else if(msgPerformative.equals("INFORM")) {
					System.out.println("The Content of My Reply is:" + msgContent.toString());
				}

			}

		}
	}
}
