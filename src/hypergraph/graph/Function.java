/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hypergraph.graph;

public class Function {

    int id;
    String label;

    public Function(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public String toString() {
        return label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
