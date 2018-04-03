package hu.bme.aut.packit.data;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Category  extends SugarRecord implements Serializable {
    String categoryName;
    String megjegyzes;
    String code;

    //////////////////////////////////////////////////////////////////////////////////////////////
    //KOSNTRUCTOR
    //////////////////////////////////////////////////////////////////////////////////////////////
    public Category() {
    }
    public Category(String categoryName, String megjegyzes, String code) {
        this.categoryName = categoryName;
        this.megjegyzes = megjegyzes;
        this.code = code;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //GETTER/SETTER
    //////////////////////////////////////////////////////////////////////////////////////////////
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getMegjegyzes() {
        return megjegyzes;
    }
    public void setMegjegyzes(String megjegyzes) {
        this.megjegyzes = megjegyzes;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
