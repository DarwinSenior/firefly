package edu.caltech.ipac.firefly.visualize.draw;

import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
/**
 * User: roby
 * Date: Dec 15, 2008
 * Time: 1:12:36 PM
 */


/**
 * @author Trey Roby
 */
public class GWTRec extends GWTShape {

    private final int _x;
    private final int _y;
    private final int _width;
    private final int _height;

//======================================================================
//----------------------- Constructors ---------------------------------
//======================================================================

    public GWTRec(Color color,
                  boolean front,
                  int lineWidth,
                  int x,
                  int y,
                  int width,
                  int height) {
        super(color,front,lineWidth);
        _x= x;
        _y= y;
        _width= width;
        _height= height;
    }

//======================================================================
//----------------------- Public Methods -------------------------------
//======================================================================

    public void draw(GWTCanvas surfaceW) {
        surfaceW.setLineWidth(getLineWidth());
        surfaceW.setStrokeStyle(getColor());
        surfaceW.beginPath();
        surfaceW.moveTo(_x,_y);
        surfaceW.lineTo(_x+_width,_y);
        surfaceW.lineTo(_x+_width,_y+_height);
        surfaceW.lineTo(_x,_y+_height);
        surfaceW.lineTo(_x,_y);
        surfaceW.closePath();
        surfaceW.stroke();
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

