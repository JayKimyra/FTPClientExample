public class FTPConnectionFactory {

    public static FTPConnection createConnection(String login, String password, String address, int port, boolean passive) {
        if (passive) {
            return new PassiveFTPConnection(login, password, address, port);
        } else {
            return new ActiveFTPConnection(login, password, address, port);
        }
    }
}