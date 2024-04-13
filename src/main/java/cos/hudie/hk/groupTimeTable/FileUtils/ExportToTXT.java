package cos.hudie.hk.groupTimeTable.FileUtils;

import cos.hudie.hk.groupTimeTable.Main;
import cos.hudie.hk.groupTimeTable.Student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT.FilePath;
import static cos.hudie.hk.groupTimeTable.Main.*;


public class ExportToTXT {
    public ExportToTXT() {
        boolean exit = false;
        List<Integer> StudentNum = new ArrayList<>();
        List<Student> StudentList = DataBaseQuery.getAllStudent();
        while (!exit) {
            int i = 0;
            for (Student student : StudentList) {
                System.out.printf("%s: %s (ID: %s) \n", i, student.getStudentName(), student.getStudentID());
                i++;
            }
            System.out.printf("%s: select all", i);
            Collections.sort(StudentNum);
            System.out.printf("(Selected: %s ) \n Please select the member to export (Please select the number: )  Finish and export with \"e\" :", StudentNum);
            Scanner scanner = new Scanner(System.in);
            String scannerInput = null;
            try {
                scannerInput = scanner.next();
                if ((Integer.parseInt(scannerInput) < StudentList.size()) && Integer.parseInt(scannerInput) >= 0) {
                    if (!StudentNum.contains(Integer.parseInt(scannerInput))) {
                        StudentNum.add(Integer.parseInt(scannerInput));
                    } else {
                        System.out.println("User added already");
                    }

                } else {
                    if ((Integer.parseInt(scannerInput)) == i) {

                    }
                    System.out.println("Wrong Number Input");
                }
            } catch (Exception e) {
                assert scannerInput != null;
                if (scannerInput.equalsIgnoreCase("e")) {
                    exit = true;
                    System.out.println("Exporting ...");
                } else {
                    System.out.println("Wrong Input with ERROR : " + e);
                }
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Integer i : StudentNum) {
            stringBuilder.append(StudentList.get(i).getStudentName()).append("\n")
                    .append(StudentList.get(i).getStudentID()).append("\n");
            for (List<Integer> integers : StudentList.get(i).TimeTable) {
                if (!integers.isEmpty()) {
                    integers.forEach(f -> stringBuilder.append(f).append(" "));
                    stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
                    stringBuilder.append("\n");
                } else {
                    stringBuilder.append("skip\n");
                }

            }
            stringBuilder.append("end");
            stringBuilder.append("\n");
        }
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");

        while (true) {
            System.out.println(ANSI_RED + "WARNING" + ANSI_RESET + " Please do not edit the export.txt file unless you know what you are doing ... \n" +
                    "Input \"ok\" to continue");
            if (new Scanner(System.in).next().equalsIgnoreCase("ok")) {
                break;
            } else {
                System.out.println("Wrong input");
            }
        }


        Path path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1));
        Path FileName = path.getFileName();
        try {
            Path file = Paths.get("outputFiles/export.txt");
            Files.writeString(file, stringBuilder);
            System.out.println("File Saved In:" + FilePath.concat("outputFiles/export.txt"));

        } catch (FileNotFoundException e) {
            System.out.println("Creating Directory");
            try {
                new File("outputFiles/").mkdirs();
                Path file = Paths.get("outputFiles/export.txt");
                Files.writeString(file, stringBuilder);
                System.out.println("File Saved In:" + FilePath.concat("outputFiles/export.txt"));
            } catch (FileNotFoundException e2) {
                System.out.println("File is using , Please close : " + FilePath.concat("outputFiles/export.txt"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Output Error with: " + e);
        }


    }
}
