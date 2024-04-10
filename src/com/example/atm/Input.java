package com.example.atm;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Random;
import java.lang.StringBuilder;

public class Input {
    private static Scanner choice = new Scanner(System.in);
    private static Scanner number_card = new Scanner(System.in);
    private static Scanner password = new Scanner(System.in);
    private static Scanner money = new Scanner(System.in);
    private static Scanner cash = new Scanner(System.in);
    private static Scanner transfer_input = new Scanner(System.in);
    private static Tro bd = new Tro();

    public int getPassword() {
        Random random = new Random();
        while(true) {
            StringBuilder password = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                int number = random.nextInt(9);
                password.append(number);
            }
            System.out.println("Случайное 4-х значное число - пароль: " + password.toString());
            return Integer.parseInt(password.toString());
        }
    }

    public static boolean isValidNumber(int number) {
        if (number < 50 || number > 1000000) {
            return false;
        }
        return number % 50 == 0;
    }

    public static int select_a_menu_item(){
        System.out.println(bd.getConnection());
        bd.get_all_SELECT();
        System.out.println("1 - создать аакаунт "+'\n'+ "2 - войти в существующий");
        int inpt = choice.nextInt();
        return inpt;
    }

    public Long getChoice(String input){
        while (true) {
            //Проверяем что-бы было 16 символов
            if (input.length() == 16) {
                long fin_inpt = Long.parseLong(input);
                return fin_inpt;

            } else if (input.length() < 16 || input.length() > 16) {
                System.out.println("Пожалуйста, введите корректный номер карты");
                return null;
            }
            return null;
        }
    }

    public static int get_info_Menu(){
        System.out.println("0 - Exit "
                +'\n'+ "1 - Balance"
                +'\n'+ "2 - Add income"
                +'\n'+ "3 - Do trancfer"
                +'\n'+ "4 - Close accaunt"
                +'\n'+ "5 - Log aut");
        int inpt = choice.nextInt();
        return inpt;
    }

    public void Menu(long num_card){
        int inpt = get_info_Menu();
        if(inpt == 0){
            // Выход из приложения
            System.exit(0);
        }else if(inpt == 1){
            //Метод с балансом
            System.out.println("Ваш баланс: "+bd.Balance(num_card));
        }else if(inpt == 2){
            // Метод с пополнением баланса
            System.out.println("Введите сумму:");
            int number = money.nextInt();
            if (isValidNumber(number)) {
                System.out.println("Сумма " + number + " является корректной");
                bd.addBalance(num_card, number);
            } else {
                System.out.println("Сумма " + number + " не является корректной");
            }
        }else if(inpt == 3){
            // Метод с переводом денег
            System.out.println("Ведите номер карты на кторый хотите отправить деньги:");
            Long account_number = transfer_input.nextLong();
            System.out.println("Введите сумму: ");
            int csh = cash.nextInt();
            try {
                bd.money_transfer(num_card, csh, account_number);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if (inpt == 4){
            // Метод с закрытием аккаунта
            bd.change_the_status(num_card);
        }else if (inpt == 5){
            System.out.print("Выход в главное меню ");
            getInput();
        }
    }

    public void getInput(){
        int inpt = select_a_menu_item();
        if (inpt == 1) {
            System.out.print("Введите Ваш номер карты: ");
            String input = number_card.nextLine();
            if (getChoice(input)!=null){
                if(bd.checking_for_uniqueness(Long.parseLong(input))==false){
                    bd.SaveNewAc(getChoice(input), getPassword());
                    while (true){
                        Menu(getChoice(input));
                    }
                }else {
                    System.out.println("Такой номер карты уже существует");
                }
            }else {
                System.out.println("Номер карты не имеет 16 цифр");
            }
        } else if (inpt == 2) {
            System.out.print("Введите Ваш номер карты: ");
            String input = number_card.nextLine();
            System.out.println("Введите ваш пин-код: ");
            int input_password = password.nextInt();
            boolean checking = bd.getVerified_data(getChoice(input), input_password);
            if(checking == true){
                while (true){
                    Menu(getChoice(input));
                }
            }else {
                System.out.println("Номер карты и пин-код не совпадают");
            }
        }
    }
}

