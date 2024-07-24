import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ActiveFTPConnection implements FTPConnection {
    private final String login;
    private final String password;
    private final String SERVER_ADDRESS;
    private final int SERVER_PORT;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ActiveFTPConnection(String login, String password, String address, int port) {
        this.login = login;
        this.password = password;
        SERVER_ADDRESS = address;
        SERVER_PORT = port;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        String line;
        do {
            line = reader.readLine();
            //System.out.println(line);
        } while (!line.split(" ")[0].equals("220"));
        writer.println("USER " + login);
        String userResponse = reader.readLine();
        if (!userResponse.startsWith("331")) {
            throw new RuntimeException(userResponse);
        }
        writer.println("PASS " + password);
        String loginResult = reader.readLine();
        if (!loginResult.startsWith("230")) {
            throw new RuntimeException(loginResult);
        }
    }


    @Override
    public String findFile() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            String ipAddress = InetAddress.getLoopbackAddress().getHostAddress().replace(".", ",");
            int port = serverSocket.getLocalPort();
            int port1 = port / 256;
            int port2 = port % 256;
            String portCommand = "PORT " + ipAddress + "," + port1 + "," + port2;
            writer.println(portCommand);
            String portResponse = reader.readLine();
            if (!portResponse.startsWith("200")) {
                throw new RuntimeException(portResponse);
            }
            writer.println("LIST");
            String listResponse = reader.readLine();
            if (!listResponse.startsWith("150")) {
                throw new RuntimeException(listResponse);
            }
            List<String> files = new ArrayList<>();
            try (Socket dataSocket = serverSocket.accept();
                 BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String dataLine;
                while ((dataLine = dataReader.readLine()) != null) {
                    files.add(dataLine);
                }
            }

            String listResponse2 = reader.readLine();
            if (!listResponse2.startsWith("226")) {
                throw new RuntimeException(listResponse);
            }
            return files.stream()
                    .filter(x -> x.toCharArray()[0] == '-')
                    .map(x -> x.split(" "))
                    .map(x -> x[x.length - 1])
                    .findAny()
                    .orElseThrow(NoSuchElementException::new);
        }
    }

    @Override
    public String downloadFileContent(String filename) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            String ipAddress = InetAddress.getLoopbackAddress().getHostAddress().replace(".", ",");
            int port = serverSocket.getLocalPort();
            int port1 = port / 256;
            int port2 = port % 256;
            String portCommand = "PORT " + ipAddress + "," + port1 + "," + port2;
            writer.println(portCommand);
            String portResponse = reader.readLine();
            if (!portResponse.startsWith("200")) {
                throw new RuntimeException(portResponse);
            }
            writer.println("RETR " + filename);
            String retrResponse = reader.readLine();
            if (!retrResponse.startsWith("150")) {
                throw new RuntimeException(retrResponse);
            }
            try (Socket dataSocket = serverSocket.accept();
                 BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String dataLine;
                while ((dataLine = dataReader.readLine()) != null) {
                    sb.append(dataLine);
                }

                String retrResponse2 = reader.readLine();
                if (!retrResponse2.startsWith("226")) {
                    throw new RuntimeException(retrResponse2);
                }
                return sb.toString();
            }
        }
    }

    @Override
    public void uploadFileContent(String filename, String content) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            String ipAddress = InetAddress.getLoopbackAddress().getHostAddress().replace(".", ",");
            int port = serverSocket.getLocalPort();
            int port1 = port / 256;
            int port2 = port % 256;
            String portCommand = "PORT " + ipAddress + "," + port1 + "," + port2;
            writer.println(portCommand);
            String portResponse = reader.readLine();
            if (!portResponse.startsWith("200")) {
                throw new RuntimeException(portResponse);
            }

            String storCommand = "STOR " + filename;
            writer.println(storCommand);
            String storResponse = reader.readLine();
            if (!storResponse.startsWith("150")) {
                throw new RuntimeException(storResponse);
            }
            try (Socket dataSocket = serverSocket.accept();
                 BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))){
                dataWriter.write(content);
            }
            String storResponse2 = reader.readLine();
            if (!storResponse2.startsWith("226")) {
                throw new RuntimeException(storResponse);
            }

        }
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
}
