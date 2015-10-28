/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hypergraph.navigationSupport;

import hypergraph.applications.hexplorer.GraphPanel;
import hypergraph.applications.hexplorer.HExplorerApplet;
import javax.swing.JFrame;

/**
 *
 * @author jpunto
 */
public class ExtendFrame extends JFrame {

    private GraphPanel panel;
    private HExplorerApplet applet;
    private ExtendPanel pa;

    public ExtendFrame(HExplorerApplet applet, ExtendPanel pa) {
        
        setBounds(250, 100, 800, 800);
        this.pa = pa;
        this.panel = applet.getGraphPanel();
        this.applet = applet;
        getContentPane().add(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }


    @Override
    public void dispose(){
        pa.shrink();
        super.dispose();
    }

}
