import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class FileProcessor {
    private final List<Student> students;

    public FileProcessor(String fileContent) {
        String[] studentEntries = fileContent.split("\\{\\s*\"id\"");
        List<Student> students = new ArrayList<>();
        for (int i = 1; i < studentEntries.length; i++) {
            String entry = studentEntries[i];
            int id = Integer.parseInt(entry.split(":")[1].split(",")[0].trim());
            String name = entry.split(":")[2].split("\"")[1].trim();
            students.add(new Student(id, name));
        }
        this.students = students;
    }
    public String getStudentsList(){
        return students.toString();
    }
    public List<Student> getStudentsByName(String name){
        return students.stream().filter(x -> x.getName().equals(name)).collect(Collectors.toList());
    }
    public Student getStudentById(int id){
        return students.stream().filter(x -> x.getId() == id).findAny().orElseThrow(NoSuchElementException::new);
    }
    public boolean addStudent(String name){
        int id = students.stream().map(Student::getId).max(Integer::compareTo).orElse(-1) + 1;
        students.add(new Student(id, name));
        return true;
    }
    public boolean removeStudentById(int id){
        return students.removeIf(x -> x.getId() == id);
    }


    public String getUploadContent() {
        return "{\"students\": [" +
                students.stream().map(x -> "{\"id\": " + x.getId() + ", \"name\": \"" + x.getName() +"\"}").collect(Collectors.joining(", ")) +
                "]}";
    }
}
