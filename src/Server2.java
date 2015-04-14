import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Server2 {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(4321);
    	System.out.println("(... expecting connection ...)");
        Socket socket = serverSocket.accept();     
    	System.out.println("(... connection established ...)");
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int bufferSize = 0;
        is = socket.getInputStream();

        bufferSize = socket.getReceiveBufferSize();
        System.out.println("Buffer size: " + bufferSize);
        fos = new FileOutputStream("src//disp2.pdf");
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
        serverSocket.close();
	}
}

