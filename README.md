# PojoGenerator

This is annotation processor. Maybe it is redundant and unneccesary, but my goal was a deeper understanding and learning, not a creating of "something useful".

## How it works?

Source file:
````java
@Pojo
public class Person {
    private String name;
    private int age;

}
````

Generated file:

````java
public class PersonPojo {
  private String name;

  private int age;

  public PersonPojo(String name, int age) {
    this.name=name;
    this.age=age;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  public void setName(String name) {
    this.name=name;
  }

  public void setAge(int age) {
    this.age=age;
  }
}
````

