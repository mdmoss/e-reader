/*
 * PostList.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

import java.io.Serializable;
import java.util.ArrayList;

public class PostList implements Serializable {
  public String book;
  public Integer page;
  public ArrayList<Post> posts = new ArrayList<Post>();

  public PostList(String book, Integer page) {
    this.book = book;
    this.page = page;
  }

  @Override
  public String toString() {
    return "PostList{" + "book=" + book + ", page=" + page + ", posts=" + posts + '}';
  }
  
  
}
