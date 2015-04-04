package org.bbop.apollo.web;

import org.bbop.apollo.web.statistics.FeatureType;
import org.bbop.apollo.web.statistics.OrganismTrack;
import org.bbop.apollo.web.statistics.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bbop.apollo.web.config.ServerConfiguration;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Servlet implementation class StatisticService
 */
@WebServlet("/StatisticsService")
public class StatisticsService extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatisticsService() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

	@Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {  
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        ArrayList<OrganismTrack> organismTracks = (ArrayList<OrganismTrack>) this.getServletConfig().getServletContext().getAttribute("OrganismTracks");
        if (organismTracks == null) {
            out.println("Error");
        }
        
        else {
            for (OrganismTrack oTrack : organismTracks) {
                for (Track track : oTrack.getTracks()) {
                    //System.out.println("in doGet, track:" + track.getName() + " last modified:" + track.getLastModified());
                    if (track.isUpdated()) {
                        oTrack.removeTrackStat(track);
                        track.load();
                        track.updateLastModified();
                        oTrack.addTrackStat(track);
                    }
                }
                try { 
                    JSONObject data = new JSONObject();
                    data.put("species", oTrack.getName());
                    data.put("abbr", oTrack.getAbbr());
                    data.put("annotations", oTrack.getNumFeatures());
                    data.put("last_modified", oTrack.getLastAnnotated());
                    List<FeatureType> ftypes = oTrack.getFeatureTypes();
                    JSONArray oarray = new JSONArray();
                    for (FeatureType f:ftypes) {
                        JSONObject obj = new JSONObject();
                        obj.put("name", f.getName());
                        obj.put("num", f.getNum());
                        List<FeatureType> subtypes = f.getSubTypes();
                        if (subtypes.isEmpty()) {
                            obj.put("subTypes", subtypes);
                        }
                        else {
                            JSONArray suboarray = new JSONArray();
                            for (FeatureType sub:subtypes) {
                                JSONObject subobj  = new JSONObject();
                                subobj.put("name", sub.getName());
                                subobj.put("num", sub.getNum());
                                suboarray.put(subobj);
                            }
                            obj.put("subTypes", suboarray);
                        }
                        oarray.put(obj);
                    }
                    data.put("ftypes", oarray);
                    out.println(data);
                } 
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //this.getServletConfig().getServletContext().setAttribute("OrganismTracks", organismTracks);
        }
        
        out.flush();
    }
}
