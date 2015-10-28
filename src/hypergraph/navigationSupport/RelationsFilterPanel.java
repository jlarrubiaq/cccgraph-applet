/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RelationsFilterPanel.java
 *
 * Created on 20-nov-2013, 17:41:06
 */
package hypergraph.navigationSupport;
import hypergraph.applications.hexplorer.GraphPanel;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.Edge;
import hypergraph.graphApi.GraphException;
import hypergraph.visualnet.TreeLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

/**
 *
 * @author Jesus
 */
public class RelationsFilterPanel extends javax.swing.JPanel {

    Graph graph;
    GraphPanel graphPanel;
    NavigationHandler nhandler;
    ArrayList <String> toRemove;
    ArrayList <String> toAdd;
    ArrayList <String> visibleTypes;     //ArrayList with the elements displayed by visibleJList
    ArrayList <String> notVisibleTypes;  //ArrayList with the elements displayed by notVisibleJList
    ArrayList <Edge> hides;
    JFrame frame;
    
    /** Creates new form RelationsFilterPanel */
    public RelationsFilterPanel(GraphPanel gp, NavigationHandler nh) {
        
        initComponents();
        nhandler = nh;
        visibleTypes = new ArrayList();
        notVisibleTypes = new ArrayList();
        graphPanel = gp;
        graph = gp.getGraph();
        toRemove = new ArrayList();
        toAdd = new ArrayList();
        hides = new ArrayList();
       
        Edge e = null;
        for (Iterator i = graph.getEdges().iterator(); i.hasNext();) {
            e = (Edge) i.next();
            if (e.getLabel().contains("/")){
                StringTokenizer relations=new StringTokenizer(e.getLabel(), "/"); 
                while(relations.hasMoreTokens()){
                    String token = relations.nextToken();
                    if(token.contains("*")){
                        if (!visibleTypes.contains(token.substring(0, token.length()-1)))
                            visibleTypes.add(token.substring(0, token.length()-1));            
                    }  
                    else if (!visibleTypes.contains(token))
                        visibleTypes.add(token);
                }
            }
            else{
                if(e.getLabel().contains("*")){
                    if (!visibleTypes.contains(e.getLabel().substring(0, e.getLabel().length()-1)))
                        visibleTypes.add(e.getLabel().substring(0, e.getLabel().length()-1));            
                }    
                else if (!visibleTypes.contains(e.getLabel()))
                    visibleTypes.add(e.getLabel());
            }
        } 
        
        Object[] notvisibles = new Object[0];
        notVisibleJList.setListData(notvisibles);
        Object[] visibles = visibleTypes.toArray();
        visibleJList.setListData(visibles);
    }
    
    public void setJFrame(JFrame j){
        
        frame = j; 
    }
    
    public void startFilter(String str_relations){
        
        StringTokenizer relations=new StringTokenizer(str_relations, "||"); 
        ArrayList <String> total = new ArrayList();    
                       
        while(relations.hasMoreTokens()){
            String token = relations.nextToken();
            if (removeEdges(token)){
                notVisibleTypes.add(token);  
                visibleTypes.remove(token);
            }
            
        }
        
        for (int i=0;i<notVisibleTypes.size();i++){
            total.add(notVisibleTypes.get(i));
        }
        notVisibleJList.setListData(total.toArray());
        total.clear();
        for (int i=0;i<visibleTypes.size();i++){
            total.add(visibleTypes.get(i));
        }
        total.addAll(toAdd);
        visibleJList.setListData(total.toArray());
       
        
       // frame.setVisible(false);
        

// Mensaje en caso de que falle

//frame.setVisible(false);
        //TreeLayout t = (TreeLayout) graphPanel.getGraphLayout();
       // t.setDefaultSize(0.9);
        graphPanel.getGraphLayout().layout();

        graphPanel.centerRootNode();  
                        
       
    }
    
    boolean removeEdges(String type){
        
        boolean removed = false;
        Edge e = null;
        for (Iterator i = graph.getEdges().iterator(); i.hasNext();) {
                e = (Edge) i.next();
                if(e.getLabel().contains("/")){
                    
                     StringTokenizer relations=new StringTokenizer(e.getLabel(), "/"); 
                     while(relations.hasMoreTokens()){
                        String token = relations.nextToken();
                        if (token.equals(type)){
                            hides.addAll(graph.removeMultipleRelation(e,type));
                            removed = true;
                        }
                        else if (token.equals(type+"*")){
                            hides.addAll(graph.removeMultipleRelation(e,type));
                            removed = true;   
                        }
                     }
                }
                
                else if (e.getLabel().equals(type)){
                    removed = true;
                    hides.add(e);
                    graph.removeMultipleRelation(e,type);
                }
        } 
        
        return removed;
    }
    
    boolean addEdges(String type){
        
        boolean added = false;
        
        for(int i=0; i<hides.size(); i++){
            
             if (hides.get(i).getLabel().equals(type)){

                try {
                    graph.addElement(hides.get(i));
                    added = true;
                } 
                catch (GraphException ex) {
                    System.out.println();
                    JOptionPane.showMessageDialog(null, (String) ex.toString(),"Edge doesn't exist", JOptionPane.ERROR_MESSAGE);
                    added = false;
                }
                hides.remove(i);
                i=i-1;
            }
        }
        return added;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        acceptButton = new javax.swing.JButton();
        rightButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        leftButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        visibleJList = new javax.swing.JList();
        searchTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        notVisibleJList = new javax.swing.JList();
        searchButton = new javax.swing.JButton();

        acceptButton.setText("Aceptar");
        acceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptButtonActionPerformed(evt);
            }
        });

        rightButton.setText(">>");
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancelar");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 11));
        jLabel2.setText("Relaciones no visibles");

        jLabel3.setText("¿Qué relación buscas?");

        leftButton.setText("<<");
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 11));
        jLabel1.setText("Relaciones visibles");

        visibleJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(visibleJList);

        notVisibleJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(notVisibleJList);

        searchButton.setText("Buscar");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(acceptButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(rightButton, 0, 0, Short.MAX_VALUE)
                                    .addComponent(leftButton))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(searchButton))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(15, 15, 15)
                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(rightButton)
                        .addGap(18, 18, 18)
                        .addComponent(leftButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(acceptButton)
                            .addComponent(cancelButton))))
                .addContainerGap(11, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightButtonActionPerformed

    String [] seleccionados;
    ArrayList <String> total = new ArrayList();
    
    seleccionados = new String [visibleJList.getSelectedValues().length];
    System.arraycopy(visibleJList.getSelectedValues(), 0, seleccionados, 0, visibleJList.getSelectedValues().length);
    
    for (int j = 0; j<seleccionados.length;j++){
        if (!toAdd.remove(seleccionados[j]))
            toRemove.add((String)seleccionados[j]);
    }
    
    total.clear();
    for (int i=0;i<notVisibleTypes.size();i++){
        if(!toAdd.contains(notVisibleTypes.get(i)))
            total.add(notVisibleTypes.get(i));
    }
    total.addAll(toRemove);
    notVisibleJList.setListData(total.toArray());
        
    total.clear();
    for (int i=0;i<visibleTypes.size();i++){
        if(!toRemove.contains(visibleTypes.get(i)))
            total.add(visibleTypes.get(i));
    }
    total.addAll(toAdd);
    visibleJList.setListData(total.toArray());
    
}//GEN-LAST:event_rightButtonActionPerformed

private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftButtonActionPerformed

    ArrayList <String> total = new ArrayList();
    String [] seleccionados;
    
    seleccionados = new String [notVisibleJList.getSelectedValues().length];
    System.arraycopy(notVisibleJList.getSelectedValues(), 0, seleccionados, 0, notVisibleJList.getSelectedValues().length);

    for (int j=0; j<seleccionados.length; j++){
        if (!toRemove.remove(seleccionados[j]))
            toAdd.add((String) seleccionados[j]);
    }
    
    for (int i=0;i<visibleTypes.size();i++){
        if(!toRemove.contains(visibleTypes.get(i)))
            total.add(visibleTypes.get(i));
    }
    total.addAll(toAdd);
    visibleJList.setListData(total.toArray());
    
    total.clear();
    for (int i=0;i<notVisibleTypes.size();i++){
        if(!toAdd.contains(notVisibleTypes.get(i)))
            total.add(notVisibleTypes.get(i));
    }
    total.addAll(toRemove);
    notVisibleJList.setListData(total.toArray());
    
}//GEN-LAST:event_leftButtonActionPerformed

private void acceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptButtonActionPerformed
    
    String list_removed="";
    String list_added="";
    int removed, added;
    removed = added = 0;
    
    //Delete relations to remove
    for (int i=0; i<toRemove.size(); i++){
        if (removeEdges(toRemove.get(i))){
            removed++;
            if(list_removed.equals(""))
                list_removed += "remove;" + toRemove.get(i).replace(" ","%20"); 
            else
                list_removed += "||" + "remove;" + toRemove.get(i).replace(" ","%20");  
            
            notVisibleTypes.add(toRemove.get(i));  
            visibleTypes.remove(toRemove.get(i));
        }
        
       
    }
  
    //Add relations to add
    for (int i=0; i<toAdd.size(); i++){
        if (addEdges(toAdd.get(i))){
            added++;
            if(list_added.equals(""))
                list_added += "add;" + toAdd.get(i).replace(" ","%20"); 
            else
                list_added += "||" + "add;" + toAdd.get(i).replace(" ","%20"); 
            
            visibleTypes.add(toAdd.get(i));
            notVisibleTypes.remove(toAdd.get(i));
        }
    } 
    
    if (removed + added > 0){
        if(removed  == 0 || added == 0){
            nhandler.sendServer("drupal/hfilterrelations/" + list_removed+list_added + "/" + graphPanel.getRoot().getName());
        }
        else{
            nhandler.sendServer("drupal/hfilterrelations/" + list_removed+"||"+list_added + "/" + graphPanel.getRoot().getName());
        }
    }
        
    toRemove.clear();
    toAdd.clear();
    notVisibleJList.setListData(notVisibleTypes.toArray());
    visibleJList.setListData(visibleTypes.toArray());
    
    graphPanel.centerRootNode();
    frame.setVisible(false);
}//GEN-LAST:event_acceptButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

    toRemove.clear();
    toAdd.clear();
    notVisibleJList.setListData(notVisibleTypes.toArray());
    visibleJList.setListData(visibleTypes.toArray());
    frame.setVisible(false);
   
}//GEN-LAST:event_cancelButtonActionPerformed

private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed

    for(int i = 0; i < notVisibleJList.getModel().getSize(); i++) {
        if (searchTextField.getText().equals(notVisibleJList.getModel().getElementAt(i)))
            notVisibleJList.setSelectedIndex(i);
    }
        
    for(int i = 0; i < visibleJList.getModel().getSize(); i++) {
        if (searchTextField.getText().equals(visibleJList.getModel().getElementAt(i)))
        visibleJList.setSelectedIndex(i);   
    }   
}//GEN-LAST:event_searchButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton leftButton;
    private javax.swing.JList notVisibleJList;
    private javax.swing.JButton rightButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JList visibleJList;
    // End of variables declaration//GEN-END:variables
}
