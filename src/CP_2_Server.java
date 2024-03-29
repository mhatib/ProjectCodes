import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class CP_2_Server {
	public static void main(String[] args) throws Exception{
		String text = "";
		String password = "3ncrypt3d";
		//Establishing connection
		ServerSocket serverSocket = new ServerSocket(4321);
    	System.out.println("---Awaiting Client Connection---");
        Socket socket = serverSocket.accept();     
    	System.out.println("---Client Connected---");

        //Receive nonce from client
    	String nonce = UploaderHelper.readFromClient(socket);    	
        System.out.println(nonce);
        
        //Hash with client password		
  		MessageDigest md = MessageDigest.getInstance("MD5");
  		md.update((nonce+=password).getBytes());
  		byte[] dgst = md.digest();
  		
        //Encrypt nonce with private key and send        
		//System.out.println("Nonce:"+new String(nonce.getBytes(),"UTF8"));
		
        UploaderHelper.encryptPrivateAndSend(dgst, socket);        
        
        text = UploaderHelper.readFromClient(socket);
        System.out.println(text);
        
       //Send server cert to client
        byte[] serverCert = UploaderHelper.convertFileToByteArray("Signed_CSECA_server_key.crt");
        UploaderHelper.sendBytes(serverCert, socket);
        
        //Receive symmetric key from client        
        byte[] encKey = UploaderHelper.receiveByteArray(socket);
        
        //Decrypt symmetric key
        PrivateKey privateKey = UploaderHelper.getPrivateKey("src//privateServer.der");
        byte[] key = UploaderHelper.decryptPri(privateKey, encKey);
        
        //Receive encrypted file from client
        byte[] ncMsg = UploaderHelper.receiveByteArray(socket);
        
        //Decrypt file and save file
        byte[] decMsg = decryptAES(ncMsg, key);
        UploaderHelper.saveBytes("dispGet.pdf",decMsg);
        
        //Establish success and end connection
        PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println("Tansfer complete");
        System.out.println("Server complete"); 
        serverSocket.close();
	}
	
	public static byte[] decryptAES(byte[] valueToEnc, byte[] keyVal) throws Exception { 
		 Key key = generateKey(keyVal); 
		 Cipher c = Cipher.getInstance("AES/ECB/NoPadding"); 
		 c.init(Cipher.DECRYPT_MODE, key); 
		 byte[] decValue = c.doFinal(valueToEnc);
		 return decValue;
	 } 
	 
	 private static Key generateKey(byte[] keyValue) throws Exception{
		 Key key = new SecretKeySpec(keyValue, "AES");
		 return key;
	 }
}
