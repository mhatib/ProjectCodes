import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;


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
        byte[] file = UploaderHelper.convertFileToByteArray("Signed_CSECA_server_key.crt");
        UploaderHelper.sendBytes(file, socket);
        
        text = UploaderHelper.readFromClient(socket);
        System.out.println(text);
        byte[] ncMsg = UploaderHelper.receiveByteArray(socket);
 
        byte[] decMsg = decrypt(ncMsg);
        UploaderHelper.saveBytes("dispGet.pdf",decMsg);
        System.out.println("Server complete"); 
        //serverSocket.close();
	}
	
	public static byte[] decrypt(byte[] valueToEnc) throws Exception { 
		 Key key = generateKey(); 
		 Cipher c = Cipher.getInstance("AES/ECB/NoPadding"); 
		 c.init(Cipher.DECRYPT_MODE, key); 

		 byte[] decValue = c.doFinal(valueToEnc);

		 return decValue;
	 } 
	 
	 private static Key generateKey() throws Exception{
		 final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' }; 
		 Key key = new SecretKeySpec(keyValue, "AES");
/*		 SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES"); 
		 key = keyFactory.generateSecret(new DESKeySpec(keyValue)); */
		 return key;
	 }
}
