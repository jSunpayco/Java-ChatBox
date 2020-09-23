import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Date;

public class ServerConnection extends Thread
{
	String user = "";



	Socket socket;
	Server server;
	DataInputStream din;
	DataOutputStream dout;
	boolean shouldRun = true;
	
	public ServerConnection(Socket socket, Server server){
		super("ServerConnectionThread");
		this.socket = socket;
		this.server = server;
	}
	
	public void sendStringToClient(String text, String name){
		try{


			dout.writeUTF(text);
			dout.flush();
		}
		catch(IOException e){

			SendStringToAllClients( "Partner has disconnected", this.user);

			System.out.println("User has disconnected");

			System.out.println(server.userA.user.equals(name));

			if( server.userA.user.equals(name) )
				server.userB = null;




			if( server.userB.user.equals(name))
				server.userA = null;


		}
	}
	
	public void SendStringToAllClients(String text, String name){

			ServerConnection sc = server.userA;
			

		    sc = server.userB;

			if( ! server.userB.user.equals(name))
			{

				if(text.equals("quit"))
				{
					System.out.println(text);
					if(sc != null)
						sc.sendStringToClient("has disconnected",name);
					server.userB = null;
				}
				else
					sc.sendStringToClient(user + ": " + text,name);
			}



	}


	
	public void run(){
		try{
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
			
			while(shouldRun){
				while(din.available() == 0){
					try{
						Thread.sleep(1);
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				Date date= new Date();
				long time = date.getTime();
				Timestamp ts = new Timestamp(time);


				String textIn = din.readUTF();
				System.out.println(ts+": Client has sent a message");
				SendStringToAllClients(textIn,user);
			}
			din.close();
			dout.close();
			socket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
}