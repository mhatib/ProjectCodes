import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

import javax.security.cert.X509Certificate;


public class Client2 {
	public static void main(String[] args) throws Exception {
//		byte[] signature = null;
//        String hostName = "localhost";        
//        int portNumber = 4321;
//        Socket socket = new Socket(hostName, portNumber);
//        PrintWriter out =new PrintWriter(socket.getOutputStream(), true);                   
//        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        String inputLine;
        
        //Authentication Protocol begins here
        
        //Create X509Certificate object from CA.crt
        InputStream inStream = new FileInputStream("src//CA.crt");
        X509Certificate CAcert = X509Certificate.getInstance(inStream);
        
        
        //fOR TESTING PURPOSES
        InputStream a = new FileInputStream("src//Server_Cert_eD");
        X509Certificate serverCert = X509Certificate.getInstance(a);
       
        
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
        
        
//        
//        if(in.readLine().equals("Hello SecStore, please prove your identity")){
////        	out.println(x);
//        }else{
//        	System.out.println("Wrong password!");
//        }
//        
//        File file = new File("src//disp.pdf");
//        double length = file.length();
//        if (length > Integer.MAX_VALUE) {
//            System.out.println("File is too large. Final test");
//        }
//        byte[] bytes = new byte[(int) length];
//        
//        socket.setSoTimeout(10000);
//        FileInputStream fileIn = new FileInputStream(file);
//        BufferedInputStream buffIn = new BufferedInputStream(fileIn);
//        BufferedOutputStream buffOut = new BufferedOutputStream(socket.getOutputStream());
//        
//        int count;
//        while ((count = buffIn.read(bytes)) > 0) {
//        	buffOut.write(bytes, 0, count);
//        }
//
//        fileIn.close();
//        buffIn.close();
//        socket.close();
//        
         	
    	  
    }
}

