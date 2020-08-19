import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
public class DBHandler {
    public static void main( String args[] ) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/chqbook",
                            "postgres", "    ");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE ORDERS" +
                    "(ID CHAR(20) PRIMARY KEY     NOT NULL," +
                    " AMOUNT          INT    NOT NULL, " +
                    " AMOUNT_PAID     INT     NOT NULL, " +
                    " CURRENCY        VARCHAR(5), " +
                    " RECEIPT         VARCHAR(15)," +
                    " STATUS           VARCHAR(15))";
            stmt.executeUpdate(sql);
            String sql1 = "CREATE TABLE PAYMENTS " +
                    "(PAYMENT_ID CHAR(20) PRIMARY KEY     NOT NULL," +
                    " AMOUNT          INT    NOT NULL, " +
                    " CURRENCY        VARCHAR(5), " +
                    "ID CHAR(20) NOT NULL," +
                    " FOREIGN KEY(id) " +
                    "REFERENCES orders(ID))";
            stmt.executeUpdate(sql1);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }
}

