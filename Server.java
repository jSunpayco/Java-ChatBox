import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.Timestamp;
import java.util.Date;


public class Server 
{



	ServerSocket ss;



	public ServerConnection userA;
	
	public ServerConnection userB;

	String usernameA = "";
	String usernameB = "";


	ArrayList<String> activityLog = new ArrayList<String>();

	boolean shouldRun = true;

	DataInputStream dis;

	public static void main(String[] args){
		new Server();
	}



	public Server(){
		try{


			System.out.println("Server: Listening on port " + 3333 + "...");

			


			ss = new ServerSocket(3333);
			do{
				
				
				Date date= new Date();
				long time = date.getTime();
				Timestamp ts = new Timestamp(time);


				if(userA == null || userB == null){
					Socket 	s = ss.accept();
					ServerConnection sc = new ServerConnection(s, this);
					dis = new DataInputStream(s.getInputStream());
					if(usernameA=="" || usernameB == "")
						sc.user = dis.readUTF();

					if(usernameA=="")
						usernameA = sc.user;
					else if(usernameB=="")
						usernameB = sc.user;


					System.out.println(ts+": Client has connected");

					activityLog.add( ts+ ": Client has connected ");
					sc.start();

					
					if(userA == null)
					{
						userA = sc;

						if(usernameA != "")
							userA.user = usernameA;
						System.out.println("User A has connected");
					}

					else if (userB == null)
					{
						userB = sc;

						if(usernameB != "")
							userB.user = usernameB;


						System.out.println("User B has connected");
					}


				}


				



			}while(userA != null || userB != null);
		}
		catch(IOException e){
			e.printStackTrace();


		} finally
		{
			System.out.println("Server: Connection is terminated.");
		}
	}

	public void shutdownServer(){

		this.shouldRun = false;

	}

}