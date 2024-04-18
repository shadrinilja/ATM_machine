package com.example.atm;
import java.sql.*;
import java.util.Properties;

public class Tro {
    public static String URL;
    public static String USER;
    public static String PASSWORD;
    PreparedStatement pstmt = null;

    public Connection getConnection(){
        try{
            Class.forName("org.postgresql.Driver");
            // Создание свойств соединения с базой данных
            Properties authorization = new Properties();
            authorization.put("user", USER); // Зададим имя пользователя БД
            authorization.put("password", PASSWORD); // Зададим пароль доступа в БД

            Connection connection = DriverManager.getConnection(URL, authorization);
            return connection;
        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }
        return null;
    }

    public void create(){
        try{
            String query = "CREATE TABLE users (\n" +
                    "    num_card bigint,\n" +
                    "    password integer,\n" +
                    "    balance integer,\n" +
                    "    status boolean\n" +
                    ")";

            pstmt = getConnection().prepareStatement(query);
            pstmt.executeQuery();
        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }


    }
    public void SaveNewAc(long num_card, int password){
        try{
            pstmt = getConnection().prepareStatement("INSERT INTO Profile(num_card,password,balance,status) VALUES(?,?,?,?)");
            pstmt.setLong(1,num_card);
            pstmt.setInt(2,password);
            pstmt.setInt(3, 10);
            pstmt.setBoolean(4, false);
            pstmt.executeUpdate();
            getConnection().close();
        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }
    }

    public Boolean checking_for_uniqueness(long num_card){
        try{
            pstmt = getConnection().prepareStatement("SELECT * FROM profile WHERE num_card = ?;");
            pstmt.setLong(1, num_card);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Если запись найдена, выводим ее на консоль
                System.out.println("Номер существует - "+rs.getString("num_card"));
                getConnection().close();
                return true;
            }else {
                System.out.println("Запись уникальна");
                return false;
            }
        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getVerified_data(long num_card, int password){
        try{
            pstmt = getConnection().prepareStatement("SELECT * FROM profile WHERE num_card = ? AND password = ?;");
            pstmt.setLong(1, num_card);
            pstmt.setInt(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Если запись найдена, выводим ее на консоль
                System.out.println("Ваш номер карты - "+rs.getString("num_card"));
                System.out.println("Ващ прин код - "+rs.getString("password"));
                getConnection().close();
                return true;
            }else {
                System.out.println("Запись не найдена");
                return false;
            }
        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }
        return null;
    }

    public void addBalance(long num_card, int money){
        try{
            pstmt = getConnection().prepareStatement("UPDATE profile SET balance = balance + ? WHERE num_card = ?;");
            pstmt.setInt(1, money);
            pstmt.setLong(2,num_card);
            int updateCount = pstmt.executeUpdate();
            if (updateCount == 0) {
                System.out.println("Запись не найдена");
            } else {
                System.out.println("Баланс пополнен");
            }
        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }
    }

    public void change_the_status(long num_card){
        try{
            pstmt = getConnection().prepareStatement("UPDATE profile SET status = ? WHERE num_card = ?;");
            pstmt.setBoolean(1, true);
            pstmt.setLong(2, num_card);
            int updateCount = pstmt.executeUpdate();
            if (updateCount != 0) {
                System.out.println("Cчет закрыт и является неакттивным");
            } else {
                System.out.println("Счет не закрыт");
            }
        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }

    }

    public void money_transfer(long num_card, int money, long input_num_card) throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        try(
                PreparedStatement updateAccountBalance1 = connection.prepareStatement(
                        "UPDATE profile SET balance = balance - ? WHERE num_card = ?");
                PreparedStatement updateAccountBalance2 = connection.prepareStatement(
                        "UPDATE profile SET balance = balance + ? WHERE num_card = ?")
        ){
            updateAccountBalance1.setInt(1, money);
            updateAccountBalance1.setLong(2, num_card);
            updateAccountBalance2.setInt(1, money);
            updateAccountBalance2.setLong(2, input_num_card);
            int result = updateAccountBalance1.executeUpdate();
            System.out.println(result);
            if (result > 0) {
                result = updateAccountBalance2.executeUpdate();
                if (result > 0) {
                    System.out.println("Перевод выполнен.");
                } else {
                    throw new SQLException("Сбой перевода для учетной записи получателя.");
                }
            } else {
                throw new SQLException("Сбой перевода для учетной записи получателя.");
            }
        } finally {
            connection.commit();
        }
    }

    public Integer Balance(long num_card){
        try{
            pstmt = getConnection().prepareStatement("SELECT * FROM profile WHERE num_card = ?;");
            pstmt.setLong(1, num_card);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Если запись найдена, выводим ее на консоль
                getConnection().close();
                return rs.getInt("balance");
            }else {
                System.out.println("Запись не найдена");
            }
        }catch (Exception e) {
            System.err.println("Ошибка доступа к базе данных!");
            e.printStackTrace();
        }
        return null;
    }

    public void get_all_SELECT(){
        try{
            Statement statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            ResultSet table = statement.executeQuery("SELECT * FROM public.profile");
            table.first(); // Выведем имена полей
            for (int j = 1; j <= table.getMetaData().getColumnCount(); j++) {
                System.out.print(table.getMetaData().getColumnName(j) + "\t\t");
            }
            System.out.println();
            table.beforeFirst(); // Выведем записи таблицы
            while (table.next()) {
                for (int j = 1; j <= table.getMetaData().getColumnCount(); j++) {
                    System.out.print(table.getString(j) + "\t\t");
                }
                System.out.println();
            }

        }catch (Exception e) {
            System.err.println("Error accessing database!");
            e.printStackTrace();
        }
    }
}
