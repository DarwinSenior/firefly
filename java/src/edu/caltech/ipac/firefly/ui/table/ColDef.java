package edu.caltech.ipac.firefly.ui.table;

import com.google.gwt.gen2.table.client.AbstractColumnDefinition;
import com.google.gwt.gen2.table.client.DefaultCellRenderer;
import edu.caltech.ipac.firefly.data.table.TableData;
import edu.caltech.ipac.firefly.data.table.TableDataView;

/**
 * Date: Jul 23, 2009
*
* @author loi
* @version $Id: ColDef.java,v 1.6 2010/12/13 23:42:01 loi Exp $
*/
public class ColDef extends AbstractColumnDefinition<TableData.Row, String> {
    private TableDataView.Column column;
    private boolean isImmutable = false;

    public ColDef() {

    }

    public ColDef(TableDataView.Column column) {
        this.column = column;
        setMinimumColumnWidth(25);
        setPreferredColumnWidth(column.getPrefWidth()*8);
        setColumnSortable(column.isSortable());
        setCellRenderer(new DefaultCellRenderer<TableData.Row, String>(true));
    }

    public boolean isImmutable() {
        return isImmutable;
    }

    public void setImmutable(boolean immutable) {
        isImmutable = immutable;
    }

    public TableDataView.Column getColumn() {
        return column;
    }

    public String getCellValue(TableData.Row rowValue) {
        return String.valueOf(rowValue.getValue(column.getName()));
    }

    public void setCellValue(TableData.Row rowValue, String cellValue) {
        rowValue.setValue(column.getName(), cellValue);
    }

    public String getName() {
        return column == null ? null : column.getName();
    }

    public String getTitle() {
        return column == null ? getName() : column.getTitle();
    }

    public String getShortDesc() {
        return column == null ? getTitle() : column.getShortDesc();
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
