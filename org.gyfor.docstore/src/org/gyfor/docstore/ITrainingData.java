package org.gyfor.docstore;

import org.gyfor.srcdoc.SegmentType;

//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.Reader;
//import java.lang.reflect.Type;
//import java.nio.file.Path;
//import java.time.LocalDate;

//import org.gyfor.math.Decimal;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonDeserializer;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParseException;
//import com.google.gson.JsonPrimitive;
//import com.google.gson.JsonSerializationContext;
//import com.google.gson.JsonSerializer;


public interface ITrainingData {

  public String[] getTargetValues(SegmentType type);
  
  public String resolveValue (SegmentType type, Object value);
  
//  static class DecimalTypeAdapter implements JsonSerializer<Decimal>, JsonDeserializer<Decimal> {
//    @Override
//    public JsonElement serialize(Decimal src, Type arg1, JsonSerializationContext arg2) {
//      return new JsonPrimitive(src);
//    }
//
//    @Override
//    public Decimal deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
//      return new Decimal(json.getAsString());
//    }
//  }
//  
//  
//  static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
//    @Override
//    public JsonElement serialize(LocalDate src, Type arg1, JsonSerializationContext arg2) {
//      return new JsonPrimitive(src.toString());
//    }
//
//    @Override
//    public LocalDate deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
//      return LocalDate.parse(json.getAsString());
//    }
//  }
  
  
//  public default void save(Path file) {
//    GsonBuilder gsonBuilder = new GsonBuilder();
//    gsonBuilder.setPrettyPrinting();
//    gsonBuilder.registerTypeAdapter(Decimal.class, new DecimalTypeAdapter());
//    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
//    Gson gson = gsonBuilder.create();
//    String json = gson.toJson(this);  
//    
//    try (OutputStream oos = new FileOutputStream(file.toFile())) {
//      oos.write(json.getBytes());
//    } catch (IOException ex) {
//      throw new RuntimeException();
//    }
//  }
//
//  @SuppressWarnings("unchecked")
//  public static <X extends ITrainingData> X load(Path file, Class<? extends ITrainingData> klass) {
//    GsonBuilder gsonBuilder = new GsonBuilder();
//    gsonBuilder.registerTypeAdapter(Decimal.class, new DecimalTypeAdapter());
//    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
//    Gson gson = gsonBuilder.create();
//
//    X data;
//    try (Reader reader = new FileReader(file.toFile())) {
//      data = (X)gson.fromJson(reader, klass);
//    } catch (IOException ex) {
//      throw new RuntimeException(ex);
//    }
//    return data;
//  }

}
