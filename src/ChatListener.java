
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatListener implements Runnable {

  Reader reader;
  

  public ChatListener(Reader reader) {
    this.reader = reader;
  }
  
  @Override
  public void run() {
    
    byte[] buf = new byte[256];
    DatagramPacket rec = new DatagramPacket(buf, 256);

    while(true) {
      try {
        reader.udpChatSocket.receive(rec);
        String msg = new String(buf);
        String username = msg.split(":", 2)[0];
        for (ChatRequest r : reader.conversations) {
          if (r.from.equals(username)) {
            System.out.println(msg);
            break;
          }
        }
      /* Zero the array to clear the already-printed message */
      Arrays.fill(buf, (byte) 0);
      } catch (IOException ex) {
        System.out.println("Error reading chat message");
      }
    }
  }
}
