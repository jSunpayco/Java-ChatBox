import java.io.*;
import java.net.*;
import javax.imageio.*;
import java.awt.image.*;
import javafx.application.*;

public class ChatReaderThread extends Thread{

    Socket socket;
    ChatBox client;
	
	DataInputStream din;
	ObjectInputStream oin;
	
	boolean exit = false;
	
    public ChatReaderThread(Socket socket, ChatBox client){
        this.socket = socket;
        this.client = client;
    }
	
    public void run(){
		
		try{
			din = new DataInputStream(socket.getInputStream());
			while(!exit){
			
				try{
					
					
					String reply = din.readUTF();
					
					if(reply.equals("-Rt#eY<H")){ /* File Receiving */
						
						oin = new ObjectInputStream(socket.getInputStream());
						Textinfo data = (Textinfo) oin.readObject();
						Platform.runLater(() -> client.fileList.getItems().add(data));
						
					}else if (!reply.equals("?41a420")){ /* Text Message */
						
						Platform.runLater(() -> {                    
							client.tAr.appendText(reply +"\n");
						});
						
					}
					
				}catch(IOException e){
					e.printStackTrace();
				}catch(ClassNotFoundException c){
					c.printStackTrace();
				}
			}
			
			close();
		}catch(IOException e){
			e.printStackTrace();
		}
    }

	public void close(){
		
		try{
			din.close();
			socket.close();
		}catch(IOException e){
			
		}
	}
	
}
