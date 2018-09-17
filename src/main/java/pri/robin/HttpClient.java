package pri.robin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class HttpClient {
    private static String host = "localhost";
    private static int port = 8888;

    public void testLongConn() throws IOException {
        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        new Thread(() -> {
            System.out.println("starting thread");
            while (true) {
                byte[] input = new byte[64];
                try {
                    System.out.println("Accepting...");
                    int readByte = socket.getInputStream().read(input);
                    System.out.println(new String(input));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        int code;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            code = scanner.nextInt();
            if (code == 0) {
                break;
            } else if (code == 1) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(5);
                byteBuffer.put((byte) 1);
                byteBuffer.putInt(0);
                socket.getOutputStream().write(byteBuffer.array());
                System.out.println("send 1 to server...");
            } else if (code == 2) {
                byte[] content = ("hello, I'm" + hashCode()).getBytes();
                ByteBuffer byteBuffer = ByteBuffer.allocate(content.length + 5);
                byteBuffer.put((byte) 2);
                byteBuffer.putInt(content.length);
                byteBuffer.put(content);
                socket.getOutputStream().write(byteBuffer.array());
                System.out.println("send 2 to server...");
            }
        }
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        new HttpClient().testLongConn();
    }
}
