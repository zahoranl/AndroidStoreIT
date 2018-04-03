package hu.bme.aut.packit.data;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Item  extends SugarRecord implements Serializable {

   private String itemName;
   private String categoryName;
   private int darab;
   private String megjegyzes;
   private String elhelyezes;
   private String code;

    //////////////////////////////////////////////////////////////////////////////////////////////
    //KONSTRUKTOR
    //////////////////////////////////////////////////////////////////////////////////////////////
    public Item(String itemName, String categoryName, int darab, String megjegyzes, String elhelyezes, String code1) {
        this.itemName = itemName;
        this.categoryName = categoryName;
        this.darab = darab;
        this.megjegyzes = megjegyzes;
        this.elhelyezes = elhelyezes;
        this.code = code1;
    }
    public Item() {
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //GETTER/SETTER
    //////////////////////////////////////////////////////////////////////////////////////////////
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public int getDarab() {
        return darab;
    }
    public void setDarab(int darab) {
        this.darab = darab;
    }
    public String getMegjegyzes() {
        return megjegyzes;
    }
    public void setMegjegyzes(String megjegyzes) {
        this.megjegyzes = megjegyzes;
    }
    public String getElhelyezes() {
        return elhelyezes;
    }
    public void setElhelyezes(String elhelyezes) {
        this.elhelyezes = elhelyezes;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

}
