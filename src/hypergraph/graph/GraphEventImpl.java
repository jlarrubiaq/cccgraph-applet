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
package hypergraph.graph;

import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphEvent;

import java.util.EventObject;

/**
 * An <code>EventObject</code> that represents changes in a graph.
 *
 * @author Jens Kanschik
 */
public class GraphEventImpl extends EventObject implements GraphEvent {

    private Element element;

    public GraphEventImpl(Graph graph) {
        super(graph);
    }

    public GraphEventImpl(Graph graph, Element e) {
        this(graph);
        this.element = e;
    }

    /**
     * @return The element involved in the change.
     */
    public Element getElement() {
        return element;
    }
}