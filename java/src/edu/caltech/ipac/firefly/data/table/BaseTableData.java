package edu.caltech.ipac.firefly.data.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: May 14, 2008
 *
 * @author loi
 * @version $Id: BaseTableData.java,v 1.4 2010/06/16 20:50:32 loi Exp $
 */
public class BaseTableData implements TableData<BaseTableData.RowData> {

    private ArrayList<RowData> data;
    private ArrayList<String> columns;
    private HashMap<String, String> attributes;
    private Ref<String> hasAccessCName = new Ref<String>(null);

    public BaseTableData() {
        this(new String[0]);
    }

    public BaseTableData(String[] columns) {
        this.columns = new ArrayList<String>(Arrays.asList(columns));
        data = new ArrayList<RowData>();
        attributes = new HashMap<String, String>();
    }

    public void setHasAccessCName(String hasAccessCName) {
        this.hasAccessCName.setSource(hasAccessCName);
    }

    public int size() {
        return data.size();
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void addColumn(int index, String name) {
        columns.add(index, name);
        for(RowData d : data) {
            d.columns = columns;
        }
    }

    /**
     * Add this row into the set.
     *
     * @param row row to be added
     * @return true if it is sucessfully added
     */
    public boolean addRow(RowData row) {
        row.setHasAccessCName(hasAccessCName);
        return data.add(row);
    }

    public boolean addRow(String[] row) {
        return addRow(new RowData(columns, row));
    }

    public boolean removeRow(int rowIdx) {
        return data.remove(rowIdx) != null;
    }

    public void clear() {
        data.clear();
    }

    /**
     * Returns a row of data at this index.
     *
     * @param idx
     * @return
     */
    public RowData getRow(int idx) {
        return data.get(idx);
    }

    public int indexOf(RowData row) {
        return data.indexOf(row);
    }

    public List<RowData> getRows() {
        return data;
    }

    public List<String> getColumnNames() {
        return columns;
    }

    public int getColumnIndex(String colName) {
        int idx = columns.indexOf(colName);
        if (idx < 0) {
            throw new IllegalArgumentException("This column does not exists: " + colName);
        }
        return idx;
    }

    /**
     * shallow clone.. not cloning the RowData objects
     * @return
     * @throws
     */
    protected BaseTableData clone() {
        BaseTableData newval = new BaseTableData();
        newval.data = (ArrayList<RowData>) data.clone();
        newval.columns = (ArrayList<String>) columns.clone();
        newval.attributes = (HashMap<String, String>) attributes.clone();

        return newval;
    }

//====================================================================
// implements HasAccessInfos
//====================================================================

    public int getSize() {
        return size();
    }

    /**
     * Returns true if the given row is accessible.
     * The row is accessible if a hasAccess column is not defined, or there
     * is a "true" value (ignoring case) in the defined hasAccess column.
     * @param index
     * @return
     */
    public boolean hasAccess(int index) {
        return this.getRow(index).hasAccess();
    }

//====================================================================

    /**
     * A data object class representing one row of data.
     */
    public static class RowData implements TableData.Row<String>, Serializable {

        private HashMap<String, String> data = new HashMap<String, String>();
        private ArrayList<String> columns;
        private Ref<String> hasAccessCName;

        public RowData() {}

        public RowData(ArrayList<String> columns, String[] data) {
            this.columns = columns;
            setValues(data);
        }

        public void setValues(String[] data) {
            this.data.clear();
            for(int i = 0; i < data.length; i ++) {
                this.data.put(columns.get(i), data[i]);
            }
        }

        String[] getData() {
            String[] vals = new String[columns.size()];
            for(int i = 0; i < columns.size(); i++) {
                vals[i] = getValue(columns.get(i));
            }
            return vals;
        }

        void setHasAccessCName(Ref<String> hasAccessCName) {
            this.hasAccessCName = hasAccessCName;
        }

        public int size() {
            return data == null ? 0 : data.size();
        }

//====================================================================
//  Implements TableData.Row but, internally stored as String.
//  This helps simplified GWT rpc serialization process.
//====================================================================
        public String getValue(int colIdx) {
            return data.get(columns.get(colIdx));
        }

        public void setValue(int colIdx, String v) {
            data.put(columns.get(colIdx), v);
        }

        public String getValue(String colName) {
            return data.get(colName);
        }

        public void setValue(String colName, String value) {
            data.put(colName, value);
        }

        public Map<String, String> getValues() {
            return data;
        }

        public boolean hasAccess() {
            if (hasAccessCName.getSource() == null) {
                return true;
            } else {
                String val = getValue(hasAccessCName.getSource());
                return Boolean.parseBoolean(val);
            }
        }
    }

    static class Ref<T extends Serializable> implements Serializable {
        private T source;

        public Ref() {}

        public Ref(T source) {
            this.source = source;
        }

        public void setSource(T source) {
            this.source = source;
        }

        public T getSource() {
            return source;
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
