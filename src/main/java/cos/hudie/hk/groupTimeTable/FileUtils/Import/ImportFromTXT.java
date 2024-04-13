package cos.hudie.hk.groupTimeTable.FileUtils.Import;

import cos.hudie.hk.groupTimeTable.Main;
import cos.hudie.hk.groupTimeTable.Student;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ImportFromTXT {
    public static final Path path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1));
    public static final Path FileName = path.getFileName();
    public static final String FilePath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1)
            .replace(FileName.toString(), "").replace("%20", " ");

    public ImportFromTXT() throws SQLException {
        String FileContent;
        String FilePath = "import/";
        System.out.println(FilePath);
        new File(FilePath).mkdirs();
        System.out.printf("Please place the file export.txt under %s directory and type \"ok\" \n OR specify the .txt file (e.g. import.txt ) " +
                        "and place the under %s directory : \n",
                FilePath, FilePath);
        String tempString = new Scanner(System.in).next();
        try {
            Path file = Paths.get(FilePath.concat((tempString.equalsIgnoreCase("ok")) ? "export.txt" : tempString));
            FileContent = Files.readString(file);
        } catch (IOException e) {
            Main.contactDeveloper(e);
            throw new RuntimeException(e);
        }
        List<String> list = List.of(FileContent.split("\r\n"));
        int i = 0;
        List<Integer> tempList = new ArrayList<>();
        while (i < list.size() - 1) {
            Student student = new Student(list.get(i++), list.get(i++));
            while (!list.get(i).equalsIgnoreCase("end")) {
                if (!list.get(i).equalsIgnoreCase("skip")) {
                    for (String s : list.get(i).split(" ")) {
                        tempList.add(Integer.parseInt(s));
                    }

                    student.TimeTable.add(new ArrayList<>(tempList));

                    tempList.clear();
                } else {
                    student.TimeTable.add(new ArrayList<>());
                }
                i++;
            }
            i++;
            while (student.TimeTable.size() < 7) {
                student.TimeTable.add(new ArrayList<>());
            }
//            Main.StudentList.add(student);
            if(!Main.DataBaseQuery.hasStudent(student.getStudentID())){
                Main.DataBaseQuery.addStudent(student);
            }else{
                System.out.println("The database has found student with id : %s . Skipping...".formatted(student.getStudentID()));
            }
        }
        System.out.println("Import successful");

    }
}
