import java.io.*;

public class Textinfo implements Serializable{

	public Textinfo(byte[] b, int size, String type, String sender, String name){
		setB(b);
		setSize(size);
		setType(type);
		setSender(sender);
		setName(name);
	}
	
	public void setB(byte[] b){
		this.b = b;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public void setType(String text){
		this.type = text;
	}
	
	public void setSender(String text){
		this.sender = text;
	}
	
	public void setName(String text){
		this.name = text;
	}
	
	public byte[] getB(){
		return this.b;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getSender(){
		return this.sender;
	}
	
	public String getName(){
		return this.name;
	}
	
	@Override
    public String toString() {
        return "[" + sender + "] " + name;
    }
	
	private byte[] b;
	private int size;
	private String type;
	private String sender;
	private String name;
	
}