package com.example.firs_best_design;

public class Group {
    String nameGroup;

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public void setIconGroup(String iconGroup) {
        this.iconGroup = iconGroup;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public String getIconGroup() {
        return iconGroup;
    }

    public Group() {
    }

    public Group(String nameGroup, String iconGroup) {
        this.nameGroup = nameGroup;
        this.iconGroup = iconGroup;
    }

    String iconGroup;
}
