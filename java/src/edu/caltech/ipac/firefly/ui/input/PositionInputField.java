package edu.caltech.ipac.firefly.ui.input;

import edu.caltech.ipac.firefly.data.form.PositionFieldDef;
import edu.caltech.ipac.util.dd.ValidationException;
import edu.caltech.ipac.visualize.plot.WorldPt;

/**
 * Created by IntelliJ IDEA.
 * User: tlau
 * Date: Nov 29, 2010
 * Time: 2:43:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PositionInputField extends TextBoxInputField  {
    private final PositionFieldDef _positionFieldDef;
//======================================================================
//----------------------- Constructors ---------------------------------
//======================================================================

    public PositionInputField(PositionFieldDef fd) {
        super(fd);
        _positionFieldDef = fd;

        getFieldLabel();
    }


    @Override
    public void setValue(String v) {
        WorldPt wp= WorldPt.parse(v);
        if (wp!=null) {

            String val= PositionFieldDef.formatPosForTextField(wp);

            try {
                _positionFieldDef.validate(val);
            } catch (ValidationException e) {
                // ignore
            }

            super.setValue(val);
        }
        else {
            _positionFieldDef.setObjectName(v);
            super.setValue(v);
        }
    }

    public WorldPt getPosition() {
        return _positionFieldDef.getPosition();
    }


    public PositionFieldDef.Input getInputType() {
        return _positionFieldDef.getInputType();

    }

    public String getObjectName() {
        return _positionFieldDef.getObjectName();
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