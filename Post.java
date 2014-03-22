
import java.io.Serializable;
import java.util.Objects;

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
    hash = 89 * hash + Objects.hashCode(this.content);
    hash = 89 * hash + Objects.hashCode(this.user);
    hash = 89 * hash + Objects.hashCode(this.book);
    hash = 89 * hash + Objects.hashCode(this.page);
    hash = 89 * hash + Objects.hashCode(this.line);
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
    if (!Objects.equals(this.content, other.content)) {
      return false;
    }
    if (!Objects.equals(this.user, other.user)) {
      return false;
    }
    if (!Objects.equals(this.book, other.book)) {
      return false;
    }
    if (!Objects.equals(this.page, other.page)) {
      return false;
    }
    if (!Objects.equals(this.line, other.line)) {
      return false;
    }
    return true;
  }

  
  
  @Override
  public String toString() {
    return "Post{" + "id=" + id + ", content=" + content + ", user=" + user + ", book=" + book + ", page=" + page + ", line=" + line + ", seen=" + seen + '}';
  }
}