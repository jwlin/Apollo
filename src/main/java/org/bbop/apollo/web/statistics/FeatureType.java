package org.bbop.apollo.web.statistics;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class FeatureType implements Comparable<FeatureType>{
    private String name;
    private int num;
    private List<FeatureType> subtypes;

    public FeatureType(String name, int num) {
        this.name = name; 
        this.num = num;
        this.subtypes = new ArrayList<FeatureType>();
    }

    public void addSubType(String name, int num) {
        FeatureType subtype = new FeatureType(name, num);
        this.subtypes.add(subtype);

        Collections.sort(subtypes);
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<FeatureType> getSubTypes() {
        return subtypes;
    }

    public String getName() {
        return this.name;
    }

    public int getNum() {
        return this.num;
    }

    @Override
    public int compareTo(FeatureType t) {   // Descending order
        return t.num - this.num;
    }
   
    /*
    @Override
    public String toString() {
        String s = this.name + ":" + this.num + "\nsubtypes:\n";
        for (FeatureType ft:this.subtypes) {
            s += "\t" + ft.name + ":" + ft.num;
        }
        return s;
    }*/
}
