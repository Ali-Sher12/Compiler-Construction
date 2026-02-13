import java.io.*;
import java.util.*;
import java.nio.file.*;

class Main{
    static void main()
    {
        try {
            PrintStream fileOut = new PrintStream("/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/TestResults.txt");
            System.setOut(fileOut);
            System.out.println("\n\n\t\t <<<<<<<<<<<<<<<<<  Test1 >>>>>>>>>>>>>>>>:\n");
            ManualScanner ms1 = new ManualScanner("/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test1.lang");
            ms1.tokenise();
            ms1.printList();
            System.out.println("\n\n\t\t <<<<<<<<<<<<<<<<<  Test2 >>>>>>>>>>>>>>>>:\n");
            ManualScanner ms2 = new ManualScanner("/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test2.lang");
            ms2.tokenise();
            ms2.printList();
            System.out.println("\n\n\t\t <<<<<<<<<<<<<<<<<  Test3 >>>>>>>>>>>>>>>>:\n");
            ManualScanner ms3 = new ManualScanner("/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test3.lang");
            ms3.tokenise();
            ms3.printList();
            System.out.println("\n\n\t\t <<<<<<<<<<<<<<<<<  Test4 >>>>>>>>>>>>>>>>:\n");
            ManualScanner ms4 = new ManualScanner("/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test4.lang");
            ms4.tokenise();
            ms4.printList();
            System.out.println("\n\n\t\t <<<<<<<<<<<<<<<<<  Test5 >>>>>>>>>>>>>>>>:\n");
            ManualScanner ms5 = new ManualScanner("/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test5.lang");
            ms5.tokenise();
            ms5.printList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}