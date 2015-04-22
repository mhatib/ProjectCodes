import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Arrays;

import javax.security.cert.X509Certificate;


public class CP_1 {
	public static void main(String[] args) throws Exception{
		String password = "3ncrypt3d";
		String hostName = "localhost";        
	    int portNumber = 4321;
	    Socket socket = new Socket(hostName, portNumber);
	    
	    //Write nonce to server
	    String nonce = "Hello SecStore, please prove your identity";
	    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println(nonce);
		
		//Receive encrypted nonceMsg from server
		byte[] nonceMsg = UploaderHelper.receiveByteArray(socket);
		
		pout.println("Send me your certificate signed by CA");
		
		byte[] cert = UploaderHelper.receiveByteArray(socket);
		UploaderHelper.saveBytes("cserve.crt",cert);
		
    	FileInputStream inStream = new FileInputStream("src//cserve.crt");
        X509Certificate serverCert = X509Certificate.getInstance(inStream);
        PublicKey serverKey = serverCert.getPublicKey();
        
        //Authentication Protocol begins here
        
        //Create X509Certificate object from CA.crt
        InputStream inStream2 = new FileInputStream("src//CA.crt");
        X509Certificate CAcert = X509Certificate.getInstance(inStream2);
        
        //Extract Public key of the CA from the CA.crt.
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
        
        byte[] dec = UploaderHelper.decrypt(serverKey,nonceMsg);
        
		//System.out.println(new String(dec,"UTF8"));
        if (Arrays.equals(dgst, dec)){
			System.out.println("Server verified");
		}
		else{
			System.out.println("False server. Possible MitM attack. Closing connection");
			socket.close();
		}
		//Block for througput testing
		//Long startTime = System.currentTimeMillis();

		byte[] file = UploaderHelper.convertFileToByteArray("disp.pdf");
		byte[][] encFile = UploaderHelper.divideArray(file,117);

		//Let the server know the total number of blocks
		pout.println(encFile.length);
		for(int i=0; i<encFile.length; i++){
			byte[] a=UploaderHelper.encryptPub(serverKey, encFile[i]);
			UploaderHelper.sendBytes(a, socket);
		}

		String completeM = UploaderHelper.readFromClient(socket);
        //Long endTime = System.currentTimeMillis();
        //System.out.println(endTime-startTime);
		System.out.println(completeM);
	}
}

