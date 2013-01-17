package edu.caltech.ipac.firefly.server.util.ipactable;

import edu.caltech.ipac.firefly.data.HasAccessInfos;
import edu.caltech.ipac.firefly.server.query.TemplateGenerator;
import edu.caltech.ipac.util.DataGroup;
import edu.caltech.ipac.util.DataType;
import edu.caltech.ipac.util.StringUtil;
import edu.caltech.ipac.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: May 14, 2009
 *
 * @author loi
 * @version $Id: DataGroupPart.java,v 1.7 2011/06/14 18:15:51 loi Exp $
 */
public class DataGroupPart implements HasAccessInfos {

    public static final String LOADING_STATUS = "Loading-Status";

    public static enum State {COMPLETED, INPROGRESS, FAILED;
                            public String toString() {
                                return StringUtils.pad(20, name());
                            }
    }

    private TableDef tableDef;
    private DataGroup data;
    private int startRow;
    private int rowCount;
    private String hasAccessCName = null;

    public DataGroupPart() {
    }

    public DataGroupPart(TableDef tableDef, DataGroup data, int startRow, int rowCount) {
        this.tableDef = tableDef;
        this.data = data;
        this.startRow = startRow;
        setRowCount(rowCount);
    }

    public String getHasAccessCName() {
        return hasAccessCName;
    }

    public void setHasAccessCName(String hasAccessCName) {
        this.hasAccessCName = hasAccessCName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public TableDef getTableDef() {
        return tableDef;
    }

    public void setTableDef(TableDef tableDef) {
        this.tableDef = tableDef;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount < 0 ? 0 : rowCount;
    }

    public DataGroup getData() {
        return data;
    }

    public void setData(DataGroup data) {
        this.data = data;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

//====================================================================
//  implements HasAccessInfos
//====================================================================
    public int getSize() {
        return data == null ? 0 : data.size();
    }

    public boolean hasAccess(int index) {
        if (index < 0 ||getHasAccessCName() == null ||
            data == null || index >= data.size()) {
            return false;
        }

        return Boolean.parseBoolean(data.get(index).getDataElement(getHasAccessCName()).toString());
    }

    public void setHasAccess(int index, boolean hasAccess) {
        if (getHasAccessCName() == null) {
            return;
        }
        DataType col = data.getDataDefintion(getHasAccessCName());
        if (col == null) {
            col = new DataType(getHasAccessCName(), String.class);
            data.addDataDefinition(col);
            data.addAttributes(TemplateGenerator.createAttribute(
                    TemplateGenerator.Tag.VISI_TAG, getHasAccessCName(),
                    TemplateGenerator.Tag.VISI_INVISIBLE));
        }
        data.get(index).setDataElement(col, String.valueOf(hasAccess));
    }

//====================================================================
//
//====================================================================

    public static class TableDef {
        private List<DataType> cols = new ArrayList<DataType>();
        private LinkedHashMap<String, DataGroup.Attribute> attributes = new LinkedHashMap<String, DataGroup.Attribute>();
        private int lineWidth;
        private int rowCount;
        private int colCount;
        private int rowStartOffset;
        private String sourceFile;
        private int lineSepLength;

        public void addAttribute(DataGroup.Attribute... attributes) {
            if (attributes != null) {
                for(DataGroup.Attribute a : attributes) {
                    this.attributes.put(a.getKey(), a);
                }
            }
        }

        public List<DataType> getCols() {
            return cols;
        }

        public void addCols(DataType col) {
            cols.add(col);
        }

        public Map<String, DataGroup.Attribute> getAttributes() {
            return attributes;
        }

        public void setStatus(State status) {
            addAttribute(new DataGroup.Attribute(LOADING_STATUS, status.name()));
        }

        public State getStatus() {
            DataGroup.Attribute a = attributes.get(LOADING_STATUS);
            if (a != null && !StringUtil.isEmpty(a.getValue())) {
                return State.valueOf(String.valueOf(a.getValue()));
            } else {
                return State.COMPLETED;
            }
        }

        public int getLineSepLength() {
            return lineSepLength;
        }

        public void setLineSepLength(int lineSepLength) {
            this.lineSepLength = lineSepLength;
        }

        public int getLineWidth() {
            return lineWidth;
        }

        public void setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
        }

        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount < 0 ? 0 : rowCount;
        }

        public int getColCount() {
            return colCount;
        }

        public void setColCount(int colCount) {
            this.colCount = colCount;
        }

        public int getRowStartOffset() {
            return rowStartOffset;
        }

        public void setRowStartOffset(int rowStartOffset) {
            this.rowStartOffset = rowStartOffset;
        }

        public String getSource() {
            return sourceFile;
        }

        public void setSource(String sourceFile) {
            this.sourceFile = sourceFile;
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
