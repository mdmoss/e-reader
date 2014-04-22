/*
 * server_ex.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

import java.io.*;
import java.lang.String;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

  List<Post> posts = new ArrayList<Post>();
  List<Client> pushList = new ArrayList<Client>();
  List<ChatClient> chatList = new ArrayList<ChatClient>();
  
  Server (int port) throws IOException, ClassNotFoundException {
    ServerSocket sock = new ServerSocket(port);
    
    System.out.println("The server is listening on " + port);
    System.out.println("The database has been initialised");
    
    while (true) {
      Socket conn = sock.accept();
      System.out.print("# ");
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
    } else if (message.getClass().equals(Display.class)) {
      sendPage((Display) message, out);
    } else if (message.getClass().equals(ChatClient.class)) {
      out.close();
      addChatClient((ChatClient) message);
    } else if (message.getClass().equals(ChatRequest.class)) {
      out.close();
      forwardChatRequest((ChatRequest) message);
    } else {
      System.out.println("Invalid message received");
    }
  }
  
  public void sendPostList(PostList pl, ObjectOutputStream out) throws IOException {
    System.out.println("Sending Post List " + pl);
    for (Post p : posts) {
      if (p.book.equals(pl.book) && p.page.equals(pl.page)) {
        pl.posts.add(p);
      }
    }
    out.writeObject(pl);
    out.close();
  }
  
  public void addPost(Post p) {
    System.out.println(p);
    
    System.out.println("New post received from " + p.user + ".");
    p.id = posts.size();
    posts.add(p);
    
    System.out.println("Post added to the database and given serial number  " + p.id + ".");
    
    for (Client c: pushList) {
      push(p, c);
    }
    
    if (pushList.isEmpty()) {
      System.out.println("Push list empty. No action required.");
    }
  }
  
  public void addClient(Client c, ObjectOutputStream out) throws IOException {
    System.out.println("Adding push client " + c);
    pushList.add(c);
    PostList full = new PostList("", 0);
    System.out.println("Got post summary");
    full.posts = new ArrayList<Post>();
    System.out.print("Forwarding posts: <");
    for (Post p : posts) {
      if (!c.posts.contains(p.id)) {
        full.posts.add(p);
        System.out.print(p.id + " ");
      }
    }
    System.out.println(">");
    out.writeObject(full);
    out.close();
  }

  public void sendPage(Display d, ObjectOutputStream out) throws IOException {
    System.out.println("Sending page " + d);

    String fileName = d.book + "_page" + d.page;
    ArrayList<String> lines = new ArrayList<String>();

    Scanner s = new Scanner(new File(fileName));
    while (s.hasNextLine()) {
      lines.add(s.nextLine());
    }

    d.lines = lines;
    out.writeObject(d);
    out.close();
  }
  
  public void push(Post p, Client c) {
    try {
      System.out.println("Pushing to " + c);
      Socket sock = new Socket(c.host, c.port);
      ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
      out.writeObject(p);
    } catch (Exception e) {
      /* We're being very risk-averse here; just deal with failure */
      System.out.println(e);
    }
  }

  private void addChatClient(ChatClient client) {
    System.out.println("Adding client " + client);
    chatList.add(client);
  }

  private void forwardChatRequest(ChatRequest req) throws IOException {
    for (ChatClient c : chatList) {
      if (c.username.equals(req.to)) {
        Socket sock = new Socket(c.host, c.port);
        ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
        out.writeObject(req);
        System.out.println(req);
        return;
      }
    }
    System.out.println("forwardChatRequest: matching user not found");
  }
}