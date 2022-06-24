package yalantis.com.sidemenu.model;

import yalantis.com.sidemenu.interfaces.Resourceble;

public class SlideMenuItem implements Resourceble {
    private int imageRes;
    private String name;

    public SlideMenuItem(String str, int i) {
        this.name = str;
        this.imageRes = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public int getImageRes() {
        return this.imageRes;
    }

    public void setImageRes(int i) {
        this.imageRes = i;
    }
}
