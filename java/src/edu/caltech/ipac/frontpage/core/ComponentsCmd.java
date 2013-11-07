package edu.caltech.ipac.frontpage.core;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.caltech.ipac.firefly.core.JsonUtils;
import edu.caltech.ipac.firefly.core.RequestCmd;
import edu.caltech.ipac.firefly.data.Param;
import edu.caltech.ipac.firefly.data.Request;
import edu.caltech.ipac.firefly.data.ServerParams;
import edu.caltech.ipac.firefly.ui.GwtUtil;
import edu.caltech.ipac.frontpage.data.DisplayData;
import edu.caltech.ipac.frontpage.ui.DataSetPanel;
import edu.caltech.ipac.frontpage.ui.FeaturePager;
import edu.caltech.ipac.frontpage.ui.ToolbarPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;



public class ComponentsCmd extends RequestCmd {

    public  static final String COMMAND = "ComponentsCmd";

    public ComponentsCmd() {
        super(COMMAND, "Components", "Components", true);
    }

    protected void doExecute(final Request req, AsyncCallback<String> callback) {
        //todo: get the parts off the of the web page and layout


        DataSetPanel dsP= new DataSetPanel("frontpageDataSetDisplay");


        List<Param> paramList = new ArrayList<Param>(8);
        paramList.add(new Param(ServerParams.FNAME, "${irsa-root-dir}/irsa-menu.js"));

        try {
            JsonUtils.jsonRequest(ServerParams.STATIC_JSON_DATA, paramList, new RequestCallback() {
                public void onResponseReceived(com.google.gwt.http.client.Request request, Response response) {
                    try {
                        String jsText= response.getText();
                        loadMenuBar(changeToJS(jsText));
                    } catch (Exception e) {
                        GwtUtil.getClientLogger().log(Level.INFO, "error on Rcb",e);
                    }
                }

                public void onError(com.google.gwt.http.client.Request request, Throwable e) {
                    GwtUtil.getClientLogger().log(Level.INFO, "error on Rcb", e);
                }
            });
        } catch (RequestException e) {
            // todo
            GwtUtil.getClientLogger().log(Level.INFO, "error load json data");
        }



        paramList = new ArrayList<Param>(8);
        paramList.add(new Param(ServerParams.FNAME, "${irsa-root-dir}/feature.js"));
        try {
            JsonUtils.jsonRequest(ServerParams.STATIC_JSON_DATA, paramList, new RequestCallback() {
                public void onResponseReceived(com.google.gwt.http.client.Request request, Response response) {
                    try {
                        String jsText= response.getText();
                        loadFeature(changeToJS(jsText));
                    } catch (Exception e) {
                        GwtUtil.getClientLogger().log(Level.INFO, "error on Rcb",e);
                    }
                }

                public void onError(com.google.gwt.http.client.Request request, Throwable e) {
                    GwtUtil.getClientLogger().log(Level.INFO, "error on Rcb",e);
                }
            });
        } catch (RequestException e) {
            // todo
            GwtUtil.getClientLogger().log(Level.INFO, "error load json data");
        }

    }

    private void loadMenuBar(JsArray<DisplayData> dataAry) {
        new ToolbarPanel("frontpageMainPageToolbar", dataAry);
    }

    private void loadFeature(JsArray<DisplayData> dataAry) {
        new FeaturePager("frontpageFeaturePager", dataAry);
    }


    private static native JsArray<DisplayData> changeToJS(String arg) /*-{
        return eval('('+arg+')');
    }-*/;

}
