package ru.nefedovadasha.address.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class DBWorker {
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/dbForAddressApp?useUnicode=true&useSSL=true&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    private Connection connection;
    private Statement statement;

    public Connection getConnection() {
        return connection;
    }

    public DBWorker() {
        try{
            Class.forName(DATABASE_DRIVER);
            connection = DriverManager.getConnection(URL,USER,PASS);
            statement = connection.createStatement();
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Person> getListFromDB(){

        ObservableList<Person> personData = FXCollections.observableArrayList();

        try {
            ResultSet res = statement.executeQuery("SELECT * FROM persons;");
            Person person;
            while (res.next()) {
                person = new Person();
                person.setId(res.getInt("person_id"));
                person.setFirstName(res.getString("fname"));
                person.setLastName(res.getString("lname"));
                person.setCity(res.getString("city"));
                person.setStreet(res.getString("street"));
                person.setPostalCode(res.getInt("postal_code"));
                if (res.getDate("birthday")!=null)
                    person.setBirthday(res.getDate("birthday").toLocalDate());
                personData.add(person);
            }
        } catch (SQLException e) {
        }

        return personData;
    }

    public void editPersonFromDB(Person person){
        int id = person.getId();
        deletePersonFromDB(person);
        try {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            statement.execute("INSERT INTO persons(person_id,fname,lname,street,postal_code,city,birthday)"
                    + " VALUES ("+id+",'"+person.getFirstName()+"','"+person.getLastName()+"','"+person.getStreet()
                    + "',"+person.getPostalCode()+",'"+person.getCity()+"','"+dateFormat.format(person.getBirthday())+"');");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNewPersonFromDB(Person person){
        try {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            statement.execute("INSERT INTO persons(fname,lname,street,postal_code,city,birthday)"
                    + " VALUES ('"+person.getFirstName()+"','"+person.getLastName()+"','"+person.getStreet()
                    + "',"+person.getPostalCode()+",'"+person.getCity()+"','"+dateFormat.format(person.getBirthday())+"');");

        } catch (SQLException e) {
            e.printStackTrace();
       }
    }

    public void deletePersonFromDB(Person person){
        try {
            statement.executeUpdate("DELETE FROM persons WHERE person_id = "+person.getId()+";");
        }
        catch (SQLException e) {}
    }
}

