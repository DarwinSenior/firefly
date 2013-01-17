package edu.caltech.ipac.firefly.visualize;
/**
 * User: roby
 * Date: 9/21/11
 * Time: 11:41 AM
 */


import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import edu.caltech.ipac.firefly.util.Dimension;
import edu.caltech.ipac.firefly.util.WebAssert;
import edu.caltech.ipac.util.StringUtils;

import java.util.List;

/**
 * @author Trey Roby
 */
public class ZoomUtil {


    public static final float[] _levels= { .03125F, .0625F, .125F,.25F,.5F, .75F, 1F,2F,3F, 4F,5F, 6F,
                                           7F,8F, 9f, 10F, 11F, 12F, 13F, 14F, 15F, 16F,32F};
//    public static final float[] _levels= { .03125F, .0625F, .125F,.25F,.5F, .75F, 1F,2F, 4F,
//                                           8F, 16F,32F};

    private static final NumberFormat _nf= NumberFormat.getFormat(".###");
    private static final NumberFormat _nfLarge= NumberFormat.getFormat(".#");
    private static final int DEFAULT_MARGIN = 30;

    public static void zoomGroup(final WebPlot.ZDir dir) {
        MiniPlotWidget mpw= AllPlots.getInstance().getMiniPlotWidget();
        final WebPlot plot= AllPlots.getInstance().getMiniPlotWidget().getCurrentPlot();
        if (mpw.isExpandedAsOne()) {
//            plot.getPlotGroup().zoom(dir);
            final float targetZLevel= getNextZoomLevel(plot.getZoomFact(), dir);
            plot.getPlotView().setZoomTo(targetZLevel, false);
        }
        else {
            final float targetZLevel= getNextZoomLevel(plot.getZoomFact(), dir);
            zoomGroupTo(targetZLevel);
        }
    }

    /**
     * Zoom the whole group to the same level.  However if the others in the group have a different arcsec / pixel then
     * try to match the arcsec / pixel zoom over the level.
     * @param level the new level for the primary plot
     */
    public static void zoomGroupTo(final float level) {
        final AllPlots allPlots= AllPlots.getInstance();
        final List<MiniPlotWidget> list= allPlots.getActiveGroupList(false);

        final WebPlot selectedPlot= allPlots.getMiniPlotWidget().getCurrentPlot();

        // determine the target zoom level and what the arcsec / pix it will be at that level
        final float targetASpix= getArcSecPerPix(selectedPlot, level);


        for(MiniPlotWidget mpwItem : list) {
            final MiniPlotWidget mpw= mpwItem;
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    if (mpw.isInit()) {
                        WebPlot plot= mpw.getCurrentPlot();
                        try {
                            float newZoomLevel= level;
                            if (targetASpix!=0F) {
                                float plotLevel= getZoomLevelForScale(plot, targetASpix);

                                // we want each plot to have the same arcsec / pixel as the target level
                                // if the new level is only slightly different then use the target level
                                newZoomLevel= (Math.abs(plotLevel-level)<.01) ? level : plotLevel;
                            }
                            mpw.getPlotView().setZoomTo(newZoomLevel, false);
                        } catch (NullPointerException e) {
                            //todo: handle null pointer exception
                        }
                    }
                    else {
                        mpw.addRequestMod(WebPlotRequest.ZOOM_TYPE, ZoomType.ARCSEC_PER_SCREEN_PIX.toString());
                        mpw.addRequestMod(WebPlotRequest.ZOOM_ARCSEC_PER_SCREEN_PIX, targetASpix+"");
                    }
                }
            });
        }
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                allPlots.fireAllPlotTasksCompleted();
            }
        });

    }


    public static float getArcSecPerPix(WebPlot p) { return getArcSecPerPix(p,p.getZoomFact()); }

    public static float getArcSecPerPix(WebPlot p, float zoomFact) { return (float) p.getPixelScale() / zoomFact; }

    public static float getZoomLevelForScale(WebPlot p, float arcsecPerPix) {
        return (float)p.getPixelScale() / arcsecPerPix;
    }


    public static String convertZoomToString(float level) {
        String retval;
        int zfInt= (int)(level*10000);

        if (zfInt>=10000)     retval= getOnePlusLevelDesc(level); // if level > then 1.0
        else if (zfInt==312)  retval= "1/32x";     // 1/32
        else if (zfInt==625)  retval= "1/16x";     // 1/16
        else if (zfInt==1250) retval= "1/8x";      // 1/8
        else if (zfInt==2500) retval= "&#188;x";   // 1/4
        else if (zfInt==7500) retval= "&#190;x";   // 3/4
        else if (zfInt==5000) retval= "&#189;x";   // 1/2
        else                  retval= _nf.format(level)+"x";

        return retval;
    }

    private static String getOnePlusLevelDesc(float level) {
        String retval;
        float remainder= level % 1;
        if (remainder < .1 || remainder>.9) {
            retval= (Math.round(level))+"x";
        }
        else {
            retval= _nfLarge.format(level)+"x";
        }
        return retval;
    }

    public static float getNextZoomLevel(float currLevel, WebPlot.ZDir dir) {
        float newLevel= 1;
        boolean found;
        if (dir==WebPlot.ZDir.UP) {
            for(float _tstLevel : _levels) {
                found= (_tstLevel>currLevel);
                if (found) {
                    newLevel= _tstLevel;
                    break;
                }
            }
        }
        else if (dir== WebPlot.ZDir.ORIGINAL) {
            newLevel= 1;
        }
        else if (dir==WebPlot.ZDir.DOWN) {
            for(int i= _levels.length-1; (i>=0); i--) {
                found= (_levels[i]<currLevel);
                if (found) {
                    newLevel= _levels[i];
                    break;
                }
            }
        }
        else {
            WebAssert.argTst(false, "unsupported ZDir");
        }
        return newLevel;
    }

    public static float getEstimatedFullZoomFactor(WebPlot plot, Dimension screenDim) {
        return getEstimatedFullZoomFactor(plot,screenDim,plot.getZoomFact());
    }

    public static float getEstimatedFullZoomFactor(WebPlot plot, Dimension screenDim, VisUtil.FullType fullType) {
        return getEstimatedFullZoomFactor(plot,screenDim,fullType,plot.getZoomFact(), DEFAULT_MARGIN);

    }

    public static float getEstimatedFullZoomFactor(WebPlot plot, Dimension screenDim, float tryMinFactor) {
        return getEstimatedFullZoomFactor(plot,screenDim,VisUtil.FullType.SMART,tryMinFactor, DEFAULT_MARGIN);

    }

    public static float getEstimatedFullZoomFactor(WebPlot plot,
                                                   Dimension screenDim,
                                                   VisUtil.FullType fullType,
                                                   float tryMinFactor, int margin) {
        if (plot==null) return 1F;
        int screenWidth= screenDim.getWidth();
        int screenHeight= screenDim.getHeight();
        int workWidth=  screenWidth> 50  ? screenWidth- margin : screenWidth;
        int workHeight= screenHeight> 50 ? screenHeight- margin : screenHeight;

        WebPlotView pv= plot.getPlotView();
        if (pv.containsAttributeKey(WebPlot.EXPANDED_TO_FIT_TYPE)) {
            try {
                String s= pv.getAttribute(WebPlot.EXPANDED_TO_FIT_TYPE).toString();
                if (!StringUtils.isEmpty(s)) fullType= Enum.valueOf(VisUtil.FullType.class,s);
            } catch (IllegalArgumentException e) {
                fullType= VisUtil.FullType.ONLY_WIDTH;
            }
        }
        return VisUtil.getEstimatedFullZoomFactor(fullType,
                                                  plot.getImageDataWidth(),
                                                  plot.getImageDataHeight(),
                                                  workWidth, workHeight, tryMinFactor);

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
