import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;


public class Client2 {
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

