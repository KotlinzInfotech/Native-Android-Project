package com.omsvision.model;

public class Account_get_set {
    int supplier_id;
   String emailid;
Boolean Isselect;

    public Boolean getIsselect() {
        return Isselect;
    }

    public void setIsselect(Boolean isselect) {
        Isselect = isselect;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }
}
