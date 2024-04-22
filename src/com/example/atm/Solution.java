package com.example.atm;

public class Solution {
    private static Tro bd = new Tro();

    public static void main(String[] args) {
        if (args.length > 0) {
            // jdbc:postgresql://localhost:5432/load
            String url = args[0];
            if (!url.startsWith("jdbc:postgresql://localhost:5432/load")) {
                System.err.println("Недопустимый формат URL, должен быть 'jdbc:postgresql://localhost:5432/<имя базы данных>'");
                return;
            }
            bd.URL = url;
        }
        if (args.length > 1) {
            // postgres
            String user = args[1];
            if (!user.equals("postgres")) {
                System.err.println("Не допустимо, должно быть 'postgres'");
                return;
            }
            bd.USER = user;
        }
        if (args.length > 2) {
            // Anny_1995
            String password = args[2];
            bd.PASSWORD = password;
        }

               String query = "CREATE TABLE IF NOT EXISTS Profile (\n" +
                "    num_card bigint,\n" +
                "    password integer,\n" +
                "    balance integer,\n" +
                "    status boolean\n" +
                ")";

        bd.create(query);
        Input input = new Input();
        input.getInput();
    }
}
