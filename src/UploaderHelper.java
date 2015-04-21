import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
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
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class UploaderHelper {
	
/*	public static void saveByteArrayToFile(String filename, byte[] bytes) throws IOException{
		Path path = Paths.get("//src/"+filename);
		Files.write(path, bytes);
	}*/
	
	public static void saveBytes(String filename, byte[] bytes) throws IOException{
		FileOutputStream output = new FileOutputStream(new File("src//"+filename));
		output.write(bytes);
	}
	
	public static byte[] convertFileToByteArray(String filename){
		Path path = Paths.get(filename);
		File file = new File("src//"+filename);
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
/*            for (int i = 0; i < b.length; i++) {
            	System.out.print((char)b[i]);
            }*/
        }catch (FileNotFoundException e) {
        	System.out.println("File Not Found.");
            e.printStackTrace();
        }catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
       }
        
       return b;        
	}
	
	public static byte[] decrypt(PublicKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
	    cipher.init(Cipher.DECRYPT_MODE, key);  
	    return cipher.doFinal(ciphertext);
	}
	
	public static byte[] decryptPri(PrivateKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
	    cipher.init(Cipher.DECRYPT_MODE, key);  
	    return cipher.doFinal(ciphertext);
	}
	
/*	public static PublicKey readPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
	    X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(convertFileToByteArray(filename));
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    return keyFactory.generatePublic(publicSpec);       
	}*/
	
	public static String readFromClient(Socket sock) throws IOException{
		InputStream inp = sock.getInputStream();
    	InputStreamReader inpReader= new InputStreamReader(inp);
        BufferedReader buffReader = new BufferedReader(inpReader);
        String text = buffReader.readLine();
        return text;
	}
	
	public static void encryptPrivateAndSend(String msg, Socket sock) throws Exception{
		PrivateKey privateKey = getPrivateKey("src//privateServer.der");
    	byte[] message = msg.getBytes("UTF8");
    	byte[] secret = encrypt(privateKey, message);
    	DataOutputStream dOut = new DataOutputStream(sock.getOutputStream());
    	dOut.writeInt(secret.length);
    	dOut.write(secret);
    	//dOut.flush();
    	//dOut.close();
	}
	
	public static void encryptPublicAndSend(byte[] msg, PublicKey key, Socket sock) throws Exception{
		//PublicKEy pubKey = getPrivateKey("src//privateServer.der");
    	//byte[] message = msg.getBytes("UTF8");
    	byte[] secret = encryptPub(key, msg);
    	DataOutputStream dOut = new DataOutputStream(sock.getOutputStream());
    	dOut.writeInt(secret.length);
    	dOut.write(secret);
    	//dOut.flush();
    	//dOut.close();
	}
	
	public static void sendBytes(byte[] msg, Socket sock) throws Exception{
		
    	DataOutputStream dOut = new DataOutputStream(sock.getOutputStream());
    	dOut.writeInt(msg.length);
    	dOut.write(msg);
    	//dOut.close();
	}
	
	public static void sendBytesWithoutLength(byte[] msg, Socket sock) throws Exception{
		
    	DataOutputStream dOut = new DataOutputStream(sock.getOutputStream());
//    	dOut.writeInt(msg.length);
    	dOut.write(msg);
    	//dOut.close();
	}
	
/*	public static void sendMultipleBytes(byte[][] msg, Socket sock) throws Exception{
		
    	DataOutputStream dOut = new DataOutputStream(sock.getOutputStream());
    	for (int i = 0; i < msg.length; i++) {
			dOut.write(msg[i]);
		}
	}*/
	
	
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
	
	public static byte[] encryptPub(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
	    cipher.init(Cipher.ENCRYPT_MODE, key);  
	    return cipher.doFinal(plaintext);
	}
	
	public static byte[][] divideArray(byte[] source, int chunksize) {
		 byte[][] Bytereturn = new byte[(int)Math.ceil(source.length / (double)chunksize)][chunksize];
		 int start = 0;
		 for(int i = 0; i < Bytereturn.length; i++) {
			 Bytereturn[i] = Arrays.copyOfRange(source,start, start + chunksize);
			 start += chunksize ;
		 }
		 return Bytereturn;
	}
	
	public static byte[] receiveByteArray(Socket sock) throws IOException{
		DataInputStream dIn = new DataInputStream(sock.getInputStream());
		int length = dIn.readInt();
System.out.println(length);
	    byte[] message = new byte[length];
	    dIn.readFully(message, 0, message.length); // read the message

	    return message;
	}
	
/*	public static byte[] glueByteArray(byte[][] bytearrays){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for(int i=0; i<bytearrays[1].length; i++){
			outputStream.write(bytearrays[1][i]);
		}
		byte[] returnArray = outputStream.toByteArray();
		return returnArray;
	}*/
}
