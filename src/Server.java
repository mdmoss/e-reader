/*
 * server_ex.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

  List<Post> posts = new ArrayList<Post>();
  List<Client> pushList = new ArrayList<Client>();
  
  Server (int port) throws IOException, ClassNotFoundException {
    while (true) {
      ServerSocket sock = new ServerSocket(port);
      Socket conn = sock.accept();
      ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
      handle(in, out);
      conn.close();
      sock.close();
    }
  }

  public void handle(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
    Object message = in.readObject();
    
    System.out.println(message.getClass());
    
    if (message.getClass().equals(PostList.class)) {
      sendPostList((PostList) message, out);
    } else if (message.getClass().equals(Post.class)) {
      out.close();
      addPost((Post) message); 
    } else {
      System.err.println("Invalid message received");
    }
  }
  
  public void sendPostList(PostList pl, ObjectOutputStream out) throws IOException {
    System.err.println("Sending Post List " + pl);
    
    for (Post p : posts) {
      System.out.println("Considering " + p);
      if (p.book.equals(pl.book) && p.page.equals(pl.page)) {
        System.err.println("Adding " + p.content);
        pl.posts.add(p);
      }
    }
    out.writeObject(pl);
  }
  
  public void addPost(Post p) {
    p.id = posts.size();
    posts.add(p);
    System.err.println("Added Post: " + p);
  }
}