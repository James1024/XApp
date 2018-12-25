package colin.com.module.opengl.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author wanglr
 * @date 2018/12/13
 */
public class PersonBean implements Parcelable {
    private int age;
    private String name;
    private String sex;
    private String grade;

    public PersonBean(int age, String name, String sex, String grade) {
        this.age = age;
        this.name = name;
        this.sex = sex;
        this.grade = grade;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.age);
        dest.writeString(this.name);
        dest.writeString(this.sex);
        dest.writeString(this.grade);
    }

    protected PersonBean(Parcel in) {
        this.age = in.readInt();
        this.name = in.readString();
        this.sex = in.readString();
        this.grade = in.readString();
    }

    public static final Parcelable.Creator<PersonBean> CREATOR = new Parcelable.Creator<PersonBean>() {
        @Override
        public PersonBean createFromParcel(Parcel source) {
            return new PersonBean(source);
        }

        @Override
        public PersonBean[] newArray(int size) {
            return new PersonBean[size];
        }
    };
}
