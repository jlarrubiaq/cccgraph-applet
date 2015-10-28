/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hypergraph.navigationSupport;

import hypergraph.applications.hexplorer.GraphPanel;
import hypergraph.applications.hexplorer.HExplorerApplet;
import java.net.URL;
import java.net.MalformedURLException;

/**
 *
 * @author Jesus
 */
public class NavigationHandler {
    
    private ExtendPanel pextends;    
    private ContextMenu contextm;
    private HExplorerApplet applet;
    private GraphPanel gp;
    
    public NavigationHandler(HExplorerApplet applet, GraphPanel gp){
        
        this.applet = applet;
        this.gp = gp;
        
        pextends = new ExtendPanel(applet, gp);     
        contextm = new ContextMenu();
        contextm.setRelationFilterPanel(new RelationsFilterPanel(gp, this)); 
        
    }
    
    public ContextMenu getContextMenu(){
    
           return contextm;
    }
    
    public ExtendPanel getExtendPanel(){
        
        return pextends;
    }
    
    protected void sendServer(String href){
         
        String base = "http://" + applet.getDocumentBase().getHost();
        URL url = null;
        try {
            url = new URL(new URL(base), href);
        } 
        catch (MalformedURLException ex) {
            System.out.println(ex.toString());
        }
        System.out.println(url.toString());
        applet.getAppletContext().showDocument(url,"_parent");
     }
    
    
}
