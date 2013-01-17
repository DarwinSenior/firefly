package edu.caltech.ipac.firefly.ui.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import edu.caltech.ipac.firefly.core.Application;
import edu.caltech.ipac.firefly.core.GeneralCommand;
import edu.caltech.ipac.firefly.data.Request;
import edu.caltech.ipac.firefly.util.PropertyChangeEvent;
import edu.caltech.ipac.firefly.util.PropertyChangeListener;
import edu.caltech.ipac.util.StringUtils;

import java.util.List;

/**
 * Date: Sep 17, 2008
 *
 * @author loi
 * @version $Id: QuickNavPanel.java,v 1.16 2010/12/18 01:13:59 loi Exp $
 */
public class QuickNavPanel extends Composite {

    private HorizontalPanel main;
    private HorizontalPanel nav;
    private int _itemSize;

    public QuickNavPanel() {
        this(20);
    }

    public QuickNavPanel(int itemSize) {
        _itemSize = itemSize;

        main = new HorizontalPanel();
        main.setWidth("100%");
        main.setHeight("20px");
        main.setStyleName("quicknav-bar");

        Application.getInstance().getDrillDownItems().addPropertyChangeListener(
                        new PropertyChangeListener(){
                            public void propertyChange(PropertyChangeEvent pce) {
                                init();
                            }
                        }
                );

        nav = new HorizontalPanel();
        main.add(nav);
        main.setCellVerticalAlignment(nav, HorizontalPanel.ALIGN_BOTTOM);
//        nav.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
        initWidget(main);
    }

    private void init() {

        List<Request> items = Application.getInstance().getDrillDownItems().getList();
        nav.clear();
        if (items.size() > 0) {
            main.setVisible(true);
            nav.add(makeItem(Application.getInstance().getHomeRequest(), true, _itemSize, "Home"));
            int maxLenght = Math.min(_itemSize, ((170 - 3*items.size())/items.size()));
            for(int i = 0; i <items.size(); i++) {
                Label l = new Label(">");
                l.setStyleName("quicknav-item");
                l.addStyleName("highlight-text");
                DOM.setStyleAttribute(l.getElement(), "margin", "0px 5px 0px 5px");
                nav.add(l);
                nav.add(makeItem(items.get(i), (i < items.size()-1), maxLenght, null));
            }
        } else {
            main.setVisible(false);
        }
    }


    protected Widget makeItem(final Request req, boolean isEnabled, int maxLabelLenght, String desc) {
        //String desc = StringUtils.isEmpty(req.getShortDesc()) ? "unknown" : req.getShortDesc();
        desc = desc == null ? getDesc(req) : desc;
        final Label l = new Label(StringUtils.shrink(desc, maxLabelLenght));
        l.setTitle(desc);
        if (isEnabled) {
            l.setStyleName("quicknav-active-item");
            l.addStyleName("highlight-text");
            l.addClickHandler(new ClickHandler(){
                public void onClick(ClickEvent ev) {
                    if (Application.getInstance().getHomeRequest().equals(req)) {
                        Application.getInstance().goHome();
                    } else {
                        Application.getInstance().processRequest(req);
                    }
                }
            });
            l.addMouseOverHandler(new MouseOverHandler(){
                public void onMouseOver(MouseOverEvent event) {
                    l.setStyleName("quicknav-active-item-hover");
                    l.addStyleName("marked-text");
                }
            });

            l.addMouseOutHandler(new MouseOutHandler(){
                public void onMouseOut(MouseOutEvent event) {
                    l.setStyleName("quicknav-active-item");
                    l.addStyleName("highlight-text");
                }
            });

        } else {
            l.setStyleName("quicknav-item");
            l.addStyleName("highlight-text");
        }


        return l;
    }

    private String getDesc(Request req) {
        String desc = null;
        GeneralCommand cmd = Application.getInstance().getCommandTable().get(req.getCmdName());
        if (cmd != null && !StringUtils.isEmpty(cmd.getLabel())) {
            if (!cmd.getLabel().endsWith(".Title")) {
                desc = cmd.getLabel();
            }
        }
        if (StringUtils.isEmpty(desc)) {
            //work around for commands without label
            desc = StringUtils.isEmpty(req.getShortDesc()) ? "unknown" : req.getShortDesc();
        }
        return desc;
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
