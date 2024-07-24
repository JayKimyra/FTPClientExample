public class Application {
    public void run() {
        try {
            UserInputHandler inputHandler = new UserInputHandler();
            boolean isPassive = inputHandler.getMode() == 2;
            String address = inputHandler.getServerAddress();
            int port = inputHandler.getServerPort();
            String login = inputHandler.getLogin();
            String password = inputHandler.getPassword();

            try(FTPConnection ftpConnection = FTPConnectionFactory.createConnection(login, password, address, port, isPassive)){

                ftpConnection.connect();
                String fileName = ftpConnection.findFile();
                String fileContent = ftpConnection.downloadFileContent(fileName);
                FileProcessor fileProcessor = new FileProcessor(fileContent);

                int action = inputHandler.getUserAction();
                loop:
                while (true){
                    switch (action){
                        case (1):
                            System.out.println(fileProcessor.getStudentsByName(inputHandler.getStudentName()));
                            break;
                        case (2):
                            System.out.println(fileProcessor.getStudentById(inputHandler.getStudentId()));
                            break;
                        case(3):
                            System.out.println(fileProcessor.addStudent(inputHandler.getStudentName()));
                            break;
                        case(4):
                            System.out.println(fileProcessor.removeStudentById(inputHandler.getStudentId()));
                            break;
                        case (5):
                            System.out.println("Сохранение..");
                            ftpConnection.uploadFileContent(fileName, fileProcessor.getUploadContent());
                            System.out.println("Выход..");
                            break loop;
                    }
                    action = inputHandler.getUserAction();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
            run();
        }
    }



    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }
}
