package edu.caltech.ipac.heritage.commands;

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.caltech.ipac.firefly.core.Application;
import edu.caltech.ipac.firefly.data.MOSRequest;
import edu.caltech.ipac.firefly.data.Param;
import edu.caltech.ipac.firefly.data.Request;
import edu.caltech.ipac.firefly.data.ServerRequest;
import edu.caltech.ipac.firefly.ui.Form;
import edu.caltech.ipac.firefly.ui.MOSPanel;
import edu.caltech.ipac.firefly.ui.NaifTargetPanel;
import edu.caltech.ipac.firefly.ui.creator.CommonParams;
import edu.caltech.ipac.firefly.ui.creator.ImageGridViewCreator;
import edu.caltech.ipac.firefly.ui.creator.WidgetFactory;
import edu.caltech.ipac.firefly.ui.creator.eventworker.DrawingLayerCreator;
import edu.caltech.ipac.firefly.ui.creator.eventworker.EventWorker;
import edu.caltech.ipac.firefly.ui.table.EventHub;
import edu.caltech.ipac.firefly.ui.table.TablePanel;
import edu.caltech.ipac.firefly.ui.table.builder.BaseTableConfig;
import edu.caltech.ipac.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tatianag
 */
public class SearchMOSCmd extends HeritageRequestCmd {

    public static final String COMMAND_NAME = "MOSQuery";
    MOSPanel mosPanel;

    public SearchMOSCmd() {
        super(COMMAND_NAME);
    }

    @Override
    protected Form createForm() {
        mosPanel = new MOSPanel();
        Form form = new Form();
        form.setHelpId("searching.byPosition");
        form.add(mosPanel);
        form.setFocus(NaifTargetPanel.NAIF_NAME_KEY);
        return form;

    }

    @Override
    protected void processRequest(Request req, AsyncCallback<String> callback) {
        createTablePreviewDisplay(true);
        EventHub hub= getResultsPanel().getPreview().getEventHub();


        /* from WISE
        <EventWorker id="movingTrackMOS" type="DataSetVisQuery">
        <QueryId>wiseMOSQuery_1b</QueryId>
        <Param key="searchProcessorId" value="WiseMOSQuery"/>
        <Param key="ExtraParams" value="queryId=wiseMOSQuery_1b"/>
        <Param key="Events" value="SearchResultEnd"/>
        <Param key="UniqueKeyColumns" value="frame_num,scan_id"/>

        <Param key="Type" value="MatchedPoint"/>
        <Param key="COLOR" value="orange"/>
        <Param key="SYMBOL" value="X"/>
        <!--<Param key="MatchColor" value="red"/>-->
        <Param key="Title" value="Observed Images"/>
        <Param key="Selection" value="True"/>
        </EventWorker>
         */
        // add event workers
        Map<String,String> params=
                StringUtils.createStringMap(
                        EventWorker.QUERY_SOURCE, "shaGridMOSQuery",
                        EventWorker.ID, "movingTrackMOS",
                        CommonParams.SEARCH_PROCESSOR_ID, "MOSQuery",
                        CommonParams.ENABLE_DEFAULT_COLUMNS, "True",
                        CommonParams.UNIQUE_KEY_COLUMNS, "bcdid",
                        "Events", "SearchResultEnd",
                        CommonParams.TYPE,  "MatchedPoint",
                        CommonParams.COLOR, "orange",
                        CommonParams.SYMBOL, "X",
                        CommonParams.TITLE, "Observed Images",
                        "Selection", "True");

        WidgetFactory factory= Application.getInstance().getWidgetFactory();
        EventWorker ew = factory.createEventWorker(DrawingLayerCreator.DATASET_VIS_QUERY, params);
        hub.bind(ew);
        ew.bind(hub);

        req.setRequestId("MOSQuery");
        req.setParam(MOSRequest.CATALOG, "spitzer_bcd");
        BaseTableConfig config = new BaseTableConfig(req, "Precovery", "Precovery search results");
        TablePanel tablePanel = addTable(config);

        //add image grid view
        List<Param> paramList = req.getParams();
        params = new HashMap<String,String>();
        for (Param p : paramList) {
            if (!p.getName().equals(ServerRequest.ID_KEY)) {
                params.put(p.getName(), p.getValue());
            }
        }
        params.put(ServerRequest.ID_KEY, "shaGridMOSQuery");
        params.put("Index", "-1"); // first view to display
        params.put(CommonParams.SEARCH_PROCESSOR_ID, "shaGridMOSQuery");
        params.put(CommonParams.PAGE_SIZE, "15");
        params.put(CommonParams.LOCK_RELATED, "true");
        params.put(CommonParams.PLOT_EVENT_WORKERS, "movingTrackMOS");

        TablePanel.View imageGridView = new ImageGridViewCreator().create(params);
        tablePanel.addView(imageGridView);
        imageGridView.bind(hub);


        loadAll();
        setResults(getResultsPanel());
        //NewTableResults tr = new NewTableResults(req, WidgetFactory.TABLE, "Precovery search");
        //WebEventManager.getAppEvManager().fireEvent(new WebEvent<NewTableResults>(this, Name.NEW_TABLE_RETRIEVED, tr));

    }
}
