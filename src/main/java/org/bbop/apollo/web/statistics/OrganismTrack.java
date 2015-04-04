package org.bbop.apollo.web.statistics;

import org.gmod.gbol.simpleObject.Feature;
import org.gmod.gbol.simpleObject.FeatureRelationship;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.Collections;

public class OrganismTrack implements Comparable<OrganismTrack>  {
    private String name;
    private String abbr;
    private List<Track> tracks;
    private Date end_date;
    private HashMap<String, Integer> types;
    private HashMap<String, Integer> subtypes;

    public OrganismTrack(String name) {
        this(name, "", "");
    }

    public OrganismTrack(String name, String abbr) {
        this(name, abbr, "");
    }

    public OrganismTrack(String name, String abbr, String date) {
        this.name = name;
        this.abbr = abbr;
        
        if (date.equals("")) {
            this.end_date = null;
        }   
        else {
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.substring(4,6))-1, Integer.parseInt(date.substring(6,8)),23,59,59);
            this.end_date = cal.getTime();
        }
        this.tracks = new ArrayList<Track>();
        this.types = new HashMap<String, Integer>();
        this.subtypes = new HashMap<String, Integer>();
    }

    public String getName() {
        return this.name;
    }

    public String getAbbr() {
        return this.abbr;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public int getNumFeatures() {
        List<FeatureType> ftypes = this.getFeatureTypes();
        int total = 0;
        for (FeatureType t : ftypes) {
            if (!t.getSubTypes().isEmpty()) {
                for (FeatureType s : t.getSubTypes()) {
                    total += s.getNum();
                }
            }
            else {
                total += t.getNum();
            }
        }
        return total;
    }

    public void addTrack(Track track) {
        this.tracks.add(track);
        this.addTrackStat(track);
    }

    public void addTrackStat(Track track) {
        // merge track.getTypes() and track.getSubTypes() into this.types and this.subtypes
        HashMap<String, Integer> t_types = track.getTypes();
        for (Map.Entry<String, Integer> entry : t_types.entrySet()) {
            if (!this.types.containsKey(entry.getKey())) {
                this.types.put(entry.getKey(), entry.getValue()); 
            }
            else {
                this.types.put(entry.getKey(), this.types.get(entry.getKey()) + entry.getValue());
            }
        }
        HashMap<String, Integer> t_subtypes = track.getSubTypes();
        for (Map.Entry<String, Integer> entry : t_subtypes.entrySet()) {
            if (!this.subtypes.containsKey(entry.getKey())) {
                this.subtypes.put(entry.getKey(), entry.getValue()); 
            }
            else {
                this.subtypes.put(entry.getKey(), this.subtypes.get(entry.getKey()) + entry.getValue());
            }
        }        
    }

    public void removeTrackStat(Track track) {
        // substract the number of track.getTypes() and track.getSubTypes() from this.types and this.subtypes
        HashMap<String, Integer> t_types = track.getTypes();
        for (Map.Entry<String, Integer> entry : t_types.entrySet()) {
            this.types.put(entry.getKey(), this.types.get(entry.getKey()) - entry.getValue());
            if (this.types.get(entry.getKey()) == 0) {
                this.types.remove(entry.getKey());
            }
        }
        HashMap<String, Integer> t_subtypes = track.getSubTypes();
        for (Map.Entry<String, Integer> entry : t_subtypes.entrySet()) {
            this.subtypes.put(entry.getKey(), this.subtypes.get(entry.getKey()) - entry.getValue());
            if (this.subtypes.get(entry.getKey()) == 0) {
                this.subtypes.remove(entry.getKey());
            }
        }
    }

    public Date getLastAnnotated() {
        Calendar cal = Calendar.getInstance();
        cal.set(1971, 0, 0, 0, 0, 0);
        Date ldate = cal.getTime(); // initialize with an very old date
        for (Track t : this.tracks) {
            if (t.getLastAnnotated().after(ldate)) {
                ldate = t.getLastAnnotated();
            }
        }
        return ldate;
    }

    public Date getEndDate() {
        return this.end_date;
    }

    public List<FeatureType> getFeatureTypes() {
        // map HashMap to ArrayList for sorting and output
        List<FeatureType> ftypes = new ArrayList<FeatureType>();

        for (Map.Entry<String, Integer> entry : this.types.entrySet()) {
            ftypes.add(new FeatureType(entry.getKey(), entry.getValue()));    
        }   
        for (Map.Entry<String, Integer> entry : this.subtypes.entrySet()) {
            String parent_type = entry.getKey().split("_")[0];
            for (FeatureType t : ftypes) {
                if (t.getName().equals(parent_type)) {
                    t.addSubType(entry.getKey(), entry.getValue());
                    break;
                }
            }
        }
        Collections.sort(ftypes);
        return ftypes;
    }

    @Override
    public int compareTo(OrganismTrack s) {   // Descending order
        Calendar thisCalendar = Calendar.getInstance();
        Calendar sCalendar = Calendar.getInstance();
        
        thisCalendar.setTime(this.getLastAnnotated());
        thisCalendar.set(Calendar.HOUR_OF_DAY, 0);
        thisCalendar.set(Calendar.MINUTE, 0);
        thisCalendar.set(Calendar.SECOND, 0);
        thisCalendar.set(Calendar.MILLISECOND, 0);
        
        sCalendar.setTime(s.getLastAnnotated());
        sCalendar.set(Calendar.HOUR_OF_DAY, 0);
        sCalendar.set(Calendar.MINUTE, 0);
        sCalendar.set(Calendar.SECOND, 0);
        sCalendar.set(Calendar.MILLISECOND, 0);
        
        if (thisCalendar.equals(sCalendar)) {
            return (s.getNumFeatures() - this.getNumFeatures());
        }
        else if (thisCalendar.before(sCalendar)) {
            return 1;
        }
        else {
            return -1;
        }
    }
}

