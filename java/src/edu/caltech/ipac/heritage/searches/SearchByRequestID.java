package edu.caltech.ipac.heritage.searches;

import edu.caltech.ipac.firefly.data.TableServerRequest;
import edu.caltech.ipac.heritage.commands.SearchByRequestIDCmd;
import edu.caltech.ipac.heritage.data.entity.DataType;
import edu.caltech.ipac.util.StringUtils;

import java.io.Serializable;


/**
 * Date: Jun 8, 2009
 *
 * @author loi
 * @version $Id: SearchByRequestID.java,v 1.10 2012/10/26 14:45:03 tatianag Exp $
 */
public class SearchByRequestID extends HeritageSearch<SearchByRequestID.Req> {

    public enum Type {AOR("aorByRequestID", DataType.AOR),
                      BCD("bcdByRequestID", DataType.BCD),
                      PBCD("pbcdByRequestID", DataType.PBCD),
                      IRS_ENHANCED(IRS_ENHANCED_SEARCH_ID, DataType.IRS_ENHANCED);
                        String searchId;
                        DataType dataType;
                        Type(String searchId, DataType dataType) {
                            this.searchId = searchId;
                            this.dataType = dataType;
                        }
                    }

    public SearchByRequestID(Type type, edu.caltech.ipac.firefly.data.Request clientReq) {
        super(type.dataType, type.dataType.getShortDesc(), new Req(type, clientReq),
                null, null);
    }

    public String getDownloadFilePrefix() {
        String preFile;
        int[] reqIDs = getSearchRequest().getReqIDs();
        if (reqIDs.length==1) {
                preFile= "aorkey-" + reqIDs[0]+ "-";

            }
            else {
                preFile= "aorkeys-";
            }
        return preFile;
    }

    public String getDownloadTitlePrefix() {
        String preTitle;
        int[] reqIDs = getSearchRequest().getReqIDs();
        if (reqIDs.length==1) {
                preTitle= "AORKEY " + reqIDs[0]+ ": ";
            }
            else {
                preTitle= "Multiple AORKEYs: ";
            }
        return preTitle;
    }

//====================================================================
//
//====================================================================

    public static class Req extends HeritageRequest implements Serializable {
        public static final String REQIDS = SearchByRequestIDCmd.REQUESTID_KEY;
        private static final String SAME_CONSTRAINTS = SearchByRequestIDCmd.INCLUDE_SAME_CONSTRAINTS_KEY;

        public Req() {}

        public TableServerRequest newInstance() {
            return new Req();
        }

        public Req(Type type, edu.caltech.ipac.firefly.data.Request req) {
            super(type.dataType);
            this.copyFrom(req);
            setRequestId(type.searchId);
        }

        public Req(Type type, int[] reqIDs) {
            setRequestId(type.searchId);
            setReqIDs(reqIDs);
        }

        public void setReqIDs(int[] reqIDs) {
            String reqkeysStr = StringUtils.toString(reqIDs, ",");
            setParam(REQIDS, reqkeysStr);
        }

        public int[] getReqIDs() {
            String reqkeysStr = getParam(REQIDS);
            return StringUtils.isEmpty(reqkeysStr) ? null : StringUtils.convertToArrayInt(reqkeysStr, ",");
        }

        public boolean includeSameConstraints() {
            return getBooleanParam(SAME_CONSTRAINTS);
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
