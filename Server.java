import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.Timestamp;
import java.util.Date;

public class Server{
    
	ServerSocket ss;
	ArrayList<ServerConnection> scArr = new ArrayList<ServerConnection>();
	ArrayList<String> scName = new ArrayList<String>();
	ArrayList<String> activityLog = new ArrayList<String>();
	String log;
	
	DataInputStream din;
	
	int init = 1;
	
	public static void main(String[] args) {
        
		new Server();
		
    }
	
	public Server(){
		try{
			
			ss = new ServerSocket(3333);
			System.out.println("Server: Connected to port 3333");
			System.out.println("Server will terminate once all clients have disconnected.");
			
			while(true){
				
				Date date= new Date();
				long time = date.getTime();
				Timestamp ts = new Timestamp(time);
				if(scArr.size() == 0){
					
					if(init == 1){
						init = 0;
						connectUser(date, time, ts);
					}else{
						disconnectServer();
					}
	
				}else{
					connectUser(date, time, ts);
				}
				
			}
			
		}catch(IOException e){
			
			e.printStackTrace();

		}finally{
			System.out.println("Server: Connection is terminated.");
		}
	}
	
	public void connectUser(Date date, long time, Timestamp ts){
		try{
			Socket 	s = ss.accept();
			ServerConnection sc = new ServerConnection(s, this);
			scArr.add(sc);
			
			sc.start();
			din = new DataInputStream(s.getInputStream());
			String uName = din.readUTF();
			
			log = "User " + uName + " has Connected";
			System.out.println(log);
			activityLog.add(log);
			
			scName.add(uName);
			scArr.get(scArr.size()-1).scName = uName;
			
			if(scArr.size() == 1)
				scArr.get(0).sendStringToClient(uName + " has connected");
			else
				sendStringToAllClients(uName + " has connected");
		
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void sendStringToAllClients(String text){
		
		if(scArr.size() == 1){
			scArr.get(0).sendStringToClient("You do not have anyone to send to");
		}else{
			for(int i = 0; i < scArr.size(); i++){
				scArr.get(i).sendStringToClient(text);
			}
		}

	}
	
	public void sendFileToAll(Textinfo data, String sender){
		for(int i = 0; i < scArr.size(); i++){
			scArr.get(i).sendFileToClient(data);
		}
	}
	
	public void disconnectSocket(String uName){
		
		for(int i = 0; i < scArr.size(); i++){
			if(uName.equals(scName.get(i))){
				scArr.remove(i);
				scName.remove(i);
				log = uName + " has Disconnected";
				System.out.println(log);
				activityLog.add(log);
			}
		}
		
		if(scArr.size() == 0){
			disconnectServer();
		}else if(scArr.size() == 1){
			scArr.get(0).sendStringToClient(uName + " has Disconnected");
		}else{
			sendStringToAllClients(uName + " has Disconnected");
		}
		
	}
	
	public void disconnectServer(){
		Scanner sc = new Scanner(System.in);
				
		String choice = new String();
		System.out.println("Would you like to save a copy of the activity log?");
		
		while(!choice.toLowerCase().equals("yes")&&
		!choice.toLowerCase().equals("no")){
			choice = sc.nextLine();
		}
		
		if(choice.toLowerCase().equals("yes")){
			try{
				PrintWriter pr = new PrintWriter("Activity Log.txt");    

				for (int i=0; i<activityLog.size() ; i++){
					pr.println(activityLog.get(i));
				}
				pr.close();
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("No such file exists.");
			}
			
			
		}else{
			System.out.println("Thank you for using the app");
		}
		System.out.println("Server Shutdown");
		try{
			din.close();
			ss.close();
			System.exit(0);
		}catch(IOException e){e.printStackTrace();}
		
	}

}
