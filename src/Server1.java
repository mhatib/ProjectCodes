import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Server1 {
	public static void main(String[] args) throws Exception {
		InputStream is = null;
		InputStreamReader isr = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedReader br = null;
        		
		//pout.println("Hello SecStore, please prove your identity");
        
		ServerSocket serverSocket = new ServerSocket(4321);
    	System.out.println("(... expecting connection ...)");
        Socket socket = serverSocket.accept();     
    	System.out.println("(... connection established ...)");
    	
    	
    	String init = "Hello SecStore, please prove your identity";
    	String initR = "Hello, this is SecStore";
    	
    	is = socket.getInputStream();
    	isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        String text = br.readLine();
    	if (text.equals(init)){
    		PrivateKey privateKey = getPrivateKey("src//privateServer.der");
        	byte[] message = initR.getBytes("UTF8");
        	byte[] secret = encrypt(privateKey, message);
        	DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        	dOut.writeInt(secret.length); 
        	dOut.write(secret);
        	

    	}
    	
    	
    	PublicKey publicKey = readPublicKey("src//publicServer.der");
        
        
        
        //byte[] recovered_message = decrypt(publicKey, secret);
        //System.out.println(new String(recovered_message, "UTF8"));
        
    	
        

    	
        /*int bufferSize = 0;
        is = socket.getInputStream();

        bufferSize = socket.getReceiveBufferSize();
        System.out.println("Buffer size: " + bufferSize);
        
        //TODO: add same file type as client. Perhaps get file name and parse for file type?
        fos = new FileOutputStream("src//disp3.pdf");
        bos = new BufferedOutputStream(fos);
        byte[] bytes = new byte[bufferSize];

        int count;
        
        while ((count = is.read(bytes)) > 0) {
            bos.write(bytes, 0, count);
        }

        bos.flush();
        bos.close();
        is.close();
        socket.close();
        serverSocket.close();*/
	}
	
	public static PrivateKey getPrivateKey(String filename) throws Exception {

        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
	
	public static byte[] encrypt(PrivateKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
	    cipher.init(Cipher.ENCRYPT_MODE, key);  
	    return cipher.doFinal(plaintext);
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

