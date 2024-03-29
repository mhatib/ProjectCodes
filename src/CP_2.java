import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.X509Certificate;


public class CP_2 {
	public static void main(String[] args) throws Exception{
		final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
		String password = "3ncrypt3d";
		//Establish connection to server
		String hostName = "10.12.20.84";        
	    int portNumber = 4321;
	    String saveFileName = "disp.pdf";	 	    
	    Socket socket = new Socket(hostName, portNumber);	    	    
		
	    //Write nonce to server
	    String nonce = ""+System.currentTimeMillis();
	    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println(nonce);
		
		//Receive encrypted nonceMsg from server
		byte[] nonceMsg = UploaderHelper.receiveByteArray(socket);
		pout.println("Send me your certificate signed by CA");
		
		//Receive serverCert and save to file				
		byte[] cert = UploaderHelper.receiveByteArray(socket);
		UploaderHelper.saveBytes("cserve.crt",cert);		
		
		//Get public key from cert
    	FileInputStream inStream = new FileInputStream("src//cserve.crt");
        X509Certificate serverCert = X509Certificate.getInstance(inStream);
        PublicKey serverKey = serverCert.getPublicKey();
        
        //Authentication Protocol begins here        
        //Retrieve public key from serverCert
        InputStream inStream2 = new FileInputStream("src//CA.crt");
        X509Certificate CAcert = X509Certificate.getInstance(inStream2);       
        PublicKey CAKey = CAcert.getPublicKey();
        
        //Check the validity and verify signed certificate.
        try{
        	System.out.println("Checking CA's validity...");
        	CAcert.checkValidity();
        }catch(Exception e){
        	System.out.println(e);
        	System.out.println("CSE-CA Certificate is invalid!");
        }
        try{
        	System.out.println("Checking the validity of server's certificate...");
        	serverCert.checkValidity();
        }catch(Exception e){
        	System.out.println(e);
        	System.out.println("Server's certificate is invalid!");
        }
        try{
        	System.out.println("Verifying CSE-CA and server's certificates");
        	serverCert.verify(CAKey);
        }catch(Exception e){
        	System.out.println(e);
        	System.out.println("CSE-CA and Server's certificate failed in verification!");
        }
        
        //Hash with password
        MessageDigest md = MessageDigest.getInstance("MD5");
		md.update((nonce+=password).getBytes());
		byte[] dgst = md.digest();
		
		//Decrypt nonceMsg
        byte[] dec = UploaderHelper.decrypt(serverKey,nonceMsg);
		//System.out.println("Nonce:"+new String(dec,"UTF8"));
		
		if (Arrays.equals(dgst, dec)){
			System.out.println("Server verified");
		}
		else{
			System.out.println("False server. Possible MitM attack. Closing connection");
			socket.close();
		}
		
		//Send AES keyvalue to server encrypted with server pubKey
		UploaderHelper.sendBytes(UploaderHelper.encryptPub(serverKey, keyValue), socket);
		
		//Encrypt file with AES and send
		byte[] file = UploaderHelper.convertFileToByteArray(saveFileName);
		byte[] encFile = encryptAES(file);		
		UploaderHelper.sendBytes(encFile, socket);
		
		String line = UploaderHelper.readFromClient(socket);
		System.out.println(line);
		//End task
		System.out.println("Client completed");
		socket.close();
	}
	
	 public static byte[] encryptAES(byte[] file) throws Exception { 
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
