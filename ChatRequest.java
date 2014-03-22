
import java.io.Serializable;

public class ChatRequest implements Serializable {
  public final String from;
  public final String to;
  public final String chatHost;
  public final int chatPort;

  public ChatRequest(String from, String to, String chatHost, int chatPort) {
    this.from = from;
    this.to = to;
    this.chatHost = chatHost;
    this.chatPort = chatPort;
  }

  @Override
  public String toString() {
    return "ChatRequest{" + "from=" + from + ", to=" + to + ", chatHost=" + chatHost + ", chatPort=" + chatPort + '}';
  }
  
}
