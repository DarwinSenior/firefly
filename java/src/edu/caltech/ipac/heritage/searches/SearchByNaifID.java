package edu.caltech.ipac.heritage.searches;

import edu.caltech.ipac.firefly.data.Request;
import edu.caltech.ipac.firefly.data.TableServerRequest;
import edu.caltech.ipac.firefly.ui.DatePanel;
import edu.caltech.ipac.heritage.commands.SearchByNaifIDCmd;
import edu.caltech.ipac.heritage.data.entity.DataType;
import edu.caltech.ipac.util.StringUtils;

import java.io.Serializable;


/**
 * Date: Jun 8, 2009
 *
 * @author loi
 * @version $Id: SearchByNaifID.java,v 1.8 2010/04/24 01:13:04 loi Exp $
 */
public class SearchByNaifID extends HeritageSearch<SearchByNaifID.Req> {

    public enum Type {AOR("aorByNaifID", DataType.AOR),
                      BCD("bcdByNaifID", DataType.BCD),
                      PBCD("pbcdByNaifID", DataType.PBCD);
                        String searchId;
                        DataType dataType;
                        Type(String searchId, DataType dataType) {
                            this.searchId = searchId;
                            this.dataType = dataType;
                        }
                    }


    public SearchByNaifID(Type type, Request clientReq) {
        super(type.dataType, type.dataType.getShortDesc(), new Req(type, clientReq),
                null, null);
    }


    public String getDownloadFilePrefix() {
        String preFile;
        int[] naifIDs = getSearchRequest().getNaifIDs();
        if (naifIDs.length==1) {
                preFile= "naifID-" + naifIDs[0]+ "-";

            }
            else {
                preFile= "naifIDs-";
            }
        return preFile;
    }

    public String getDownloadTitlePrefix() {
        String preTitle;
        int[] naifIDs = getSearchRequest().getNaifIDs();
        if (naifIDs.length==1) {
                preTitle= "NAIF ID " + naifIDs[0]+ ": ";
            }
            else {
                preTitle= "Multiple NAIF IDs: ";
            }
        return preTitle;
    }
    

//====================================================================
//
//====================================================================

    public static class Req extends HeritageRequest implements Serializable {

        private static final String NAIF_ID = SearchByNaifIDCmd.NAIFID_KEY;

        public Req() {}

        public TableServerRequest newInstance() {
            return new Req();
        }

        public Req(Type type, Request req) {
            super(type.dataType);
            this.copyFrom(req);
            // for backward compatibility
            if (!StringUtils.isEmpty(getParam(DatePanel.START_DATE_KEY))) {
                req.setParam(SearchByNaifIDCmd.START_DATE_KEY, getParam(DatePanel.START_DATE_KEY));
                req.removeParam(DatePanel.START_DATE_KEY);
            }
            if (!StringUtils.isEmpty(getParam(DatePanel.END_DATE_KEY))) {
                req.setParam(SearchByNaifIDCmd.END_DATE_KEY, getParam(DatePanel.END_DATE_KEY));
                req.removeParam(DatePanel.END_DATE_KEY);
            }


            setRequestId(type.searchId);
        }

        /*
        public Date getStartDate() {
            return getDateParam(START_DATE);
        }

        public Date getEndDate() {
            return getDateParam(END_DATE);
        }
        */

        public int[] getNaifIDs() {
            String naifidsStr = getParam(NAIF_ID);
            return StringUtils.isEmpty(naifidsStr) ? null : StringUtils.convertToArrayInt(naifidsStr, ",");
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

