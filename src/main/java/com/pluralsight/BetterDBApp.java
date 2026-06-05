package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class BetterDBApp {

    public static void main(String[] args) {

        //did we pass in a username and password
        //if not, the application must die
        if(args.length != 2){
            //display a message to the user
            System.out.println("Application needs two args to run: A username and a password for the db");
            //exit the app due to failure because we dont have a username and password from the command line
            System.exit(1);
        }

        //get the username and password from args[]
        String username = args[0];
        String password = args[1];

        //create the datasouce using the username and pw passed in from the command line when the app is run
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        //create a scanner to deal with user input
        Scanner myScanner = new Scanner(System.in);

        //try to get a connection to the db and throw up if we don't
        try(Connection connection = dataSource.getConnection()){

            //now that we have a connection, lets interact with the user
            while(true){

                //display a menu to the user
                System.out.println("""
                        What do you want to do?
                            1) Display All Films
                            2) Display All Employees
                            3) Display Film By ID
                            0) Exit the dang app
                        """);

                //deal the the users choice from the menu
                switch (myScanner.nextInt()){
                    case 1:
                        displayAllFilms(connection);
                        break;
                    case 2:
                        displayAllEmployees(connection);
                        break;
                    case 3:
                        displayFilmByID(connection);
                        break;
                    case 0:
                        System.out.println("See Ya Later");
                        System.exit(0);
                    default:
                        System.out.println("Invalid Selection");
                }

            }

        }catch (SQLException e){
            //we threw up because we couldn't connecct
            System.out.println("unable to connect to the database - " + e.getMessage());
            System.exit(1);
        }

    }

    //this method takes a connection to the db and uses it to query the db for all the films and display them
    public static void displayAllFilms(Connection connection){

        String sql = """
                    select
                        film_id,
                        title,
                        description
                    from
                        film
                """;

        try(
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet results = stmt.executeQuery();
        ) {

            //use the helper method to print our results from the database
            printResults(results);

        }catch (SQLException e){
            System.out.println("can't get all films " + e.getMessage());
        }
    }

    public static void displayAllEmployees(Connection connection){

        String sql = """
                    select
                        staff_id,
                        first_name,
                        last_name
                    from
                        staff
                """;

        try(
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet results = stmt.executeQuery();
        ) {

            //use the helper method to print our results from the database
            printResults(results);

        }catch (SQLException e){
            System.out.println("can't get all staff members " + e.getMessage());
        }
    }

    public static void displayFilmByID(Connection connection){

        Scanner myScanner = new Scanner(System.in);

        //ask the user what id they want to view
        System.out.println("What Film ID would you like to view?");

        int filmID = myScanner.nextInt();

        String sql = """
                    select
                        *
                    from
                        film
                    where
                        film_id = ?
                """;

        try(
                PreparedStatement stmt = connection.prepareStatement(sql);

        ) {

            //replate the ? in the query with the film id we got from the user
            stmt.setInt(1, filmID);

            try(ResultSet results = stmt.executeQuery();){
                //use the helper method to print our results from the database
                printResults(results);
            }catch (SQLException e){
                System.out.println("No film with the id of: " + filmID);
            }



        }catch (SQLException e){
            System.out.println("can't get all films " + e.getMessage());
        }
    }


    //this method will be used in the displayMethods to actually print the results to the screen
    public static void printResults(ResultSet results) throws SQLException {
        //get the meta data so we have access to the field names
        ResultSetMetaData metaData = results.getMetaData();
        //get the number of rows returned
        int columnCount = metaData.getColumnCount();

        //this is looping over all the results from the DB
        while(results.next()){

            //loop over each column in the rown and display the data
            for (int i = 1; i <= columnCount; i++) {
                //gets the current colum name
                String columnName = metaData.getColumnName(i);
                //get the current column value
                String value = results.getString(i);
                //print out the column name and column value
                System.out.println(columnName + ": " + value + " ");
            }

            //print an empty line to make the results prettier
            System.out.println();

        }

    }

}