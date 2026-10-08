package model;

public class Pasajero {

    private final int survived;
    private final int pclass;
    private final String sex;
    private final Double age;
    private final String embarked;

    public Pasajero(int survived, int pclass, String sex,
                    Double age, String embarked) {

        this.survived = survived;
        this.pclass = pclass;
        this.sex = sex;
        this.age = age;
        this.embarked = embarked;
    }

    public int getSurvived() {
        return survived;
    }

    public int getPclass() {
        return pclass;
    }

    public String getSex() {
        return sex;
    }

    public Double getAge() {
        return age;
    }

    public String getEmbarked() {
        return embarked;
    }

    @Override
    public String toString() {
        return "Pasajero[" +
                "survived=" + survived +
                ", pclass=" + pclass +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", embarked='" + embarked + '\'' +
                ']';
    }
}
