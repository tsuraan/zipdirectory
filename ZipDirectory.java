package org.apache.lucene.store;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import java.io.InputStream;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.util.LinkedList;

public class ZipDirectory extends Directory
{
  private ZipFile file;

  public ZipDirectory(String path)
    throws IOException
  {
    try {
      file = new ZipFile(path);
    } catch(ZipException e) {
      throw new IOException("Invalid zip file");
    }
  }

  public String[] list()
    throws IOException
  {
    Enumeration entries = file.entries();
    LinkedList list = new LinkedList();
    while(entries.hasMoreElements()) {
      ZipEntry e = (ZipEntry)entries.nextElement();
      list.add(e.getName());
    }

    String[] strings = new String[list.size()];
    list.toArray(strings);
    return strings;
  }

  public boolean fileExists(String name)
    throws IOException
  {
    return file.getEntry(name) != null;
  }

  public long fileModified(String name)
    throws IOException
  {
    return file.getEntry(name).getTime();
  }

  public void touchFile(String name)
    throws IOException
  {
    fail("Touch");
  }

  public void deleteFile(String name)
    throws IOException
  {
    fail("Delete");
  }

  /** @deprecated
   */
  public void renameFile(String from, String to)
    throws IOException
  {
    fail("Rename");
  }

  public long fileLength(String name)
    throws IOException
  {
    return file.getEntry(name).getSize();
  }

  public IndexOutput createOutput(String name)
    throws IOException
  {
    fail("CreateOutput");
    return null;
  }

  public IndexInput openInput(String name)
    throws IOException
  {
    return new ZipIndexInput(name, file);
  }

  public void close()
    throws IOException
  {
    file.close();
  }
  
  private void fail(String operation)
    throws IOException
  {
    throw new IOException(operation + " failed: Zip file is read-only");
  }
}
