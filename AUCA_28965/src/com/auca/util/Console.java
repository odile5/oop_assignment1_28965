package com.auca.util;

import java.util.Scanner;

/** Holds the ONE shared Scanner for System.in. Everything is read with nextLine(). */
public class Console {
    private static final Scanner SCANNER = new Scanner(System.in);

    private Console() {
    }

    public static String read(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) { // input finished (e.g. Ctrl+D): stop cleanly
            System.out.println("\nNo more input. Goodbye.");
            System.exit(0);
        }
        return SCANNER.nextLine();
    }
}
