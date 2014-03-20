/*
 * server_ex.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;

public class Reader {
  HashSet<Post> posts = new HashSet<Post>();
  String mode;
  Integer interval;
  String username;
  String serverName;
  Integer serverPort;
  
  InetAddress server;
  
  String currentBook;
  Integer currentPage;

  public Reader(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
    mode = args[0];
    interval = Integer.parseInt(args[1]);
    username = args[2];
    serverName = args[3];
    serverPort = Integer.parseInt(args[4]);
    
    /* Sanity checks for spec-conforming input */
    assert (mode.equals("push") || mode.equals("poll"));
    
    server = InetAddress.getByName(serverName);
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
    while (true) {
      String[] command = reader.readLine().split("\\s");
      switch (command[0]) {
        case "display":
          display(command);
          break;
        case "post_to_forum":
          post_to_forum(command);
          break;
        case "read_post":
          read_post(command);
          break;
        default:
          System.out.println("Invalid command: " + command[0]);
          break;
      }
    }
  }
  
  void display(String[] command) throws FileNotFoundException, IOException, ClassNotFoundException {
    
    String book = command[1];
    int page = Integer.parseInt(command[2]);
    
    currentBook = book;
    currentPage = page;
    
    if (this.mode.equals("poll")) {
      updatePosts(book, page);
    }
    
    String fileName = "pages/" + book + "_page" + page;
    BufferedReader file = new BufferedReader(new FileReader(fileName));
    String text = null;
    int line = 1;
    while((text = file.readLine()) != null) {
      String marker = " ";
      for (Post p : posts) {
        if (p.book.equals(book) && p.page == page && p.line == line) {
          if (marker != "n") {
            if (p.seen) {
              marker = "m";
            } else {
              marker = "n";
            }
          }
        }
      }
      System.out.println(marker + " " + text);
      line++;
    }
  }
  
  void post_to_forum(String[] command) throws IOException {
    if (currentBook == null || currentPage == null) return;
    Integer line = Integer.parseInt(command[1]);
    String content = command[2];
    
    Socket sock = new Socket(server, serverPort);
    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
    
    Post p = new Post(0, content, username, currentBook, currentPage, line);
    out.writeObject(p);
  }
  
  void read_post(String[] command) {
    if (currentBook == null || currentPage == null) return;
    Integer line = Integer.parseInt(command[1]);
    
    System.out.println(currentBook + " p." + currentPage + ", line:" + line);
    
    for (Post p: posts) {
      if (p.book.equals(currentBook) && p.page.equals(currentPage) && 
              p.line.equals(line) && p.seen == false) {
        p.seen = true;
        System.out.println(p.id + " " + p.user + ": " + p.content);
      }
    }
  }
  
  void updatePosts(String book, int page) throws IOException, ClassNotFoundException {
    Socket sock = new Socket(server, serverPort);
    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
    out.writeObject(new PostList(book, page));
    ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
    PostList res = (PostList) in.readObject();
    System.out.println("Got " + res.posts.size() + " posts");
    posts.addAll(res.posts);
    sock.close();
  };
}
