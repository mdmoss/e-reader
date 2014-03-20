
import java.io.Serializable;

/*
 * Client.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

public class Client implements Serializable {

  public final String host;
  public final int port;
  
  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public String toString() {
    return "Client{" + "host=" + host + ", port=" + port + '}';
  }
}
