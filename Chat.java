import java.io.*;
import java.util.*;
import java.net.*;

public class Chat
{
	ChatConnect cc;
	
	public static void main(String[] args){
		new Chat();
	}
	
	public Chat(){
		try{
			Socket s = new Socket("localhost", 3333);
			cc = new ChatConnect(s, this);
			
			cc.start();
			
			listenForInput();
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void listenForInput(){
		Scanner sc = new Scanner(System.in);
		
		while(true){
			while(!sc.hasNextLine()){
				try{
					Thread.sleep(1);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		
		String input = sc.nextLine();
		
		if(input.toLowerCase().equals("quit")){

			System.out.println("Disconnected");
			cc.sendStringToServer(input);

			break;	
		}
		
		cc.sendStringToServer(input);	
		}
		cc.close();
		System.exit(0);
	}

}