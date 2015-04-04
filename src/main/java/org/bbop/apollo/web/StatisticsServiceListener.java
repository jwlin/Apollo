package org.bbop.apollo.web;

import org.bbop.apollo.web.statistics.FeatureType;
import org.bbop.apollo.web.statistics.OrganismTrack;
import org.bbop.apollo.web.statistics.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bbop.apollo.web.config.ServerConfiguration;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Context Listener implementation class StatisticServiceListener
 */
@WebListener
public class StatisticsServiceListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            ArrayList<OrganismTrack> organismTracks = new ArrayList<OrganismTrack>();
            ServletContext ctx = event.getServletContext();
            //ServerConfiguration serverConfig = new ServerConfiguration(ctx.getResourceAsStream("/config/config.xml"));
            ServerConfiguration serverConfig = new ServerConfiguration(ctx);
            Collection<ServerConfiguration.TrackConfiguration> tracks = serverConfig.getTracks().values();
            for (ServerConfiguration.TrackConfiguration track : tracks) {
                String organism = track.getOrganism();
                int index = -1;
                for (OrganismTrack oTrack : organismTracks) {
                    if (oTrack.getName().equals(organism)) {
                        index = organismTracks.indexOf(oTrack);
                        break;
                    }
                }
                if (index < 0) {
                    OrganismTrack o = new OrganismTrack(organism);
                    organismTracks.add(o);
                    index = organismTracks.size() - 1;
                }
                Track tr = new Track(track.getName(), serverConfig.getDataStoreDirectory());
                organismTracks.get(index).addTrack(tr);
                //System.out.println("in listener, track:" + track.getName() + " last modified: " + tr.getLastModified());
                tr.updateLastModified();
                //System.out.println("in listener2, track:" + track.getName() + " last modified: " + tr.getLastModified());
            }
            ctx.setAttribute("OrganismTracks", organismTracks);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
