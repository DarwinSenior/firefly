/*
 * License information at https://github.com/Caltech-IPAC/firefly/blob/master/License.txt
 */
package edu.caltech.ipac.firefly.core.layout;

import edu.caltech.ipac.firefly.core.Application;
import edu.caltech.ipac.firefly.ui.table.EventHub;
import edu.caltech.ipac.firefly.ui.table.TablePanel;
import edu.caltech.ipac.firefly.ui.GwtUtil;
import edu.caltech.ipac.firefly.util.event.WebEventListener;
import edu.caltech.ipac.firefly.util.event.WebEvent;
import edu.caltech.ipac.firefly.util.event.Name;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Date: Jan 17, 2012
 *
 * @author loi
 * @version $Id: LayoutSelector.java,v 1.6 2012/08/03 03:46:51 tatianag Exp $
 */
public class LayoutSelector extends Composite {

    private SimplePanel optionsWrapper = new SimplePanel();
    private EventHub hub;
    private Name selView = null;

    public LayoutSelector() {
        HorizontalPanel fp = new HorizontalPanel();
        fp.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
        Label lbl = new Label("View Options:");
        lbl.addStyleName("result-title");
        fp.add(lbl);
        fp.add(optionsWrapper);
        initWidget(fp);
        setVisible(false);
    }

    public void setHub(EventHub hub) {
        this.hub = hub;
//        selView = null;
        hub.getEventManager().addListener(EventHub.ON_TABLE_SHOW, new WebEventListener() {
            public void eventNotify(WebEvent ev) {
                layout();
            }
        });

        hub.getEventManager().addListener(EventHub.ON_TABLE_HIDE, new WebEventListener() {
            public void eventNotify(WebEvent ev) {
                setVisible(false);
            }
        });
    }

    public void layout() {
        TablePanel table = hub.getActiveTable();
        optionsWrapper.clear();
        if (table == null) {
            return;
        }

        selView = table.getActiveView();
        if (selView == null) {
            if (getFirstVisibleView(table) != null) {
                selView = getFirstVisibleView(table).getName();
            }
        }

        HorizontalPanel options = new HorizontalPanel();
        List<TablePanel.View> views = table.getVisibleViews();
        for (TablePanel.View v : views) {
            options.add(GwtUtil.getFiller(5, 0));
            options.add(makeImage(v));
        }
        options.add(GwtUtil.getFiller(10, 0));
        optionsWrapper.setWidget(options);
        LayoutSelector loSel = Application.getInstance().getLayoutManager().getLayoutSelector();
        if (loSel != null) {
            if (views.size() > 1) {
                setVisible(true);
            } else {
                setVisible(false);
            }
        }
    }

    private Widget makeImage(final TablePanel.View v) {
        Image img = new Image(v.getIcon());
        img.setSize("24px", "24px");
        if (v.getName().equals(selView)) {
            img.addStyleName("selected-view");
            return img;
        } else {
            Widget w = GwtUtil.makeImageButton(img, v.getShortDesc(), new ClickHandler() {
                public void onClick(ClickEvent event) {
                    selView = v.getName();
                    TablePanel table = hub.getActiveTable();
                    table.switchView(selView);
                    layout();
                }
            });
            w.addStyleName("selectable-view");
            return w;
        }
    }

    private TablePanel.View getFirstVisibleView(TablePanel table) {
        List<TablePanel.View> views = table.getViews();
        for (TablePanel.View v : views) {
            if (!v.isHidden()) {
                return v;
            }
        }
        return null;
    }

}
