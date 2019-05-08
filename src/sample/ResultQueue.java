package sample;

import java.util.LinkedList;

//queue with results for multithread using
public class ResultQueue extends LinkedList<SearchResult> {
    private boolean searching = false;

    public boolean isSearchFinished()   {
        return !searching;
    }

    public void finish()    {
        searching = false;
    }

    public void start() {
        searching = true;
    }
}
