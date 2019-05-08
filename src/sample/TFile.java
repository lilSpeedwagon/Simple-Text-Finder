package sample;

import java.io.File;

//main purpose of this class - using file name instead of absolute path
//when it displayed in TreeView
public class TFile extends File {
    public TFile(String path)   {
        super(path);
    }
    public TFile(File file) {
        super(file.toString());
    }
    @Override
    public String toString()    {
        return getName();
    }
    @Override
    public TFile getParentFile()    {
        return new TFile(super.getParentFile());
    }
    @Override
    public boolean equals(Object obj)   {
        if (obj instanceof File)    {
            return ((File) obj).getAbsolutePath().equals(this.getAbsolutePath());
        }
        return false;
    }
}