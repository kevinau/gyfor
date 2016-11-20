package org.gyfor.value;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class ExistingDirectory implements Path {

  private final Path path;
  
  public ExistingDirectory (Path path) {
    this.path = path;
    validate();
  }
  
  public ExistingDirectory (Path parent, String other) {
    this.path = parent.resolve(other);
    validate();
  }
  
  
  public ExistingDirectory (String first, String... next) {
    this.path = Paths.get(first, next);
    validate();
  }
  
  
  private void validate () {
    if (!Files.exists(path)) {
      throw new IllegalArgumentException("Path (" + path + ") does not exist");
    }
    if (!Files.isDirectory(path)) {
      throw new IllegalArgumentException("Path (" + path + ") does not name a directory");
    }
  }

  @Override
  public String toString() {
    return path.toString();
  }
  
  @Override
  public int compareTo(Path arg0) {
    return path.compareTo(arg0);
  }

  @Override
  public boolean endsWith(Path arg0) {
    return path.endsWith(arg0);
  }

  @Override
  public boolean endsWith(String arg0) {
    return path.endsWith(arg0);
  }

  @Override
  public Path getFileName() {
    return path.getFileName();
  }

  @Override
  public FileSystem getFileSystem() {
    return path.getFileSystem();
  }

  @Override
  public Path getName(int arg0) {
    return path.getName(arg0);
  }

  @Override
  public int getNameCount() {
    return path.getNameCount();
  }

  @Override
  public Path getParent() {
    return path.getParent();
  }

  @Override
  public Path getRoot() {
    return path.getRoot();
  }

  @Override
  public boolean isAbsolute() {
    return path.isAbsolute();
  }

  @Override
  public Iterator<Path> iterator() {
    return path.iterator();
  }

  @Override
  public Path normalize() {
    return path.normalize();
  }

  @Override
  public WatchKey register(WatchService arg0, Kind<?>... arg1) throws IOException {
    return path.register(arg0, arg1);
  }

  @Override
  public WatchKey register(WatchService arg0, Kind<?>[] arg1, Modifier... arg2) throws IOException {
    return path.register(arg0, arg1, arg2);
  }

  @Override
  public Path relativize(Path arg0) {
    return path.relativize(arg0);
  }

  @Override
  public Path resolve(Path arg0) {
    return path.resolve(arg0);
  }

  @Override
  public Path resolve(String arg0) {
    return path.resolve(arg0);
  }

  @Override
  public Path resolveSibling(Path arg0) {
    return path.resolveSibling(arg0);
  }

  @Override
  public Path resolveSibling(String arg0) {
    return path.resolveSibling(arg0);
  }

  @Override
  public boolean startsWith(Path arg0) {
    return path.startsWith(arg0);
  }

  @Override
  public boolean startsWith(String arg0) {
    return path.startsWith(arg0);
  }

  @Override
  public Path subpath(int arg0, int arg1) {
    return path.subpath(arg0, arg1);
  }

  @Override
  public Path toAbsolutePath() {
    return path.toAbsolutePath();
  }

  @Override
  public File toFile() {
    return path.toFile();
  }

  @Override
  public Path toRealPath(LinkOption... arg0) throws IOException {
    return path.toRealPath(arg0);
  }

  @Override
  public URI toUri() {
    return path.toUri();
  }
  
}
