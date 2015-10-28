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
package hypergraph.hyperbolic;

import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

public class PoincareProjector implements Projector {

    public Point[] getLineSegments(ModelPoint mp1, ModelPoint mp2, JComponent c, boolean desviacion) {
        
        Point[] lineSegments;
        ModelPanelUI ui = (ModelPanelUI) ((ModelPanel) c).getUI();
        ui.applyViewMatrix(mp1, c);
        ui.applyViewMatrix(mp2, c);
        
        if (ui.isDraft() || ((Complex) mp1).dist((Complex) mp2) < 0.1) {
            lineSegments = new Point[2];
            lineSegments[0] = ui.map(mp1, c);
            lineSegments[1] = ui.map(mp2, c);
            return lineSegments;
        }

        Complex current;
        current = (Complex) mp1;

        int n = 12;
        if (((Complex) mp1).dist((Complex) mp2) > 1) {
            n = 12;
        }
        lineSegments = new Point[n + 1];
        lineSegments[0] = ui.map(current, c);
        Isometry trans = ((ModelPanel) c).getModel().getTranslation(mp1, mp2, 1 / (double) n);
        for (int i = 1; i < n + 1; i++) {
            trans.apply(current);
            lineSegments[i] = ui.map(current, c);
        }

        if (desviacion) {
            desvia(lineSegments, false, n, 12);
        }

        return lineSegments;
    }
/*
 * Desvia la línea de puntos opuesto indica en qué sentido se produce la desviación
 * n, número de puntos
 * incr, distancia máxima de desvío
*/
    
    private void desvia(Point[] lineSegments, boolean opuesto, int n, int incr) {
        
        double angulo, desplX, desplY, hipo = incr, desv;

        angulo = Math.atan((lineSegments[n].getY() - lineSegments[0].getY()) / (lineSegments[n].getX() - lineSegments[0].getX()));
        desplX = hipo * Math.sin(angulo);
        desplY = hipo * Math.cos(angulo);

        if ((lineSegments[n].getX() - lineSegments[0].getX()) > 0) { // de izda a dcha
            desplX = -desplX;
            desplY = -desplY;
        }

        if (n > 5) {
            for (int i = 1; i < n; i++) {
                desv = n / 2 - Math.abs(n / 2 - i);
                if (!opuesto) {
                    lineSegments[i].x = (int) (lineSegments[i].x + desplX * Math.sqrt(desv));
                    lineSegments[i].y = (int) (lineSegments[i].y - desplY * Math.sqrt(desv));
                } 
                else {
                    lineSegments[i].x = (int) (lineSegments[i].x - desplX * Math.sqrt(desv));
                    lineSegments[i].y = (int) (lineSegments[i].y + desplY * Math.sqrt(desv));
                }
            }

        }


    }

    public PoincareProjector() {
    }

    public Point map(ModelPoint mp, JComponent c) {
        
        Complex z = (Complex) mp;
        
        return new Point((int) Math.floor((z.real + 1) * 0.5 * c.getWidth()), (int) Math.floor((1 - z.imag) * 0.5 * c.getHeight()));
    }

    public ModelPoint inversMap(Point p, JComponent c) {
        
        Complex z = new Complex();
        
        if (c.getWidth() != 0) {
            z.real = p.getX() * 2 / (double) c.getWidth() - 1;
        }
        if (c.getHeight() != 0) {
            z.imag = 1 - p.getY() * 2 / (double) c.getHeight();
        }
        if (z.norm2() < 1.0) {
            return z;
        } 
        else {
            return null;
        }
    }

    public Point2D getScale(ModelPoint mp, JComponent c) {
        
        double scale = 1 - ((Complex) mp).norm2();
        
        return new Point2D.Double(scale, scale);
    }
}
