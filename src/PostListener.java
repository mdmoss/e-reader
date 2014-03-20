
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PostListener implements Runnable {

  Reader reader;

  public PostListener(Reader reader) {
    this.reader = reader;
  }
  
  @Override
  public void run() {
    try {
      Socket conn = reader.pushSock.accept();
      ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
      Post p = (Post) in.readObject();
      if (p.book.equals(reader.currentBook) && p.page.equals(reader.currentPage)) {
        System.out.println("There are new posts.");
      }
      System.err.println("debug: post added");
      reader.posts.add(p);
      conn.close();
      
    } catch (IOException ex) {
      System.out.println("Error reading push");
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(PostListener.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}
