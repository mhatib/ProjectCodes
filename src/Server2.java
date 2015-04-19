import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

public class Server2 {
	byte[] decryptedByteArray;
	public static void main(String[] args) throws Exception {
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
        byte[] file = UploaderHelper.convertFileToByteArray("Signed_CSECA_server_key.crt");
        UploaderHelper.sendBytes(file, socket);
        
        text = UploaderHelper.readFromClient(socket);
        int blockn = Integer.parseInt(text);
        
        PrivateKey server_private_key = UploaderHelper.getPrivateKey("src//privateServer.der");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		for(int i=0; i<blockn; i++){
			byte[] tempByteArrayBlock = UploaderHelper.receiveByteArray(socket);
			tempByteArrayBlock=UploaderHelper.decryptPri(server_private_key, tempByteArrayBlock);
			outputStream.write(tempByteArrayBlock);
		}
		byte decryptedByteArray[] = outputStream.toByteArray();
		UploaderHelper.saveBytes("savedFile.pdf", decryptedByteArray);
        System.out.println("complete");
	}
}

