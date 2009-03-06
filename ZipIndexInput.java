package org.apache.lucene.store;

import org.apache.lucene.store.IndexInput;
import java.io.InputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipIndexInput extends IndexInput {
  private ZipEntry entry;
  private ZipFile  file;
  private InputStream stream;
  long position;

  public ZipIndexInput(String name, ZipFile f)
    throws IOException
  {
    ZipEntry e = f.getEntry(name);
    if(e == null) {
      throw new IOException("No entry with name " + name);
    }
    init(e, f);
  }

  public ZipIndexInput(ZipEntry e, ZipFile f) 
    throws IOException
  {
    init(e, f);
  }

  public synchronized byte readByte()
    throws IOException
  {
    byte b[] = new byte[1];
    if(stream.read(b) != 1) {
      throw new IOException();
    }
    position++;
    return b[0];
  }

  public synchronized void readBytes(byte[] b, int offset, int len)
    throws IOException
  {
    position += stream.read(b, offset, len);
  }

  public void close()
    throws IOException
  {
    position = -1;
    stream.close();
  }

  public long getFilePointer()
  {
    return position;
  }

  public void seek(long pos)
    throws IOException
  {
    if(pos < position) {
      // we need to start over because our inputstream doesn't have  a seek
      // we should probably use mark and reset...
      resetStream();
      stream.skip(pos);
      position = pos;
    }
    else {
      long togo = pos - position;
      stream.skip(togo);
      position = pos;
    }
  }

  public long length()
  {
    return entry.getSize();
  }

  private void resetStream()
    throws IOException
  {
    stream = file.getInputStream(entry);
    position = 0;
  }

  private void init(ZipEntry e, ZipFile f)
    throws IOException
  {
    entry  = e;
    file   = f;
    resetStream();
  }
}

