package com.internship.summer.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.springframework.stereotype.Service;

@Service
public class LogService {
	private PrintStream outputPrintStream;
    private PrintStream errorPrintStream;

    public LogService() {
    	File logFolder = new File("log");
        if (!logFolder.exists()) {
            logFolder.mkdirs(); // Create the log folder and its parents if they don't exist
        }
        
        // Define the log file names
        File outputLogFile = new File(logFolder,"output.log");
        File errorLogFile = new File(logFolder,"error.log");

        try {
            // Create FileOutputStreams for the log files
            FileOutputStream outputLogStream = new FileOutputStream(outputLogFile, true); // true for append mode
            FileOutputStream errorLogStream = new FileOutputStream(errorLogFile, true); // true for append mode

            // Create custom PrintStream instances for the log files
            outputPrintStream = new PrintStream(outputLogStream);
            errorPrintStream = new PrintStream(errorLogStream);

            // Redirect the standard output and standard error streams to the log files
            System.setOut(outputPrintStream);
            System.setErr(errorPrintStream);
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions related to file handling here
        }
    }

    // Method to close the log streams when the service is destroyed
    public void closeLogStreams() {
        outputPrintStream.close();
        errorPrintStream.close();
    }
}
