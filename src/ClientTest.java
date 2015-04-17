import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.X509Certificate;


public class ClientTest {
	public static void main(String[] args) throws Exception{
		byte[] message = null;

		String hostName = "localhost";        
	    int portNumber = 4321;
	    Socket socket = new Socket(hostName, portNumber);
	    
	    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println("Hello SecStore, please prove your identity");
		
		message = UploaderHelper.receiveByteArray(socket);
		pout.println("Send me your certificate signed by CA");
		
		byte[] cert = UploaderHelper.receiveByteArray(socket);
		UploaderHelper.saveBytes("cserve.crt",cert);
		
    	FileInputStream inStream = new FileInputStream("src//cserve.crt");
        X509Certificate CAcert = X509Certificate.getInstance(inStream);
        PublicKey CAKey = CAcert.getPublicKey();
        
        byte[] dec = UploaderHelper.decrypt(CAKey,message);
		System.out.println(new String(dec,"UTF8"));
		byte[] file = UploaderHelper.convertFileToByteArray("disp.pdf");
		byte[] encFile = encrypt(file);
		UploaderHelper.sendBytes(encFile, socket);
		System.out.println("Client completed");
	}
	
	 public static byte[] encrypt(byte[] file) throws Exception { 
		 Key key = generateKey(); 
		 Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding"); 
		 c.init(Cipher.ENCRYPT_MODE, key); 
		 byte[] encValue = c.doFinal(file); 
		 //String encryptedValue = new BASE64Encoder().encode(encValue); 
		 return encValue; 
	 } 
	 
	 private static Key generateKey() throws Exception{
		 final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' }; 
		 Key key = new SecretKeySpec(keyValue, "AES");
		 //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES"); 
		 //key = keyFactory.generateSecret(new DESKeySpec(keyValue)); 
		 return key;
	 }
}
