/*
 *  Copyright (C) 2003  Jens Kanschik,
 * 	mail : jensKanschik@users.sourceforge.net
 *
 *  Part of <hypergraph>, an open source project at sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package hypergraph.applications.hexplorer;

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphSystem;
import hypergraph.graphApi.GraphSystemFactory;
import hypergraph.graphApi.Node;
import hypergraph.graphApi.algorithms.GraphUtilities;
import hypergraph.graphApi.io.GraphWriter;
import hypergraph.graphApi.io.GraphXMLWriter;
import hypergraph.graphApi.io.SAXReader;

import java.applet.AppletContext;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HExplorerApplet extends JApplet {

    /** Stores the graph that the applet shows. */
    public GraphPanel graphPanel;
    Graph graph;

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    @Override
    public void setSize(int ancho, int alto) {
        super.setSize(ancho, alto);
        this.setBounds(0, 0, ancho, alto);
        validate();

    }

    public void añadePanel() {
        getContentPane().add(graphPanel);
    }

    /**@inheritDoc */
    public void init() {

        
        // Cargar el parámetro file (XML), procesarlo y generar un objeto Graph...
        // ******  Primer parametro: "hg.php..."  ******
        String file = getParameter("file");
        GraphSystem graphSystem = null;
        
        try {
            graphSystem = GraphSystemFactory.createGraphSystem("hypergraph.graph.GraphSystemImpl", null);
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.exit(8);
        }

        graph = null;
        URL url = null;
        try {
            // codeBase es la url del directorio que contiene el applet
            url = new URL(getCodeBase(), file);
            SAXReader reader = new SAXReader(graphSystem, url);
            ContentHandlerFactory ch = new ContentHandlerFactory();
            
            ch.setBaseUrl(getCodeBase());
            reader.setContentHandlerFactory(ch);
            graph = reader.parse();
        } 
        catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null,
                    "Could not find file " + url.getFile() + ". \n"
                    + "Start applet with default graph", "File not found", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception : " + fnfe);
            fnfe.printStackTrace(System.out);
        } 
        catch (SAXException saxe) {
            JOptionPane.showMessageDialog(null,
                    "Error while parsing file" + url.getFile() + ". \n"
                    + "Exception : " + saxe + ". \n"
                    + "Start applet with default graph", "Parsing error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception : " + saxe);
            saxe.getException().printStackTrace();
            saxe.printStackTrace(System.out);
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "General error while reading file " + url + ". \n"
                    + "Exception : " + e + ". \n"
                    + "Start applet with default graph", "General error", JOptionPane.ERROR_MESSAGE);
            System.out.println(url);
            System.out.println("Exception : " + e);
            e.printStackTrace(System.out);
        }

        if (graph == null) {
            graph = GraphUtilities.createTree(graphSystem, 2, 3);
        }
        // Iniciar panel de grafo y cargar propiedades desde hg.prop o su hermano small...
        //panel con parámetros de pintado por defecto
        graphPanel = new GraphPanel(graph, this);

        // ******  Second parameter: hg.prop or small ******
        file = getParameter("properties");
        if (file != null) {
            try {
                url = new URL(getCodeBase(), file);
                graphPanel.loadProperties(url.openStream());
            } 
            catch (FileNotFoundException fnfe) {
                JOptionPane.showMessageDialog(null,
                        "Could not find propertyfile " + url.getFile() + ". \n"
                        + "Start applet with default properties", "File not found", JOptionPane.ERROR_MESSAGE);
            } 
            catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "General error while reading file " + url.getFile() + ". \n"
                        + "Exception : " + e + ". \n"
                        + "Start applet with default properties", "General error", JOptionPane.ERROR_MESSAGE);
                System.out.println(url);
                System.out.println("Exception : " + e);
                e.printStackTrace(System.out);
            }
        }


        // ******  Third parameter (central node) ******
        String center = getParameter("center");
        if (center != null) {
            Node n = (Node) graph.getElement(center); 
            if (n != null) {
                 graphPanel.setRoot(n);
            }
        }

       // ******  Fourth parameter 
        String str_relations = getParameter("relations");
        if (str_relations != null) {
            graphPanel.startFilterRelations(str_relations);
            
        }

        getContentPane().add(graphPanel); // se añade para que sea mostrado
        
        
    }

    public String getGraphXML() {
        
        try {
            OutputStream os = new ByteArrayOutputStream();
            GraphWriter graphWriter = new GraphXMLWriter(new OutputStreamWriter(os));
            graphWriter.write(getGraphPanel().getGraph());
            
            return os.toString();
        } 
        catch (IOException ioe) {
            ioe.printStackTrace();
            
            return ioe.toString();
        }
    }
}

