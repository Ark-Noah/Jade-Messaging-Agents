import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import jade.core.AID;
import jade.core.behaviours.*;

import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.*;

public class MyAgentComm extends GuiAgent {

	private Gui gui;
	ACLMessage msg;
	AMSAgentDescription[] agents = null;

	protected void setup() {

		gui = new Gui();

		AMSAgentDescription[] agents = null;
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println("Problem searching AMS: " + e);
			e.printStackTrace();
		}

		msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("Ping");

		for (int i = 0; i < agents.length; i++) {
			gui.cmbReceiver.addItem(agents[i].getName());
		}

		gui.txtSent.setText(msg.getContent().toString());

		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) // BIND THE RECEIVED TXT TO THE TEXTFIELD HERE
					System.out.println("== Answer" + " <- " + msg.getContent() + " from " + msg.getSender().getName());
				if (msg != null) {
					gui.txtReceived.setText(msg.getContent().toString());
				}
				block();
			}
		});
		gui.setAgent(this);
	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// GET THE MESSAGE TO BE SENT HERE
		msg.setContent(gui.txtContent.getText());

		// ADD THE TARGET HERE
		msg.addReceiver((AID) gui.cmbReceiver.getSelectedItem());

		send(msg);
		gui.txtSent.setText(msg.getContent().toString());
	}

	class Gui extends JFrame implements ActionListener {

		private MyAgentComm myAgent;
		public JButton B;
		private JToolBar jToolBar1;
		private JPanel contentPane;
		private JTextField txtContent;
		private JButton btnSend, btnCancel;
		public JComboBox cmbReceiver, cmbPerformative;
		JTextArea txtReceived, txtSent;

		protected void frameInit() {
			super.frameInit();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 565, 333);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			txtContent = new JTextField();
			txtContent.setColumns(10);
			txtContent.setBounds(12, 162, 173, 35);
			contentPane.add(txtContent);

			JLabel lblNewLabel = new JLabel("Performative");
			lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblNewLabel.setBounds(12, 13, 138, 16);
			contentPane.add(lblNewLabel);

			JLabel lblReceiver = new JLabel("Receiver");
			lblReceiver.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblReceiver.setBounds(12, 78, 56, 16);
			contentPane.add(lblReceiver);

			JLabel lblContent = new JLabel("Content");
			lblContent.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblContent.setBounds(12, 146, 56, 16);
			contentPane.add(lblContent);

			cmbReceiver = new JComboBox();
			cmbReceiver.setBounds(12, 95, 173, 35);
			contentPane.add(cmbReceiver);

			cmbPerformative = new JComboBox();
			cmbPerformative.setBounds(12, 30, 173, 35);
			contentPane.add(cmbPerformative);
			cmbPerformative.addItem("INFORM");
			cmbPerformative.addItem("REQUEST");

			btnSend = new JButton("Send");
			btnSend.setBounds(96, 224, 124, 35);
			contentPane.add(btnSend);
			btnSend.addActionListener(this);

			btnCancel = new JButton("Cancel");
			btnCancel.setBounds(295, 224, 124, 35);
			contentPane.add(btnCancel);

			txtSent = new JTextArea();
			txtSent.setBackground(new Color(255, 240, 245));
			txtSent.setBounds(213, 30, 124, 167);
			contentPane.add(txtSent);

			txtReceived = new JTextArea();
			txtReceived.setBackground(new Color(255, 235, 205));
			txtReceived.setEditable(false);
			txtReceived.setBounds(376, 30, 124, 167);
			contentPane.add(txtReceived);

			JLabel lblSentMessage = new JLabel("Sent Message");
			lblSentMessage.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblSentMessage.setBounds(213, 14, 124, 16);
			contentPane.add(lblSentMessage);

			JLabel lblReceivedMessage = new JLabel("Received Message");
			lblReceivedMessage.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblReceivedMessage.setBounds(376, 14, 124, 16);
			contentPane.add(lblReceivedMessage);
			setVisible(true);
		}

		public void setAgent(MyAgentComm a) {
			myAgent = a;
		}

		public void actionPerformed(java.awt.event.ActionEvent ae) {
			GuiEvent ge = new GuiEvent(this, 1);

			if (ae.getSource() == this.btnSend) {
				myAgent.postGuiEvent(ge);
			}
		}
	}
}