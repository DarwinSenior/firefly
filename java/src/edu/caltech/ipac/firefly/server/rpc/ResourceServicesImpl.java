package edu.caltech.ipac.firefly.server.rpc;

import edu.caltech.ipac.firefly.core.RPCException;
import edu.caltech.ipac.firefly.data.Request;
import edu.caltech.ipac.firefly.data.ResourcePath;
import edu.caltech.ipac.firefly.data.Version;
import edu.caltech.ipac.firefly.data.table.RawDataSet;
import edu.caltech.ipac.firefly.rpc.ResourceServices;
import edu.caltech.ipac.firefly.server.RequestOwner;
import edu.caltech.ipac.firefly.server.ResourceManager;
import edu.caltech.ipac.firefly.server.ServerContext;
import edu.caltech.ipac.firefly.server.util.Logger;
import edu.caltech.ipac.firefly.server.util.QueryUtil;
import edu.caltech.ipac.firefly.server.util.VersionUtil;
import edu.caltech.ipac.firefly.server.Counters;
import edu.caltech.ipac.firefly.server.visualize.VisContext;
import edu.caltech.ipac.firefly.util.BrowserInfo;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: Nov 9, 2007
 *
 * @author loi
 * @version $Id: ResourceServicesImpl.java,v 1.44 2012/04/20 19:49:17 roby Exp $
 */
public class ResourceServicesImpl extends BaseRemoteService implements ResourceServices {

    private static final Logger.LoggerImpl _statsLog= Logger.getLogger(Logger.INFO_LOGGER);

    public RawDataSet getIpacTable(ResourcePath path, Request params) throws RPCException {
        try {
            File f = null;
            if (path.getPathType().equals(ResourcePath.PathType.FILE) ) {
                f = new File(ServerContext.getWorkingDir(), path.getPath());
            } else if (path.getPathType().equals(ResourcePath.PathType.URL)) {
                f = ResourceManager.getFromUrl(new URL(path.getPath()), "ipactbl-",".tbl");
            }
    
            RawDataSet dataset =  new ResourceManager().getIpacTableView(f, params.getStartIndex(), params.getPageSize(),
                    params.getSortInfo(), QueryUtil.convertToDataFilter(params.getFilters()));
            return dataset;
        } catch (Throwable e) {
            throw createRPCException(e);
        }
    }

    // TODO: convert calls to this methods to getIpacTable(ResourcePath path, Req params)
    public RawDataSet getIpacTable(String filePath, Request params) throws RPCException {
        try {
            RawDataSet dataset =  new ResourceManager().getIpacTableView(VisContext.convertToFile(filePath), params.getStartIndex(), params.getPageSize(),
                    params.getSortInfo(), QueryUtil.convertToDataFilter(params.getFilters()));
            return dataset;

        } catch (Throwable e) {
            throw createRPCException(e);
        }
    }

    public String getSessionId() {
        return ServerContext.getRequestOwner().getSessionId();
    }

    public Version getVersion(String userAgentStr) {
        BrowserInfo bi= new BrowserInfo(userAgentStr);
        String ua= (bi.isAllRecognized()) ? null : userAgentStr;

        List<Object> l= new ArrayList<Object>(10);
        l.add("b");
        l.add(bi.getBrowserString());
        l.add("v");
        l.add(bi.getVersionString());
        l.add("os");
        l.add(bi.getPlatformDesc());
        if (!bi.isAllRecognized()) {
            l.add("ua");
            l.add(ua);
        }
        RequestOwner owner= ServerContext.getRequestOwner();
        if (owner.isCrossSite()) {
            l.add("xs");
            l.add(owner.getReferrer());
        }

        _statsLog.stats("client", l.toArray(new Object[l.size()]));
        Counters.getInstance().increment(Counters.Category.Browser, "Loads");

        return  VersionUtil.getAppVersion();
    }
}