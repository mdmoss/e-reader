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
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reader {
  
  public final String pageDir = "/home/mdm/e-reader/pages/";
  
  HashSet<Post> posts = new HashSet<Post>();
  String mode;
  Integer interval;
  String username;
  String serverName;
  Integer serverPort;
  
  InetAddress server;
  
  String currentBook;
  Integer currentPage;
  
  Timer poll = null;

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
      String in = reader.readLine();
      if (in == null) break;
      String[] command = in.split("\\s", 2);
      switch (command[0]) {
        case "display":
          display(command[1]);
          break;
        case "post_to_forum":
          post_to_forum(command[1]);
          break;
        case "read_post":
          read_post(command[1]);
          break;
        default:
          System.out.println("Invalid command: " + command[0]);
          break;
      }
    }
  }
  
  void display(String args) throws FileNotFoundException, IOException, ClassNotFoundException {
    String[] command = args.split("\\s");
    
    String book = command[0];
    int page = Integer.parseInt(command[1]);
    
    currentBook = book;
    currentPage = page;
    
    if (this.mode.equals("poll")) {
      updatePosts(book, page);
    }
    
    String fileName = pageDir + book + "_page" + page;
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
    
    schedulePoll(book, page);
  }
  
  void post_to_forum(String args) throws IOException {
    String[] command = args.split("\\s", 2);
    if (currentBook == null || currentPage == null) return;
    Integer line = Integer.parseInt(command[0]);
    String content = command[1];
    
    Socket sock = new Socket(server, serverPort);
    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
    
    Post p = new Post(0, content, username, currentBook, currentPage, line);
    out.writeObject(p);
  }
  
  void read_post(String args) {
    String[] command = args.split("\\s");
    if (currentBook == null || currentPage == null) return;
    Integer line = Integer.parseInt(command[0]);
    
    System.out.println(currentBook + " p." + currentPage + ", line:" + line);
    
    for (Post p: posts) {
      if (p.book.equals(currentBook) && p.page.equals(currentPage) && 
              p.line.equals(line) && p.seen == false) {
        p.seen = true;
        System.out.println(p.id + " " + p.user + ": " + p.content);
      }
    }
  }
  
  int updatePosts(String book, int page) throws IOException, ClassNotFoundException {
    Socket sock = new Socket(server, serverPort);
    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
    out.writeObject(new PostList(book, page));
    ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
    PostList res = (PostList) in.readObject();
    int sizePre = posts.size();
    posts.addAll(res.posts);
    sock.close();
    return posts.size() - sizePre;
  };
  
  void schedulePoll(String book, int page) {
    if (poll != null) {
      poll.cancel();
    }
    
    final String cBook = book;
    final int cpage = page;
    
    new java.util.Timer().scheduleAtFixedRate(
      new java.util.TimerTask() {
        @Override
        public void run() {
          try {
            int created = updatePosts(cBook, cpage);
            if (created > 0) {
              System.out.println("There are new posts.");
            }
          } catch (IOException | ClassNotFoundException ex) {
          }
        }
    }, 
    interval * 1000, 
    interval * 1000);
  }
}
