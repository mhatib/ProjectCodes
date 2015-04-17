import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Client1 {
	public static void main(String[] args) throws Exception {
		InputStream is = null;
		InputStreamReader isr = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedReader br = null;
		
        String hostName = "localhost";        
        int portNumber = 4321;
        Socket socket = new Socket(hostName, portNumber);   
        
        PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println("Hello SecStore, please prove your identity");
		String initR = "Hello, this is SecStore";
		boolean trust = false;
		
		
		DataInputStream dIn = new DataInputStream(socket.getInputStream());
		int length = dIn.readInt();                    // read length of incoming message
		if(length>0) {
		    byte[] message = new byte[length];
		    dIn.readFully(message, 0, message.length); // read the message
		    PublicKey publicKey = readPublicKey("src//publicServer.der");
	        byte[] recovered_message = decrypt(publicKey, message);
	        String received = new String(recovered_message, "UTF8");
	        if (received.equals(initR)){
	        	trust = true;
	        }
		}
		
		if (trust){
			System.out.println("Let's go");
		}
        
        
        
/*        File file = new File("src//disp.pdf");
        double length = file.length();
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large. Final test");
        }
        byte[] bytes = new byte[(int) length];
        
       
        socket.setSoTimeout(10000);
        FileInputStream fileIn = new FileInputStream(file);
        BufferedInputStream buffIn = new BufferedInputStream(fileIn);
        BufferedOutputStream buffOut = new BufferedOutputStream(socket.getOutputStream());
        
        int count;
        while ((count = buffIn.read(bytes)) > 0) {
        	buffOut.write(bytes, 0, count);
        }

        fileIn.close();
        buffIn.close();
        socket.close();*/
        
         	
    	  
    }
	
	public static byte[] decrypt(PublicKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
	    cipher.init(Cipher.DECRYPT_MODE, key);  
	    return cipher.doFinal(ciphertext);
	}
	
	public static byte[] readFileBytes(String filename) throws IOException
	{
	    Path path = Paths.get(filename);
	    return Files.readAllBytes(path);        
	}

	public static PublicKey readPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
	    X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(readFileBytes(filename));
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    return keyFactory.generatePublic(publicSpec);       
	}
}

