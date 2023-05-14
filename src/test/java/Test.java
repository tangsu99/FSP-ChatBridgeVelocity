import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Test {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1", 5700);
            OutputStream os = s.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            //向服务器端发送一条消息
            bw.write(1 + "awa");
            bw.flush();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
