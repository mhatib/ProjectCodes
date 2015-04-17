import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class ServerTest {
	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket = new ServerSocket(4321);
    	System.out.println("(... expecting connection ...)");
        Socket socket = serverSocket.accept();     
    	System.out.println("(... connection established ...)");
    	
        String init = "Hello SecStore, please prove your identity";
        String initR = "Hello, this is SecStore";
    	String text = readFromClient(socket);
    	
        System.out.println(text);
        if (text.equals(init)){
        	encryptPrivateAndSend(initR, socket);
        }
        
        text = readFromClient(socket);
        System.out.println(text);
        sendCert(socket);
	}
	
    static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }
	
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
    	dOut.write(secret);
    	//dOut.close();
	}
	
	public static void sendCert(Socket sock) throws IOException{
		File file = new File("src//Signed_CSECA_server_key.crt");
        InputStream in = new FileInputStream(file);
        OutputStream out = sock.getOutputStream();
        copy(in, out);
        out.close();
        in.close();
        System.out.println("done");
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
}
