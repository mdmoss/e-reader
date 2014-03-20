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
    ServerSocket sock = new ServerSocket(port);
    while (true) {
      Socket conn = sock.accept();
      ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
      handle(in, out);
      conn.close();
    }
  }

  public void handle(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
    Object message = in.readObject();
    
    if (message.getClass().equals(PostList.class)) {
      sendPostList((PostList) message, out);
    } else if (message.getClass().equals(Post.class)) {
      out.close();
      addPost((Post) message); 
    } else if (message.getClass().equals(Client.class)) {
      addClient((Client) message, out);
    } else {
      System.err.println("Invalid message received");
    }
  }
  
  public void sendPostList(PostList pl, ObjectOutputStream out) throws IOException {
    System.err.println("Sending Post List " + pl);
    for (Post p : posts) {
      if (p.book.equals(pl.book) && p.page.equals(pl.page)) {
        pl.posts.add(p);
      }
    }
    out.writeObject(pl);
    out.close();
  }
  
  public void addPost(Post p) {
    System.err.println("Adding post " + p);
    p.id = posts.size();
    posts.add(p);
    for (Client c: pushList) {
      push(p, c);
    }
  }
  
  public void addClient(Client c, ObjectOutputStream out) throws IOException {
    System.err.println("Adding client " + c);
    pushList.add(c);
    PostList full = new PostList("", 0);
    full.posts = new ArrayList(posts);
    out.writeObject(full);
    out.close();
  }
  
  public void push(Post p, Client c) {
    try {
      System.err.println("Pushing to " + c);
      Socket sock = new Socket(c.host, c.port);
      ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
      out.writeObject(p);
    } catch (Exception e) {
      /* We're being very risk-averse here; just deal with failure */
      System.err.println(e);
    }
  }
}