package edu.caltech.ipac.firefly.commands;

import com.google.gwt.user.client.ui.Image;
import edu.caltech.ipac.firefly.resbundle.images.VisIconCreator;
import edu.caltech.ipac.firefly.visualize.MiniPlotWidget;


public class FlipRightCmd extends FlipCmd {
    public static final String CommandName= "flipRight";

    public FlipRightCmd(MiniPlotWidget mpw) { super(CommandName,mpw, FlipDir.RIGHT); }

    @Override
    public Image createCmdImage() {
        VisIconCreator ic= VisIconCreator.Creator.getInstance();
        String iStr= getIconProperty();
        if (iStr!=null && iStr.equals(CommandName+".Icon"))  {
            return new Image(ic.getSideRightArrow());
        }
        return null;
    }

}