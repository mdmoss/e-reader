/*
 * Display.java
 *
 * Matthew Moss <mdm@cse.unsw.edu.au>
 * comp3331 s1 2014
 */

import java.io.Serializable;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;

public class Display implements Serializable {
  public String book;
  public Integer page;
  public ArrayList<String> lines = new ArrayList<String>();

  public Display(String book, Integer page) {
    this.book = book;
    this.page = page;
  }

  @Override
  public String toString() {
    return "Display{" +
      "book='" + book + '\'' +
      ", page=" + page +
      ", lines=" + lines +
      '}';
  }
}
