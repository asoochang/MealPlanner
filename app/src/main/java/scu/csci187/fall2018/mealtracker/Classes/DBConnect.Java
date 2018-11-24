package scu.csci187.fall2018.mealtracker.Classes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBConnect{

    private static Connection c;

    private static void connect() throws Exception{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String address= "24.6.52.232:3306";
        c = DriverManager.getConnection(address, "nutritionBoy", "bestTeacherNA");
    }
    public static PreparedStatement prepareState(String sql) throws Exception{
        if (c == null){
            connect();
        }
        return c.prepareStatement(sql);
    }
}
