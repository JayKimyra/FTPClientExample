import java.io.*;

public interface FTPConnection extends AutoCloseable {

    void connect() throws IOException;


    String downloadFileContent(String filename) throws IOException;

    String findFile() throws IOException;

    void uploadFileContent(String filename, String content) throws IOException;
}
