package com;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class ClientWindow
{
	private JFrame Root;
	private JTextField ServerHostPort,TextInputField;
	private JTextArea Text;
	private JButton ConnectSwitch;
	ClientWindow()
	{
		Root=new JFrame("Client");
		Root.setLayout(null);
		Root.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Root.setSize(1500,1500);
		
		Container RootContainer=Root.getContentPane();
		
		Font ft=new Font("Arial", Font.BOLD, 25);
		
		JLabel label=new JLabel("Server Host and Port:");
		label.setBounds(10,20,300,50);
		label.setFont(ft);
		RootContainer.add(label);
		
		ServerHostPort=new JTextField(40);
		ServerHostPort.setFont(ft);
		ServerHostPort.setBounds(315,20,900,50);
		RootContainer.add(ServerHostPort);
		
		ConnectSwitch=new JButton("Connect");
		ConnectSwitch.setBounds(1220,20,200,50);
		ConnectSwitch.setFont(ft);
		RootContainer.add(ConnectSwitch);
		
		label=new JLabel("Type here:");
		label.setBounds(10,80,160,50);
		label.setFont(ft);
		RootContainer.add(label);
		
		TextInputField=new JTextField(40);
		TextInputField.setFont(ft);
		TextInputField.setBounds(165,80,900,50);
		RootContainer.add(TextInputField);
		
		label=new JLabel("(Enter for send, !? for help)");
		label.setBounds(1070,80,320,50);
		label.setFont(ft);
		RootContainer.add(label);
		
		Text=new JTextArea();
		Text.setBounds(10,150,1400,1250);
		Text.setFont(ft);
		Text.setBorder(BorderFactory.createEtchedBorder());
		RootContainer.add(Text);
	}
	
	public void setVisible()
	{
		Root.setVisible(true);
	}
	public synchronized void setConnectText(boolean b)
	/* set the label of button ConnectSwitch
	 * true  : Connect
	 * false : Disconnect
	 */
	{
		if (b)
		{
			ConnectSwitch.setText("Connect");
		}
		else
		{
			ConnectSwitch.setText("Disconnect");
		}
	}
	public synchronized String getServerAddress()
	{
		return ServerHostPort.getText();
	}
	public synchronized void setConnectSwitch_AL(ActionListener l)
	{
		ConnectSwitch.addActionListener(l);
	}
	public synchronized void setText(String content)
	{
		Text.setText(content);
	}
	public synchronized void appendText(String str)
	{
		Text.append(str);
	}
	public synchronized String getInputContent()
	{
		try
		{
			return TextInputField.getText();
		}
		catch(NullPointerException npexc)
		{
			return "";
		}
		
	}
	public synchronized void setInputContent(String str)
	{
		TextInputField.setText(str);
	}
	public synchronized void addInputFieldActionListener(ActionListener l)
	{
		TextInputField.addActionListener(l);
	}
	public synchronized void removeallInputFieldActinListener()
	{
		ActionListener[]ala=TextInputField.getActionListeners();
		for (int i=0;i<ala.length;i++)
		{
			TextInputField.removeActionListener(ala[i]);	
		}
	}
	public synchronized void clearInputField()
	{
		TextInputField.setText("");
	}
}

class ClientCommunicateController
{
	private boolean ConnectedState;
	@SuppressWarnings("unused")
	private ClientWindow wdGUI;
	private Socket serverSocket;
	private DataInputStream dis;
	private DataOutputStream dos;
	ClientCommunicateController()
	{}
	public synchronized void setwdGUI(ClientWindow wd)
	{
		wdGUI=wd;	
	}
	public synchronized void setConnectedState(boolean b)
	{
		ConnectedState=b;
	}
	public synchronized void setServerSocket(Socket s)
	{
		serverSocket=s;
	}
	public synchronized boolean getConnectedState()
	{
		return ConnectedState;
	}
	public synchronized void openIOStreamBySocket() throws IOException
	{
		dis=new DataInputStream(serverSocket.getInputStream());
		dos=new DataOutputStream(serverSocket.getOutputStream());
	}
	public synchronized void closeIOStreamBySocket() throws IOException
	{
		dis.close();
		dos.close();
	}
	public String readFlow() throws IOException
	{
		return dis.readUTF();
	}
	public synchronized void closeSocket() throws IOException
	{
		serverSocket.close();
	}
	public void sendInfo(String str) throws IOException
	{
		dos.writeUTF(str);
	}
	public void sendTerminateInfoToServer() throws IOException
	{
		dos.writeUTF("!End");
	}
	
}

class AL_BN_ConnectSwitch implements ActionListener
{
	ClientWindow wdGUI;
	ClientCommunicateController CCC;
	AL_BN_ConnectSwitch(ClientWindow wd,ClientCommunicateController ccc)
	{
		wdGUI=wd;
		CCC=ccc;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (CCC.getConnectedState()==false)
		{
			String ServerAddress=wdGUI.getServerAddress();
			InetAddress ServerIP;
			byte[] bs=new byte[4];
			int ServerPort;
			
			try
			{
				String []tmp=ServerAddress.split(":");
				if (tmp.length!=2)
				{
					throw new Exception("Illegal Server Net Address "
							+ "(IP address + Port).");
				}
				String []ipstr=tmp[0].split("\\.");
				
				if (ipstr.length!=4)
				{
					throw new Exception("Illegal IP address.");
				}
				for (int i=0;i<4;i++)
				{
					try
					{
						int it=Integer.parseInt(ipstr[i]);
						if ((it<0)||(it >255))
						{
							throw new Exception("Illegal IP address.");
						}
						bs[i]=(byte)it;
					}
					catch(NumberFormatException nfexc)
					{
						throw new Exception("Illegal IP address.");
					}
				}
				try
				{
					ServerPort=Integer.parseInt(tmp[1]);
					if ((ServerPort<0)||(ServerPort>65535))
					{
						throw new Exception("Illegal PortNumber.");
					}
				}
				catch(NumberFormatException nfexc)
				{
					throw new Exception("Illegal IP address.");
				}
			}
			catch(Exception exc)
			{
				wdGUI.setText("Error:"+exc.getMessage()+"\n");
				return;
			}
			
			try
			{
				ServerIP=InetAddress.getByAddress(bs);
			}
			catch(UnknownHostException uhexc)
			{
				wdGUI.setText("Error:UnknownHostException.\n");
				return;
			}
			
			Socket s1;
			
			try {
				s1=new Socket(ServerIP,ServerPort);
			} catch (IOException e) {
				wdGUI.setText("Error:Unable to Create Socket.\n");
				return;
			}
			
			
			try {
				CCC.setServerSocket(s1);
				CCC.setConnectedState(true);
				CCC.openIOStreamBySocket();
				wdGUI.setConnectText(false);
				wdGUI.appendText("Connect to server!\n");
				
			} catch (IOException e) {
				e.printStackTrace();
				CCC.setServerSocket(null);
				CCC.setConnectedState(false);
				wdGUI.setConnectText(true);
				wdGUI.setText("Error:Unable to open IOStream.\n");
			}
			
			
			AL_JTF_TextInputField al_t=new AL_JTF_TextInputField(wdGUI,CCC);
			wdGUI.addInputFieldActionListener(al_t);
			
			//open another thread to get info from server
			new Thread(new TextUpdator(wdGUI,CCC)).start();
			
			
		}
		else
		{
			//to disconnect			
			try {
				CCC.sendTerminateInfoToServer();
			} catch (IOException e1) {
				e1.printStackTrace();
				wdGUI.setText("Error:Unable to send Terminate Info to Server.\n");
			}
			
			try {
				CCC.closeIOStreamBySocket();
			} catch (IOException e) {
				e.printStackTrace();
				wdGUI.setText("Error:Unable to close IOStream.\n");
			}
			wdGUI.setConnectText(true);
			CCC.setConnectedState(false);
			try {
				CCC.closeSocket();
			} catch (IOException e) {
				e.printStackTrace();
				wdGUI.setText("Error:Unable to close socket.\n");
			}
			CCC.setServerSocket(null);
			wdGUI.removeallInputFieldActinListener();
			wdGUI.appendText("Disconnect to server!\n");
		}
	}
	
}

class AL_JTF_TextInputField implements ActionListener
{
	ClientWindow wdGUI;
	ClientCommunicateController CCC;
	AL_JTF_TextInputField(ClientWindow wd,ClientCommunicateController ccc)
	{
		wdGUI=wd;
		CCC=ccc;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String st=wdGUI.getInputContent();
		if (st=="!?")
		{
			wdGUI.setText("You can type in the text field below\n"
					+ "Enter to send this line.\n");
		}
		else
		{
			try {
				CCC.sendInfo(st);
				wdGUI.appendText("Client:"+st+"\n");
				wdGUI.clearInputField();
				if (st.indexOf("!End")!=-1)
				{
					CCC.setConnectedState(false);
					CCC.closeIOStreamBySocket();
					CCC.closeSocket();
					wdGUI.setConnectText(true);
					wdGUI.appendText("Disconnect to server!\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
				wdGUI.appendText("Error:Unable to send this info!\n");
			}
		}
	}
	
}


class TextUpdator implements Runnable
{
	ClientWindow wdGUI;
	ClientCommunicateController CCC;
	TextUpdator(ClientWindow wd,ClientCommunicateController ccc)
	{
		wdGUI=wd;
		CCC=ccc;
	}
	@Override
	public void run() {
		while (CCC.getConnectedState())
		{

			String st="";
			try {
				st=CCC.readFlow();
			} catch (IOException e) {
				e.printStackTrace();
				wdGUI.appendText("Error:Read Flow error."
						+ "Quit recommanded.\n");
			}
			wdGUI.appendText("Server:"+st+"\n");

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
public class Client {
	public static void main(String args[])
	{
		ClientWindow wdGUI=new ClientWindow();
		wdGUI.setConnectText(true);
		wdGUI.setVisible();
		
		ClientCommunicateController CCC=new ClientCommunicateController();
		CCC.setwdGUI(wdGUI);
		CCC.setConnectedState(false);
		
		AL_BN_ConnectSwitch al_bn_cs=new AL_BN_ConnectSwitch(wdGUI,CCC);
		wdGUI.setConnectSwitch_AL(al_bn_cs);
		
	}

}
