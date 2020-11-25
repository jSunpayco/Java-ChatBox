import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatBox extends Application 
{
	Stage window;
	Scene s1;
	
	Button send;
	Button img;
	Button dlB;
	TextArea tAr;
	Label lbl;
	Button log;
	TextField tFi;
	ListView<Textinfo> fileList;
	
	Socket s;
	int hasConn = 0;
	int user = 0;
	String username;
	
	String fileName;
	String fileType;
	byte[] b;
	Textinfo data;
	
	ChatReaderThread crt;
	
	DataOutputStream dout;
	DataInputStream din;
	ObjectOutputStream oos;
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage;
		
		AnchorPane layout = new AnchorPane();
		
		tFi = new TextField();
		tFi.setLayoutX(13);
		tFi.setLayoutY(460);
		tFi.setPrefSize(360,34);
		layout.getChildren().add(tFi);
		
		send = new Button();
		send.setText("Enter");
		send.setLayoutX(393);
		send.setLayoutY(460);
		send.setPrefSize(60,18);
		layout.getChildren().add(send);
		send.setOnAction(e -> LoginListener());
		
		img = new Button();
		img.setText("Add File");
		img.setLayoutX(393);
		img.setLayoutY(364);
		img.setPrefSize(75,26);
		layout.getChildren().add(img);
		img.setDisable(true);
		img.setOnAction(e -> uploadImage());
		
		dlB = new Button();
		dlB.setText("Download");
		dlB.setLayoutX(470);
		dlB.setLayoutY(364);
		dlB.setPrefSize(90,26);
		layout.getChildren().add(dlB);
		dlB.setDisable(true);
		dlB.setOnAction(e -> downloadImage());
		
		tAr = new TextArea();
		tAr.setEditable(false);
		tAr.setLayoutX(13);
		tAr.setLayoutY(14);
		tAr.setPrefSize(374,335);
		layout.getChildren().add(tAr);
		
		lbl = new Label("Port Number");
		lbl.setLayoutX(14);
		lbl.setLayoutY(433);
		lbl.setPrefSize(110,18);
		layout.getChildren().add(lbl);
		
		fileList = new ListView<>();
		fileList.setLayoutX(393);
		fileList.setLayoutY(14);
		fileList.setPrefSize(200,335);
		layout.getChildren().add(fileList);
		
		log = new Button("Exit");
		log.setLayoutX(460);
		log.setLayoutY(460);
		log.setPrefSize(60,18);
		layout.getChildren().add(log);
		log.setOnAction(e -> DisconnectListener());
		
		s1 = new Scene(layout, 605, 500);
		
        window.setScene(s1);
		window.setTitle("DLSUsap");
        window.show();
		window.setOnCloseRequest(e -> DisconnectListener());
		tAr.appendText("Please enter a port number\n");
	}
	
	
	public void connectToServer(int port){
		try{
			this.s = new Socket("localhost", port);
			
			hasConn = 1;
			tFi.clear();
			tAr.appendText("Connected to Server! Please enter a username\n");
			lbl.setText("Enter Username:");
			send.setText("Login");
			crt = new ChatReaderThread(s, this);
			crt.start();	
		}
		catch(UnknownHostException e){
			e.printStackTrace();
			AlertBox.display("Error!", "Unknown Host Exception");
			System.exit(0);
		}
		catch(IOException e){
			e.printStackTrace();
			AlertBox.display("Error!", "Invalid Port Number");
		}
	}
	
	public Stage getStage(){
		return this.window;
	}
	
	public ChatReaderThread getC(){
		return this.crt;
	}
	
	public void setName(){
		this.username = "";
	}
	
	public ChatBox getChat(){
		return this;
	}
	
	public void LoginListener() {
				
		if(tFi.getText().trim().isEmpty()){
			AlertBox.display("Error", "Write Something");
		}else{
							
			if(hasConn == 0){		
				connectToServer(Integer.parseInt(tFi.getText().trim()));
							
			}else if(username == null || username.isEmpty()){
				try{
					username = tFi.getText();
					dout = new DataOutputStream(this.s.getOutputStream());
					dout.writeUTF(username);
					dout.flush();
					tFi.clear();
					lbl.setText("Enter Message:");
					send.setText("Send");
					img.setDisable(false);
					dlB.setDisable(false);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			else{
				try{
					dout.writeUTF(username + ": " + tFi.getText());
					dout.flush();
					tFi.clear();	
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}			
				
	}
	
	public void uploadImage() {
				
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(
			 new FileChooser.ExtensionFilter("Text Files", "*.txt"),
             new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
		);
		File file = fc.showOpenDialog(getStage());
			
		if(file != null){
		
			try{
				fileName = file.getName();
				fileType = fileName.substring(fileName.lastIndexOf("."));
				
				b = new byte[(int) file.length()];
				din = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				din.readFully(b, 0, b.length);
				Textinfo data = new Textinfo(b, (int) file.length(), fileType, username, fileName);
				
				dout.writeUTF("-Rt#eY<H");
				dout.writeUTF(username);
				oos = new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(data);
				
				dout.flush();
				oos.flush();
				
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	
	public void downloadImage(){
		
		Textinfo data = (Textinfo) fileList.getSelectionModel().getSelectedItem();
		FileChooser fc = new FileChooser();
		
		if(data.getType().equals(".txt")){
			fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Text Files", "*.txt")
			);
		}else{
			fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
			);
		}
		
		File file = fc.showSaveDialog(getStage());
		try{
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data.getB());
			fos.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void DisconnectListener () {
		try{
			crt.exit = true;
			dout.writeUTF("?41a420");
			s.close();
			Platform.exit();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
}