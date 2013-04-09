package edu.caltech.ipac.fftools.core;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.caltech.ipac.firefly.commands.CatalogSearchCmd;
import edu.caltech.ipac.firefly.commands.ImageSelectCmd;
import edu.caltech.ipac.firefly.commands.ImageSelectDropDownCmd;
import edu.caltech.ipac.firefly.commands.IrsaCatalogDropDownCmd;
import edu.caltech.ipac.firefly.commands.OverviewHelpCmd;
import edu.caltech.ipac.firefly.core.Application;
import edu.caltech.ipac.firefly.core.Creator;
import edu.caltech.ipac.firefly.core.DefaultRequestHandler;
import edu.caltech.ipac.firefly.core.GeneralCommand;
import edu.caltech.ipac.firefly.core.LoginManager;
import edu.caltech.ipac.firefly.core.LoginManagerImpl;
import edu.caltech.ipac.firefly.core.MenuGenerator;
import edu.caltech.ipac.firefly.core.RequestHandler;
import edu.caltech.ipac.firefly.core.layout.LayoutManager;
import edu.caltech.ipac.firefly.core.layout.Region;
import edu.caltech.ipac.firefly.ui.GwtUtil;
import edu.caltech.ipac.firefly.ui.panels.Toolbar;
import edu.caltech.ipac.firefly.visualize.AllPlots;
import edu.caltech.ipac.firefly.visualize.Vis;

import java.util.HashMap;
import java.util.Map;

public class FFToolsStandaloneCreator implements Creator {

    public static final String APPLICATION_MENU_PROP = "AppMenu";
//    private static final boolean SUPPORT_LOGIN= true;
    private static final boolean SUPPORT_LOGIN= false;
//    private static final String CATALOG_NAME= "Catalogs";
    private static final String CATALOG_NAME= IrsaCatalogDropDownCmd.COMMAND_NAME;
//    private Toolbar.RequestButton catalog= null;
    private TabPlotWidgetFactory factory= new TabPlotWidgetFactory();
    private StandaloneUI aloneUI;
    private StandaloneToolBar toolbar;
    IrsaCatalogDropDownCmd catalogDropDownCmd;

    public FFToolsStandaloneCreator() {
    }






    public boolean isApplication() { return true; }


    public void activateToolbarCatalog() {
        DeferredCommand.addCommand(new Command() {
            public void execute() { catalogDropDownCmd.execute();  }
        } );
    }
    public StandaloneUI getStandaloneUI() { return aloneUI; }

    public Toolbar getToolBar() {
        // todo

        toolbar.setToolbarTopSizeDelta(47);
        GwtUtil.setStyles(toolbar, "zIndex", "10", "position", "absolute");
        toolbar.setVisible(true);
        toolbar.setWidth("100%");
        AllPlots.getInstance().setToolBarIsPopup(false);
        AllPlots.getInstance().setMouseReadoutWide(true);

        Vis.init(new Vis.InitComplete() {
            public void done() {
                Map<String, GeneralCommand> map= Application.getInstance().getCommandTable();
                map.putAll(AllPlots.getInstance().getCommandMap());
                MenuGenerator gen = MenuGenerator.create(map,false);
                gen.createToolbarFromProp(APPLICATION_MENU_PROP, toolbar);
                Widget visToolBar= AllPlots.getInstance().getMenuBarInline();
                FFToolsStandaloneLayoutManager lm=
                        (FFToolsStandaloneLayoutManager)Application.getInstance().getLayoutManager();
                lm.getMenuLines().clear();


                DockLayoutPanel controlLine= new DockLayoutPanel(Style.Unit.PX);
                controlLine.addEast(lm.getLayoutSelector(), 185);
                controlLine.add(visToolBar);
                controlLine.setHeight("39px");

//                lm.getMenuLines().add(visToolBar);
                lm.getMenuLines().add(controlLine);
                lm.getMenuLines().add(Application.getInstance().getToolBar().getWidget());
                AllPlots.getInstance().setMenuBarMouseOverHidesReadout(false);

                Application.getInstance().getToolBar().getWidget().addStyleName("tool-bar-widget");
                visToolBar.addStyleName("vis-tool-bar-widget");
                ImageSelectCmd isCmd= (ImageSelectCmd)AllPlots.getInstance().getCommand(ImageSelectCmd.CommandName);
                isCmd.setUseDropdownCmd(map.get(ImageSelectDropDownCmd.COMMAND_NAME));
                isCmd.setPlotWidgetFactory(factory);


                Region helpReg= lm.getRegion(LayoutManager.VIS_MENU_HELP_REGION);
                helpReg.setDisplay(AllPlots.getInstance().getMenuBarInlineStatusLine());
            }
        });
        return toolbar;
    }



    public Map makeCommandTable() {
        // todo

        toolbar = new StandaloneToolBar();
        aloneUI= new StandaloneUI(factory);
        factory.setStandAloneUI(aloneUI);
        Toolbar.RequestButton catalog = new Toolbar.RequestButton(CATALOG_NAME, IrsaCatalogDropDownCmd.COMMAND_NAME,
                                                                  "Catalogs", "Search and load IRSA catalog");
        toolbar.addButton(catalog, 0);
        ImageSelectDropDownCmd isddCmd= new ImageSelectDropDownCmd();
        isddCmd.setPlotWidgetFactory(factory);

        catalogDropDownCmd= new IrsaCatalogDropDownCmd() {
            @Override
            protected void catalogDropSearching() {
                aloneUI.eventSearchingCatalog();
            }

            @Override
            protected void doExecute() {
                aloneUI.eventOpenCatalog();
                super.doExecute();
            }
        };


        HashMap<String, GeneralCommand> commands = new HashMap<String, GeneralCommand>();
        addCommand(commands, catalogDropDownCmd);
        addCommand(commands, new OverviewHelpCmd());
        commands.put(FFToolsImageCmd.COMMAND, new FFToolsImageCmd(factory, aloneUI));
//        commands.put(FFToolsCatalogCmd.COMMAND, new FFToolsCatalogCmd(aloneUI));
        commands.put(CatalogSearchCmd.COMMAND_NAME, new CatalogSearchCmd());
        commands.put(ImageSelectDropDownCmd.COMMAND_NAME, isddCmd);

        return commands;
    }


    private void addCommand(HashMap<String, GeneralCommand> maps, GeneralCommand c) {
        maps.put(c.getName(), c);
    }



    public RequestHandler makeCommandHandler() { return new DefaultRequestHandler(); }

    public LoginManager makeLoginManager() {
        return SUPPORT_LOGIN ? new LoginManagerImpl() : null;
    }

    public String getAppDesc() { return "IRSA general FITS/Catalog Viewer"; }
    public String getAppName() {
        return "fftools";
    }


    public LayoutManager makeLayoutManager() { return new FFToolsStandaloneLayoutManager(); }

    public String getLoadingDiv() { return "application"; }


    private class StandaloneToolBar extends Toolbar {
        @Override
        protected boolean getShouldExpandDefault() {
            StandaloneUI.Mode mode= aloneUI.getMode();
            return mode==StandaloneUI.Mode.IMAGE_ONLY ||
                   mode==StandaloneUI.Mode.CATALOG_START ||
                   mode==StandaloneUI.Mode.INIT;
        }

        @Override
        protected void expandDefault() {
            StandaloneUI.Mode mode= aloneUI.getMode();
            if (mode== StandaloneUI.Mode.IMAGE_ONLY) {
                aloneUI.expandImage();
            }
            else {
                if (mode==StandaloneUI.Mode.CATALOG_START) {
                    this.select(CATALOG_NAME);
                }
                else {
                    this.select(ImageSelectDropDownCmd.COMMAND_NAME);
                }
            }
        }

        @Override
        protected boolean getShouldHideCloseOnDefaultTab() {
            StandaloneUI.Mode mode= aloneUI.getMode();
            return mode==StandaloneUI.Mode.CATALOG_START ||
                   mode==StandaloneUI.Mode.INIT;
        }

        @Override
        protected boolean isDefaultTabSelected() {
            StandaloneUI.Mode mode= aloneUI.getMode();
            String cmd= getSelectedCommand();
            if (cmd==null) {
                return false;
            }
            else if (mode==StandaloneUI.Mode.CATALOG_START) {
                return cmd.equals(CATALOG_NAME);
            }
            else if (mode==StandaloneUI.Mode.INIT) {
                return cmd.equals(ImageSelectDropDownCmd.COMMAND_NAME);
            }
            else {
                return false;
            }
        }
    }


}