import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

public class CP_1_Server {
	byte[] decryptedByteArray;
	public static void main(String[] args) throws Exception {
		
		//Establish connection
		ServerSocket serverSocket = new ServerSocket(4321);
    	System.out.println("(... expecting connection ...)");
        Socket socket = serverSocket.accept();     
    	System.out.println("(... connection established ...)");
    	
    	//Get nonce from client
    	PrintWriter pout = new PrintWriter(socket.getOutputStream(),true);    	
    	String nonce = UploaderHelper.readFromClient(socket);    	
        System.out.println(nonce);

        //Encrypt nonce with private key and send
        UploaderHelper.encryptPrivateAndSend(nonce.getBytes(), socket);
        
        //Send certificate to client
        byte[] file = UploaderHelper.convertFileToByteArray("Signed_CSECA_server_key.crt");
        UploaderHelper.sendBytes(file, socket);
        
        String text2 = UploaderHelper.readFromClient(socket);
        System.out.println("Client: " +text2);
        String text3 = UploaderHelper.readFromClient(socket);
        System.out.println("Number of block " +text3);
        int blockn = Integer.parseInt(text3);
        System.out.println(blockn);        
        
        PrivateKey server_private_key = UploaderHelper.getPrivateKey("src//privateServer.der");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.out.println("Receiving starts");
		
		//Recieve each block from client, decrypt and write to stream
        for(int i=0; i<blockn; i++){
			byte[] s=UploaderHelper.decryptPri(server_private_key, UploaderHelper.receiveByteArray(socket));
			outputStream.write(s);
		}
        
        pout.println("Receiving complete");
		System.out.println("Receiving complete");
		byte decryptedByteArray[] = outputStream.toByteArray();
		UploaderHelper.saveBytes("savedFile1.pdf", decryptedByteArray);
        System.out.println("Server complete");
        serverSocket.close();
        
	}
}

