/*
 * server_ex.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

import java.io.IOException;

public class server_ex {
  public static void main(String[] args) throws ClassNotFoundException, IOException {
    /* Parse the port option from command line */
    if (args.length != 1) {
      System.err.println("Usage: java server_ex port_number");
      System.exit(1);
    }
    int port = Integer.parseInt(args[0]);
    
    Server s = new Server(port);
  }
}

