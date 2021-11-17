package groupChat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class main extends JFrame{

	JPanel panel;
	public main() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400,460);
		try {
	        String host="",room="";
		    String name=JOptionPane.showInputDialog("Enter Your Name");
		    if(name==null||name.equals(""))
		        {JOptionPane.showMessageDialog(null, "Name cannot be blank");return;}
		    int mode=JOptionPane.showConfirmDialog(null,"Create a chatroom or connect to existing one?\nYes - Create Chat Room\nNo - Jion a Chat Room","Create or Join?",JOptionPane.YES_NO_OPTION);
		    if(mode==1)
		        {
		        host=JOptionPane.showInputDialog("Enter the host ip address");
		        if(host==null||host.equals(""))
		            {JOptionPane.showMessageDialog(null, "IP of host is mandatory");return;}
		        }
		    else
		        room=JOptionPane.showInputDialog("Name your chat room");
		        panel= new ChatRoom(name,mode,host,room);
		        add(panel);
		        setVisible(true);
		        } catch (Exception ex) {JOptionPane.showMessageDialog(null,ex);}
	}
	
	public static void main(String args[]) {
       main obj = new main();
		
    }
}
