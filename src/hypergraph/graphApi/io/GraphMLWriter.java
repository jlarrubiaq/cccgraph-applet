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
package hypergraph.graphApi.io;

import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Group;
import hypergraph.graphApi.Node;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class GraphMLWriter extends AbstractGraphWriter {

    public GraphMLWriter(String fileName) throws IOException {
        super(fileName);
    }

    public GraphMLWriter(File outputFile) throws IOException {
        super(outputFile);
    }

    public void writeHeader() throws IOException {

        writer.write("<?xml version=\"1.0\"?>\n");
        writer.write("<!DOCTYPE graphml SYSTEM \"graphml.dtd\">\n");
        writer.write("<!--This file has been automatically created by HyperGraph.-->\n");
        writer.write("<graphml>\n");
        writer.write("  <key id=\"label\" for=\"node\" />\n");
        writer.write("  <graph " + "edgedefault=\"directed\" " + "id=\"" + graph.getName() + "\">\n");
    }

    public void writeGroup(Group group) throws IOException {
        
        writer.write("    <group "
                + "name=\"" + group.getName() + "\" ");
        
        if (group.getGroup() != null) {
            writer.write("group=\"" + group.getGroup().getName() + "\" ");
        }
        writer.write(">\n");
        writer.write("    </group>\n");
    }

    public void writeNode(Node node) throws IOException {
        
        writer.write(
                "    <node "
                + "id=\"" + node.getName() + "\" ");
        if (node.getGroup() != null) {
            writer.write("group=\"" + node.getGroup().getName() + "\" ");
        }
        writer.write(">\n");
        writer.write("      <data key=\"label\">" + node.getLabel() + "</data>\n");
        writer.write("    </node>\n");
    }

    public void writeEdge(Edge edge) throws IOException {
        
        writer.write(
                "    <edge "
                + "id=\"" + edge.getName() + "\" "
                + "source=\"" + edge.getSource().getName() + "\" "
                + "target=\"" + edge.getTarget().getName() + "\" ");
        
        if (edge.getGroup() != null) {
            writer.write("group=\"" + edge.getGroup().getName() + "\" ");
        }
        writer.write(">\n");

        if (edge.getLabel() != null) {
            writer.write("      <label>" + edge.getLabel() + "</label>\n");
        }
        writer.write("    </edge>\n");
    }

    public void writeFooter() throws IOException {
        
        writer.write("  </graph>\n");
        writer.write("</graphml>\n");
        writer.close();
    }
}
