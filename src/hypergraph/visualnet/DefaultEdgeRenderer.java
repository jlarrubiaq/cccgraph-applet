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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Edge;
import hypergraph.hyperbolic.Isometry;
import hypergraph.hyperbolic.LineRenderer;
import hypergraph.hyperbolic.ModelPoint;
import hypergraph.hyperbolic.PoincareProjector;
import hypergraph.hyperbolic.TextRenderer;
import java.awt.Point;

/**
 * @author Jens Kanschik
 */
public class DefaultEdgeRenderer extends JComponent implements EdgeRenderer {

    //	********************************************************
    //	private variables that are accessed via getter/setter.
    //	********************************************************
    /** The <code>TextRenderer</code> that is used to render the label
     * of the edge if the label is visible.*/
    private TextRenderer labelRenderer;
    /** The <code>LineRenderer</code> that is used to render the edge.*/
    private LineRenderer lineRenderer;
    /** Indicates whether the label of the edge has to be shown or not. */
    private boolean labelVisible;
    //	********************************************************
    //	protected variables 
    //	********************************************************
    /** The graph panel to which a node has to be drawn. */
    protected GraphPanel graphPanel;
    /** The stroke of the edge. */
    protected BasicStroke lineStroke;

    //	********************************************************
    //	getter / setter
    //	********************************************************
    /**
     * Sets the <code>TextRenderer</code> that is used to render the label.
     * You may set it to any implementation of the interface {@link TextRenderer}.
     * If you set it to <code>null</code>, the text renderer of the graph panel
     * (resp. the inherited property from the ModelPanel) is used.
     * @param tr The new text renderer or <code>null</code> if you want to use the
     * renderer of the graph panel.
     */
    public void setLabelRenderer(TextRenderer tr) {
        
        labelRenderer = tr;
    }

    /**
     * @return The <code>TextRenderer</code> that is used to render the label of the edge.
     */
    public TextRenderer getLabelRenderer() {
        
        if (labelRenderer == null) {
            labelRenderer = graphPanel.getTextRenderer();
        }
        
        return labelRenderer;
    }

    /**
     * Sets the <code>LineRenderer</code> that is used to render the edge.
     * You may set it to any implementation of the interface {@link LineRenderer}.
     * If you set it to <code>null</code>, the line renderer of the graph panel
     * (resp. the inherited property from the ModelPanel) is used.
     * @param tr The new line renderer or <code>null</code> if you want to use the
     * renderer of the graph panel.
     */
    public void setLineRenderer(LineRenderer lr) {
        
        lineRenderer = lr;
    }

    /**
     * @return The <code>LineRenderer</code> that is used to render the edge.
     */
    public LineRenderer getLineRenderer() {
        
        if (lineRenderer == null) {
            lineRenderer = graphPanel.getLineRenderer();
        }
        
        return lineRenderer;
    }

    /** {@inheritDoc} */
    public void setLabelVisible(boolean vis) {
        
        labelVisible = vis;
    }

    /** {@inheritDoc} */
    public boolean isLabelVisible() {
        
        return labelVisible;
    }

    //	********************************************************
    //	actual rendering
    //	********************************************************
    public void configure(GraphPanel gp, Edge edge, boolean desvia) {
        
        graphPanel = gp;
        setBounds(0, 0, gp.getWidth(), gp.getHeight());
        AttributeManager attrMgr = gp.getGraph().getAttributeManager();
        ModelPoint mp1 = gp.getGraphLayout().getGraphLayoutModel().getNodePosition(edge.getSource());
        ModelPoint mp2 = gp.getGraphLayout().getGraphLayoutModel().getNodePosition(edge.getTarget());
        
        if (mp1 != null && mp2 != null) {
            getLineRenderer().configure(gp, mp1, mp2);
            
            if (getLineRenderer() instanceof ArrowLineRenderer) {
                ((ArrowLineRenderer) getLineRenderer()).setShowArrows(edge.isDirected());
                ((ArrowLineRenderer) getLineRenderer()).setDesviado(desvia);
            }
            
            if (attrMgr != null) {
                Color colour2 = null;
                colour2 = (Color) attrMgr.getAttribute(GraphPanel.EDGE_LINECOLOR, edge);
                if (colour2 != null) {
                    if (edge.getSource().equals(gp.getHoverElement()) || edge.getTarget().equals(gp.getHoverElement())
                            || edge.equals(gp.getHoverElement())) {
                        
                        colour2 = colour2.darker();
                        ModelPoint center = (ModelPoint) mp1.clone();
                        if (desvia) {
                            center = desvia(mp1, mp2, gp);
                        } 
                        else {
                            Isometry isom = gp.getModel().getTranslation(mp1, mp2, 0.4);
                            isom.apply(center);
                        }
                        getLabelRenderer().configure(gp, center, edge.getLabel());
                        getLabelRenderer().setBackground(Color.white);
                        Color colour = null;
                        if (attrMgr != null) {
                            colour = (Color) attrMgr.getAttribute(GraphPanel.EDGE_TEXTCOLOR, edge);
                        }
                        if (colour != null) {
                            getLabelRenderer().setColor(colour);
                        }
                    }
                    getLineRenderer().setColor(colour2);
                }
                float[] stroke = (float[]) attrMgr.getAttribute(GraphPanel.EDGE_STROKE, edge);
                float lineWidth = ((Float) attrMgr.getAttribute(GraphPanel.EDGE_LINEWIDTH, edge)).floatValue();
                if (stroke != null && stroke.length > 0) {
                    lineStroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, stroke, 0.0f);
                } 
                else if (stroke != null && stroke.length == 0) {
                    lineStroke = null;
                } 
                else {
                    lineStroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
                }
            }
        }
    }

    private ModelPoint desvia(ModelPoint mp1, ModelPoint mp2, GraphPanel panel) {
        
        Point[] lineSegments = panel.getProjector().getLineSegments((ModelPoint) mp1.clone(), (ModelPoint) mp2.clone(), panel, true);
        return panel.unProject(lineSegments[lineSegments.length / 2]);
    }

    /** Paints the edge using {@link hypergraph.hyperbolic.LineRenderer}.
     *  If the boolean property <code>labelVisible</code> is set to <code>true</code>,
     *  the label of the edge is shown between the two nodes.
     *  @param g The Graphics object to draw into.
     */
    public void paint(Graphics g) {
        
        if (lineStroke == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(lineStroke);
        getLineRenderer().getComponent().paint(g);
        if (labelVisible) {
            g.translate(getLabelRenderer().getComponent().getX(), getLabelRenderer().getComponent().getY());
            getLabelRenderer().getComponent().paint(g);
        }
    }

    public Component getComponent() {
        
        return this;
    }
}
