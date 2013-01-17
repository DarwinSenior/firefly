package edu.caltech.ipac.firefly.data;

import edu.caltech.ipac.util.dd.UIAttrib;

import java.util.ArrayList;

/**
 * Date: Mar 19, 2008
 *
 * @author loi
 * @version $Id: MenuItemAttrib.java,v 1.5 2010/09/28 17:59:24 roby Exp $
 */
public class MenuItemAttrib extends UIAttrib {

    private ArrayList<MenuItemAttrib> children = new ArrayList<MenuItemAttrib>();
    private MenuItemAttrib parent;
    private boolean separator= false;
    private boolean important= false;

    public MenuItemAttrib() {
    }

    public MenuItemAttrib(boolean separator) {
        this.separator= separator;
    }

    public MenuItemAttrib(String name, String label, String desc, String shortDesc, String icon) {
        super(name, label, desc, shortDesc, icon);
    }

    public void addMenuItem(MenuItemAttrib menuItem) {
        children.add(menuItem);
        menuItem.parent = this;
    }

    public MenuItemAttrib[] getChildren() {
        return children.toArray(new MenuItemAttrib[children.size()]);
    }

    public MenuItemAttrib getParent() {
        return parent;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public boolean isSeparator() { return separator; 
    }

    public void setImportant(boolean important) {
        this.important= important;
    }

    public boolean isImportant() { return important; }

    /**
     * traverse this tree from this node.  return null if not found
     * @param name the name of the MenuItemAttrib to search for
     * @return the MenuItemAttrib with the given name
     */
    public MenuItemAttrib findMenuItem(String name) {

        if (name == null) return null;

        if (getName().equals(name)) {
            return this;
        } else {
            for (int i = 0; i < children.size(); i++) {
                MenuItemAttrib child = children.get(i);
                return child.findMenuItem(name);
            }
        }
        return null;
    }


}
/*
* THIS SOFTWARE AND ANY RELATED MATERIALS WERE CREATED BY THE CALIFORNIA
* INSTITUTE OF TECHNOLOGY (CALTECH) UNDER A U.S. GOVERNMENT CONTRACT WITH
* THE NATIONAL AERONAUTICS AND SPACE ADMINISTRATION (NASA). THE SOFTWARE
* IS TECHNOLOGY AND SOFTWARE PUBLICLY AVAILABLE UNDER U.S. EXPORT LAWS
* AND IS PROVIDED AS-IS TO THE RECIPIENT WITHOUT WARRANTY OF ANY KIND,
* INCLUDING ANY WARRANTIES OF PERFORMANCE OR MERCHANTABILITY OR FITNESS FOR
* A PARTICULAR USE OR PURPOSE (AS SET FORTH IN UNITED STATES UCC 2312-2313)
* OR FOR ANY PURPOSE WHATSOEVER, FOR THE SOFTWARE AND RELATED MATERIALS,
* HOWEVER USED.
*
* IN NO EVENT SHALL CALTECH, ITS JET PROPULSION LABORATORY, OR NASA BE LIABLE
* FOR ANY DAMAGES AND/OR COSTS, INCLUDING, BUT NOT LIMITED TO, INCIDENTAL
* OR CONSEQUENTIAL DAMAGES OF ANY KIND, INCLUDING ECONOMIC DAMAGE OR INJURY TO
* PROPERTY AND LOST PROFITS, REGARDLESS OF WHETHER CALTECH, JPL, OR NASA BE
* ADVISED, HAVE REASON TO KNOW, OR, IN FACT, SHALL KNOW OF THE POSSIBILITY.
*
* RECIPIENT BEARS ALL RISK RELATING TO QUALITY AND PERFORMANCE OF THE SOFTWARE
* AND ANY RELATED MATERIALS, AND AGREES TO INDEMNIFY CALTECH AND NASA FOR
* ALL THIRD-PARTY CLAIMS RESULTING FROM THE ACTIONS OF RECIPIENT IN THE USE
* OF THE SOFTWARE.
*/
