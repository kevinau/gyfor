package org.gyfor.object.test.data;

import java.util.Arrays;
import java.util.List;

import org.gyfor.object.CodeSource;
import org.gyfor.object.ICodeSource;
import org.gyfor.object.ValueSourceListener;
import org.gyfor.object.value.Code;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.ICode;

@SuppressWarnings("unused")
public class CodeBased {

  public enum Gender {
    MALE,
    FEMALE,
    UNKNOWN;
  }
  
  public static class WeekdayNames implements ICodeSource {

    private ICode mon = new Code("mon", "Monday");
    private ICode tue = new Code("tue", "Tuesday");
    private ICode wed = new Code("wed", "Wednesday");
    private ICode thu = new Code("thu", "Thursday");
    private ICode fri = new Code("fri", "Friday");
    private ICode sat = new Code("sat", "Saturday");
    private ICode sun = new Code("sun", "Sunday");
    
    @Override
    public List<ICode> values() {
      return Arrays.asList(mon, tue, wed, thu, fri, sat, sun);
    }

    @Override
    public void addValueSourceListener(ValueSourceListener x) {
    }

    @Override
    public void removeValueSourceListener(ValueSourceListener x) {
    }
    
  }
  
  public Boolean boolean1;
  
  public Gender gender;
  
  public EntityLife entityLife;
  
  @CodeSource(WeekdayNames.class)
  public ICode code;
  
  //public IImageCodeValue imageCode;
  
}
