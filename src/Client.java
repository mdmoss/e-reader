
import java.io.Serializable;
import java.util.ArrayList;

/*
 * Client.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

public class Client implements Serializable {

  public final String host;
  public final int port;
  public final ArrayList<Integer> posts; 
  
  public Client(String host, int port, ArrayList<Integer> posts) {
    this.host = host;
    this.port = port;
    this.posts = posts;
  }

  @Override
  public String toString() {
    return "Client{" + "host=" + host + ", port=" + port + ", posts=" + posts + '}';
  }
}
