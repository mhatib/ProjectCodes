import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.X509Certificate;


public class CP_2 {
	public static void main(String[] args) throws Exception{
		
		//Establish connection to server
		byte[] message = null;
		final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
		String hostName = "localhost";        
	    int portNumber = 4321;
	    Socket socket = new Socket(hostName, portNumber);
	    
	    //Write nonce to server
	    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println("Hello SecStore, please prove your identity");
		
		//Receive envrypted nonceMsg from server
		message = UploaderHelper.receiveByteArray(socket);
		
		//Verify nonceMsg
		
		//Receive serverCert and save to file
		pout.println("Send me your certificate signed by CA");		
		byte[] cert = UploaderHelper.receiveByteArray(socket);
		UploaderHelper.saveBytes("cserve.crt",cert);
		
		//Verify cert with CA.crt
		
		//Get public key from cert
    	FileInputStream inStream = new FileInputStream("src//cserve.crt");
        X509Certificate CAcert = X509Certificate.getInstance(inStream);
        PublicKey CAKey = CAcert.getPublicKey();
        
        //Decrypt nonceMsg
        byte[] dec = UploaderHelper.decrypt(CAKey,message);
		System.out.println(new String(dec,"UTF8"));
		
		//Send AES keyvalue to server encrypted with server pubKey
		UploaderHelper.sendBytes(UploaderHelper.encryptPub(CAKey, keyValue), socket);
		
		//Encrypt file with AES and send
		byte[] file = UploaderHelper.convertFileToByteArray("disp.pdf");
		byte[] encFile = encrypt(file);		
		UploaderHelper.sendBytes(encFile, socket);
		
		//End task
		System.out.println("Client completed");
	}
	
	 public static byte[] encrypt(byte[] file) throws Exception { 
		 Key key = generateKey(); 
		 Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding"); 
		 c.init(Cipher.ENCRYPT_MODE, key); 
		 byte[] encValue = c.doFinal(file);  
		 return encValue; 
	 } 
	 
	 private static Key generateKey() throws Exception{
		 final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' }; 
		 Key key = new SecretKeySpec(keyValue, "AES");
		 return key;
	 }
}
