package edu.caltech.ipac.firefly.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import edu.caltech.ipac.firefly.util.BrowserUtil;

import java.util.Iterator;

/**
 * Date: Oct 29, 2009
*
* @author loi
* @version $Id: TitlePanel.java,v 1.9 2010/11/05 22:44:36 roby Exp $
*/
public class TitlePanel extends Composite implements HasWidgets, RequiresResize {

    private Label titleLabel = new Label();
    private Widget body;
    private HorizontalPanel titleBar = new HorizontalPanel();
    private HorizontalPanel titleWrapper;
    private DockLayoutPanel dlp;

    public TitlePanel(String title, Widget body) {
        this(title,body,true);
    }

    public TitlePanel(String title, Widget body, boolean showTitle) {
        titleWrapper = new HorizontalPanel();
        makeTitleLabel(titleWrapper);
        titleWrapper.add(titleLabel);
        titleWrapper.add(titleBar);
        titleWrapper.add(GwtUtil.getFiller(10, 1));
        titleWrapper.setCellWidth(titleBar, "100%");
        titleWrapper.setCellHorizontalAlignment(titleBar, HorizontalPanel.ALIGN_RIGHT);

        dlp = new DockLayoutPanel(Style.Unit.PX);
        if (showTitle) {
            dlp.addNorth(titleWrapper, 23);
            dlp.addNorth(GwtUtil.getFiller(1,2), 2);
        }
        dlp.add(body);
        dlp.setSize("100%", "100%");
//
//
//        FlowPanel vp = new FlowPanel();
//        vp.add(titleWrapper);
//        vp.add(body);
//        vp.setSize("100%", "100%");
        initWidget(dlp);
        this.body = body;

//        dlp.setStyleName("standard-border");
        setTitle(title);
        if (BrowserUtil.isIE()) {
            DOM.setStyleAttribute(titleLabel.getElement(), "whiteSpace", "nowrap");
        }
    }

    public void add(Widget w) {
        throw new IllegalArgumentException("add not available, all widgets set in constructor");
    }

    public void clear() {
        throw new IllegalArgumentException("clear not available");
    }

    public Iterator<Widget> iterator() { return dlp.iterator();  }

    public boolean remove(Widget w) {
        throw new IllegalArgumentException("remove not available");
    }

    @Override
    protected void onLoad() {
        DOM.setStyleAttribute(body.getElement(), "right", "1px");
        DOM.setStyleAttribute(body.getElement(), "bottom", "1px");
    }

    public int getTitleBarHeight() {
        return GwtUtil.getElementHeight(titleWrapper);
    }

    public void addToTitle(Widget w) {
        titleBar.add(w);
    }

    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

//    public void onResize(int width, int height) {
//        if (body instanceof ResizableWidget) {
//            height-= GwtUtil.getElementHeight(titleLabel);
//            ((ResizableWidget)body).onResize(width-7,height);
//        }
//    }

    private void makeTitleLabel(Widget w) {
        w.setStyleName("title-bar");
        w.addStyleName("title-bg-color");
        w.addStyleName("title-color");
        w.addStyleName("title-font-family");
    }


    public void onResize() {
        if (body instanceof RequiresResize) {
            ((RequiresResize)body).onResize();
        }

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
