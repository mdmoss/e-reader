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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reader {
  
  public final String pageDir = "pages/";
  
  HashSet<Post> posts = new HashSet<Post>();
  String mode;
  Integer interval;
  String username;
  String serverName;
  Integer serverPort;
  
  InetAddress server;
  
  String currentBook;
  Integer currentPage;
  
  ServerSocket pushSock;
  ServerSocket chatSocket;
  
  Timer poll;
  
  List<ChatRequest> conversations = new ArrayList<ChatRequest>();
  
  DatagramSocket udpChatSocket;
  
  LinkedBlockingQueue<String> input = new LinkedBlockingQueue<String>();
  LinkedBlockingQueue<String> confirmations = new LinkedBlockingQueue<String>();
  
  final int DEFAULT_RETRIES = 10;

  public Reader(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, InterruptedException {
    mode = args[0];
    interval = Integer.parseInt(args[1]);
    username = args[2];
    serverName = args[3];
    serverPort = Integer.parseInt(args[4]);
    
    /* Sanity checks for spec-conforming input */
    assert (mode.equals("push") || mode.equals("pull"));
    
    server = InetAddress.getByName(serverName);
    
    if (mode.equals("push")) {
      pushSock = openSocket(DEFAULT_RETRIES);
      assert(pushSock != null);
      registerPush();
    }
    
    chatSocket = openSocket(DEFAULT_RETRIES);
    assert(chatSocket != null);
    registerChat();
    
    udpChatSocket = openUDPSocket(DEFAULT_RETRIES);
    assert(udpChatSocket != null);
    new Thread(new ChatControlListener(this)).start();
    new Thread(new ChatListener(this)).start();
    
    new Thread(new StdinListener(this)).start();
    
    
    while (true) {
      String in = input.take();
      String[] command = in.split("\\s", 2);
      if (command[0].equals("display")) {
        display(command[1]);
      } else if (command[0].equals("post_to_forum")) {
        post_to_forum(command[1]);
      } else if (command[0].equals("read_post")) {
        read_post(command[1]);
      } else if (command[0].equals("chat_request")) {
        chat_request(command[1]);
      } else if (command[0].equals("chat")) {
        chat(command[1]);
      } else {
        System.out.println("Invalid command: " + command[0]);
      }
    }
  }
  
  void display(String args) throws FileNotFoundException, IOException, ClassNotFoundException {
    String[] command = args.split("\\s");
    
    String book = command[0];
    int page = Integer.parseInt(command[1]);
    
    currentBook = book;
    currentPage = page;
    
    if (this.mode.equals("pull")) {
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
    
    if (this.mode.equals("pull")) {
      schedulePoll(book, page);
    }
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
    
    poll = new java.util.Timer();        
    poll.scheduleAtFixedRate(
      new java.util.TimerTask() {
        @Override
        public void run() {
          try {
            int created = updatePosts(cBook, cpage);
            if (created > 0) {
              System.out.println("There are new posts.");
            }
          } catch (IOException e) {
          } catch (ClassNotFoundException ex){}
        }
    }, 
    interval * 1000, 
    interval * 1000);
  }

  private void registerPush() throws IOException, ClassNotFoundException {
    Socket sock = new Socket(server, serverPort);
    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
    ArrayList<Integer> knownPosts = new ArrayList<Integer>();
    for (Post p : posts) {
      knownPosts.add(p.id);
    }
    
    out.writeObject(new Client(InetAddress.getHostAddress(), pushSock.getLocalPort(), knownPosts));
    ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
    PostList full = (PostList) in.readObject();
    for (Post p: full.posts) {
      posts.add(p);
    }
    
    new Thread(new PostListener(this)).start();
  }

  private ServerSocket openSocket(int retries) throws SocketException {
    Random gen = new Random();
    try {
      ServerSocket s = new ServerSocket(gen.nextInt(65536 - 1024) + 1024);
      return s;
    } catch (IOException ex) {
      if (retries > 0) {
        return openSocket(retries - 1);
      } else {
        throw new SocketException();
      }
    }
  }
  
  private DatagramSocket openUDPSocket(int retries) throws SocketException {
    Random gen = new Random();
    try {
      DatagramSocket s = new DatagramSocket(gen.nextInt(65536 - 1024) + 1024);
      return s;
    } catch (IOException ex) {
      if (retries > 0) {
        return openUDPSocket(retries - 1);
      } else {
        throw new SocketException();
      }
    }
  }

  private void registerChat() throws IOException {
    Socket sock = new Socket(server, serverPort);
    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
    out.writeObject(new ChatClient(username, InetAddress.getLocalHost().getHostAddress(), chatSocket.getLocalPort()));
    new Thread(new ChatControlListener(this)).start();
  }

  private void chat_request(String args) throws IOException {
    String[] command = args.split("\\s");
    String target = command[0];
    Socket sock = new Socket(server, serverPort);
    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
    ChatRequest req = new ChatRequest(username, target, InetAddress.getLocalHost().getHostAddress(), udpChatSocket.getLocalPort());
    out.writeObject(req);
    conversations.add(req);
  }

  private void chat(String args) throws SocketException, IOException {
    String[] command = args.split("\\s", 2);
    String target = command[0];
    String msg = command[1];
    
    for (ChatRequest c : conversations) {
      if (c.from.equals(target)) {
        InetSocketAddress peer = new InetSocketAddress(c.chatHost, c.chatPort);
        String raw = username + ":" + msg;
        byte[] buf = raw.getBytes();
        DatagramPacket p = new DatagramPacket(raw.getBytes(), buf.length, peer);
        udpChatSocket.send(p);
      }
    }
  }
}
