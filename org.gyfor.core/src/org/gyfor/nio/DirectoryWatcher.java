package org.gyfor.nio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.value.ExistingDirectory;

/** 
 * A wrapper around the Java WatchService that specifically supports
 * watching for file creation.
 * <p>
 * This class is used as follows:
 * <ol>
 * <li>Create an instance of NewFileWatchService.</li>
 * <li>Register those directories you want to watch, using registerDirectory().</li>
 * <li>Start the watcher, using start().</li>
 * <li>If necessary, queue existing files, using queueExistingFiles().</li>
 * </ol>
 * @author Kevin
 *
 */
public class DirectoryWatcher implements AutoCloseable {
  
  private static final int WAIT_TIME = 1000;
  
  private WatchService watchService;
  
  private final Timer timer = new Timer();
  private final List<Path> waitList = new ArrayList<>();
  //private final Path topDir;
  private final Pattern pattern;
  private final IProcessor processor;
  
  private final Map<WatchKey, Path> keys = new HashMap<>();
  
  
  public static interface IProcessor {
    public void process (Path path, WatchEvent.Kind<?> kind);
  }
  
  
  public DirectoryWatcher (Path topDir, Pattern pattern, IProcessor processor) {
    // We obtain the file system of the Path
    FileSystem fs = topDir.getFileSystem ();
    
    // We create the new WatchService using the new try() block
    try {
      watchService = fs.newWatchService();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    
    this.pattern = pattern;
    this.processor = processor;
  }
  
  
  public void start () {
    try {    
      // Start the infinite polling loop
      WatchKey key = null;
      while(true) {
        key = watchService.take();

        Path dir = keys.get(key);
        if (dir == null) {
            System.err.println("WatchKey not recognized!!");
            continue;
        }
        
        // Dequeueing events
        Kind<?> kind = null;
        for(WatchEvent<?> watchEvent : key.pollEvents()) {
          // Get the type of the event
          kind = watchEvent.kind();
          if (kind == OVERFLOW) {
            continue; //loop
          } else if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
            // A new file was created or modified.  We don't look for deleted files.
            // Wait for the file to become stable before invoking the processor.
            Path eventFile = (Path)watchEvent.context();
            queueFile (dir.resolve(eventFile), kind);
          }
        }
        
        if(!key.reset()) {
          break; //loop
        }
      }
    } catch (InterruptedException ex) {
      // Ignore this exception
    } catch (ClosedWatchServiceException ex) {
      // Ignore this exception
    }
  }


  public void queueExistingFiles () {
    for (Path registeredDir : keys.values()) {
      // The following is required to overcome a bug (feature?) of the Java Path class.
      Path dir2;
      if (registeredDir instanceof ExistingDirectory) {
        dir2 = Paths.get(registeredDir.toString());
      } else {
        dir2 = registeredDir;
      }
      // ENd bug fix.

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir2)) {
        for (Path fileEntry : stream) {
          queueFile (fileEntry, ENTRY_CREATE);
        }
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }
  
  
  private void queueFile (Path path, Kind<?> kind) {
    String fileName = path.getFileName().toString();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.find()) {
      synchronized (waitList) {
        if (!waitList.contains(path)) {
          waitList.add(path);
  
          // Wait for the file to be stable
          long size;
          try {
            size = Files.size(path);
          } catch (IOException e) {
            size = -1;
          }
          timer.schedule(new WaitStableTask(processor, path, kind, size), WAIT_TIME); 
        }
      }
    }
  }

  
  @Override
  public void close () {
    try {
      watchService.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public void registerDirectory (Path path) {
    registerDirectory (path, false);
  }
  
  
  public void registerDirectory (Path path, boolean recursive) {
    try {
      WatchKey key = path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
      keys.put(key, path);
      
      if (recursive) {
        String[] childNames = path.toFile().list();
        for (String childName : childNames) {
          Path childPath = path.resolve(childName);
          if (Files.isDirectory(childPath) && Files.isReadable(childPath)) {
            registerDirectory (childPath, recursive);
          }
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  private class WaitStableTask extends TimerTask {

    private final IProcessor processor;
    private final Path path;
    private final Kind<?> kind;
    private final long lastLength;
    
    private WaitStableTask (IProcessor processor, Path path, Kind<?> kind, long lastLength) {
      this.processor = processor;
      this.path = path;
      this.kind = kind;
      this.lastLength = lastLength;
    }
    
    @Override
    public void run() {
      long currLength;
      if (Files.exists(path)) {
        try {
          currLength = Files.size(path);
        } catch (IOException ex) {
          currLength = -1;
        }
      } else {
        currLength = -1;
      }
      if (currLength == lastLength) {
        synchronized (waitList) {
          waitList.remove(path);
        }
        if (currLength == -1) {
          // Do nothing.  The file has gone.
        } else {
          processor.process(path, kind);
        }
      } else {
        timer.schedule(new WaitStableTask(processor, path, kind, currLength), WAIT_TIME);
      }
    }
  }
}
