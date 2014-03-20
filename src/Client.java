
import java.util.List;

/*
 * Post.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

public class Client {

  public final int port;
  public final List<Integer> seen;
  
  public Client(int port, List<Integer> seen) {
    this.port = port;
    this.seen = seen;
  }
}
