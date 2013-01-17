package edu.caltech.ipac.firefly.visualize.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.caltech.ipac.firefly.core.Application;
import edu.caltech.ipac.firefly.data.Param;
import edu.caltech.ipac.firefly.ui.BaseDialog;
import edu.caltech.ipac.firefly.ui.ButtonType;
import edu.caltech.ipac.firefly.ui.GwtUtil;
import edu.caltech.ipac.firefly.ui.PopupPane;
import edu.caltech.ipac.firefly.ui.PopupUtil;
import edu.caltech.ipac.firefly.ui.input.SimpleInputField;
import edu.caltech.ipac.firefly.util.WebAppProperties;
import edu.caltech.ipac.firefly.util.WebClassProperties;
import edu.caltech.ipac.firefly.util.WebUtil;
import edu.caltech.ipac.firefly.util.event.Name;
import edu.caltech.ipac.firefly.util.event.WebEvent;
import edu.caltech.ipac.firefly.util.event.WebEventListener;
import edu.caltech.ipac.firefly.visualize.Band;
import edu.caltech.ipac.firefly.visualize.PlotState;
import edu.caltech.ipac.firefly.visualize.PrintableUtil;
import edu.caltech.ipac.firefly.visualize.ReplotDetails;
import edu.caltech.ipac.firefly.visualize.WebPlot;
import edu.caltech.ipac.firefly.visualize.WebPlotView;
import edu.caltech.ipac.util.dd.EnumFieldDef;


/**
 * User: roby
 * Date: Jan 26, 2009
 * Time: 3:45:42 PM
 */



/**
 * @author Trey Roby
 */
public class FitsDownloadOpsDialog extends BaseDialog {

    interface PFile extends WebAppProperties.PropFile { @Source("FitsDownloadOpsDialog.prop") TextResource get(); }
    private static final WebClassProperties _prop= new WebClassProperties(FitsDownloadOpsDialog.class, (PFile)GWT.create(PFile.class));

    private final WebPlotView _pv;
    private SimpleInputField _dType= SimpleInputField.createByProp(_prop.makeBase("dType"), new SimpleInputField.Config("75px"));
    private SimpleInputField _whichOp= null;
    private SimpleInputField _bandSelect= null;
    private boolean _showWhichOp = false;
    private FlowPanel _topPanel= new FlowPanel();


//======================================================================
//----------------------- Constructors ---------------------------------
//======================================================================

    public FitsDownloadOpsDialog(WebPlotView pv) {
        super(pv, ButtonType.OK, _prop.getTitle(),
              true, "visualization.fitsDownloadOptions");
        _pv= pv;
        WebPlot plot= _pv.getPrimaryPlot();
        Button ok= getButton(ButtonID.OK);
        ok.setText(_prop.getName("download"));

        plot.getPlotView().getEventManager().addListener(Name.REPLOT,
                                                         new ReplotListener(this) );
        createContents();

        _topPanel.setSize("225px", "200px");
        GwtUtil.setStyle(_topPanel, "borderBottom", "solid 1px");
        this.setWidget(_topPanel);
    }

//======================================================================
//----------------------- Public Methods -------------------------------
//======================================================================


    @Override
    public void setVisible(boolean v) {
        if (v) {
//            DeferredCommand.addCommand(new Command() {
//                public void execute() { updateNewPlot(); }
//            });
        }
        super.setVisible(v, PopupPane.Align.CENTER);
    }

    public static void showOptions(WebPlotView pv) {
        FitsDownloadOpsDialog dialog= new FitsDownloadOpsDialog(pv);
        dialog.setVisible(true);
    }


//======================================================================
//------------------ Private / Protected Methods -----------------------
//======================================================================



    private void updateOptionsVisible() {
        if (_bandSelect!=null) {
            WebPlot plot= _pv.getPrimaryPlot();
            _bandSelect.setVisible(plot.isThreeColor() && _dType.getValue().equals("fits"));
        }
        if (_whichOp!=null && _showWhichOp) {
            _whichOp.setVisible(_dType.getValue().equals("fits"));
        }
    }





    private void createContents() {
        WebPlot plot= _pv.getPrimaryPlot();


        VerticalPanel panel= new VerticalPanel();
        panel.add(_dType);
        _dType.getField().addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                updateOptionsVisible();
            }
        });

        if (plot.isThreeColor()) {
            EnumFieldDef fd= new EnumFieldDef();
            fd.setMask("[RADIO]");
            fd.setName("FitsDownloadOpsDialog.whichColor");
            fd.setMaxWidth(100);
            fd.setOrientation(EnumFieldDef.Orientation.Vertical);
            fd.setLabel(_prop.getName("cband"));
            boolean first= true;
            for(Band band : plot.getBands()) {
                fd.addItem(band.toString(), band.toString());
                if (first)  {
                    fd.setDefaultValue(band.toString());
                    first= false;
                }

            }
            _bandSelect= SimpleInputField.createByDef(fd, new SimpleInputField.Config("75px"));
            panel.add(_bandSelect);
        }


        PlotState state= plot.getPlotState();

        _showWhichOp = state.hasOperation(PlotState.Operation.CROP) && !state.hasOperation(PlotState.Operation.ROTATE);
        _whichOp= SimpleInputField.createByProp(_prop.makeBase("whichOp"), new SimpleInputField.Config("75px"));
        panel.add(_whichOp);
        _whichOp.setVisible(_showWhichOp);



        _topPanel.add(panel);
        updateOptionsVisible();
    }




    @Override
    protected void inputComplete() {
        Frame f= Application.getInstance().getNullFrame();
        WebPlot plot= _pv.getPrimaryPlot();
        PlotState state= plot.getPlotState();
        String url;
        if (_dType.getValue().equals("fits")) {
            Band band= Band.NO_BAND;
            if (_bandSelect!=null) {
                band=  Band.parse(_bandSelect.getValue());
            }
            String fitsFile= state.getOriginalFitsFileStr(band)==null  || _whichOp.getValue().equals("modified")?
                             state.getWorkingFitsFileStr(band) :
                             state.getOriginalFitsFileStr(band);

            url= WebUtil.encodeUrl(GWT.getModuleBaseURL()+ "servlet/Download",
                                          new Param("file", fitsFile),
                                          new Param("log", "true"));
            if (url!=null) f.setUrl(url);
        }
        else {
//            url= createImageUrl(plot);
            retrievePng(plot);
        }

    }


//    public static String createImageUrl(WebPlot plot) {
//        Param[] params= new Param[] {
////                new Param("ctx", plot.getPlotState().getContextString()),
//                new Param("state", plot.getPlotState().toString()),
//                new Param("type", "full"),
//        };
//        return WebUtil.encodeUrl(GWT.getModuleBaseURL()+ "sticky/FireFly_ImageDownload", params);
//    }

    private void retrievePng(WebPlot plot) {
        PrintableUtil.createPrintableImage(plot, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                PopupUtil.showError("Could not retrieve png", "Could not retrieve png");
            }

            public void onSuccess(String fname) {
                String url = WebUtil.encodeUrl(GWT.getModuleBaseURL() + "servlet/Download",
                                               new Param("file", fname));
                Application.getInstance().getNullFrame().setUrl(url);
            }
        });
    }



// =====================================================================
// -------------------- Inner Classes ----------------------------------
// =====================================================================



    /**
     * making this class static and passing a parameter makes code splitting happen better
     */
    private static class ReplotListener implements WebEventListener {

        private FitsDownloadOpsDialog _dialog;

        ReplotListener(FitsDownloadOpsDialog dialog) { _dialog= dialog; }

        public void eventNotify(WebEvent ev) {
            ReplotDetails details= (ReplotDetails)ev.getData();
            if (details.getReplotReason()== ReplotDetails.Reason.IMAGE_RELOADED) {
                if (_dialog.isVisible()) _dialog.setVisible(false);
            }
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
 * A PARTICULAR USE OR PURPOSE (AS SET FORTH IN UNITED STATES UCC 2312- 2313)
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
