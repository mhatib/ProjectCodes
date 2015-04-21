import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.X509Certificate;


public class CP_1 {
	public static void main(String[] args) throws Exception{
			byte[] message = null;
			String hostName = "10.12.20.84";        
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
			
			//Block for througput testing
			Long startTime = System.currentTimeMillis();

			byte[] file = UploaderHelper.convertFileToByteArray("csetextbook.pdf");
			byte[][] encFile = UploaderHelper.divideArray(file,117);
//			System.out.println("Number of blocks: "+encFile.length);
//			System.out.println("Block size: "+encFile[0].length);

			//Let the server know the total number of blocks
			pout.println(encFile.length);
			int lastBlockSize = encFile.length%117;
			pout.println(lastBlockSize);
			DataOutputStream fileOut = new DataOutputStream(socket.getOutputStream());
//			System.out.println("Start encryption");
			for(int i=0; i<encFile.length; i++){
//				System.out.println("Encrypting "+i+" block");
				byte[] a=UploaderHelper.encryptPub(CAKey, encFile[i]);
				fileOut.writeInt(a.length);
				fileOut.write(a);
			}
//			System.out.println("Encryption Completed");
//			System.out.println("Transfer complete");
			String completeM = UploaderHelper.readFromClient(socket);
	        //Block for througput testing
	        Long endTime = System.currentTimeMillis();
	        System.out.println(endTime-startTime);
			System.out.println(completeM);
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

