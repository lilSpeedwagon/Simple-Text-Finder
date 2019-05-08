package sample;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextFinder {

    private static Logger logger = Logger.getLogger(TextFinder.class.getName());

    private ResultQueue results;
    private int totalFiles;
    private int totalMatches;

    public TextFinder(ResultQueue results)  {
        this.results = results;
    }

    private SearchResult searchInFile(File file, String text)   {
        logger.info("Reading file " + file.getName() + "...");
        TFile tfile = new TFile(file);
        SearchResult result = new SearchResult(tfile, text);
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
                    totalMatches++;
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

    //recursive search
    private void searchInDir(File dir, String text, String extension)   {
        logger.info("Searching in directory " + dir.toString() + "...");

        //open all files in current dir
        for (File file : dir.listFiles()){
            if (file.isFile())  {
                if (file.getName().endsWith(extension)) {
                    SearchResult result = searchInFile(file, text);
                    if (!result.isEmpty()) {
                        results.push(result);
                        totalFiles++;
                    }
                }
            }
            if (file.isDirectory()) {
                searchInDir(file, text, extension);
            }
        }
    }

    public void searchText(File path, String text, String extension)    {
        if (text.isEmpty())
            return;

        results.start();
        totalFiles = 0;
        totalMatches = 0;

        if (path.isFile())  {
            results.push(searchInFile(path, text));
        } else {
            //if path is directory use recursive search in all included directories and files
            searchInDir(path, text, extension);

            logger.info("Searching is finished. " + totalFiles + " files and " + totalMatches + " matching founded.");
        }

        results.finish();
    }
}
