package org.gyfor.object.test;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.impl.EntityModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.impl.PlanContext;
import org.junit.Assert;
import org.junit.Test;


public class PersonTest {

  public static class Person {

    private String name;
    
    private String knownAs;
    
    private String phoneNumber;
    
    private String phoneNumber2;
    
    private String emailAddress;
    
    public Person() {
      this.name = "";
      this.knownAs = "";
      this.phoneNumber = "";
      this.phoneNumber2 = "";
      this.emailAddress = "";
    }
    
    public Person(String name, String knownAs, String phoneNumber, String phoneNumber2, String emailAddress) {
      this.name = name;
      this.knownAs = knownAs;
      this.phoneNumber = phoneNumber;
      this.phoneNumber2 = phoneNumber2;
      this.emailAddress = emailAddress;
    }

    
    public String getName() {
      return name;
    }

    public String getKnownAs() {
      return knownAs;
    }

    public String getPhoneNumber() {
      return phoneNumber;
    }
    
    public String getPhoneNumber2() {
      return phoneNumber2;
    }
    
    public String getEmailAddress() {
      return emailAddress;
    }
    
  }
  
  @Test
  public void testNameMappedMembers () {
    PlanContext planContext = new PlanContext();
    IEntityPlan<Person> personPlan = planContext.getEntityPlan(Person.class);
    
    IEntityModel personModel = new EntityModel(personPlan);
    Assert.assertEquals(0, personModel.getMembers().size());
    
    Person person = new Person();
    personModel.setValue(person);
    Assert.assertEquals(5, personModel.getMembers().size());
  }

}
