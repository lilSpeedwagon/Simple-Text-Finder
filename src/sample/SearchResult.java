package sample;

import com.sun.javafx.geom.Vec2d;

import java.io.File;
import java.util.Vector;

/*
    structure which contains results of search in current file
 */

public class SearchResult {
    private Vector<Integer> positions;    //positions of every matching
    private File path;                  //file which contains string pattern
    private boolean empty = true;       //if no matching found
    private String text;                //string pattern to match

    public SearchResult(File path, String text)  {
        this.path = path;
        this.text = text;
        positions = new Vector<>();
    }

    public void addPosition(int pos)    {
        positions.add(pos);
        empty = false;
    }

    public Vector<Integer> getPositions()   {
        return positions;
    }

    public int size()   {
        return positions.size();
    }

    public File getFile()   {
        return path;
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty()    {
        return empty;
    }
}
