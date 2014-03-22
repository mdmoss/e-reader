
import java.io.Serializable;
import java.lang.Object;

/*
 * Post.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

public class Post implements Serializable {
  public int id;
  public final String content;
  public final String user;
  public final String book;
  public final Integer page;
  public final Integer line;
  
  public boolean seen = false;

  public Post(int id, String content, String user, String book, Integer page, Integer line) {
    this.id = id;
    this.content = content;
    this.user = user;
    this.book = book;
    this.page = page;
    this.line = line;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + this.id;
    hash = 89 * hash + this.content.hashCode();
    hash = 89 * hash + this.user.hashCode();
    hash = 89 * hash + this.book.hashCode();
    hash = 89 * hash + this.page.hashCode();
    hash = 89 * hash + this.line.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Post other = (Post) obj;
    if (this.id != other.id) {
      return false;
    }
    if (!this.content.equals(other.content)) {
      return false;
    }
    if (!this.user.equals(other.user)) {
      return false;
    }
    if (!this.book.equals(other.book)) {
      return false;
    }
    if (!this.page.equals(other.page)) {
      return false;
    }
    if (!this.line.equals(other.line)) {
      return false;
    }
    return true;
  }

  
  
  @Override
  public String toString() {
    return "Post{" + "id=" + id + ", content=" + content + ", user=" + user + ", book=" + book + ", page=" + page + ", line=" + line + ", seen=" + seen + '}';
  }
}
