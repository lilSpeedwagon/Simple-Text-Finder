package sample;

import com.sun.javafx.geom.Vec2d;

import java.io.File;
import java.util.Vector;

public class SearchResult {
    private Vector<Vec2d> positions;
    private File path;
    private boolean empty = true;
    private String text;

    public SearchResult(File path, String text)  {
        this.path = path;
        this.text = text;
        positions = new Vector<>();
    }

    public void addPosition(int line, int pos)    {
        positions.add(new Vec2d(line, pos));
        empty = false;
    }

    public Vector<Vec2d> getPositions()   {
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
