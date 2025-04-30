package esprit.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    public final String URL="jdbc:mysql://localhost:3306/mentordb";
    public final String USER ="root";
    public final String PWD ="";
    private Connection cnx;
    public static MyDataBase myDataBase;
    private MyDataBase(){

        try {
            cnx= DriverManager.getConnection(URL,USER,PWD);
            System.out.println("cnx etablie");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static MyDataBase  getInstance(){
        if(myDataBase ==null)
            myDataBase=new MyDataBase();
        return myDataBase;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(URL, USER, PWD);
                System.out.println("Connexion réinitialisée");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la connexion : " + e.getMessage());
        }
        return cnx;
    }
    
}
