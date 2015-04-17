import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;


public class Client1 {
	public static void main(String[] args) throws UnknownHostException, IOException, CertificateException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		byte[] message = null;

		String hostName = "localhost";        
	    int portNumber = 4321;
	    Socket socket = new Socket(hostName, portNumber);
	    
	    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println("Hello SecStore, please prove your identity");
		
		message = UploaderHelper.receiveByteArray(socket);
		pout.println("Send me your certificate signed by CA");
		
		UploaderHelper.receiveCertificate(socket);

    	InputStream inStream = new FileInputStream("src//cserv.crt");
        X509Certificate CAcert = X509Certificate.getInstance(inStream);
        PublicKey CAKey = CAcert.getPublicKey();
        
        byte[] dec = UploaderHelper.decrypt(CAKey,message);
		System.out.println(new String(dec,"UTF8"));
		
	}
}

