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
package hypergraph.visualnet;

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphException;
import hypergraph.graphApi.Node;
import hypergraph.graphApi.io.CSSColourParser;
import hypergraph.hyperbolic.ModelPanel;
import hypergraph.hyperbolic.ModelPoint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * @author Jens Kanschik
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GraphPanel extends ModelPanel implements MouseListener, 
        GraphLayoutListener, GraphSelectionListener {

    /**
     * The attribute name for foreground colour of nodes.
     */
    public static final String NODE_FOREGROUND = "node.color";
    /**
     * The attribute name for background colour of nodes.
     */
    public static final String NODE_BACKGROUND = "node.bkcolor";
    /**
     * The attribute name for an icon of a node.
     */
    public static final String NODE_ICON = "node.icon";
    /**
     * The attribute name for foreground colour of edges.
     */
    public static final String EDGE_TEXTCOLOR = "edge.textcolor";
    public static final String EDGE_LINECOLOR = "edge.linecolor";
    public static final String EDGE_LINEWIDTH = "edge.linewidth";
    public static final String EDGE_STROKE = "edge.stroke";
    public static final String NODE_EXPANDED = "NODE_EXPANDED";
    public final ExpandAction expandAction = new ExpandAction(true);
    public final ExpandAction shrinkAction = new ExpandAction(false);
    protected Graph graph;
    GraphLayout graphLayout;
    GraphSelectionModel selectionModel;
    private Element hoverElement;
    protected Node lastMouseClickNode;
    private NodeRenderer nodeRenderer;
    private EdgeRenderer edgeRenderer;
    protected int initialWidth = 300;//ojo, cargar de hg.prop
    protected int initialHeight = 300;
    protected Hashtable<String, ArrayList> papelera = new Hashtable<String, ArrayList>();
    protected Node actualNode = null;
    protected Node root = null;

    public GraphPanel(Graph graph) {
        
        super(); // llama al constructor de ModelPanel
        setGraph(graph); // evidentemente...
        createGraphLayout();
        createGraphSelectionModel();
        selectionModel.addSelectionEventListener(this);
        setNodeRenderer(new DefaultNodeRenderer());
        setEdgeRenderer(new DefaultEdgeRenderer());
        //initDefaultAttributes(); llamado por loadProperties()
        /*Node n=null;
        try {
        n = graph.createNode("raiz");
        } catch (GraphException ex) {
        Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Object e[] = graph.getEdges().toArray();
        Edge edge = (Edge) e[0];
        graph.createEdge(n ,edge.getSource());*/

    }

    public int getInitialWidth() {
        return this.initialWidth;
    }

    public int getInitialHeight() {
        return this.initialHeight;
    }

    public void centerActualNode() {
        if (actualNode != null && graph.getNodes().contains(actualNode)) {
            centerNode(actualNode);
        }
    }
    
     public void centerRootNode() {
         if  (root != null && graph.getNodes().contains(root)) {
            centerNode(root);
        }
     
     }
    
    public void setRoot(Node n){
        
        root = n;
        ((TreeLayout) getGraphLayout()).setRoot(n);   
    }
    
    public Node getRoot(){
        
        return root;
    }

    /**
     * Establece los atributos por defecto para las edge: color, stroke, ancho
     * y para los nodos
     */
    protected void initDefaultAttributes() {
        
        AttributeManager amgr = graph.getAttributeManager();

        String colorString = getPropertyManager().getString("hypergraph.hyperbolic.line.color");
        Color color = CSSColourParser.stringToColor(colorString);
        
        if (color == null || colorString == null) {
            color = Color.LIGHT_GRAY;
        }
        colorString = getPropertyManager().getString("hypergraph.hyperbolic.textline.color");
        Color color2 = CSSColourParser.stringToColor(colorString);
        colorString = getPropertyManager().getString("hypergraph.hyperbolic.text.background");
        Color color3 = CSSColourParser.stringToColor(colorString);

        amgr.setAttribute(EDGE_LINECOLOR, graph, color);
        amgr.setAttribute(EDGE_TEXTCOLOR, graph, color2);
        amgr.setAttribute(NODE_BACKGROUND, graph, color3);
        amgr.setAttribute(EDGE_STROKE, graph, null);
        amgr.setAttribute(EDGE_LINEWIDTH, graph, new Float(1));
        
        Collection c = graph.getNodes();
        Object o[] = c.toArray();
        for (int i = 0; i < o.length; i++) {
            Node n = (Node) o[i];
            if (n.getGroup() != null && n.getGroup().getName().equals("concepto")) {
                this.shrinkNode(n); // se contrae el nodo concepto
            }
        }
    }

    public void createGraphLayout() {
        
        GraphLayout layout;
        
        try {
            Class layoutClass = getPropertyManager().getClass("hypergraph.visualnet.layout.class");
            layout = (GraphLayout) layoutClass.newInstance();
        } 
        catch (Exception e) {
            layout = new TreeLayout(getGraph(), getModel(), getPropertyManager());
        }
        setGraphLayout(layout);
    }

    public void loadProperties(InputStream is) throws IOException {
        
        super.loadProperties(is);
        createGraphLayout();
        initDefaultAttributes();
    }

    public void setGraphLayout(GraphLayout layout) {
        
        if (graphLayout != null) {
            graphLayout.getGraphLayoutModel().removeLayoutEventListener(this);
        }
        graphLayout = layout;
        graphLayout.setGraph(graph);
        graphLayout.setProperties(getPropertyManager());
        graphLayout.setModel(getModel());
        GraphLayoutModel glm = new DefaultGraphLayoutModel();
        graphLayout.setGraphLayoutModel(glm);
        graphLayout.layout();
        graphLayout.getGraphLayoutModel().addLayoutEventListener(this);
    }

    public GraphLayout getGraphLayout() {
        
        return graphLayout;
    }

    void createGraphSelectionModel() {
        
        setGraphSelectionModel(new DefaultGraphSelectionModel(getGraph()));
    }

    public void setGraphSelectionModel(GraphSelectionModel gsm) {
        
        selectionModel = gsm;
    }

    public GraphSelectionModel getSelectionModel() {
        
        return selectionModel;
    }

    public void setGraph(Graph g) {
        
        graph = g;
        if (graphLayout != null) {
            graphLayout.setGraph(graph);
        }
    }

    public Graph getGraph() {
        
        return graph;
    }

    public void valueChanged(GraphLayoutEvent e) {
        
        repaint();
    }

    protected void checkLayout() {
        
        if (!getGraphLayout().isValid()) {
            getGraphLayout().layout();
        }
    }

    public Iterator getVisibleNodeIterator() {
        
        return graphLayout.getGraph().getNodes().iterator();
    }

    public Iterator getVisibleEdgeIterator() {
        
        return graphLayout.getGraph().getEdges().iterator();
    }

    public void paint(Graphics g) {
        // synchronized (graph) {
        checkLayout();
        Graphics2D g2 = (Graphics2D) g;

        if (getUI().isDraft()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        } else {
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        super.paint(g);

        GraphLayoutModel glm = getGraphLayout().getGraphLayoutModel();
        // se pintan todas las aristas
        for (Iterator i = getVisibleEdgeIterator(); i.hasNext();) {
            Edge edge = (Edge) i.next();
            if (edge != hoverElement) {
                if (graph.isConnectedWithThisOrientation(edge.getTarget(), edge.getSource())) {
                    edgeRenderer.configure(this, edge, true);
                } else {
                    edgeRenderer.configure(this, edge, false);
                }
                paintRenderer(g, edgeRenderer);
            }
        }
        // se pintan todos los nodos...
        for (Iterator i = getVisibleNodeIterator(); i.hasNext();) {
            Node node = (Node) i.next();
            if (node != hoverElement) {
                ModelPoint mp = glm.getNodePosition(node);
                nodeRenderer.configure(this, mp, node);
                paintRenderer(g, nodeRenderer);
            }
        }
        // se pinta el elemento con el cursor encima
        if (hoverElement != null) {
            if (hoverElement.getElementType() == Element.NODE_ELEMENT) {
                ModelPoint mp = glm.getNodePosition((Node) hoverElement);
                nodeRenderer.configure(this, mp, (Node) hoverElement);
                paintRenderer(g, nodeRenderer);
                this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            if (hoverElement.getElementType() == Element.EDGE_ELEMENT) {
                Edge edge = (Edge) hoverElement;
                if (graph.isConnectedWithThisOrientation(edge.getTarget(), edge.getSource())) {
                    edgeRenderer.configure(this, (Edge) hoverElement, true);
                } else {
                    edgeRenderer.configure(this, (Edge) hoverElement, false);
                }
                paintRenderer(g, edgeRenderer);
                this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        // }
    }

    /**
     * @return
     */
    public Element getHoverElement() {
        
        return hoverElement;
    }

    protected void setHoverElement(Element element, boolean repaint) {
        
        hoverElement = element;
        
        if (repaint) {
            repaint();
        }
    }

    /** This method is called when the user clicked on the logo.
     * @param event The mouse event that repesents the mouse click on the logo. */
    protected void logoClicked(MouseEvent event) {
    }

    public boolean hasExpander(Node node) {
        
        if (!getGraphLayout().isExpandingEnabled()) {
            
            return false;
        }
        AttributeManager amgr = graphLayout.getGraph().getAttributeManager();
        ExpandAction action = (ExpandAction) amgr.getAttribute(NODE_EXPANDED, node);
        
        return action != null;
    }

    public boolean isExpanded(Node node) {
        
        AttributeManager amgr = graphLayout.getGraph().getAttributeManager();
        ExpandAction action = (ExpandAction) amgr.getAttribute(NODE_EXPANDED, node);
        
        if (action == null) {
            return false;
        }
        
        return action == shrinkAction;
    }

    public void expandNode(Node node) {

        AttributeManager amgr = graphLayout.getGraph().getAttributeManager();
        ArrayList elementos = (ArrayList) papelera.get(node.getLabel());

        if (elementos != null) {
            for (int i = 0; i < elementos.size(); i = i + 2) {
                Node item = (Node) elementos.get(i);
                Edge arista = (Edge) elementos.get(i + 1);
                try {

                    graph.addElement(arista);
                    graph.addElement(item);
                } catch (GraphException ex) {
                    System.out.println("Excepción: " + ex.toString());
                }
            }
            amgr.setAttribute(NODE_EXPANDED, node, shrinkAction);
        }
    }

    public void shrinkNode(Node node) {

        ArrayList elementos = new ArrayList();
        AttributeManager amgr = graph.getAttributeManager();
        Collection aristas = graph.getEdges(node);
        Object[] edgeArray = aristas.toArray();

        for (int i = 0; i < edgeArray.length; i++) {
            Edge edge = (Edge) edgeArray[i];
            if (edge.getSource() == node) {
                Node target = edge.getTarget();
                if (target.getGroup() != null && target.getGroup().getName().equals("item")) {
                    elementos.add(target); //ítem
                    elementos.add(edge);  //arista
                    graph.removeElement(target);
                }
            }
        }
        papelera.put(node.getLabel(), elementos);

        amgr.setAttribute(NODE_EXPANDED, node, expandAction);
    }

    /** Centers the given node.
     * @param node The node that has to be moved to the center.
     */
    public void centerNode(Node node) {
        
        if (node != null) {
            
            GraphLayoutModel glm = getGraphLayout().getGraphLayoutModel();
            getUI().center(glm.getNodePosition(node), this);
        }
    }

    /** Returns the element at the position <code>point</code>.
     * This can be either a node or an edge.
     * If a node and an edge are at the same position, the node is returned.
     *
     * @param point The position of the element
     * @return The element that is located at the position <code>point</code>.
     */
    public Element getElement(Point point) {
        
        GraphLayoutModel glm = getGraphLayout().getGraphLayoutModel();
        // make sure that neither the graph nor the graph layout are changed while getting the nearest node.
        //  synchronized (graph) {
        //  synchronized (glm) {
        // check nodes first
        NodeRenderer nr = getNodeRenderer();
        Point p = new Point();
        
        for (Iterator i = getVisibleNodeIterator(); i.hasNext();) {
            Node node = (Node) i.next();
            nr.configure(this, glm.getNodePosition(node), node);
            Component c = nr.getComponent();
            p.setLocation(point.getX() - c.getX(), point.getY() - c.getY());
            if (c.contains(p)) {
                return node;
            }
        }
        // now check edges
        for (Iterator i = getVisibleEdgeIterator(); i.hasNext();) {
            Edge edge = (Edge) i.next();
            ModelPoint mp1 = glm.getNodePosition(edge.getSource());
            ModelPoint mp2 = glm.getNodePosition(edge.getTarget());
            ModelPoint mp3 = unProject(point);
            if (mp3 != null && mp1 != null && mp2 != null) {
                if (!graph.isConnectedWithThisOrientation(edge.getTarget(), edge.getSource())) {
                    if (getModel().getDistance(mp3, mp1, mp2, true, true) < 0.025) {
                        return edge;
                    }
                } else { // no se visualiza mediante una recta, sino mediante una curva
                    Point[] lineSegments = getProjector().getLineSegments((ModelPoint) mp1.clone(), (ModelPoint) mp2.clone(), this, true);
                    ModelPoint mpa = unProject(lineSegments[0]);
                    for (int j = 0; j < lineSegments.length - 1; j++) {
                        ModelPoint mpb = unProject(lineSegments[j + 1]);
                        if (mpa != null && mpb != null && getModel().getDistance(mp3, mpa, mpb, true, true) < 0.025) {
                            return edge;
                        }
                        mpa = mpb;
                    }
                }
            }
            //  }
            //   }
        }
        
        return null;
    }

    /** Is called when the user clicked on a node.
     *  The default implementation centers the node */
    public void nodeClicked(Node node, MouseEvent e) {
        
        if (isExpanded(node)) {
            this.shrinkNode(node);
        } 
        else {
            this.expandNode(node);
        }
        lastMouseClickNode = node;
        if (lastMouseClickNode != null) {
            // centerNode(lastMouseClickNode);
        }
    }

    public void edgeClicked(Edge edge) {
    }

    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        setHoverElement(null, false);
        Element element = getElement(e.getPoint());
        
        if (element != null && element.getElementType() == Element.NODE_ELEMENT) {
            actualNode = (Node) element;
            nodeClicked((Node) element, e);
            //if (e.getButton()!=MouseEvent.BUTTON1) {
            //} else {
            NodeRenderer nr = getNodeRenderer();
            GraphLayoutModel glm = getGraphLayout().getGraphLayoutModel();
            nr.configure(this, glm.getNodePosition((Node) element), (Node) element);
            //}
        } 
        //else if (element != null && element.getElementType() == Element.EDGE_ELEMENT && e.getButton() == MouseEvent.BUTTON3) {
            //edgeClicked((Edge) element);
        //}
    }

    public void mouseMoved(MouseEvent e) {
        
        Element element = getElement(e.getPoint());
        //if (element != this.getHoverElement())
        setHoverElement(element, true);
    }

    public void valueChanged(GraphSelectionEvent e) {
    }
    
    //***********************************************************************
    // ******************** Clase ExpandAction ******************************
    //***********************************************************************

    public class ExpandAction implements ActionListener {

        private boolean expand;

        public ExpandAction(boolean expand) {
            this.expand = expand;
        }

        public void actionPerformed(ActionEvent e) {
            
            Node node = (Node) e.getSource();
            
            if (expand) {
                expandNode(node);
            } 
            else {
                shrinkNode(node);
            }
            repaint();
        }
    }

    //************************************************************************
    //************************************************************************
    /**
     * @return
     */
    public EdgeRenderer getEdgeRenderer() {
        
        return edgeRenderer;
    }

    /**
     * @param renderer
     */
    public void setEdgeRenderer(EdgeRenderer renderer) {
        
        edgeRenderer = renderer;
    }

    public void setNodeRenderer(NodeRenderer nodeRenderer) {
        
        this.nodeRenderer = nodeRenderer;
    }

    public NodeRenderer getNodeRenderer() {
        
        return nodeRenderer;
    }
}
