import java.io.ByteArrayOutputStream;
import java.util.Arrays;


public class CP_1 {
	
	//Divides the byte array into several blocks of specified chunsize
	public static byte[][] divideArray(byte[] source, int chunksize) {
        byte[][] Bytereturn = new byte[(int)Math.ceil(source.length / (double)chunksize)][chunksize];
        int start = 0;
        for(int i = 0; i < Bytereturn.length; i++) {
        	Bytereturn[i] = Arrays.copyOfRange(source,start, start + chunksize);
            start += chunksize ;
        }
        return Bytereturn;
    }
	
	public static byte[] glueByteArray(byte[][] bytearrays){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for(int i=0; i<bytearrays[1].length; i++){
			outputStream.write(bytearrays[1][i]);
		}
		byte[] returnArray = outputStream.toByteArray();
		return returnArray;
	}
	
	
}
