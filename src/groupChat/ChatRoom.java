package groupChat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;


public class ChatRoom extends JPanel{
	    public static final int HOST_MODE=0;
	    public static final int CLIENT_MODE=1;
	    JButton btn_send;
	    JScrollPane jScrollPane1;
	    JTextArea jTextArea1;
	    JLabel lbl_ipNroomName;
	    JTextField txt_mymsg;
	    int mode;
	    String Name;
	    String roomname;
	    InetAddress hostip;
	    ChatRoom pt;
	    DatagramSocket socket;
	    ArrayList<client> ClientList;
	    byte[] b;
	   
		public ChatRoom(String myname,int mod,String ip,String room)
		{
		     try{
		        Name=myname;
		        mode=mod;
		        hostip=InetAddress.getByName(ip);
		        roomname=room;
		        setLayout(null);
		        setSize(400,460);
		        lbl_ipNroomName = new JLabel("",SwingConstants.CENTER);
		        lbl_ipNroomName.setForeground(Color.WHITE);
		        setBackground(Color.black);
		    txt_mymsg = new JTextField();
		    txt_mymsg.setBackground(Color.DARK_GRAY);
		    txt_mymsg.setForeground(Color.WHITE);
		    btn_send = new JButton("Send");
		    jScrollPane1 = new JScrollPane();
		    jTextArea1 = new JTextArea(8,15);
		    jTextArea1.setBackground(Color.DARK_GRAY);
		    jTextArea1.setForeground(Color.WHITE);
		    ClientList=new ArrayList<>();
		    add(lbl_ipNroomName);
		    lbl_ipNroomName.setBounds(10,10,getWidth()-30,40);
		    add(txt_mymsg);
		    pt=this;
		    txt_mymsg.setBounds(10,lbl_ipNroomName.getY()+lbl_ipNroomName.getHeight(),getWidth()-130,30);
		    add(btn_send);
		    btn_send.setBounds(txt_mymsg.getWidth()+20,txt_mymsg.getY(),80,30);
		    jScrollPane1.setViewportView(jTextArea1);
		    add(jScrollPane1);
		    jScrollPane1.setBounds(10,btn_send.getY()+40,lbl_ipNroomName.getWidth(),getHeight()-20-jScrollPane1.getY()-110);
		    btn_send.setEnabled(false);
		    jTextArea1.setEditable(false);
		    txt_mymsg.setEnabled(false);
		    btn_send.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    String s=txt_mymsg.getText();
		    if(s.equals("")==false)
		        {
		        if(mode==HOST_MODE)
		            broadcast(Name+": "+s);
		        else
		            sendToHost(Name+": "+s);
		        txt_mymsg.setText("");
		        }
		        }
		    });
		   
		    if(mode==HOST_MODE)
		        {
		        socket=new DatagramSocket(37988);
		        lbl_ipNroomName.setText("My IP:"+InetAddress.getLocalHost().getHostAddress());
		        }
		    else
		        {
		        socket=new DatagramSocket();
		        String reqresp="!!^^"+Name+"^^!!";
		        DatagramPacket pk=new DatagramPacket(reqresp.getBytes(),reqresp.length(),hostip,37988);
		        socket.send(pk);
		        b=new byte[300];
		        pk=new DatagramPacket(b,300);
		        socket.setSoTimeout(6000);
		        socket.receive(pk);
		        reqresp=new String(pk.getData());
		        if(reqresp.contains("!!^^"))
		            {
		            roomname=reqresp.substring(4,reqresp.indexOf("^^!!"));
		            lbl_ipNroomName.setText("ChatRoom: "+roomname);
		            btn_send.setEnabled(true);
		            txt_mymsg.setEnabled(true);
		            }
		        else{
		            JOptionPane.showMessageDialog(pt,"No response from the server");System.exit(0);
		                }
		            }
		        Messenger.start();
		        }catch(Exception ex){JOptionPane.showMessageDialog(null,ex);}
		}
		
		
		public void broadcast(String str)
		{
		try {
		DatagramPacket pack=new DatagramPacket(str.getBytes(),str.length());
		for(int i=0;i<ClientList.size();i++)
		    {
		    pack.setAddress(InetAddress.getByName(ClientList.get(i).ip));
		    pack.setPort(ClientList.get(i).port);
		    socket.send(pack);
		    }
		jTextArea1.setText(jTextArea1.getText()+"\n"+str);
		} catch (Exception ex) {JOptionPane.showMessageDialog(pt,ex);}
		}
		
		public void sendToHost(String str)
		{
		DatagramPacket pack=new DatagramPacket(str.getBytes(),str.length(),hostip,37988);
		try {socket.send(pack);} catch (Exception ex)
		{JOptionPane.showMessageDialog(pt,"Sending to server failed");}
		}
		
		Thread Messenger=new Thread()
		{
			public void run()
			{
				try {
					while(true)
					    {
					    b=new byte[300];
					    DatagramPacket pkt=new DatagramPacket(b,300);
					    socket.setSoTimeout(0);
					    socket.receive(pkt);
					    String s=new String(pkt.getData());
					    if(mode==HOST_MODE){
					        if(s.contains("!!^^"))
					        {
						        client temp=new client();
						        temp.ip=pkt.getAddress().getHostAddress();
						        temp.port=pkt.getPort();
						        broadcast(s.substring(4,s.indexOf("^^!!"))+" joined.");
						        ClientList.add(temp);
						        s="!!^^"+roomname+"^^!!";
						        pkt=new DatagramPacket(s.getBytes(),s.length(),InetAddress.getByName(temp.ip),temp.port);
						        socket.send(pkt);
						        btn_send.setEnabled(true);
						        txt_mymsg.setEnabled(true);
					        }
					    else{
					        	broadcast(s);
					        }
					    }
					else
					    {
					    jTextArea1.setText(jTextArea1.getText()+"\n"+s);
					        }
					    }
				}catch (IOException ex) {
					JOptionPane.showMessageDialog(pt,ex);System.exit(0);
					}
			}
		};
	}
	
class client{
	public String ip;
	public int port;
	public String name;
}
