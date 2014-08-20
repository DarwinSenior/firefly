package edu.caltech.ipac.firefly.fuse.data.provider;
/**
 * User: roby
 * Date: 8/20/14
 * Time: 1:32 PM
 */


import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.caltech.ipac.firefly.data.table.TableMeta;
import edu.caltech.ipac.firefly.fuse.data.ImagePlotDefinition;
import edu.caltech.ipac.firefly.fuse.data.config.SelectedRowData;
import edu.caltech.ipac.firefly.visualize.WebPlotRequest;
import edu.caltech.ipac.firefly.visualize.ZoomType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Trey Roby
 */
public class SpitzerDataSetConverter extends AbstractDataSetInfoConverter {

    public SpitzerDataSetConverter() {
        super(Arrays.asList(DataVisualizeMode.FITS), "target");
    }

    @Override
    public ImagePlotDefinition getImagePlotDefinition(TableMeta meta) {
        return new ImagePlotDefinition("seip", Arrays.asList("target"));
    }


    @Override
    public void getImageRequest(SelectedRowData selRowData, GroupMode mode, AsyncCallback<Map<String, WebPlotRequest>> cb) {
        String path= selRowData.getSelectedRow().getValue("fname");
        WebPlotRequest r= WebPlotRequest.makeURLPlotRequest("http://irsa.ipac.caltech.edu/data/SPITZER/Enhanced/SEIP/" + path, "SEIP");
        Map<String,WebPlotRequest> map= new HashMap<String, WebPlotRequest>(1);
        r.setTitle("Spitzer: SEIP");
        r.setZoomType(ZoomType.TO_WIDTH);
        map.put("seip", r);
        cb.onSuccess(map);
    }
}

/*
 * THIS SOFTWARE AND ANY RELATED MATERIALS WERE CREATED BY THE CALIFORNIA 
 * INSTITUTE OF TECHNOLOGY (CALTECH) UNDER A U.S. GOVERNMENT CONTRACT WITH 
 * THE NATIONAL AERONAUTICS AND SPACE ADMINISTRATION (NASA). THE SOFTWARE 
 * IS TECHNOLOGY AND SOFTWARE PUBLICLY AVAILABLE UNDER U.S. EXPORT LAWS 
 * AND IS PROVIDED AS-IS TO THE RECIPIENT WITHOUT WARRANTY OF ANY KIND, 
 * INCLUDING ANY WARRANTIES OF PERFORMANCE OR MERCHANTABILITY OR FITNESS FOR 
 * A PARTICULAR USE OR PURPOSE (AS SET FORTH IN UNITED STATES UCC 2312- 2313) 
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
