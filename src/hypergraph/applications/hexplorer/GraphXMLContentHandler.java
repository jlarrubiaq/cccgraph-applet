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

import java.awt.Color;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import hypergraph.graphApi.*;
import hypergraph.graphApi.io.CSSColourParser;

import org.xml.sax.*;

/**
 * @author Jens Kanschik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GraphXMLContentHandler
        extends hypergraph.graphApi.io.GraphXMLContentHandler {

    private URL baseUrl;

    public GraphXMLContentHandler(URL baseUrl) {

        this.baseUrl = baseUrl;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        super.startElement(namespaceURI, localName, qName, atts);
        if (qName.equals("line")) {
            startElementLine(atts);
            return;
        }
        if (qName.equals("fill")) {
            startElementFill(atts);
            return;
        }
        if (qName.equals("ref")) {
            startElementRef(atts);
            return;
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

        super.endElement(namespaceURI, localName, qName);
        if (qName.equals("line")) {
            endElementLine();
            return;
        }
        if (qName.equals("fill")) {
            endElementFill();
            return;
        }
    }

    protected void startElementLine(Attributes atts) {

        Group group = null;
        Color colour = null;
        String tag = "";
        float[] dashed_stroke = {5, 3};
        float[] dotted_stroke = {1, 4};
        float[] dash_dotted_stroke = {5, 3, 1, 3};
        float[] empty_stroke = {};
        float[] stroke = null;
        Float lineWidth = null;
        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equals("class")) {
                String groupName = atts.getValue(i);
                group = (Group) graph.getElement(groupName);
                if (group == null) {
                    try {
                        group = graph.createGroup(groupName);
                    } catch (GraphException ge) {
                        ge.printStackTrace();
                    }
                }
                continue;
            }
            if (atts.getQName(i).equals("colour")
                    || atts.getQName(i).equals("color")) {
                colour = CSSColourParser.stringToColor(atts.getValue(i));
                continue;
            }
            if (atts.getQName(i).equals("linewidth")) {
                lineWidth = new Float(atts.getValue(i));
                continue;
            }
            if (atts.getQName(i).equals("linestyle")) {
                if (atts.getValue(i).equalsIgnoreCase("dashed")) {
                    stroke = dashed_stroke;
                }
                if (atts.getValue(i).equalsIgnoreCase("dotted")) {
                    stroke = dotted_stroke;
                }
                if (atts.getValue(i).equalsIgnoreCase("dash-dotted")) {
                    stroke = dash_dotted_stroke;
                }
                if (atts.getValue(i).equalsIgnoreCase("none")) {
                    stroke = empty_stroke;
                }
                continue;
            }
            if (atts.getQName(i).equals("tag")) {
                tag = atts.getValue(i);
            }
        }
        Element attrElement = currentElement;
        if (attrElement == null) {
            if (group == null) {
                attrElement = graph;
            } else {
                attrElement = group;
            }
        }
        String attrType = null;
        AttributeManager attrMgr = graph.getAttributeManager();
        if (colour != null) {
            if (attrElement.getElementType() == Element.NODE_ELEMENT
                    || tag.equals("node")) {
                attrMgr.setAttribute(GraphPanel.NODE_FOREGROUND, attrElement, colour);
            }
            if (attrType == null
                    && (attrElement.getElementType() == Element.EDGE_ELEMENT
                    || tag.equals("edge"))) {
                attrMgr.setAttribute(GraphPanel.EDGE_LINECOLOR, attrElement, colour);
            }
        }
        if (lineWidth != null) {
            if (attrElement.getElementType() == Element.EDGE_ELEMENT
                    || tag.equals("edge")) {
                attrMgr.setAttribute(GraphPanel.EDGE_LINEWIDTH, attrElement, lineWidth);
            }
        }
        if (stroke != null) {
            if (attrElement.getElementType() == Element.EDGE_ELEMENT
                    || tag.equals("edge")) {
                attrMgr.setAttribute(GraphPanel.EDGE_STROKE, attrElement, stroke);
            }
        }
    }

    protected void endElementLine() {
    }

    protected void startElementFill(Attributes atts) {

        Group group = null;
        Color colour = null;
        String tag = "";
        Icon icon = null;

        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equals("class")) {
                String groupName = atts.getValue(i);
                group = (Group) graph.getElement(groupName);
                if (group == null) {
                    try {
                        group = graph.createGroup(groupName);
                    } catch (GraphException ge) {
                        ge.printStackTrace();
                    }
                }
                continue;
            }
            if (atts.getQName(i).equals("colour")
                    || atts.getQName(i).equals("color")) {
                colour = CSSColourParser.stringToColor(atts.getValue(i));
                continue;
            }
            if (atts.getQName(i).equals("xlink:href")) {
                String href = atts.getValue(i);

                try {
                    URL url = new URL(baseUrl, href);
                    icon = new ImageIcon(url);
                } catch (Exception e) {
                    System.out.println(e);
                }
                continue;
            }
            if (atts.getQName(i).equals("tag")) {
                tag = atts.getValue(i);
            }
        }
        Element attrElement = currentElement;
        if (attrElement == null) {
            if (group == null) {
                attrElement = graph;
            } else {
                attrElement = group;
            }
        }

        String attrType = null;
        AttributeManager attrMgr = graph.getAttributeManager();
        if (colour != null) {
            if (attrElement.getElementType() == Element.NODE_ELEMENT
                    || tag.equals("node")) {
                attrMgr.setAttribute(GraphPanel.NODE_BACKGROUND, attrElement, colour);
            }
        }
        if (icon != null) {
            if (attrElement.getElementType() == Element.NODE_ELEMENT
                    || tag.equals("node")) {
                attrMgr.setAttribute(GraphPanel.NODE_ICON, attrElement, icon);
            }
        }
    }

    protected void endElementFill() {
    }

    protected void startElementRef(Attributes atts) {

        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equals("xlink:href")) {
                if (currentElement == null) {
                    return;
                }
                String href = atts.getValue(i);
                AttributeManager attrMgr = graph.getAttributeManager();
                attrMgr.setAttribute("xlink:href", currentElement, href);
            }
            if (atts.getQName(i).equals("xlink:show")) {
                if (currentElement == null) {
                    return;
                }
                String href = atts.getValue(i);
                AttributeManager attrMgr = graph.getAttributeManager();
                attrMgr.setAttribute("xlink:show", currentElement, href);
            }
        }
    }
}
