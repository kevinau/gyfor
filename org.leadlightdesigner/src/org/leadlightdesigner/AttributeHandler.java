package org.leadlightdesigner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class AttributeHandler {

  public static void saveAttributes (Path path, String name, Object attrib) {
    Gson gson = new GsonBuilder().create();

    UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
    
    Charset charset = Charset.defaultCharset();
    try {
      String value = gson.toJson(attrib);
      view.write(name, charset.encode(value));
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }


  @SuppressWarnings("unchecked")
  public static <T> T getAttributes (Path path, String name, Class<T> klass) {
    UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
    
    try {
      List<String> names = view.list();
      System.out.println("Attributes of " + path + ": " + name);
      for (String n : names) {
        System.out.println("- " + n);
      }
    
      ByteBuffer buff = ByteBuffer.allocate(view.size(name));
      view.read(name, buff);
      buff.flip();
      String value = Charset.defaultCharset().decode(buff).toString();
      
      Gson gson = new Gson();
      Object attributes = gson.fromJson(value, klass);
      return (T)attributes;
    } catch (JsonSyntaxException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }


}
