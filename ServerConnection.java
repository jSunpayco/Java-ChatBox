import java.io.*;
import java.net.*;
import javax.imageio.*;
import java.awt.image.*;
import java.sql.Timestamp;
import java.util.Date;

public class ServerConnection extends Thread{

	String scName;
    
	Socket socket;
    Server server;
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	DataInputStream din;
	DataOutputStream dout;
	
	int shouldRun = 1;
	String log;
	
    public ServerConnection(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }
	
	//send message back to client
    public void sendStringToClient(String text){
		
		try{
			dout.writeUTF(text);
			dout.flush();		
		}catch(IOException e){
			e.printStackTrace();				
		}
       
    }
	
	public void sendFileToClient(Textinfo data){
		
		try{
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(data);
			oos.flush();		
		}catch(IOException e){
			e.printStackTrace();				
		}
       
    }
	
	@Override
    public void run(){

        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
			
            while (shouldRun == 1){
				
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
				
				String text = din.readUTF();
				
				if(text.equals("?41a420")){
					
					server.disconnectSocket(scName);
					shouldRun = 0;
					
				}else{
					if(text.equals("-Rt#eY<H")){ /* File Message */
						
						if(server.scArr.size()==1){
							sendStringToClient("You do not have anyone to send to");
							String user = din.readUTF();
							ois = new ObjectInputStream(socket.getInputStream());
							Textinfo data = (Textinfo) ois.readObject();
						}else{
							server.sendStringToAllClients("-Rt#eY<H");
							
							String user = din.readUTF();
							ois = new ObjectInputStream(socket.getInputStream());
							Textinfo data = (Textinfo) ois.readObject();
							
							server.sendFileToAll(data, user);
							log = ts+": "+user+" has sent a file";
							System.out.println(log);
							server.activityLog.add(log);
							server.sendStringToAllClients(user + " has sent a file");
						}		
					}else{
						String user = text.substring(0, text.indexOf(':'));
						server.sendStringToAllClients(text);
						log = ts+": "+user+" has sent a message";
						System.out.println(log);
						server.activityLog.add(log);
					}
				}
			}
			dout.close();
			din.close();
			socket.close();
						
		}catch(IOException ex) {
			ex.printStackTrace();
        }catch(ClassNotFoundException c){
			c.printStackTrace();
		}

    }

}
