import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;


public class ClientTest {
	public static void main(String[] args) throws UnknownHostException, IOException, CertificateException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		byte[] message = null;

		String hostName = "localhost";        
	    int portNumber = 4321;
	    Socket socket = new Socket(hostName, portNumber);
	    
	    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println("Hello SecStore, please prove your identity");
		
		message = receiveByteArray(socket);
		System.out.println(message.toString());
		pout.println("Send me your certificate signed by CA");
		receiveCertificate(socket);
		
		//CA public keysigned_CS
    	InputStream inStream = new FileInputStream("src//cserv.crt");
        X509Certificate CAcert = X509Certificate.getInstance(inStream);
        PublicKey CAKey = CAcert.getPublicKey();
        
        byte[] dec = decrypt(CAKey,message);
		System.out.println(new String(dec,"UTF8"));
		
	}
	
	public static byte[] decrypt(PublicKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
	    cipher.init(Cipher.DECRYPT_MODE, key);  
	    return cipher.doFinal(ciphertext);
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
	
    static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }
	
	public static byte[] receiveByteArray(Socket sock) throws IOException{
		DataInputStream dIn = new DataInputStream(sock.getInputStream());
	    byte[] message = new byte[128];
	    dIn.readFully(message, 0, message.length); // read the message
	    return message;
	}
	
	public static void receiveCertificate(Socket sock) throws IOException{		
		InputStream in = sock.getInputStream();
        OutputStream out = new FileOutputStream("src//cserv.crt");
        copy(in, out);
        out.close();
        in.close();
	}
	
	public static byte[] convertFileToByteArray(String filename){
		Path path = Paths.get("src/"+filename);
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
    

}
