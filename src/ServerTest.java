import java.net.ServerSocket;
import java.net.Socket;


public class ServerTest {
	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket = new ServerSocket(4321);
    	System.out.println("(... expecting connection ...)");
        Socket socket = serverSocket.accept();     
    	System.out.println("(... connection established ...)");
    	
        String init = "Hello SecStore, please prove your identity";
        String initR = "Hello, this is SecStore";
        
    	String text = UploaderHelper.readFromClient(socket);
    	
        System.out.println(text);
        if (text.equals(init)){
        	UploaderHelper.encryptPrivateAndSend(initR, socket);
        }
        
        text = UploaderHelper.readFromClient(socket);
        System.out.println(text);
        UploaderHelper.sendCert(socket);
        
        serverSocket.close();
	}
}
