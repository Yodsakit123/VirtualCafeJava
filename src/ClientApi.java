import java.io.*;
import java.net.Socket;

public class ClientApi implements Closeable {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public ClientApi(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public Message send(Message req) throws IOException {
        out.write(DataManager.gson().toJson(req));
        out.write("\n");
        out.flush();
        String line = in.readLine();
        return DataManager.gson().fromJson(line, Message.class);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
