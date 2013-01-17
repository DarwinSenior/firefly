package edu.caltech.ipac.firefly.ui.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.CellRenderer;
import com.google.gwt.gen2.table.client.ColumnDefinition;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.MutableTableModel;
import com.google.gwt.gen2.table.event.client.PageLoadEvent;
import com.google.gwt.gen2.table.event.client.RowCountChangeEvent;
import com.google.gwt.gen2.table.event.client.RowCountChangeHandler;
import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import edu.caltech.ipac.firefly.data.table.BaseTableColumn;
import edu.caltech.ipac.firefly.data.table.SelectionInfo;
import edu.caltech.ipac.firefly.data.table.TableData;
import edu.caltech.ipac.firefly.data.table.TableDataView;
import edu.caltech.ipac.firefly.ui.table.renderer.AlignRenderer;
import edu.caltech.ipac.firefly.util.ListenerSupport;
import edu.caltech.ipac.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

/**
 * An customized version of the {@link com.google.gwt.gen2.table.client.PagingScrollTable} that updated the header and footer tables to reflect the
 * currently visible rows.
 */
public class SelectionTable extends BasicPagingTable {

    /**
     * The previous list of visible column definitions.
     */
    private List<ColumnDefinition<TableData.Row, ?>> lastColDefs = null;

    /**
     * The {@link com.google.gwt.user.client.ui.CheckBox} used to select all rows.
     */
    private CheckBox selectAllCheckBox = new CheckBox();
    private SelectionInfo selectInfo = new SelectionInfo();
    private ListenerSupport<SelectListener> listeners = new ListenerSupport<SelectListener>();
    private int totalRows;
    private int lastPageSize;


    /**
     * Construct a new {@link SelectionTable}.
     *
     * @param tableModel      the underlying table model
     * @param tableDataView the column definitions
     */
    public SelectionTable(String name, MutableTableModel<TableData.Row> tableModel,
                   TableDataView tableDataView) {
        super(name, tableModel, new DataTable(), new SelectionTableDef(tableDataView));
        ((DataTable)getDataTable()).setTable(this);
        totalRows = tableDataView.getTotalRows();
        // Setup the selectAll checkbox
        selectAllCheckBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent ev) {
                if (selectAllCheckBox.getValue() && selectInfo.getSelectedCount() == 0) {
                    selectAll();
                } else {
                    deselectAll();
                }
            }
        });

        selectInfo.setRowCount(getTableModel().getRowCount());
        this.getTableModel().addRowCountChangeHandler(new RowCountChangeHandler(){
                    public void onRowCountChange(RowCountChangeEvent event) {
                        selectInfo.setRowCount(event.getNewRowCount());
                    }
                });
    }

//====================================================================
//  selection support
//====================================================================
    public void addSelectionTableListener(SelectListener listener) {
        listeners.addListener(listener);
    }

    public boolean removeSelectionTableListener(SelectListener listener) {
        return listeners.removeListener(listener);
    }

    public void select(Integer... rowIdx) {
        if (rowIdx != null && rowIdx.length > 0) {
            for(int i : rowIdx) {
                int dataIdx = getAbsoluteFirstRowIndex() + i;
                selectInfo.select(dataIdx);
                setSelected(i, true);
            }
            fireSelectedEvent();
        }
    }

    public boolean isSelected(int rowIdx) {
        return selectInfo.isSelected(rowIdx + getAbsoluteFirstRowIndex());
    }

    public boolean isSelectAll() {
        return selectInfo.isSelectAll();
    }

    public void selectAll() {
        selectInfo.selectAll();
        boolean singlePage = this.getRowCount() == totalRows;
        ArrayList<Integer> noaccessRows = singlePage ? new ArrayList<Integer>() : null;

        for(int i = 0; i < getRowCount(); i++) {
            TableData.Row row = getRowValue(i);
            setSelected(i, true);

            if (singlePage && !row.hasAccess()) {
                noaccessRows.add(i);
            }
        }
        if (noaccessRows != null && noaccessRows.size() > 0) {
            // if it's single page.. unselect the rows without access.
            deselect(noaccessRows.toArray(new Integer[noaccessRows.size()]));
        }
        fireSelectedEvent();
    }

    public void deselect(final Integer... rowIdx) {
        if (rowIdx != null && rowIdx.length > 0) {
            for(int i : rowIdx) {
                int dataIdx = getAbsoluteFirstRowIndex() + i;
                selectInfo.deselect(dataIdx);
                setSelected(i, false);
            }
            fireSelectedEvent();
        }
    }

    public SortedSet<Integer> getSelectedRows() {
        return selectInfo.getSelected();
    }

    public void deselectAll() {
        selectInfo.deselectAll();
        for(int i = 0; i <= getRowCount(); i++) {
            setSelected(i, false);
        }
        fireSelectedEvent();
    }

        SelectionInfo getSelectInfo() {
        return selectInfo;
    }

    void setSelectionInfo(SelectionInfo selInfo) {
        selectInfo = selInfo;
        for (int i = 0; i <= getRowCount(); i++) {
            setSelected(i, isSelected(i));
        }
        fireSelectedEvent();
    }

    //====================================================================
//
//====================================================================

    static interface SelectListener {
        void onRowSelectChange(SelectionTable table, SelectionInfo selectedIndices);
    }

//====================================================================
//
//====================================================================

    protected void fireSelectedEvent() {
        selectAllCheckBox.setValue(isSelectAll());
        listeners.fireEvent(new ListenerSupport.Function<SelectListener>(){
                public void execute(SelectListener sl) {
                    sl.onRowSelectChange(SelectionTable.this, selectInfo);
                }
            });
    }


    private static String getCheckboxHtml(Object value) {
        boolean selected = Boolean.parseBoolean(String.valueOf(value));
        return "<input type='checkbox' " + (selected ? "checked " : "") + "/>";
    }

    private void setSelected(int viewIdx, boolean isSelected) {

        TableData.Row row = getRowValue(viewIdx);
        if (row != null) {
            boolean hasAccess = row.hasAccess();
            getDataTable().setHTML(viewIdx, 0, hasAccess ? getCheckboxHtml(isSelected) : "");
        }
    }

    boolean fillWidthPending;
    @Override
    protected void onDataTableRendered() {

        if (getPageSize() > 200) {
            // large page size:  do fast rendering
            if (!fillWidthPending && isAttached()
                && getResizePolicy() == ResizePolicy.FILL_WIDTH) {
              fillWidthPending = true;
              DeferredCommand.addCommand(new Command() {
                public void execute() {
                  fillWidthPending = false;
                  fillWidth();
                }
              });
            }
            resizeTablesVertically();
            fireEvent(new PageLoadEvent(getCurrentPage()));
        } else {
            super.onDataTableRendered();
        }
    }

    @Override
    protected void setData(int firstRow, Iterator<TableData.Row> rows) {

        ArrayList<TableData.Row> cloneRows = new ArrayList<TableData.Row>();
        for(int idx = 0; rows.hasNext(); idx++) {
            TableData.Row row = rows.next();
            cloneRows.add(row);
            row.setValue(SelectionTableDef.SELECTED, String.valueOf(selectInfo.isSelected(firstRow+idx)));
        }

        // Set the actual data
        super.setData(firstRow, cloneRows.iterator());

        // Get the visible column definitions
        List<ColumnDefinition<TableData.Row, ?>> colDefs = getTableDefinition().getVisibleColumnDefinitions();
        updateHeaderTable(colDefs);

        if (lastPageSize != this.getPageSize()) {
            lastPageSize = this.getPageSize();
            recalculateSelectAllBox();
        }
    }

    /**
     * Update the header table to match the data table.
     */
    protected void updateHeaderTable(List<ColumnDefinition<TableData.Row, ?>> colDefs) {
        super.updateHeaderTable(colDefs);

        setColumnWidth(0, colDefs.get(0).getPreferredColumnWidth());
        recalculateSelectAllBox();
    }

    private void recalculateSelectAllBox() {

        boolean hasAccess = true;
        if (this.getRowCount() == totalRows) {
            hasAccess = false;
            for (TableData.Row r : this.getRowValues()) {
                if (r.hasAccess()) {
                    hasAccess = true;
                    break;
                }
            }
        }
        Widget box = hasAccess? selectAllCheckBox : new SimplePanel();

        FlexTable.FlexCellFormatter formatter = getHeaderTable().getFlexCellFormatter();
        getHeaderTable().setWidget(0, 0, box);
        formatter.setHorizontalAlignment(0, 0,
                HasHorizontalAlignment.ALIGN_CENTER);
    }

//====================================================================
//
//====================================================================

    @SuppressWarnings("deprecation")
    public static class DataTable extends BasicPagingTable.DataTable {
        private SelectionTable table;

        public DataTable() {}

        void setTable(SelectionTable table) {
            this.table = table;
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);

            switch (DOM.eventGetType(event)) {

                // Prevent native inputs from being checked
                case Event.ONCLICK: {
                    // Get the target row
                    Element targetCell = getEventTargetCell(event);
                    if (targetCell == null) {
                        return;
                    }

                    if ( StringUtils.isEmpty(targetCell.getInnerHTML()) ) {
                        return;         // ignore cell without a checkbox
                    }
                    Element targetRow = DOM.getParent(targetCell);
                    int targetRowIndex = getRowIndex(targetRow);

                    // Select the row
                    if (targetCell == targetRow.getFirstChild()) {
                        if (table.isSelected(targetRowIndex)) {
                            table.deselect(targetRowIndex);
                        } else {
                            table.select(targetRowIndex);
                        }
                    }
                }
                break;
            }
        }
    }


//====================================================================
//
//====================================================================

    public static class SelectionTableDef extends TableDef {
        public static final String SELECTED = "SELECTED";
        static final CellRenderer<TableData.Row, String> alignRenderer = new AlignRenderer(HasHorizontalAlignment.ALIGN_CENTER);

        public SelectionTableDef(TableDataView def) {
            super(addDummy(def));
            addColumnDefinition(0, new ColDef() {
                {
                    setMinimumColumnWidth(30);
                    setPreferredColumnWidth(30);
                    setMaximumColumnWidth(30);
                    setColumnSortable(false);
                    setImmutable(true);
                }

                @Override
                public CellRenderer<TableData.Row, String> getCellRenderer() {
                    return alignRenderer;
                }

                public String getCellValue(TableData.Row rowValue) {
                    return rowValue.hasAccess() ? getCheckboxHtml(rowValue.getValue(SELECTED)) : "";
                }

                public void setCellValue(TableData.Row rowValue, String cellValue) {
                    // not implemented
                }

                public String getTitle() {
                    return "selected";
                }
            });
        }

        private static TableDataView addDummy(TableDataView def) {
            BaseTableColumn cb = new BaseTableColumn("dummy");
            cb.setVisible(false);
            def.addColumn(0, cb);
            return def;
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
