import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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


public class UploaderHelper {
	
	public void saveByteArrayToFile(String filename, byte[] bytes) throws IOException{
		Path path = Paths.get("//src/"+filename);
		Files.write(path, bytes);
	}
	
	public static byte[] convertFileToByteArray(String filename){
		Path path = Paths.get("//src/"+filename);
		File file = path.toFile();
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
            	System.out.print((char)b[i]);
            }
        }catch (FileNotFoundException e) {
        	System.out.println("File Not Found.");
            e.printStackTrace();
        }catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
       }
        
       return b;        
	}
	
	public void sendByteArray(Socket socket, byte[] bytes) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedOutputStream buffOut = new BufferedOutputStream(socket.getOutputStream());
		BufferedInputStream buffIn = null;
		int count = buffIn.read(bytes);
		while ((count = buffIn.read(bytes)) > 0) {
        	buffOut.write(bytes, 0, count);
        } 
		in.close();
		buffOut.close();
		buffIn.close();
	}
	
	public byte[] receiveByteArray(Socket socket, byte[] bytes){
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedOutputStream buffOut = new BufferedOutputStream(socket.getOutputStream());
		BufferedInputStream buffIn = null;
		return b;
	}
	
	public static byte[] decrypt(PublicKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
	    cipher.init(Cipher.DECRYPT_MODE, key);  
	    return cipher.doFinal(ciphertext);
	}
	
	public static PublicKey readPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
	    X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(convertFileToByteArray(filename));
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    return keyFactory.generatePublic(publicSpec);       
	}
}
