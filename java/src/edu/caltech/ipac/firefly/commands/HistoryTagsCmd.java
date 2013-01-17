package edu.caltech.ipac.firefly.commands;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import edu.caltech.ipac.firefly.core.*;
import edu.caltech.ipac.firefly.core.layout.LayoutManager;
import edu.caltech.ipac.firefly.data.Request;
import edu.caltech.ipac.firefly.data.TableServerRequest;
import edu.caltech.ipac.firefly.data.table.TableData;
import edu.caltech.ipac.firefly.data.table.TableDataView;
import edu.caltech.ipac.firefly.rpc.UserServices;
import edu.caltech.ipac.firefly.ui.Form;
import edu.caltech.ipac.firefly.ui.GwtUtil;
import edu.caltech.ipac.firefly.ui.PopupUtil;
import edu.caltech.ipac.firefly.ui.input.SimpleInputField;
import edu.caltech.ipac.firefly.ui.table.TabPane;
import edu.caltech.ipac.firefly.ui.table.TablePanel;
import edu.caltech.ipac.firefly.ui.table.builder.BaseTableConfig;
import edu.caltech.ipac.firefly.util.event.WebEvent;
import edu.caltech.ipac.firefly.util.event.WebEventListener;


/**
 * User: balandra
 * Date: Oct 9, 2009
 * Time: 11:54:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryTagsCmd extends RequestCmd {
    public static final String COMMAND_NAME = "HistoryTags";
    public static final String TAG = "Tags";
    public static final String HISTORY = "History";

    private static final String COL_ID_KEY = "queryid";
    private static final String COL_FAV_KEY = "favorite";
    private static final String COL_DESC_KEY = "description";

    private static GeneralCommand markFavorite;
    private static boolean isFav;
    private TablePanel histTable;
    private TablePanel tagTable;

    public HistoryTagsCmd(){
        super(COMMAND_NAME);
    }

    @Override
    protected void doExecute(Request req, AsyncCallback<String> callback) {
//        req.setDoSearch(true);
        processRequest(req, callback);
    }

    protected Form createForm() {
        return null;
    }

    protected void processRequest(Request req, AsyncCallback<String> callback) {

        final SimpleInputField tagName = SimpleInputField.createByProp("HistoryTags.tagname");

        Button tagIt = new Button("Tag It", new ClickHandler(){
            public void onClick(ClickEvent event) {
                if (Application.getInstance().hasSearchResult()) {
                    if (tagName.validate()) {
                        TagCmd.TagItCmd.doTag(tagName.getValue(), new BaseCallback(){
                                    public void doSuccess(Object result) {
                                        tagTable.gotoPage(1);
                                    }
                                });
                    }
                } else {
                    PopupUtil.showInfo(null, "Tag Current Search",
                                       "No search results to tag.");
                }
            }
        });


        final HorizontalPanel hp = new HorizontalPanel();
        hp.add(tagName);
        hp.add(tagIt);
        boolean taggingPossible = false;
        Request currentSearchRequest = Application.getInstance().getRequestHandler().getCurrentSearchRequest();
        if (Application.getInstance().hasSearchResult() && currentSearchRequest != null) {
            final GeneralCommand cmd = Application.getInstance().getCommand(currentSearchRequest.getCmdName());
            if (cmd instanceof RequestCmd && ((RequestCmd)cmd).isTagSupported(currentSearchRequest)) {
                taggingPossible = true;
            }
        }
        hp.setVisible(taggingPossible);
        final TabPane<TablePanel> tabPane = new TabPane<TablePanel>();
        tabPane.setHeight("400px");
        tabPane.setHelpId("basics.history");

//        tab = new TableGroupPreviewCombo(null);

//        tab.setTabGroupTitle("Search History and Tags");
        histTable = new TagHistoryConfig(false).createAndLoadTable();
        tagTable = new TagHistoryConfig(true).createAndLoadTable();
        tabPane.addTab(histTable, "History");
        tabPane.addTab(tagTable, "Tags");

//        TitlePanel ttp = tab.getTableTitlePanel();
//        ttp.addToTitle(HelpManager.makeHelpIcon("results.tagging", false));

//        onComplete(1);
//        setResults(tab);

        FlowPanel fp = new FlowPanel();
        fp.add(hp);
        fp.add(tabPane);
        GwtUtil.setStyle(fp, "margin", "5px");
        registerView(LayoutManager.DROPDOWN_REGION, fp);
        callback.onSuccess("");

    }

    private static void setupToolbar(final TablePanel table, final boolean isTags) {
        GeneralCommand remove = new GeneralCommand("remove", "Remove", "Remove selected search from table", true) {
            protected void doExecute() {
                final int page = table.getTable().getCurrentPage();
                TableData.Row[] row =  table.getTable().getHighlightRows();
                if (row != null && row.length > 0) {
                    AsyncCallback callback = new AsyncCallback() {
                        public void onFailure(Throwable caught) {
                        }

                        public void onSuccess(Object result) {
                            table.gotoPage(page);
                        }
                    };

                    if(isTags){
                        String tagname = String.valueOf(row[0].getValue("tagname"));
                        UserServices.App.getInstance().removeTag(tagname, callback);
                    } else {
                        int sIdx = Integer.parseInt(row[0].getValue("queryid").toString());
                         UserServices.App.getInstance().removeSearch(new int[]{sIdx}, callback);
                    }

                }
            }
        };

        GeneralCommand resubmit = new GeneralCommand("resubmit", "Resubmit Search", "Resubmit the selected search", true) {
            protected void doExecute() {
                resubmitSearch(table, isTags);
            }
        };

        table.clearToolButtons(true, false, true);
        table.addToolButton(resubmit, false);
        table.addToolButton(remove, false);

        if (!isTags) {
            markFavorite = new GeneralCommand("mAsFav", "Mark as Favorite", "Mark/Unmark selected search as favorite", true) {
                protected void doExecute() {
                    TableData.Row[] row = table.getTable().getHighlightRows();
                    String str = String.valueOf(row[0].getValue("description"));
                    if (!String.valueOf(row[0].getValue(1)).equals("yes")) {
                        PopupUtil.showInputDialog(null, "Enter a description for this favorite:", str, 100, new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                isFav = true;
                                doUpdate(String.valueOf(event.getSource()), table);
                            }
                        }, null);
                    } else {
                        isFav = false;
                        doUpdate(str, table);
                    }
                }
            };
            table.addToolButton(markFavorite, false);
        }

    }

    private static void resubmitSearch(TablePanel table,boolean isTags){
        TableData.Row[] row =  table.getTable().getHighlightRows();
        String str;
        if(isTags){
            str = row[0].getValue(2).toString();
        } else {
            str = row[0].getValue(4).toString();
        }

        Application.getInstance().processRequest(Request.parse(str));
    }

    private static void doUpdate(final String desc, final TablePanel table){
        final TableData.Row[] row =  table.getTable().getHighlightRows();
        if (row != null && row.length > 0) {
            AsyncCallback callback = new AsyncCallback() {
                public void onFailure(Throwable caught) {
                    PopupUtil.showError("System Error",
                            "Unexpected error while updating history.  If this problem persist, contact tech support for help.");
                }
                public void onSuccess(Object result) {
                    row[0].setValue(COL_FAV_KEY, isFav ? "yes" : "no");
                    row[0].setValue(COL_DESC_KEY, desc);
                    table.getTable().reloadPage();
                }
            };
            String str = String.valueOf(row[0].getValue(COL_ID_KEY));
            Integer id = Integer.valueOf(str);
            UserServices.App.getInstance().updateSearchHistory(id,isFav,desc,callback);
        }

    }

//====================================================================
//
//====================================================================
    static class TagHistoryConfig extends BaseTableConfig<TableServerRequest> {
        private static String HIST = "searchHistory";
        private static String TAGS = "tags";
        //private static String HIST = SearchType.SEARCH_HISTORY.getRequestId();
        //private static String TAGS = SearchType.TAGS.getRequestId();
        private boolean isTags;

        public TagHistoryConfig(boolean isTags) {
            super(new TableServerRequest(isTags ? TAGS : HIST), isTags ? "Tags" : "Search History",
                            isTags ? "Tags" : "Search History", null, "", "");
            this.isTags = isTags;
        }

        public TablePanel createAndLoadTable() {
            final TablePanel table = new TablePanel(getTitle(), getLoader());
            table.getEventManager().addListener(TablePanel.ON_INIT, new WebEventListener() {
                public void eventNotify(WebEvent ev) {
                    setupToolbar(table, isTags);
                    ensureFavButton(table);
                    addDoubleClick(table, isTags);
                }
            });
            if (!isTags) {
                table.getEventManager().addListener(TablePanel.ON_ROWHIGHLIGHT_CHANGE, new WebEventListener() {
                    public void eventNotify(WebEvent ev) {
                        ensureFavButton(table);
                    }
                });
            }
            table.init();
            return table;
        }

        private void addDoubleClick(final TablePanel table, final boolean isTags) {
            DoubleClickHandler dch = new DoubleClickHandler() {
                public void onDoubleClick(DoubleClickEvent event) {
                    resubmitSearch(table, isTags);
                }
            };
            table.addDoubleClickListner(dch);
        }

        private void ensureFavButton(TablePanel table) {
            if (markFavorite != null) {
                TableData.Row[] row = table.getTable().getHighlightRows();
                if (row.length > 0) {
                    if (!String.valueOf(row[0].getValue(1)).equals("yes")) {
                        markFavorite.setLabel("Mark Favorite");
                    } else {
                        markFavorite.setLabel("Unmark Favorite");
                    }
                }
            }
        }

        @Override
        public void onLoad(TableDataView data) {
            if (getSearchRequest().getRequestId().endsWith(TAGS)) {
                for (TableDataView.Column c : data.getColumns()) {
                    if (c.getName().equals("tagname")) {
                        c.setWidth(30);
                        c.setTitle("Tag Name");
                    } else if (c.getName().equals("timecreated")) {
                        c.setWidth(15);
                        c.setTitle("Created");
                    } else if (c.getName().equals("description")) {
                        c.setWidth(200);
                        c.setTitle("Description");
                    } else {
                        c.setHidden(true);
                        c.setVisible(false);
                    }
                }
            } else {
                for (TableDataView.Column c : data.getColumns()) {
                    if (c.getName().equals("favorite")) {
                        c.setWidth(7);
                        c.setTitle("Favorite");
                    } else if (c.getName().equals("timeadded")) {
                        c.setWidth(15);
                        c.setTitle("Created");
                    } else if (c.getName().equals("description")) {
                        c.setWidth(200);
                        c.setTitle("Description");
                    } else {
                        c.setHidden(true);
                        c.setVisible(false);
                    }
                }
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
