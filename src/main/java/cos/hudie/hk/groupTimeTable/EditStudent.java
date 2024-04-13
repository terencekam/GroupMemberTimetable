package cos.hudie.hk.groupTimeTable;

import cos.hudie.hk.groupTimeTable.FileUtils.ExportToExcel;
import cos.hudie.hk.groupTimeTable.FileUtils.ExportToTXT;
import cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT;
import cos.hudie.hk.groupTimeTable.FileUtils.Import.WebPaste;
import cos.hudie.hk.groupTimeTable.GroupManager.Group;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Scanner;


public class EditStudent {
    public static void EditStudent(Group group, String SID) {
        EditStudent(group.getGroupID(), Main.DataBaseQuery.getStudent(SID));
    }

    public static void EditStudent(int GroupID, String SID) {
        EditStudent(GroupID, Main.DataBaseQuery.getStudent(SID));
    }

    public static void EditStudent(int groupID, Student student) {
        var end = false;
        Scanner scanner = new Scanner(System.in);

        while (!end) {
            System.out.println();
            System.out.println("""
                    1. edit StudentName
                    2. edit StudentTimeTable
                    3. set this student as GroupLeader
                    4. exit
                    """);
            try {
                switch (scanner.nextInt()) {
                    case 1:
                        System.out.printf("Please input new Student Name (Old Student Name: %s): ", student.getStudentName());
                        student.setStudentName(scanner.next() + scanner.nextLine());
                        break;
                    case 2:
                        System.out.print("Please input the days you want to edit: \n" +
                                "ie: Monday = 0 , Tuesday = 1 , Wednesday = 2 , Thursday = 3 ," +
                                "Friday = 4 , Saturday = 5 , Sunday = 6");
                        var TimeSet = new ArrayList<Integer>();
                        var days = scanner.nextInt();
                        System.out.printf("Input %s 's student Time Table Session, Finish by \"e\": \n (%s)", student.getStudentName(), DayOfWeek.values()[days]);
                        int i = 1;
                        String temp;
                        do {
                            System.out.printf("Session %s [Start Time , End Time ]: ", i);
                            i++;
                            temp = scanner.next();
                            int StartTime = 0;
                            if (!temp.equalsIgnoreCase("e")) {
                                try {
                                    StartTime = Integer.parseInt(temp);
                                    var EndTime = scanner.nextInt();
                                    if(EndTime>=2330||StartTime<=30){
                                        System.out.println("The Input Time need to be in range 0030 - 2330");
                                    }else if ((EndTime%100 != 30 && EndTime%100 != 0) || (StartTime%100 != 30 && StartTime%100 != 0)) {
                                        System.out.println("The minutes should be 30 or 00");
                                    }else{
                                        if (StartTime < EndTime) {
                                            TimeSet.add(StartTime);
                                            TimeSet.add(EndTime);
                                        } else {
                                            System.out.println("The session you input is incorrect since the Start Time >= End Time , Please input again");
                                            i--;
                                        }
                                    }

                                } catch (Exception e) {
                                    System.out.println("Wrong input: " + e);
                                    i--;
                                }

                            }
                        } while (!temp.equalsIgnoreCase("e"));
                        student.TimeTable.set(days, TimeSet);
                        break;
                    case 3:
                        System.out.printf("Student %s has been set as Group Leader , if there are existing one , it will be replaced", student.getStudentName());
                        student.setGroupLeader();
                        Main.DataBaseQuery.updateGroupMember(groupID, student);
                    case 4:
                        end = true;
                        Main.DataBaseQuery.updateStudent(student);
                        break;
                    default:
                        System.out.println("Wrong Number Input");
                }
            } catch (Exception e) {
                Main.contactDeveloper(e);
            }
        }


        System.out.println(student);
    }

    public static void EditLocalStudent(Student student) {
        var end = false;
        Scanner scanner = new Scanner(System.in);

        while (!end) {
            System.out.println();
            System.out.println("""
                    1. edit StudentName
                    2. edit StudentTimeTable
                    3. exit
                    """);
            try {
                switch (scanner.nextInt()) {
                    case 1:
                        System.out.printf("Please input new Student Name (Old Student Name: %s): ", student.getStudentName());
                        student.setStudentName(scanner.next() + scanner.nextLine());
                        break;
                    case 2:
                        System.out.print("Please input the days you want to edit: \n" +
                                "ie: Monday = 0 , Tuesday = 1 , Wednesday = 2 , Thursday = 3 ," +
                                "Friday = 4 , Saturday = 5 , Sunday = 6");
                        var TimeSet = new ArrayList<Integer>();
                        var days = scanner.nextInt();
                        System.out.printf("Input %s 's student Time Table Session, Finish by \"e\": \n (%s)", student.getStudentName(), DayOfWeek.values()[days]);
                        int i = 1;
                        String temp;
                        do {
                            System.out.printf("Session %s [Start Time , End Time ]: ", i);
                            i++;
                            temp = scanner.next();
                            int StartTime = 0;
                            if (!temp.equalsIgnoreCase("e")) {
                                try {
                                    StartTime = Integer.parseInt(temp);
                                    var EndTime = scanner.nextInt();
                                    if(EndTime>=2330||StartTime<=0030){
                                        System.out.println("The Input Time need to be in range 0030 - 2330");
                                    }else if ((EndTime%100 != 30 && EndTime%100 != 0) || (StartTime%100 != 30 && StartTime%100 != 0)) {
                                        System.out.println("The minutes should be 30 or 00");
                                    }else{
                                        if (StartTime < EndTime) {
                                            TimeSet.add(StartTime);
                                            TimeSet.add(EndTime);
                                        } else {
                                            System.out.println("The session you input is incorrect since the Start Time >= End Time , Please input again");
                                            i--;
                                        }
                                    }

                                } catch (Exception e) {
                                    System.out.println("Wrong input: " + e);
                                    i--;
                                }

                            }
                        } while (!temp.equalsIgnoreCase("e"));
                        student.TimeTable.set(days, TimeSet);
                        break;
                    case 3:
                        end = true;
                        Main.DataBaseQuery.updateStudent(student);
                        break;
                    default:
                        System.out.println("Wrong Number Input");
                }
            } catch (Exception e) {
                Main.contactDeveloper(e);
            }
        }


        System.out.println(student);
    }

    public static void StudentInitialisation() {
        System.out.println("Please Input Student Name: ");
        Scanner scanner = new Scanner(System.in);
        var SName = scanner.nextLine();
        String accept;
        String SID = "";
        do {
            System.out.println("Please Input Student ID");
            SID = scanner.next();

            System.out.printf("Please confirm the SID : %s %s  \n" +
                    "%s Warning! Student ID Cannot edit unless you remove the old student and make a new one \n " +
                    "%sConfirm Please type 'y' or press any key to re-enter: ", Main.ANSI_CYAN, SID, Main.ANSI_RED, Main.ANSI_RESET);
            accept = scanner.next();
        } while (!accept.equalsIgnoreCase("y"));
        if (!Main.DataBaseQuery.hasStudent(SID)) {
            var thisStudent = new Student(SName, SID);
            var input = 0;
            while (input != 1 && input!=2) {
                try {
                    System.out.println("""
                            Please select the TimeTable input method
                            1. input by typing
                            2. input by webPaste (If you don't know how to do so, Please find the docs)
                            """);
                    var tempInput = scanner.next();
                    input = Integer.parseInt(tempInput);

                    switch (input) {
                        case 1:
                            for (int i2 = 0; i2 < DayOfWeek.values().length; i2++) {
                                var TimeSet = new ArrayList<Integer>();
                                System.out.printf("Input %s 's student Time Table Session, Finish by \"e\" or Skip All by \"SkipAll\": \n (%s)", SName, DayOfWeek.values()[i2]);
                                int i = 1;
                                String temp;
                                do {
                                    System.out.printf("Session %s [Start Time , End Time ]: ", i);
                                    i++;
                                    temp = scanner.next();
                                    int StartTime;
                                    if ((!temp.equalsIgnoreCase("e")) && (!temp.equalsIgnoreCase("SkipAll"))) {
                                        try {
                                            StartTime = Integer.parseInt(temp);
                                            var EndTime = scanner.nextInt();
                                            if(EndTime>=2330||StartTime<=0030){
                                                System.out.println("The Input Time need to be in range 0030 - 2330");
                                            }else if ((EndTime%100 != 30 && EndTime%100 != 0) || (StartTime%100 != 30 && StartTime%100 != 0)) {
                                                System.out.println("The minutes should be 30 or 00");
                                            }else{
                                                if (StartTime < EndTime) {
                                                    TimeSet.add(StartTime);
                                                    TimeSet.add(EndTime);
                                                } else {
                                                    System.out.println("The session you input is incorrect since the Start Time >= End Time , Please input again");
                                                    i--;
                                                }
                                            }


                                        } catch (Exception e) {
                                            System.out.println("Wrong input: " + e);
                                            i--;
                                        }

                                    }

                                } while (!temp.equalsIgnoreCase("e") && !temp.equalsIgnoreCase("SkipAll"));
                                thisStudent.TimeTable.add(TimeSet);
                                if (temp.equalsIgnoreCase("SkipAll")) {
                                    for (int i3 = 0; i3 < (DayOfWeek.values().length - i2 - 1); i3++) {
                                        thisStudent.TimeTable.add(new ArrayList<>());
                                    }
                                    break;
                                }
                            }
                            Main.DataBaseQuery.addStudent(thisStudent);
                            break;
                        case 2:
                            thisStudent.TimeTable = WebPaste.WebPaste();
                            Main.DataBaseQuery.addStudent(thisStudent);
                            break;
                        default:
                            System.out.println("Error: Wrong Input");
                    }

                } catch (Exception e) {
                    Main.contactDeveloper(e);
                }
            }
        } else {
            System.out.println("This Student has added to database already!");
        }


    }

    public static void StudentEditor() {
        boolean exit = false;
        while (!exit) {
            System.out.println("""
                    1. add new Student
                    2. edit student
                    3. Delete a student
                    4. search student profile
                    5. print all student profile
                    6. export specific Student to txt
                    7. export specific Student to excel
                    8. import Student from txt
                    9. save and exit
                    """);
            try {
                switch (new Scanner(System.in).nextInt()) {
                    case 1:
                        StudentInitialisation();
                        break;
                    case 2:
                        System.out.println("Input a StudentID to edit:");
                        String SID = new Scanner(System.in).next();
                        if (Main.DataBaseQuery.hasStudent(SID)) {
                            EditStudent.EditLocalStudent(Main.DataBaseQuery.getStudent(SID));
                        } else {
                            System.out.println("Student doesn't exist in this group");
                        }
                        break;
                    case 3:
                        System.out.println("Input a StudentID to remove:");
                        SID = new Scanner(System.in).next();
                        if (Main.DataBaseQuery.hasStudent(SID)) {
                            Main.DataBaseQuery.deleteStudent(SID);
                            System.out.println("Student Deleted");
                        } else {
                            System.out.println("Student not found");
                        }
                        break;
                    case 4:
                        System.out.println("Input a StudentID to search:");
                        SID = new Scanner(System.in).next();
                        if (Main.DataBaseQuery.hasStudent(SID)) {
                            System.out.println(Main.DataBaseQuery.getStudent(SID));
                        } else {
                            System.out.println("Student not found");
                        }
                        break;
                    case 5:
                        Main.DataBaseQuery.getAllStudent().forEach(System.out::println);
                        break;
                    case 6:
                        new ExportToTXT();
                        break;
                    case 7:
                        ExportToExcel.ExportToExcelStudent();
                        break;
                    case 8:
                        new ImportFromTXT();
                        break;
                    case 9:
                        exit = true;
                        break;
                    default:
                        System.out.println("Wrong Input");
                }
            } catch (Exception e) {
                Main.contactDeveloper(e);
            }
        }
    }


}
