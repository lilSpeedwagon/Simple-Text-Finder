package sample;

import java.util.Vector;

//structure which contains results of search in current file

public class SearchResult {
    private Vector<Integer> positions;    //positions of every matching
    private TFile path;                  //file which contains string pattern
    private boolean empty = true;       //if no matching found
    private String text;                //string pattern to match

    public SearchResult(TFile path, String text)  {
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

    public TFile getFile()   {
        return path;
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty()    {
        return empty;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SearchResult)    {
            return ((SearchResult) obj).getFile().getAbsolutePath().equals(path.getAbsolutePath());
        }
        return false;
    }
}
