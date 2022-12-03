import java.io.Serializable;

public class Employee implements Serializable {

    private String name;
    private String lastName;
    private int age;
    private int yearBorn;

    public Employee(String name, String lastName, int age, int yearBorn) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.yearBorn = yearBorn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getYearBorn() {
        return yearBorn;
    }

    public void setYearBorn(int yearBorn) {
        this.yearBorn = yearBorn;
    }
}
