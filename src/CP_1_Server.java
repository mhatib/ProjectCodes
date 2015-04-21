import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

public class CP_1_Server {
	byte[] decryptedByteArray;
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(4321);
    	System.out.println("(... expecting connection ...)");
        Socket socket = serverSocket.accept();     
    	System.out.println("(... connection established ...)");
    	PrintWriter pout = new PrintWriter(socket.getOutputStream(),true);
		DataInputStream dIn = new DataInputStream(socket.getInputStream());
    	
    	String init = "Hello SecStore, please prove your identity";
        String initR = "Hello, this is SecStore";
        
        String text = UploaderHelper.readFromClient(socket);
        
        System.out.println(text);
        if (text.equals(init)){
        	UploaderHelper.encryptPrivateAndSend(initR, socket);
        }
        byte[] file = UploaderHelper.convertFileToByteArray("Signed_CSECA_server_key.crt");
        UploaderHelper.sendBytes(file, socket);
        
        String text2 = UploaderHelper.readFromClient(socket);
        System.out.println("Client: " +text2);
        String text3 = UploaderHelper.readFromClient(socket);
        System.out.println("Length per block " +text3);
        int blockn = Integer.parseInt(text3);
        System.out.println(blockn);
        
        PrivateKey server_private_key = UploaderHelper.getPrivateKey("src//privateServer.der");
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		System.out.println("Receiving starts");
		
//		//Block for througput testing
//		Long startTime = System.currentTimeMillis();
//        //Block for througput testing
//        Long endTime = System.currentTimeMillis();
//        System.out.println();
		
		byte[] tempByteArrayBlock = new byte[blockn];
        for(int i=0; i<blockn; i++){
			System.out.println(i);
			dIn.read(tempByteArrayBlock,0, blockn);
//			tempByteArrayBlock=UploaderHelper.decryptPri(server_private_key, tempByteArrayBlock);
			outputStream.write(tempByteArrayBlock);
		}
        pout.println("Receiving complete");
		System.out.println("Receiving complete");
		byte decryptedByteArray[] = outputStream.toByteArray();
		UploaderHelper.saveBytes("savedFile1.pdf", decryptedByteArray);
        System.out.println("complete");
        
	}
}

