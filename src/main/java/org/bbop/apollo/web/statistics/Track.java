package org.bbop.apollo.web.statistics;

import org.bbop.apollo.web.datastore.JEDatabase;
import org.gmod.gbol.bioObject.AbstractSingleLocationBioFeature;
import org.gmod.gbol.bioObject.SequenceAlteration;
import org.gmod.gbol.simpleObject.Feature;
import org.gmod.gbol.simpleObject.FeatureProperty;
import org.gmod.gbol.simpleObject.FeatureLocation;
import org.gmod.gbol.simpleObject.FeatureRelationship;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.File;

import org.apache.tools.ant.DirectoryScanner;

public class Track /*implements Comparable<Track>*/ {
    private String name;
    private String filePath;
    private long lastModified;      // modified time of the lastest .jdb file
    private Date lastAnnotated;     // time of last annotation
    private HashMap<String, Integer> types;
    private HashMap<String, Integer> subtypes;

    public Track(String name, String dataStoreDirectory) {
        this.name = name;
        this.filePath = dataStoreDirectory + "/" + name;
        this.lastModified = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(1971, 0, 0, 0, 0, 0); 
        this.lastAnnotated = cal.getTime();
        this.types = new HashMap<String, Integer>();
        this.subtypes = new HashMap<String, Integer>();
        if (this.lastFileModifiedTime() > 0) {
            this.load();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public Date getLastAnnotated() {
        return this.lastAnnotated;
    }

    public HashMap<String, Integer> getTypes() {
        return this.types;
    }

    public HashMap<String, Integer> getSubTypes() {
        return this.subtypes;
    }

    @Override
    public String toString() {
        return "Name: " + this.name + /*"\tFeatures: " + this.features.size() +*/ "\tLast Modified:" + this.lastAnnotated;   
    }
    
    public long lastFileModifiedTime() {
        // if the directory does not exist, quit
        File fp = new File(this.filePath);
        if ( (!fp.exists()) || (!fp.isDirectory()) ) {
            return -1;
        }
        // check if .jdb files exist in filePath, 
        // and if so, compares the latest one with lastModified 
        // to determine if the DB should be opened and read
        DirectoryScanner scr = new DirectoryScanner();
        scr.setBasedir(this.filePath);
        scr.setIncludes(new String[] {"*.jdb"});
        scr.scan();
        String[] jdbs = scr.getIncludedFiles();

        if (jdbs.length <= 0) {
            return -1;
        }

        // jdbs.length > 0
        File lastModifiedFile = new File(this.filePath + "/" + jdbs[0]);
        for (int i=1; i<jdbs.length; ++i) {
            File tmpFile =  new File(this.filePath + "/" + jdbs[i]);
            if (lastModifiedFile.lastModified() < tmpFile.lastModified()) {
                lastModifiedFile = tmpFile;
            }
        }
        return lastModifiedFile.lastModified();
    }

    public boolean isUpdated() {
        long last = this.lastFileModifiedTime();
        //System.out.println("in isUpdated: old=" + this.lastModified + ", new=" + last);
        if (this.lastModified >= last) {
                return false;
        }
        else {
            return true; 
        }
    }

    public void updateLastModified() {
        long last = this.lastFileModifiedTime();
        this.lastModified = last;
        //System.out.println("in updateTime: " + this.name + " Updated");
    }

    public void load() {    // load .jdb DB data into this object
        //System.out.println("in Load");
        try { 
            // clear original data
            Calendar cal = Calendar.getInstance();
            cal.set(1971, 0, 0, 0, 0, 0);
            this.lastAnnotated = cal.getTime();
            this.types.clear();
            this.subtypes.clear();

            // set readOnly = false to fix the error msg in catalina.out:
            //   java.lang.IllegalArgumentException: je.env.isReadOnly is set to true in the config parameter 
            //   which is incompatible with the value of false in the underlying environment
            JEDatabase db = new JEDatabase(this.filePath, false);
            List<Feature> features = new ArrayList<Feature>();
            db.readFeatures(features);
            db.close();

            for (Feature f : features) {
                // updated the last modified date of this scaffold
                if (f.getTimeLastModified().after(this.lastAnnotated)) {
                    this.lastAnnotated = f.getTimeLastModified();
                }

                // get FeatureTypes
                // type of parent feature
                String parent_type = f.getType().toString().replaceAll("sequence:", "");
                if (!this.types.containsKey(parent_type)) {
                    this.types.put(parent_type, 1); 
                }   
                else {
                    this.types.put(parent_type, this.types.get(parent_type) + 1); 
                }   
                // types of child features
                Set<FeatureRelationship> childFeatures = f.getChildFeatureRelationships();
                for (FeatureRelationship relation : childFeatures) {
                    String child_type = parent_type + "_" + relation.getSubjectFeature().getType().toString().replaceAll("sequence:", "");
                    if (!this.subtypes.containsKey(child_type)) {
                        this.subtypes.put(child_type, 1); 
                    }   
                    else {
                        this.subtypes.put(child_type, this.subtypes.get(child_type) + 1); 
                    }   
                }
            }
            // delete reference to features to facilita GC
            features.clear();
            features = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
}
