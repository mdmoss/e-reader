
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatControlListener implements Runnable {

  Reader reader;

  public ChatControlListener(Reader reader) {
    this.reader = reader;
  }
  
  @Override
  public void run() {
    try {
      Socket conn = reader.chatSocket.accept();
      ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
      ChatRequest cr = (ChatRequest) in.readObject();
      System.err.println("debug: chat request get");
      
      boolean seen = false;
      ChatRequest prev = null;
      for (ChatRequest r: this.reader.conversations) {
        if (r.to.equals(cr.from)) {
          /* We've seen it before - add it to conversations and we're done */
          seen = true;
          prev = r;
        }
      }
      if (seen) {
        /* This is an accept of a previous chat request - update the port info */
        this.reader.conversations.remove(prev);
        this.reader.conversations.add(cr);
        System.out.println(cr.from + " accepted your chat request");
      } else {
        reader.confirmations.clear();
        System.out.println("Accept chat from " + cr.from + "? [y/n]");
        
        if (reader.confirmations.take().equals("y")) {
          System.out.println("Chat accepted!");
          this.reader.conversations.add(cr);
          
          Socket sock = new Socket(reader.server, reader.serverPort);
          ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
          out.writeObject(new ChatRequest(
                  reader.username,
                  cr.from,
                  reader.udpChatSocket.getLocalAddress().getHostAddress(), 
                  reader.udpChatSocket.getLocalPort(),
                  true));
          
        } else {
          System.out.println("Chat refused!");
        }        
      }
      
      conn.close();
      
    } catch (IOException ex) {
      System.out.println("Error reading chat request");
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(PostListener.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(ChatControlListener.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}

