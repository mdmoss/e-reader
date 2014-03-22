
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
 * reader_ex.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

public class reader_ex {
  
  public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, InterruptedException {
    if (args.length != 5) {
      System.err.println("Usage: java reader mode polling_interval user_name server_name server_port_number");
      System.exit(1);
    }
    Reader r = new Reader(args);
  }
}