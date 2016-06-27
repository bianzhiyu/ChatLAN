package com;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class ServerWindow
{
	private JFrame Root;
	private JTextField TextInputField;
	private JTextArea Text;
	private JLabel ServerInfo;
	ServerWindow()
	{
		Root=new JFrame("ComServer");
		Root.setLayout(null);
		Root.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Root.setSize(1500,1500);
		
		Container RootContainer=Root.getContentPane();
		
		Font ft=new Font("Arial", Font.BOLD, 25);
		
		ServerInfo=new JLabel("ServerInfo.........");
		ServerInfo.setFont(ft);
		ServerInfo.setBounds(10,20,1000,50);
		RootContainer.add(ServerInfo);
		
		JLabel label=new JLabel("Type here:");
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
	public synchronized void setText(String str)
	{
		Text.setText(str);
	}
	public synchronized void setServerInfo(String str)
	{
		ServerInfo.setText(str);
	}
	public synchronized void appendText(String str)
	{
		Text.append(str);
	}
	public synchronized void setTypeListener(ActionListener l)
	{
		TextInputField.addActionListener(l);
	}
	public synchronized void removeAllTypeListener()
	{
		ActionListener[]ala=TextInputField.getActionListeners();
		for (int i=0;i<ala.length;i++)
		{
			TextInputField.removeActionListener(ala[i]);	
		}
	}
	public synchronized String getInputArea()
	{
		return TextInputField.getText();
	}
	public synchronized void clearInputArea()
	{
		TextInputField.setText("");
	}
}

class ServerCommunicateController
{
	private ServerWindow swGUI;
	private boolean ConnectedState;
	private ServerSocket serverSocket;
	private DataInputStream dis;
	private DataOutputStream dos;
	ServerCommunicateController()
	{
		ConnectedState=false;
	}
	public synchronized void setConnectedState(boolean b)
	{
		ConnectedState=b;
	}
	public void setServerWindow(ServerWindow sw)
	{
		swGUI=sw;
	}
	public int startServerSocket()
	{
		try{
			serverSocket = new ServerSocket(0);
		}
		catch(IOException e){
			System.out.println(e.toString());
			return 1;
		}
		
		return 0;
	}
	public int startServerSocket(int portnum)
	{
		try{
			serverSocket = new ServerSocket(portnum);
		}
		catch(IOException e){
			System.out.println(e.toString());
			return 1;
		}
		
		return 0;
	}
	public void closeServerSocket() throws IOException
	{
		serverSocket.close();
	}
	public void working()
	{
		InetAddress LocalHostAdd=null;
		try {
			LocalHostAdd=InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int portnum=serverSocket.getLocalPort();
		swGUI.setServerInfo(LocalHostAdd.toString()
				+":"
				+portnum);
		
		Socket onesocket=null;
		this.setConnectedState(false);
		
		while (true)
		{
			try {
				onesocket=this.serverSocket.accept();
				
				swGUI.appendText("Connect to client!\n");
				
				dis=new DataInputStream(
						onesocket.getInputStream());
				dos=new DataOutputStream(
						onesocket.getOutputStream());
				
				this.setConnectedState(true);
				
				AL_Type typeal=new AL_Type(swGUI,this);
				
				swGUI.setTypeListener(typeal);
				
				String readinfo;
				
				while (true)
				{
					readinfo=dis.readUTF();
					if (readinfo.indexOf("!End")!=-1)
					{
						break;
					}
					
					swGUI.appendText("Client:"+readinfo+"\n");
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				swGUI.removeAllTypeListener();
				typeal=null;
				
				dis.close();
				dos.close();
				onesocket.close();
				onesocket=null;
				this.setConnectedState(false);
				swGUI.appendText("Disconnect to Client!\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public synchronized void  sendMessage(String str)
	{
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class AL_Type implements ActionListener
{
	ServerWindow wdGUI;
	ServerCommunicateController SCC;
	AL_Type(ServerWindow wd,ServerCommunicateController s)
	{
		wdGUI=wd;SCC=s;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String content=wdGUI.getInputArea();
		
		SCC.sendMessage(content);
		wdGUI.appendText("Server:"+content+"\n");
		
		wdGUI.clearInputArea();
	}
	
}

class SocketStreamUpdator implements Runnable
{
	
	SocketStreamUpdator()
	{
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}

public class Server {
	public static void main(String[] args)
	{
		ServerWindow wdGUI=new ServerWindow();
		wdGUI.setVisible();
		
		ServerCommunicateController SCC=new ServerCommunicateController();
		SCC.setServerWindow(wdGUI);
		SCC.setConnectedState(false);
		
		int portnum=63312;
		int flag=SCC.startServerSocket(portnum);//set port number=63312
		
		if (flag!=0)
		{
			wdGUI.setText("Unable to set serversocket.");
		}
		else
		{
			try {
				InetAddress LocalHostAdd=InetAddress.getLocalHost();
				wdGUI.setServerInfo(LocalHostAdd.toString()
						+":"
						+portnum);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
			
			SCC.working();
			
			
			try {
				SCC.closeServerSocket();
				System.out.println("Close");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		
	}

}
