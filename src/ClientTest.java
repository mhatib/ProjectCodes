import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientTest {
	public static void main(String[] args) throws UnknownHostException, IOException{
		byte[] message = null;

		String hostName = "localhost";        
	    int portNumber = 4321;
	    Socket socket = new Socket(hostName, portNumber);
	    
	    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);		
		pout.println("Hello SecStore, please prove your identity");
		
		message = receiveByteArray(socket);
		System.out.println(message.toString());
		
		pout.println("Send me your certificate signed by CA");
		InputStream is = null;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    int bufferSize = 0;
	    
        is = socket.getInputStream();

        bufferSize = socket.getReceiveBufferSize();
        System.out.println("Buffer size: " + bufferSize);

        fos = new FileOutputStream("src//servercert.crt");
        bos = new BufferedOutputStream(fos);
        byte[] bytes = new byte[bufferSize];
        //System.out.println(bytes.length);
        int count;
        while ((count = is.read(bytes)) > 0) {
        	System.out.println(count);
            bos.write(bytes, 0, count);
        }
	    
	}
	
	public static byte[] receiveByteArray(Socket sock) throws IOException{
		DataInputStream dIn = new DataInputStream(sock.getInputStream());
	    byte[] message = new byte[128];
	    dIn.readFully(message, 0, message.length); // read the message
	    return message;
	}
	
	public static void receiveCertificate(Socket sock) throws IOException{
		InputStream is = null;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    int bufferSize = 0;
	    
        is = sock.getInputStream();

        bufferSize = sock.getReceiveBufferSize();
        System.out.println("Buffer size: " + bufferSize);

        fos = new FileOutputStream("src//disp2.pdf");
        bos = new BufferedOutputStream(fos);
        byte[] bytes = new byte[bufferSize];
        //System.out.println(bytes.length);
        int count;
        while ((count = is.read(bytes)) > 0) {
        	System.out.println(count);
            bos.write(bytes, 0, count);
        }
        //bos.close();
	}
    

}
