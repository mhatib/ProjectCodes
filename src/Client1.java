import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;


public class Client1 {
	public static void main(String[] args) throws Exception {
        String hostName = "localhost";        
        int portNumber = 4321;
        Socket socket = new Socket(hostName, portNumber);        
        
        File file = new File("src//disp.pdf");
        double length = file.length();
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large. Final test");
        }
        byte[] bytes = new byte[(int) length];
        
        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream("src//publicServer.der");
        ks.load(fis, "password".toCharArray()); // There are other ways to read the password.
        fis.close();
        String alias = "myalias";

        Key key = ks.getKey(alias, "password".toCharArray());
        if (key instanceof PrivateKey) {
          // Get certificate of public key
          Certificate cert = ks.getCertificate(alias);

          // Get public key
          PublicKey publicKey = cert.getPublicKey();

          // Return a key pair
          new KeyPair(publicKey, (PrivateKey) key);
        }
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
        socket.close();
        
         	
    	  
    }
}

