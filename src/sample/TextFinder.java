package sample;

import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextFinder {

    private static Logger logger = Logger.getLogger(TextFinder.class.getName());

    private SearchResult searchInFile(File file, String text)   {
        logger.info("Reading file " + file.getName() + "...");

        SearchResult result = new SearchResult(file, text);
        try {
            //opening file and init scanner
            FileReader reader = new FileReader(file);
            Scanner scanner = new Scanner(reader);

            //search pattern in every line and save [ line, position ] of every matching
            int posCounter = 0;
            while (scanner.hasNext())   {
                String line = scanner.nextLine();

                int position = line.indexOf(text);
                while (position != -1)    {
                    result.addPosition(posCounter + position);
                    position = line.indexOf(text, position + 1);
                }
                posCounter += line.length() + 1;
            }

            //closing file and scanner
            scanner.close();
            reader.close();

            logger.info("Done. " + result.size() + " matching found.");
        }   catch(IOException e)    {
            logger.log(Level.WARNING, "Error. Can't open this file.");
        }
        return result;
    }

    private LinkedList<SearchResult> searchInDir(File dir, String text, String extension)   {
        logger.info("Searching in directory " + dir.toString() + "...");

        LinkedList<SearchResult> results = new LinkedList<>();

        for (File file : dir.listFiles()){
            if (file.isFile())  {
                if (file.getName().endsWith(extension)) {
                    SearchResult result = searchInFile(file, text);
                    if (!result.isEmpty()) {
                        results.add(result);
                    }
                }
            }
            if (file.isDirectory()) {
                //recursive search in directory and concatinating results
                results.addAll(searchInDir(file, text, extension));
            }
        }

        return results;
    }

    public LinkedList<SearchResult> searchText(File path, String text, String extension)    {
        LinkedList<SearchResult> results;

        if (path.isFile())  {
            results = new LinkedList<>();
            results.add(searchInFile(path, text));
        } else {
            //if path is directory use recursive search in all included directories and files
            int totalFiles = 0;
            int totalMatches = 0;

            results = searchInDir(path, text, extension);

            //counting of matching
            for (SearchResult result : results) {
                if (!result.isEmpty())  {
                    totalFiles++;
                    totalMatches += result.size();
                }
            }

            logger.info("Searching is finished. " + totalFiles + " files and " + totalMatches + " matching founded.");
        }

        return results;

    }
}
