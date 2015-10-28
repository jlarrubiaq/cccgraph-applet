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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Jens Kanschik
 *
 */
public abstract class AbstractMap2D implements Map2D {

    protected Map map;
    protected int size;

    //*********************************************************************
    //********************* Clase Entry ***********************************
    //*********************************************************************
    private class Entry implements Map2D.Entry {

        Object key1;
        Object key2;
        Object value;

        public Entry(Object key1, Object key2, Object value) {
            this.key1 = key1;
            this.key2 = key2;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public Object getKey1() {
            return key1;
        }

        public Object getKey2() {
            return key2;
        }
    }

    public void clear() {
        if (map != null) {
            map.clear();
        }
    }
    
    //*********************************************************************
    //*********************************************************************
       

    //informa de si existe la arista que va desde key1 a key2
    public boolean containsKey(Object key1, Object key2) {
        
        if (map == null) {
            return false;
        }
        if (!map.containsKey(key1)) {
            return false;
        }
        Map submap = (Map) map.get(key1);
        
        return submap.containsKey(key2);
    }

    // ojo no modificado, no utilizado
    public boolean containsKey(Object key) {
        
        if (map == null) {
            return false;
        }
        
        return map.containsKey(key);
    }

    // recorre todo el mapa para informar si existe la arista value
    public boolean containsValue(Object value) {
        
        if (map == null) {
            return false;
        }
        
        Map submap;
        synchronized (map) {
            for (Iterator i = map.values().iterator(); i.hasNext();) {
                submap = (Map) i.next();
                if (submap.containsValue(value)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // método no revisado, no utilizado
    public Set entrySet() {
        
        Set result = new HashSet();
        Map submap;
        Set keySet = map.keySet();
        
        synchronized (keySet) {
            for (Iterator i1 = keySet.iterator(); i1.hasNext();) {
                Object key1 = i1.next();
                
                for (Iterator i2 = keySet.iterator(); i2.hasNext();) {
                    Object key2 = i2.next();
                    Object value = get(key1, key2);
                    if (value != null) {
                        result.add(new Entry(key1, key2, value));
                    }
                }
            }
        }
        
        return result;
    }

    // obtiene la arista que va de key1 a key2
    public Object get(Object key1, Object key2) {
        
        Map submap = get(key1);
        
        if (submap == null) {
            return null;
        }
        
        return submap.get(key2);
    }

    // obtiene todas las aristas que nacen en key o llegan a key
    public Map get(Object key) {
        
        if (map == null) {
            return null;
        }
        HashMap m = new HashMap();
        Map submap;
        Object o;
        submap = (Map) map.get(key);
        if (submap != null) {
            m.putAll(submap);
        }
        for (Iterator i = map.values().iterator(); i.hasNext();) {
            submap = (Map) i.next();
            o = submap.get(key);
            if (o != null) {
                m.put(Math.random(), o);
            }
        }
        
        return m;
    }

    public boolean isEmpty() {
        
        if (map == null) {
            return true;
        }
        
        return map.isEmpty();
    }

    public Set keySet() {
        
        if (map == null) {
            return Collections.EMPTY_SET;
        }
        
        return map.keySet();
    }

    protected abstract Map createMap();

    // inserta la arista(value) que va desde key1 a key2
    public Object put(Object key1, Object key2, Object value) {
        
        if (map == null) {
            map = createMap();
        }
        Map submap1;
        if (!map.containsKey(key1)) {
            submap1 = createMap();
            map.put(key1, submap1);
        } 
        else {
            submap1 = (Map) map.get(key1);
        }
        Object oldValue = submap1.put(key2, value);
        if (oldValue == null) {
            size++;
        }
        
        return oldValue;
    }

    // método no revisado, no utilizado
    public void putAll(Map2D newMap) {
        
        Collection newEntries = newMap.entrySet();
        
        for (Iterator i = newEntries.iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();
        }
    }

    // elimina la arista que va desde key1 a key2
    public void remove(Object key1, Object key2) {
        
        if (map == null) {
            return;
        }
        Map submap1;
        if (!map.containsKey(key1)) {
            return;
        }
        submap1 = (Map) map.get(key1);
        Object oldValue = submap1.remove(key2);

        if (oldValue != null) {
            size--;
        }
    }

    // elimina todas las aristas que nacen en key o llegan a key
    // OJO, vigilar su uso
    public void remove(Object key) {
        
        if (map == null) {
            return;
        }
        
        Map submap;

        submap = (Map) map.get(key);
        if (submap != null) {
            for (Iterator iter = submap.keySet().iterator(); iter.hasNext();) {
                Object key2 = iter.next();
                if (submap.remove(key2) != null) // elimina las aristas key,key2
                {
                    size--;
                }
            }
        }
        map.remove(key);
        for (Iterator i = map.values().iterator(); i.hasNext();) {
            submap = (Map) i.next();
            if (submap.remove(key) != null) // elimina las aristas key2,key
            {
                size--;
            }
        }

    }

    public int size() {
        
        return size;
    }

    // Devuelve todas las aristas, es decir, todos los valores del Map2d
    public Collection values() {
        
        Collection v = map.values();
        Collection result;
        
        if (v == null) {
            return Collections.EMPTY_SET;
        }
        result = new HashSet();
        synchronized (v) {
            for (Iterator i1 = v.iterator(); i1.hasNext();) {
                Map submap = (Map) i1.next();
                Collection c = submap.values();
                synchronized (c) {
                    result.addAll(c);
                }
            }
        }
        
        return result;
    }

    public String toString() {
        
        return "[AbstractMap2D : " + map + " ] ";
    }
}
