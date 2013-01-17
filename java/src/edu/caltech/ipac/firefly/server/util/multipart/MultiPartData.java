package edu.caltech.ipac.firefly.server.util.multipart;

import edu.caltech.ipac.util.cache.StringKey;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Jul 28, 2010
 *
 * @author loi
 * @version $Id: MultiPartData.java,v 1.2 2012/03/23 18:39:34 roby Exp $
 */
public class MultiPartData implements Serializable {
    private Map<String, String> params = new HashMap<String, String>();
    private List<UploadFileInfo> files = new ArrayList<UploadFileInfo>();
    private StringKey cacheKey= null;


    public MultiPartData() { this(null); }

    public MultiPartData(StringKey cacheKey) {
        this.cacheKey= cacheKey;
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public void addFile(UploadFileInfo fi) {
        files.add(fi);
    }

    public void addFile(String pname, File file, String fileName, String contentType) {
        addFile(new UploadFileInfo(pname, file, fileName, contentType));
    }

    public Map<String, String> getParams() {
        return params;
    }

    public List<UploadFileInfo> getFiles() {
        return files;
    }

    public StringKey getCacheKey() { return cacheKey; }
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
