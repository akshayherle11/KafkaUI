package db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LiteDb {

  private static Connection c = null;

  public static Connection getConnection() {
    if (c == null) {
      connect();
    }
    return c;
  }

  public static void connect() {
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:liteDB.db");

    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public static ResultSet runQuery(String q) {
    try {
      Statement statement = getConnection().createStatement();
      ResultSet rs = statement.executeQuery(q);
   //   statement.close();
      return rs;
    } catch (SQLException e) {
      System.err.println(e);
    }
    return null;
  }

  public static void runQueryCommit(String q) {
    try {
      Statement statement = getConnection().createStatement();
         statement.execute(q);
         statement.close();

    } catch (SQLException e) {
      System.err.println(e);
    }
  }
}
