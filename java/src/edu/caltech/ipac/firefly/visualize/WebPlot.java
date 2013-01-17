package edu.caltech.ipac.firefly.visualize;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import edu.caltech.ipac.firefly.data.DataEntry;
import edu.caltech.ipac.firefly.rpc.PlotService;
import edu.caltech.ipac.firefly.rpc.PlotServiceAsync;
import edu.caltech.ipac.firefly.util.Dimension;
import edu.caltech.ipac.firefly.util.WebAssert;
import edu.caltech.ipac.firefly.visualize.task.VisTask;
import edu.caltech.ipac.visualize.plot.CoordinateSys;
import edu.caltech.ipac.visualize.plot.ImagePt;
import edu.caltech.ipac.visualize.plot.ImageWorkSpacePt;
import edu.caltech.ipac.visualize.plot.ProjectionException;
import edu.caltech.ipac.visualize.plot.ProjectionPt;
import edu.caltech.ipac.visualize.plot.Pt;
import edu.caltech.ipac.visualize.plot.WorldPt;
import edu.caltech.ipac.visualize.plot.projection.Projection;

import java.util.HashMap;
import java.util.Map;


/**
 * This class contains plot information.
 * Publicly this class operations in many coordinate system.
 * Some include a Image coordinate system, a world coordinate system, and a screen
 * coordinate system.
 * <ul>
 * <li>The image coordinate system is the coordinate system of the data. 
 * <li>The world coordinate system is the system that the data represents
 *        (i.e. the coordinate system of the sky)
 * <li>Screen coordinates are the pixel values of the screen.
 * </ul>
 *
 * @author Trey Roby
 * @version $Id: WebPlot.java,v 1.68 2012/12/14 23:59:58 roby Exp $
 */
public class WebPlot {

    public enum ZDir {UP,DOWN,ORIGINAL}

    // =======================================================================
    // ------------------    constants for Attributes -------------------------
    // =======================================================================

    public static final String MOVING_TARGET_CTX_ATTR=   "MOVING_TARGET_CTX_ATTR";

    /**
     * This will probably be a WebMouseReadoutHandler class
     * @see WebMouseReadoutHandler
     */
    public static final String READOUT_ATTR=             "READOUT_ATTR";

    /**
     * This will probably be an HashMap<Integer, String> class
     * @see java.util.HashMap
     */
    public static final String READOUT_ROW_PARAMS=             "READOUT_ROW_PARAMS";

    /**
     * This will probably be a ActiveTarget.PosEntry class
     * @see ActiveTarget.PosEntry
     */
    public static final String FIXED_TARGET=             "FIXED_TARGET";

    /**
     * This will probably be a double with the requested size of the plot
     * @see ActiveTarget.PosEntry
     */
    public static final String REQUESTED_SIZE=             "REQUESTED_SIZE";


    /**
     * This will probably be a RecSelection class
     * @see edu.caltech.ipac.firefly.visualize.draw.RecSelection
     */
    public static final String SELECTION=                "SELECTION";

    /**
     * This will probably be a LineSelection class
     * @see edu.caltech.ipac.firefly.visualize.draw.LineSelection
     */
    public static final String ACTIVE_DISTANCE=          "ACTIVE_DISTANCE";

    /**
     * This is a String describing why this plot can't be rotated.  If it is defined then
     * rotating is disabled.
     */
    public static final String DISABLE_ROTATE_REASON=          "DISABLE_ROTATE_HINT";

    /**
     * what should happen when multi-fits images are changed.  If set the zoom is set to the same level
     * eg 1x, 2x ect.  If not set then flipping should attempt to make the image the same arcsec/screen pixel.
     */
    public static final String FLIP_ZOOM_BY_LEVEL= "FLIP_ZOOM_BY_LEVEL";

    /**
     * what should happen when multi-fits images are changed.  If set the zoom is set to the same level
     * eg 1x, 2x ect.  If not set then flipping should attempt to make the image the same arcsec/screen pixel.
     */
    public static final String FLIP_ZOOM_TO_FILL= "FLIP_ZOOM_TO_FILL";

    /**
     * if set, when expanded the image will be zoom to no bigger than this level;
     * this should be a subclass of Number
     */
    public static final String MAX_EXPANDED_ZOOM_LEVEL = "MAX_EXPANDED_ZOOM_LEVEL";

    /**
     * if set, must be one of the string values defined by the enum ZoomUtil.FullType
     * currently is is ONLY_WIDTH, WIDTH_HEIGHT, ONLY_HEIGHT
     */
    public static final String EXPANDED_TO_FIT_TYPE = "MAX_EXPANDED_ZOOM_LEVEL";

    public static final String UNIQUE_KEY = "UNIQUE_KEY";


    private static final int  THREE_COLOR_LEN= 3;


    private final CoordinateSys _imageCoordSys;
    private final Projection    _projection;
    private final int           _dataWidth;
    private final int           _dataHeight;
    private final int           _imageScaleFactor;
    private final WebPlotGroup  _plotGroup;
    private       PlotState     _plotState;
    private final WebFitsData   _webFitsData[];
    private final Map<String,Object>  _attributes= new HashMap<String,Object>(3);
    private final TileDrawer    _tileDrawer;


    private int       _offsetX= 0;
    private int       _offsetY= 0;
    private final String    _dataDesc;
    private String    _plotDesc     = "";
    private boolean   _alive    = true;
    private float     _initialZoomLevel= 1.0F;
    private float     _percentOpaque   = 1.0F;

    private Dimension _padDim= new Dimension(0,0);

    private int _viewPortX= 0;
    private int _viewPortY= 0;
    private Dimension _viewPortDim= new Dimension(42,42); // small dummy initialization

    public WebPlot(WebPlotInitializer wpInit) {
        _plotGroup= new WebPlotGroup(this,wpInit.getPlotState().getZoomLevel());
        _plotState       = wpInit.getPlotState();
        _tileDrawer      = new TileDrawer(this,wpInit.getInitImages());
        _imageCoordSys   = wpInit.getCoordinatesOfPlot();
        _projection      = wpInit.getProjection();
        _dataWidth       = wpInit.getDataWidth();
        _dataHeight      = wpInit.getDataHeight();
        _imageScaleFactor= wpInit.getImageScaleFactor();
        _plotDesc        = wpInit.getPlotDesc();
        _dataDesc        = wpInit.getDataDesc();

        _webFitsData= new WebFitsData[THREE_COLOR_LEN];
        WebFitsData webFitsData[]= wpInit.getFitsData();
        for(int i= 0; (i<THREE_COLOR_LEN); i++) {
            if (webFitsData.length>=i+1) {
                _webFitsData[i]= webFitsData[i];
            }
        }
    }


    public AbsolutePanel getWidget() { return _tileDrawer.getWidget(); }


    public void refreshWidget(PlotImages images, boolean overlay) {
        _tileDrawer.refreshWidget(images, overlay);
    }


    public void refreshWidget(PlotImages images) {
        refreshWidget(images, false);
    }

    public PlotState getPlotState() { return _plotState; }
    public void setPlotState(PlotState state) { _plotState= state; }

    public void drawTilesInArea(ScreenPt spt, int width, int height) {
        _tileDrawer.drawTilesForArea(spt.getIX(), spt.getIY(),width,height);
    }



    public WebPlotGroup getPlotGroup() { return _plotGroup; }
    public TileDrawer getTileDrawer() { return _tileDrawer; }
    public float getZoomFact() { return _plotGroup.getZoomFact(); }


    public WebFitsData getFitsData(Band band) {
        return _webFitsData[band.getIdx()];
    }

    public void setFitsData(WebFitsData  data, Band band) {
        _webFitsData[band.getIdx()]= data;
    }

    public WebHistogramOps getHistogramOps(Band band) {
        WebHistogramOps retval;
        if (band== Band.NO_BAND) {
            retval= new WebHistogramOps(this, Band.NO_BAND);
        }
        else {
            threeColorOK(band);
            retval= new WebHistogramOps(this,band);
        }
        return retval;
    }

    public void setViewPort(int x, int y, int width, int height) {
        _viewPortX= x;
        _viewPortY= y;
        _viewPortDim= new Dimension(width,height);
    }

    public int getViewPortX() { return _viewPortX; }
    public int getViewPortY() { return _viewPortY; }
    public Dimension getViewPortDimension() { return _viewPortDim; }

    public int getImageWidth()  { return _plotGroup.getImageSize().getWidth();   }
    public int getImageHeight() { return _plotGroup.getImageSize().getHeight();  }


    /**
     * returns the first used band. It is possible that this method will return null.  You should always check.
     * @return the first name used.
     */
    public Band getFirstBand() {
        return getBands().length>0 ?  getBands()[0] : null;
    }

    /**
     * Get an array of used band.  It is possible that this routine will return a array of length 0
     * @return the bands in use
     */
    public Band[] getBands() { return _plotState.getBands(); }
    public boolean isThreeColor()  { return _plotState.isThreeColor(); }

    public int getColorTableID() { return _plotState.getColorTableId(); }

    private void threeColorOK(Band band) {
        WebAssert.argTst(_plotState.getColorTableId(),
                   "Must be in three color mode to use this routine");
        WebAssert.argTst( (band== Band.RED || band== Band.GREEN || band== Band.BLUE),
                       "band must be RED, GREEN, or BLUE");
    }

    /**
     * This method will return the width of the image in screen coordinates.
     * This number will change as the plot is zoomed up and down.
     * @return the width of the plot
     */
    public int     getScreenWidth()  { return _plotGroup.getScreenSize().getWidth(); }

    /**
     *  This method will return the height of the image in screen coordinates.
     *  This number will change as the plot is zoomed up and down.
     * @return the height of the plot
     */
    public int     getScreenHeight() { return _plotGroup.getScreenSize().getHeight();}

    /**
     * This method will return the width of the image data.
     * This number will not change as the plot is zoomed up and down.
     * @return the width of the image data
     */
    public int     getImageDataWidth() { return _dataWidth*_imageScaleFactor; }

    /**
     * This method will return the height of the image data.
     * This number will not change as the plot is zoomed up and down.
     * @return the height of the image data
     */
    public int     getImageDataHeight() { return _dataHeight*_imageScaleFactor; }

    /**
     * This method will return the width of the image data.
     * This number will not change as the plot is zoomed up and down.
     * @return the width of the image data
     */
    public int     getImageWorkSpaceWidth() { return (_dataWidth*_imageScaleFactor)+_padDim.getWidth()*2; }

    /**
     * This method will return the height of the image data.
     * This number will not change as the plot is zoomed up and down.
     * @return the height of the image data
     */
    public int     getImageWorkSpaceHeight() { return (_dataHeight*_imageScaleFactor)+_padDim.getHeight()*2; }


    public boolean isBlankImage() {
        return isBlankImage(_plotState.firstBand());
    }

    public boolean isBlankImage(Band band) {
        WebPlotRequest req=_plotState.getWebPlotRequest(band);
        return (req!=null && req.getRequestType()==RequestType.BLANK);
    }

    /**
     * This method will return the width of the image in the world coordinate
     * system (probably degrees on the sky).
     * @return the width of the image data in world coord system.
     */
    public double getWorldPlotWidth() {
        return _projection.getPixelWidthDegree() *_dataWidth;
    }

    public Projection getProjection() { return _projection; }

    /**
     * This method will return the height of the image in the world coordinate
     * system (probably degrees on the sky).
     * @return the height of the image data in world coord system.
     */
    public double  getWorldPlotHeight() {
        return _projection.getPixelHeightDegree() *_dataHeight;
    }



    /**
     * Determine if a world point is in data Area of the plot
     * @param iwPt the point to test.
     * @return boolean true if it is in the data boundaries, false if not.
     */
    public boolean pointInData(ImageWorkSpacePt iwPt) {
        boolean retval= false;
        if (pointInPlot(iwPt)) {
            ImagePt ipt= getImageCoords(iwPt);
            double x= ipt.getX();
            double y= ipt.getY();
            retval= (x >= 0 && x <= _dataWidth && y >= 0 && y <= _dataHeight );
        }
        return retval;
    }


    /**
     * Determine if a image point is in the plot boundaries.
     * @param pt the point to test.
     * @return boolean true if it is in the boundaries, false if not.
     */
    public boolean pointInData( Pt pt) {
        boolean retval= false;
        if (pt instanceof WorldPt) {
            try {
                ImageWorkSpacePt ipt= getImageWorkSpaceCoords((WorldPt)pt);
                retval= pointInData(ipt);
            } catch (ProjectionException e) {
                retval= false;
            }
        }
        else if (pt instanceof ImageWorkSpacePt) {
            retval= pointInData((ImageWorkSpacePt)pt);
        }
        else if (pt instanceof ImagePt) {
            ImageWorkSpacePt ipt= this.getImageWorkSpaceCoords((ImagePt)pt);
            retval= pointInData(ipt);
        }
        else if (pt instanceof ScreenPt) {
            ImageWorkSpacePt ipt= this.getImageWorkSpaceCoords((ScreenPt)pt);
            retval= pointInData(ipt);
        }
        else if (pt instanceof ViewPortPt) {
            ImageWorkSpacePt ipt= this.getImageWorkSpaceCoords((ViewPortPt)pt);
            retval= pointInData(ipt);
        }
        else {
            WebAssert.argTst(false, "unknown Pt type");
        }

        return retval;
    }




    /**
     * Determine if a image point is in the plot boundaries.
     * @param ipt the point to test.
     * @return boolean true if it is in the boundaries, false if not.
     */
    public boolean pointInPlot( ImageWorkSpacePt ipt) {
        return _plotGroup.pointInPlot(ipt);
    }

    /**
     * Determine if a image point is in the plot boundaries.
     * @param pt the point to test.
     * @return boolean true if it is in the boundaries, false if not.
     */
    public boolean pointInPlot( Pt pt) {
        boolean retval= false;
        if (pt==null) {
            return retval;
        }
        if (pt instanceof WorldPt) {
            try {
                ImageWorkSpacePt ipt= getImageWorkSpaceCoords((WorldPt)pt);
                retval= pointInPlot(ipt);
            } catch (ProjectionException e) {
                retval= false;
            }
        }
        else if (pt instanceof ImageWorkSpacePt) {
            retval= pointInPlot((ImageWorkSpacePt)pt);
        }
        else if (pt instanceof ImagePt) {
            ImageWorkSpacePt ipt= this.getImageWorkSpaceCoords((ImagePt)pt);
            retval= pointInPlot(ipt);
        }
        else if (pt instanceof ScreenPt) {
            ImageWorkSpacePt ipt= this.getImageWorkSpaceCoords((ScreenPt)pt);
            retval= pointInPlot(ipt);
        }
        else if (pt instanceof ViewPortPt) {
            ImageWorkSpacePt ipt= this.getImageWorkSpaceCoords((ViewPortPt)pt);
            retval= pointInPlot(ipt);
        }
        else {
            WebAssert.argTst(false, "unknown Pt type");
        }

        return retval;
    }

    public boolean pointInViewPort( ViewPortPt vpt) {
        int x= vpt.getIX();
        int y= vpt.getIY();
        return (x>=0 && y>=0 && x<=_viewPortDim.getWidth() && y<=_viewPortDim.getHeight());
    }


    /**
     * Determine if a point is in the view port boundaries.
     * @param pt the point to test.
     * @return boolean true if it is in the boundaries, false if not.
     */
    public boolean pointInViewPort(Pt pt) {
        boolean retval= false;
        if (pt instanceof WorldPt) {
            try {
                retval= pointInViewPort(getViewPortCoords((WorldPt)pt));
            } catch (ProjectionException e) {
                retval= false;
            }
        }
        else if (pt instanceof ImageWorkSpacePt) {
            retval= pointInViewPort(getViewPortCoords((ImageWorkSpacePt)pt));
        }
        else if (pt instanceof ImagePt) {
            retval= pointInViewPort(getViewPortCoords((ImagePt)pt));
        }
        else if (pt instanceof ScreenPt) {
            retval= pointInViewPort(getViewPortCoords((ScreenPt)pt));
        }
        else if (pt instanceof ViewPortPt) {
            retval= pointInViewPort((ViewPortPt)pt);
        }
        else {
            WebAssert.argTst(false, "unknown Pt type");
        }
        return retval;
    }



    /**
     * get the coordinate system of the plot.
     * @return  CoordinateSys  the coordinate system.
     */
    public CoordinateSys getCoordinatesOfPlot() { return _imageCoordSys; }

    /**
     * get the flux of a given image point point on the plot.
     * @param pt the image point
     * @param callback the image point as String
     */

    public void getFluxLight(ImagePt pt, AsyncCallback<String[]> callback) {

        if (pointInData(pt)) {
            Band bands[]= _plotState.getBands();
            FileAndHeaderInfo pahi[]= new FileAndHeaderInfo[bands.length];
            for(int i= 0; (i<bands.length); i++) {
                pahi[i]= _plotState.getFileAndHeaderInfo(bands[i]);
            }

            ImagePt deciModPt= new ImagePt( pt.getX(), pt.getY());
            VisTask.getInstance().getFlux(pahi, deciModPt, callback);
        }
        else {
            callback.onFailure(null);
        }
    }

    public void getFlux(ImagePt pt,
                        final AsyncCallback<double[]> callback) {
        PlotServiceAsync pserv=PlotService.App.getInstance();
//        int decimation= _plotState.getDecimationLevel(_plotState.firstBand());
        int decimation= 1;
        ImagePt deciModPt= new ImagePt( pt.getX()/decimation, pt.getY()/decimation);
        pserv.getFlux(_plotState, deciModPt, new AsyncCallback<WebPlotResult>() {
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            public void onSuccess(WebPlotResult result) {
                _plotState.setContextString(result.getContextStr());
                DataEntry.DoubleArray fluxResult =
                        (DataEntry.DoubleArray) result.getResult(WebPlotResult.FLUX_VALUE);
                if (fluxResult != null) {
                    double fluxvals[] = fluxResult.getArray();
                    callback.onSuccess(fluxvals);
                } else {
                    callback.onFailure(new NullPointerException("fluxval is null"));
                }
            }
        });
    }

    /**
     * get the scale (in arcseconds) that one image pixel of data represents.
     * @return double the scale of one pixel.
     */
    public double getPixelScale(){
        return _projection.getPixelScaleArcSec();
    }



    public ImageWorkSpacePt getImageWorkSpaceCoords(ViewPortPt vpt) {
        return getImageWorkSpaceCoords(getScreenCoords(vpt));
    }


    public ImageWorkSpacePt getImageWorkSpaceCoords(ScreenPt pt) {
        return getImageWorkSpaceCoords(pt,getZoomFact());
    }

    public ImageWorkSpacePt getImageWorkSpaceCoords(ScreenPt pt, float altZLevel) {
        return new ImageWorkSpacePt(pt.getIX() / altZLevel,
                                    getImageHeight()-pt.getIY()/altZLevel);
    }


    public ImageWorkSpacePt getImageWorkSpaceCoords(ImagePt pt) {
        return new ImageWorkSpacePt(pt.getX()+_offsetX, pt.getY()+_offsetY);
    }

    public ImageWorkSpacePt getImageWorkSpaceCoords( WorldPt wpt) throws ProjectionException {
        ImagePt ipt= getImageCoords(wpt);
        return getImageWorkSpaceCoords(ipt);
    }



    /**
     * Return the image coordinates given screen x & y.
     * @param pt screen coordinates to convert from
     * @return ImagePt the translated coordinates
     */
    public ImagePt getImageCoords(ScreenPt pt) {

        return getImageCoords(getImageWorkSpaceCoords(pt));
    }

    public ImagePt getImageCoords(ViewPortPt vpt) {
        return getImageCoords(getScreenCoords(vpt));
    }


    /**
     * This will be overridden for ImagePlot where the plot might be an overlay
     * @param sipt the ImageWorkSpacePt point 
     * @return ImagePt the converted point
     */

   public ImagePt getImageCoords(ImageWorkSpacePt sipt) {
        return new ImagePt(sipt.getX()-_offsetX, sipt.getY()-_offsetY);
   }

    /**
     * Return the image coordinates given a WorldPt class
     * @param wpt the class containing the point in sky coordinates
     * @return ImagePt the translated coordinates
     * @throws edu.caltech.ipac.visualize.plot.ProjectionException if the point cannot be projected into an ImagePt
     */
    public ImagePt getImageCoords( WorldPt wpt)
                                  throws ProjectionException {
        if (!_imageCoordSys.equals(wpt.getCoordSys())) {
            wpt= VisUtil.convert(wpt,_imageCoordSys);
        }
        ProjectionPt proj_pt= _projection.getImageCoords(wpt.getLon(),wpt.getLat());
//        int decimation= _plotState.getDecimationLevel(_plotState.firstBand());
        int decimation= 1;
        return new ImagePt( (proj_pt.getX() * decimation)  + 0.5F ,  (proj_pt.getY() * decimation) + 0.5F);
    }




    public ViewPortPt getViewPortCoords(ScreenPt spt)  {
        return new ViewPortPt( spt.getIX()-_viewPortX, spt.getIY()-_viewPortY);
    }

    public ViewPortPt getViewPortCoords(ImagePt ipt)  {
        return getViewPortCoords(getScreenCoords(ipt));
    }
    public ViewPortPt getViewPortCoords(ImagePt ipt, float altZLevel)  {
        return getViewPortCoords(getScreenCoords(ipt,altZLevel));
    }
    public ViewPortPt getViewPortCoords(ImageWorkSpacePt ipt)  {
        return getViewPortCoords(getScreenCoords(ipt));
    }
    public ViewPortPt getViewPortCoords(ImageWorkSpacePt ipt, float altZLevel)  {
        return getViewPortCoords(getScreenCoords(ipt,altZLevel));
    }
    public ViewPortPt getViewPortCoords(WorldPt wpt)  throws ProjectionException {
        return getViewPortCoords(getScreenCoords(wpt));
    }

    public ViewPortPt getViewPortCoords(WorldPt wpt, float altZLevel) throws ProjectionException {
        return getViewPortCoords(getScreenCoords(wpt));
    }

    public ViewPortPt getViewPortCoords(Pt pt) throws ProjectionException {
        return getViewPortCoords(getScreenCoords(pt));
    }


    /**
     * Return the screen coordinates given Pt
     * @param pt the point to translate
     * @return ScreenPt the screen coordinates
     * @throws ProjectionException if the point cannot be projected into a ScreePt
     */
    public ScreenPt getScreenCoords(Pt pt) throws ProjectionException {
        ScreenPt retval= null;

        if      (pt instanceof WorldPt)          retval= getScreenCoords((WorldPt)pt);
        else if (pt instanceof ImageWorkSpacePt) retval= getScreenCoords((ImageWorkSpacePt)pt);
        else if (pt instanceof ImagePt)          retval= getScreenCoords((ImagePt)pt);
        else if (pt instanceof ScreenPt)         retval= (ScreenPt)pt;
        else if (pt instanceof ViewPortPt)       retval= getScreenCoords((ViewPortPt)pt);
        else {
            WebAssert.argTst(false, "unknown Pt type");
        }

        return retval;
    }

    public static Pt makePt(Class<? extends Pt> cType,  double x, double y) throws ProjectionException {
        Pt retval= null;
        if      (cType==WorldPt.class)          retval= new WorldPt(x,y);
        else if (cType==ImageWorkSpacePt.class) retval= new ImageWorkSpacePt(x,y);
        else if (cType==ImagePt.class)          retval= new ImagePt(x,y);
        else if (cType==ScreenPt.class)         retval= new ScreenPt((int)x,(int)y);
        else if (cType==ViewPortPt.class)       retval= new ViewPortPt((int)x,(int)y);
        else {
            WebAssert.argTst(false, "unknown Pt type");
        }

        return retval;
    }



    /**
     * Return the screen coordinates given WorldPt
     * @param wpt the world point to translate
     * @return ScreenPt the screen coordinates
     * @throws ProjectionException if the point cannot be projected into a ScreePt
     */
    public ScreenPt getScreenCoords(WorldPt wpt)
                                  throws ProjectionException {

        ImageWorkSpacePt iwpt= getImageWorkSpaceCoords(wpt);
        return  getScreenCoords(iwpt);
    }

    /**
     * Return the screen coordinates given WorldPt and alternate zoom level.
     * Make sure you know what you are doing if you are using this method
     * @param wpt the world point to translate
     * @return Point2D the screen coordinates
     * @throws ProjectionException if the point cannot be projected into a Point2D
     */
    public ScreenPt getScreenCoords(WorldPt wpt, float altZLevel)
                                     throws ProjectionException {
        ImageWorkSpacePt iwpt= getImageWorkSpaceCoords(wpt);
        return  getScreenCoords(iwpt, altZLevel);
    }

    /**
     * Return the screen coordinates given ImagePt
     * @param ipt the image point to translate
     * @return Point2D the screen coordinates
     */
    public ScreenPt getScreenCoords(ImagePt ipt) {
        return getScreenCoords(ipt,getZoomFact());
    }



    /**
     * Return the screen coordinates given ImagePt and alternate zoom level.
     * Make sure you know what you are doing if you are using this method
     * @param ipt the image point to translate
     * @param altZLevel use the passed zoom level instead of the level of the plot
     * @return Point2D the screen coordinates
     */
    public ScreenPt getScreenCoords(ImagePt ipt, float altZLevel) {
        return getScreenCoords(getImageWorkSpaceCoords(ipt),altZLevel);
    }


    public ScreenPt getScreenCoords(ViewPortPt vpt) {
        return new ScreenPt(vpt.getIX()+_viewPortX, vpt.getIY()+_viewPortY);
    }

    /**
     * Return the screen coordinates given ImageWorkSpacePt
     * @param ipt the ImageWorkSpace point to translate
     * @return Point2D the screen coordinates
     */
    public ScreenPt getScreenCoords(ImageWorkSpacePt ipt) {
        return getScreenCoords(ipt,getZoomFact());
    }


    /**
     * Return the screen coordinates given ImageWorkSpacePt
     * and alternate zoom level.
     * Make sure you know what you are doing if you are using this method
     * @param ipt the ImageWorkSpace point to translate
     * @param altZLevel use the passed zoom level instead of the level of the plot
     * @return Point2D the screen coordinates
     */
    public ScreenPt getScreenCoords(ImageWorkSpacePt ipt, float altZLevel) {
        return new ScreenPt((int)((ipt.getX())*altZLevel),
                            (int)((getImageHeight() - (ipt.getY())) *altZLevel));
    }



    /**
     * Return the world coordinates given screen x & y.
     * @param pt the screen coordinates to convert to world coordinates
     * @param outputCoordSys the coordinate system you want this screen coordinates
     *                      translated into
     * @return WorldPt the translated coordinates
     * @throws ProjectionException if the point cannot be projected into a WorldPt
     */
    public WorldPt getWorldCoords(ScreenPt pt, CoordinateSys outputCoordSys) throws  ProjectionException {
        ImageWorkSpacePt iwspt = getImageWorkSpaceCoords(pt);
        return getWorldCoords(iwspt,outputCoordSys);
    }


    /**
     * Return the world coordinates given screen x & y.
     * @param pt the screen coordinates to convert to world coordinates
     * @return WorldPt the translated coordinates
     * @throws ProjectionException if the point cannot be projected into a WorldPt
     */
    public WorldPt getWorldCoords(ScreenPt pt) throws  ProjectionException {
        ImageWorkSpacePt iwspt = getImageWorkSpaceCoords(pt);
        return getWorldCoords(iwspt);
    }

    public WorldPt getWorldCoords(ViewPortPt vpt) throws  ProjectionException {
        ScreenPt spt = getScreenCoords(vpt);
        return getWorldCoords(spt);
    }

    public WorldPt getWorldCoords(ViewPortPt vpt, CoordinateSys outputCoordSys) throws  ProjectionException {
        ScreenPt spt = getScreenCoords(vpt);
        return getWorldCoords(spt, outputCoordSys);
    }

    /**
     * Return the screen coordinates given Pt
     * @param pt the point to translate
     * @return WorldPt the world coordinates
     * @throws ProjectionException if the point cannot be projected into a ScreePt
     */
    public WorldPt getWorldCoords(Pt pt) throws ProjectionException {
        WorldPt retval= null;

        if      (pt instanceof WorldPt)          retval= (WorldPt)pt;
        else if (pt instanceof ImageWorkSpacePt) retval= getWorldCoords((ImageWorkSpacePt)pt);
        else if (pt instanceof ImagePt)          retval= getWorldCoords((ImagePt)pt);
        else if (pt instanceof ScreenPt)         retval= getWorldCoords((ScreenPt)pt);
        else if (pt instanceof ViewPortPt)       retval= getWorldCoords((ViewPortPt)pt);
        else {
            WebAssert.argTst(false, "unknown Pt type");
        }

        return retval;
    }



    /**
     * Return the J2000 sky coordinates given a image x (fsamp) and  y (fline)
     * package in a ImagePt class
     * @param pt the ImageWorkSpacePt
     * @return WorldPt the translated coordinates
     * @throws ProjectionException if the point cannot be projected into an WorldPt
     */
    public WorldPt getWorldCoords(ImageWorkSpacePt pt) throws ProjectionException {
        return getWorldCoords(getImageCoords(pt));
    }
    /**
     * Return the sky coordinates given a image x (fsamp) and  y (fline)
     * package in a ImageWorkSpacePt class
     * @param ipt  the image point
     * @param outputCoordSys The coordiate system to return
     * @return WorldPt the translated coordinates
     * @throws ProjectionException if the point cannot be projected into an WorldPt
     */
    public WorldPt getWorldCoords( ImageWorkSpacePt ipt, CoordinateSys outputCoordSys) throws ProjectionException {
        return getWorldCoords(getImageCoords(ipt),outputCoordSys);
    }


    /**
     * Return the J2000 sky coordinates given a image x (fsamp) and  y (fline)
     * package in a ImagePt class
     * @param pt the ImageWorkSpacePt
     * @return WorldPt the translated coordinates
     * @throws ProjectionException if the point cannot be projected into an WorldPt
     */
    public WorldPt getWorldCoords(ImagePt pt) throws ProjectionException {
        return getWorldCoords(pt, CoordinateSys.EQ_J2000);
    }
    /**
     * Return the sky coordinates given a image x (fsamp) and  y (fline)
     * package in a ImageWorkSpacePt class
     * @param ipt  the image point
     * @param outputCoordSys The coordiate system to return
     * @return WorldPt the translated coordinates
     * @throws ProjectionException if the point cannot be projected into an WorldPt
     */
    public WorldPt getWorldCoords( ImagePt ipt, CoordinateSys outputCoordSys) throws ProjectionException {
//        int decimation= _plotState.getDecimationLevel(_plotState.firstBand());
        int decimation= 1;
        double x= ipt.getX() / decimation;
        double y= ipt.getY() / decimation;
        WorldPt wpt= _projection.getWorldCoords(x - .5F ,y - .5F );
        if (!outputCoordSys.equals(wpt.getCoordSys())) {
            wpt= VisUtil.convert(wpt, outputCoordSys);
        }
        return wpt;
    }



    /**
     * Return a point the represents the passed point with a distance in
     * World coordinates added to it.
     * @param wp the world point WorldPt
     * @param x the x of the world coordinates distance away from the point.
     * @param y the y of the world coordinates distance away from the point.
     * @return ImagePt the new point
     * @throws ProjectionException if the point cannot be projected into an ImagePt
     */
    public ImagePt getDistanceCoords(WorldPt wp, double x, double y)
                                                throws ProjectionException {

       ImageWorkSpacePt iwpt= getImageWorkSpaceCoords(wp);
       ImagePt pt= new ImagePt(iwpt.getX(), iwpt.getY());
        return _projection.getDistanceCoords(pt,x,y);
    }


    /**
     * Return a point the represents the passed point with a distance in
     * Image coordinates added to it.
     * @param pt the initial image point
     * @param x the x of the world coordinates distance away from the point.
     * @param y the y of the world coordinates distance away from the point.
     * @return ImagePt the new point
     * @throws ProjectionException if the point cannot be projected into an ImagePt
     */
    public ImageWorkSpacePt getDistanceCoords(ImageWorkSpacePt pt, double x, double y)
                                  throws ProjectionException {
        return _projection.getDistanceCoords(pt,x,y);
    }

    /**
     * specificly release any reasources held by this object
     * any subclasses who override this method should do a 
     * super.freeResoureces()
     */
    public void freeResources() {
        _alive= false;
    }

    public boolean isAlive() { return _alive; }

    public void setAttribute(String key, Object attribute) {
        _attributes.put(key,attribute);
    }
    public void removeAttribute(String key) {
        _attributes.remove(key);
    }

    public Object getAttribute(String key) {
        return _attributes.get(key);
    }

    public boolean containsAttributeKey(String key) {
        return _attributes.containsKey(key);
    }


    /**
     * Set the level a image will be zoom when it is plotted
     * @param  initialZoomLevel the initial zoom level
     */
    public void setInitialZoomLevel(float initialZoomLevel) {
       _initialZoomLevel= initialZoomLevel;
    }

    /**
     * Get the level a image will be zoom when it is plotted
     * @return float the initial zoom level
     */
    public float getInitialZoomLevel() {
       return _initialZoomLevel;
    }



    /**
     * Get the PlotView.
     * A plot contains a reference to the PlotView that contains it.
     * A plot may be in only one PlotView.
     * @return PlotView the PlotView this plot is in.
     */
    public WebPlotView getPlotView() { return _plotGroup.getPlotView(); }

    /**
     * Set a description of this plot.
     * @param d the plot description
     */
    public void   setPlotDesc(String d) { _plotDesc= d; }

    /**
     * Get the description of this plot.
     * @return String the plot description
     */
    public String getPlotDesc()         { return _plotDesc; }

    /**
     * Get the description of this fits data for this plot.
     * @return String the plot description
     */
    public String getDataDesc()         { return _dataDesc; }


    public void setPercentOpaque(float percentOpaque) {
         _percentOpaque= percentOpaque;
    }

    public float getPercentOpaque() {
         return _percentOpaque;
    }

    public String toString() {
        return getPlotDesc();
    }

    public boolean isRotated() { return _plotState.getRotateType()!= PlotState.RotateType.UNROTATE; }
    public PlotState.RotateType getRotationType() { return _plotState.getRotateType(); }
    public double getRotationAngle() { return _plotState.getRotationAngle(); }

    public boolean isRotatable() {
        boolean retval= false;
        Projection proj= getProjection();
        if (proj!=null) {
            retval= !proj.isWrappingProjection();
            if (retval) {
                retval= !containsAttributeKey(DISABLE_ROTATE_REASON);
            }
        }
        return retval;
    }


    public String getNonRotatableReason() {
        String retval;
        if (!isRotatable()) {
            Projection p= getProjection();
            if (containsAttributeKey(DISABLE_ROTATE_REASON)) {
                if (getAttribute(DISABLE_ROTATE_REASON) instanceof String) {
                    retval= (String)getAttribute(DISABLE_ROTATE_REASON);
                }
                else {
                    retval= "FITS image can't be rotated";
                }
            }
            else {
                if (p.isWrappingProjection()) {
                    retval= "FITS image with projection of type " +
                            p.getProjectionName() +
                            " can't be rotated";

                }
                else {
                    retval= "FITS image can't be rotated";
                }
            }
        }
        else {
           retval= null;
        }
        return retval;
    }

    private static final double    DtoR      = Math.PI/180.0;
    private static final double    RtoD      = 180.0/Math.PI;

    public boolean coordsWrap(WorldPt wp1, WorldPt wp2) {
        boolean retval= false;
        if (_projection.isWrappingProjection()) {
            try {
                double worldDist= computeDistance(wp1,wp2);
                double pix= _projection.getPixelWidthDegree();
                double value1= worldDist/pix;

                ImageWorkSpacePt ip1= getImageWorkSpaceCoords(wp1);
                ImageWorkSpacePt ip2= getImageWorkSpaceCoords(wp2);

                double xdiff= ip1.getX()-ip2.getX();
                double ydiff= ip1.getY()-ip2.getY();
                double imageDist= Math.sqrt(xdiff*xdiff + ydiff*ydiff);

                //System.out.println("worldDist / pix="+value1 +
                //                   "    imageDist="+ imageDist);
                retval= ((imageDist / value1) > 3);
            } catch (ProjectionException e) {
                retval= false;
            }
        }
        return retval;
    }

    private static double computeDistance(WorldPt p1, WorldPt p2) {
        double lon1Radius  = p1.getLon() * DtoR;
        double lon2Radius  = p2.getLon() * DtoR;
        double lat1Radius  = p1.getLat() * DtoR;
        double lat2Radius  = p2.getLat() * DtoR;
        double cosine =
                Math.cos(lat1Radius)*Math.cos(lat2Radius)*
                        Math.cos(lon1Radius-lon2Radius)
                        + Math.sin(lat1Radius)*Math.sin(lat2Radius);

        if (Math.abs(cosine) > 1.0)
            cosine = cosine/Math.abs(cosine);
        return RtoD*Math.acos(cosine);
    }


    public void setImagePixPadding(Dimension padDim) {
        _padDim= padDim;
        setOffsetX(_padDim.getWidth());
        setOffsetY(_padDim.getHeight());
        _plotGroup.computeMinMax();
    }

    public void setArcSecPadding(double padWidth, double padHeight) {
        double scale= getPixelScale();
        setImagePixPadding( new Dimension((int)(padWidth/scale),  (int)(padHeight/scale)));
    }

    /**
     * This method will force total size to be a with and a height.  If the image is less
     * than the size passed then the image is padded.  If the image size is greater then the method
     * does nothing.
     * @param imWidth new width in arcsec
     * @param imHeight new height in arcsec
     */
    public void setImageArcSecSize(double imWidth, double imHeight) {
        double scale= getPixelScale();
        int newWidth= (int)(imWidth/scale);
        int newHeight= (int)(imHeight/scale);
        if (newWidth>=_dataWidth && newHeight>=_dataHeight) {
            setImagePixPadding(new Dimension((newWidth-_dataWidth)/2, (newHeight-_dataHeight)/2 ));
        }


    }


    public Dimension getPaddingDimension() { return _padDim; }



    // =======================================================================
   // ------------------    Private / Protected / Package Methods   ---------
   // =======================================================================

   void setOffsetX(int x) {_offsetX= x;}
   int  getOffsetX() {return _offsetX;}

   void setOffsetY(int y) {_offsetY= y;}
   int  getOffsetY() {return _offsetY;}


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
