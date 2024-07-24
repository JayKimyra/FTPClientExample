import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PassiveFTPConnection implements FTPConnection {
    private final String login;
    private final String password;
    private final String SERVER_ADDRESS;
    private final int SERVER_PORT;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public PassiveFTPConnection(String login, String password, String address, int port) {
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
    public String downloadFileContent(String filename) throws IOException {
        String ans;
        StringBuilder sb = new StringBuilder();
        writer.println("PASV");
        ans = reader.readLine();
        if (!ans.startsWith("227")) {
            throw new RuntimeException(ans);
        }
        String parts = getPartsFromPASV(ans);
        try (Socket dataSocket = new Socket(parts.split(":")[0], Integer.parseInt(parts.split(":")[1]));
             BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {

            writer.println("RETR " + filename);
            String retrResponse = reader.readLine();
            if (!retrResponse.startsWith("150")) {
                throw new RuntimeException(retrResponse);
            }

            String dataLine;
            while ((dataLine = dataReader.readLine()) != null) {
                sb.append(dataLine);
            }

            String retrResponse2 = reader.readLine();
            if (!retrResponse2.startsWith("226")) {
                throw new RuntimeException(retrResponse2);
            }
        }
        return sb.toString();
    }

    @Override
    public String findFile() throws IOException {
        writer.println("PASV");
        String line = reader.readLine();
        String parts = getPartsFromPASV(line);

        List<String> files = new ArrayList<>();
        try (Socket dataSocket = new Socket(parts.split(":")[0], Integer.parseInt(parts.split(":")[1]));
             BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {

            writer.println("LIST");
            String listResponse = reader.readLine();
            if (!listResponse.startsWith("150")) {
                throw new RuntimeException(listResponse);
            }

            // Чтение данных переданных по соединению передачи данных
            String dataLine;
            while ((dataLine = dataReader.readLine()) != null) {
                files.add(dataLine);
            }
            reader.readLine();
        }
        return files.stream()
                .filter(x -> x.toCharArray()[0] == '-')
                .map(x -> x.split(" "))
                .map(x -> x[x.length - 1])
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }


    private String getPartsFromPASV(String response) {
        String[] arr = response.split(" ");
        String[] parts = arr[arr.length - 1].substring(1, arr[arr.length - 1].length() - 1).split(",");
        String address = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
        return address + ":" + port;
    }

    @Override
    public void uploadFileContent(String filename, String content) throws IOException {
        writer.println("PASV");
        String line = reader.readLine();
        String parts = getPartsFromPASV(line);

        try (Socket dataSocket = new Socket(parts.split(":")[0], Integer.parseInt(parts.split(":")[1]));
             BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))) {

            String storCommand = "STOR " + filename;
            writer.println(storCommand);
            String storResponse = reader.readLine();
            if (!storResponse.startsWith("150")) {
                throw new RuntimeException(storResponse);
            }
            dataWriter.write(content);
        }
        String storResponse2 = reader.readLine();
        if (!storResponse2.startsWith("226")) {
            throw new RuntimeException(storResponse2);
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
