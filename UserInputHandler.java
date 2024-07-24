import java.util.NoSuchElementException;
import java.util.Scanner;

public class UserInputHandler {
    private final Scanner scanner = new Scanner(System.in);
    public String getLogin() {
        System.out.println("Введите логин: ");
        return scanner.nextLine();
    }

    public String getPassword() {
        System.out.println("Введите пароль: ");
        return scanner.nextLine();
    }
    public int getUserAction() {
        System.out.println("Выберете действие");
        System.out.println( "1.\tПолучение списка студентов по имени\n" +
                            "2.\tПолучение информации о студенте по id\n" +
                            "3.\tДобавление студента ( id генерируется автоматически)\n" +
                            "4.\tУдаление студента по id\n" +
                            "5.\tЗавершение работы");
        int action = Integer.parseInt(scanner.nextLine());
        if (action < 1 || action > 5) throw new NoSuchElementException();
        return action;
    }

    public String getServerAddress() {
        System.out.println("Введите адрес сервера: ");
        return scanner.nextLine();
    }
    public int getServerPort() {
        System.out.println("Введите порт сервера: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public String getStudentName() {
        System.out.println("Введите имя студента: ");
        return scanner.nextLine();
    }

    public int getStudentId() {
        System.out.println("Введите id студента: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public int getMode() {
        System.out.println("Выберете режим");
        System.out.println( "1.\tАктивный\n" +
                            "2.\tПассивный");
        int action = Integer.parseInt(scanner.nextLine());
        if (action < 1 || action > 2) throw new NoSuchElementException();
        return action;
    }
}
