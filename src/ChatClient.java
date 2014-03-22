
import java.io.Serializable;

public class ChatClient implements Serializable {

  public final String username;
  public final String host;
  public final int port;

  public ChatClient(String username, String host, int port) {
    this.username = username;
    this.host = host;
    this.port = port;
  }

  @Override
  public String toString() {
    return "ChatClient{" + "username=" + username + ", host=" + host + ", port=" + port + '}';
  }
}
