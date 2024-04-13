package cos.hudie.hk.groupTimeTable.FileUtils;

import com.aspose.cells.*;
import cos.hudie.hk.groupTimeTable.GroupManager.Group;
import cos.hudie.hk.groupTimeTable.Main;
import cos.hudie.hk.groupTimeTable.Student;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT.FilePath;
import static cos.hudie.hk.groupTimeTable.Main.DataBaseQuery;

public class ExportToExcel {
    public static void ExportToExcelStudent() {
        Workbook workbook = new Workbook();

        workbook.getWorksheets().removeAt(0);

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

        for (Student student : StudentList) {
            var worksheet = workbook.getWorksheets().add(student.getStudentID());
            Style style = workbook.createStyle();
            style.setPattern(BackgroundType.SOLID);
            style.setBackgroundColor(Color.getRed());
            style.setForegroundColor(Color.getRed());
            worksheet.getCells().get(0, 0).setValue("Student Name:");
            worksheet.getCells().get(1, 0).setValue("Student ID:");
            worksheet.getCells().get(0, 1).setValue(student.getStudentName());
            worksheet.getCells().get(1, 1).setValue(student.getStudentID());
            worksheet.getCells().get(2, 0).setValue("Student TimeTable:");
            QuickPrintTime(worksheet, 4, 0);
            int i3 = 1;
            for (List<Integer> integers : student.TimeTable) {
                for (int i = 0; i < integers.size(); i++) {
                    int StartingTime = integers.get(i++);
                    int EndingTime = integers.get(i);
                    for (int i2 = 4; i2 < 51; i2++) {
                        if (Integer.parseInt((String) worksheet.getCells().get(i2, 0).getValue()) == StartingTime) {
                            int Index = i2 + 1;
                            while (Integer.parseInt((String) worksheet.getCells().get(Index - 1, 0).getValue()) != EndingTime) {
                                worksheet.getCells().get(Index, i3).setStyle(style);
                                Index++;
                            }
                            break;
                        }
                    }
                }
                i3++;
            }
        }
        try {
            workbook.save("outputFiles/Student.xlsx");
            System.out.println("File Saved In:" + FilePath
                    .concat("outputFiles/Student.xlsx"));
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Creating Directory");
            try {
                new File(FilePath.concat("outputFiles/")).mkdirs();
                workbook.save("outputFiles/Student.xlsx", SaveFormat.XLSX);
                System.out.println("File Saved In:" + FilePath.concat("outputFiles/Student.xlsx"));
            } catch (Exception e2) {
                System.out.println("File is using , Please close : " + FilePath.concat("outputFiles/Student.xlsx"));
            }
        }
    }

    public static void ExportToExcelPerGroup(Group group) throws Exception {
        group.sortTimeTable();
        Workbook workbook = new Workbook();

        workbook.getWorksheets().removeAt(0);

        for (Student student : group.StudentList) {
            var worksheet = workbook.getWorksheets().add(student.getStudentID());
            Style style = workbook.createStyle();
            style.setPattern(BackgroundType.SOLID);
            style.setBackgroundColor(Color.getRed());
            style.setForegroundColor(Color.getRed());
            worksheet.getCells().get(0, 0).setValue("Student Name:");
            worksheet.getCells().get(1, 0).setValue("Student ID:");
            worksheet.getCells().get(0, 1).setValue(student.getStudentName());
            worksheet.getCells().get(1, 1).setValue(student.getStudentID());
            worksheet.getCells().get(2, 0).setValue("Student TimeTable:");
            QuickPrintTime(worksheet, 4, 0);
            int i3 = 1;
            for (List<Integer> integers : student.TimeTable) {
                for (int i = 0; i < integers.size(); i++) {
                    int StartingTime = integers.get(i++);
                    int EndingTime = integers.get(i);
                    for (int i2 = 4; i2 < 51; i2++) {
                        if (Integer.parseInt((String) worksheet.getCells().get(i2, 0).getValue()) == StartingTime) {
                            int Index = i2 + 1;
                            while (Integer.parseInt((String) worksheet.getCells().get(Index - 1, 0).getValue()) != EndingTime) {
                                worksheet.getCells().get(Index, i3).setStyle(style);
                                Index++;
                            }
                            break;
                        }
                    }
                }
                i3++;
            }
        }
        var worksheet = workbook.getWorksheets().add("Student Time Table");
        for (Student student : group.StudentList) {
            Style style = workbook.createStyle();
            style.setPattern(BackgroundType.SOLID);
            style.setBackgroundColor(Color.getRed());
            style.setForegroundColor(Color.getRed());
            QuickPrintTime(worksheet, 1, 0);
            int i3 = 1;
            for (List<Integer> integers : student.TimeTable) {
                for (int i = 0; i < integers.size(); i++) {
                    int StartingTime = integers.get(i++);
                    int EndingTime = integers.get(i);
                    for (int i2 = 1; i2 < 49; i2++) {
                        if (Integer.parseInt((String) worksheet.getCells().get(i2, 0).getValue()) == StartingTime) {
                            int Index = i2 + 1;
                            while (Integer.parseInt((String) worksheet.getCells().get(Index - 1, 0).getValue()) != EndingTime) {
                                worksheet.getCells().get(Index, i3).setStyle(style);
                                Index++;
                            }
                            break;
                        }
                    }
                }
                i3++;
            }
        }
        worksheet = workbook.getWorksheets().add("Members Free Time");
        int i3 = 1;
        for (List<Integer> table : group.FinalTimeTable) {
            Style style = workbook.createStyle();
            style.setPattern(BackgroundType.SOLID);
            style.setBackgroundColor(Color.getLightGreen());
            style.setForegroundColor(Color.getLightGreen());
            QuickPrintTime(worksheet, 1, 0);

            for (int i = 0; i < table.size(); i++) {
                int StartingTime = table.get(i++);
                int EndingTime = table.get(i);
                for (int i2 = 1; i2 < 49; i2++) {
                    if (Integer.parseInt((String) worksheet.getCells().get(i2, 0).getValue()) == StartingTime) {
                        int Index = i2 + 1;
                        while (Integer.parseInt((String) worksheet.getCells().get(Index - 1, 0).getValue()) != EndingTime) {
                            worksheet.getCells().get(Index, i3).setStyle(style);
                            Index++;
                        }
                        break;
                    }
                }
            }
            i3++;
        }
        worksheet = workbook.getWorksheets().add("Members INFO");
        worksheet.getCells().get(0, 0).setValue("Student Name");
        worksheet.getCells().get(0, 1).setValue("Student ID");
        worksheet.getCells().get(0, 3).setValue("Tutorial Group");
        worksheet.getCells().get(0, 2).setValue("Group Leader");
        worksheet.getCells().get(1, 3).setValue(group.getTutorGroup());
        for (int i = 1; i < group.StudentList.size() + 1; i++) {
            worksheet.getCells().get(i, 0).setValue(group.StudentList.get(i - 1).getStudentName());
            worksheet.getCells().get(i, 1).setValue(group.StudentList.get(i - 1).getStudentID());
            worksheet.getCells().get(i, 2).setValue(group.StudentList.get(i - 1).isGroupLeader() ? "*" : "");
        }
        worksheet = workbook.getWorksheets().add("Memo");
        worksheet.getCells().get(0, 0).setValue("Memo Name");
        worksheet.getCells().get(0, 1).setValue("Starting Time");
        worksheet.getCells().get(0, 2).setValue("Ending Time");
        worksheet.getCells().get(0, 3).setValue("Description");
        for (int i = 1; i < group.MemoList.size(); i++) {
            worksheet.getCells().get(i, 0).setValue(group.MemoList.get(i).getName());
            worksheet.getCells().get(i, 1).setValue(group.MemoList.get(i).getStartingTime_DATE() + " " + group.MemoList.get(i).getStartingTime_TIME());
            worksheet.getCells().get(i, 2).setValue(group.MemoList.get(i).getEndingTime_DATE() + " " + group.MemoList.get(i).getEndingTime_TIME());
            worksheet.getCells().get(i, 3).setValue(group.MemoList.get(i).getDescription());
        }
        Path path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1));
        Path FileName = path.getFileName();
        try {
            workbook.save("outputFiles/%s.xlsx".formatted(group.getGroupID() + "_" + group.getLectureID()), SaveFormat.XLSX);
            System.out.println("File Saved In:" + FilePath
                    .concat("outputFiles/%s.xlsx".formatted(group.getGroupID() + "_" + group.getLectureID())));
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.out.println("Creating Directory");
            try {
                new File("outputFiles/").mkdirs();
                workbook.save("outputFiles/%s.xlsx".formatted(group.getGroupID() + "_" + group.getLectureID()), SaveFormat.XLSX);
                System.out.println("File Saved In:" + FilePath.concat("outputFiles/%s.xlsx".formatted(group.getGroupID() + "_" + group.getLectureID())));
            } catch (FileNotFoundException e2) {
                System.out.println("File is using , Please close : " + FilePath.concat("outputFiles/%s.xlsx".formatted(group.getGroupID() + "_" + group.getLectureID())));
            }
        }
    }

    public static void QuickPrintTime(Worksheet worksheet, int StartingRow, int StartingColumn) {
        StartingRow--;
        StartingColumn++;
        worksheet.getCells().get(StartingRow, StartingColumn++).setValue("Monday");
        worksheet.getCells().get(StartingRow, StartingColumn++).setValue("Tuesday");
        worksheet.getCells().get(StartingRow, StartingColumn++).setValue("Wednesday");
        worksheet.getCells().get(StartingRow, StartingColumn++).setValue("Thursday");
        worksheet.getCells().get(StartingRow, StartingColumn++).setValue("Friday");
        worksheet.getCells().get(StartingRow, StartingColumn++).setValue("Saturday");
        worksheet.getCells().get(StartingRow, StartingColumn).setValue("Sunday");
        StartingColumn -= 7;
        StartingRow++;
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0000");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0030");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0100");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0130");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0200");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0230");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0300");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0330");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0400");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0430");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0500");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0530");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0600");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0630");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0700");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0730");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0800");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0830");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0900");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("0930");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1000");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1030");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1100");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1130");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1200");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1230");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1300");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1330");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1400");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1430");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1500");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1530");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1600");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1630");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1700");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1730");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1800");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1830");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1900");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("1930");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("2000");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("2030");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("2100");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("2130");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("2200");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("2230");
        worksheet.getCells().get(StartingRow++, StartingColumn).setValue("2300");
        worksheet.getCells().get(StartingRow, StartingColumn).setValue("2330");
    }
}
