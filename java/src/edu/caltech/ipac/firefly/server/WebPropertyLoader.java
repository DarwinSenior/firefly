package edu.caltech.ipac.firefly.server;

import edu.caltech.ipac.firefly.server.util.Logger;
import edu.caltech.ipac.util.AppProperties;
import edu.caltech.ipac.util.FileUtil;
import edu.caltech.ipac.util.action.Prop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Date: Nov 9, 2007
 *
 * @author loi
 * @version $Id: WebPropertyLoader.java,v 1.12 2012/09/06 22:42:46 loi Exp $
 */
public class WebPropertyLoader {


    public static final String ACCESSABLE_SERVER_PROPS= "client.accessible.props";
    public static final String WEBAPP_PROPERTIES = "webapp-properties";
    public static final String WEB_PROPERTIES_DIR = "properties-dir";
    public static final String THIS_JAR = "firefly.jar";
    public static final String SERVER_PROP_FILE = "_server.prop";
    private static final Properties _webPdb= new Properties();
//    private static final Properties _webPdb= null;


//    public static HashMap<String,String> getPropertyMap() {
//        System.out.println("Getting properties...");
//        return AppProperties.convertMainPropertiesToMap();
//    }



    public static String getAllPropertiesAsString() {
        return AppProperties.convertPropertiesToString(_webPdb);
    }


    /**
     * Loads all of the properties file in the given resources directory.
     * It will search first the class path, and then from file system.
     * @param resourcesDir the directory where the jars are found
     */
    public static void loadDirectoryProperties(String resourcesDir) {

        try {
            URL url = WebPropertyLoader.class.getResource(resourcesDir );
            Properties targetPdb;

            File resDir;
            if ( url != null ) {
                resDir = new File(url.toURI());
            } else {
                resDir = new File(resourcesDir);
            }
            List<String> logList= new ArrayList<String>(50);
            if (resDir.isDirectory()) {
                File[] props = resDir.listFiles(new FilenameFilter(){
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".prop") || name.endsWith(".properties");
                            }
                        });
                for(File f : props) {
                    try {
                        boolean serverOnly= f.getName().equals(SERVER_PROP_FILE );
                        targetPdb= serverOnly ? null : _webPdb;
                        AppProperties.addApplicationProperties(f, false,targetPdb);
                        logList.add( (serverOnly ? "Loaded server only file: ": "Loaded file: ") +f.getName() );
                    } catch (IOException e) {
                        logList.add("Could not load: " + f.getPath());
                        logList.add("        "+ e.toString());
                    }
                }
            }
            Logger.info(logList.toArray(new String[logList.size()]));
        } catch (URISyntaxException e) {
            Logger.warn("Unable to load resources from directory:" + resourcesDir, e.toString());
        } catch (IllegalArgumentException e) {
            Logger.warn("Unable to load resources from directory:" + resourcesDir, e.toString());
        }
    }


    public static void loadAllProperties(String resourcesDir) {
        URL url= getThisClassURL();
        String urlStr= null;
        try {
            urlStr = URLDecoder.decode(url.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int start= urlStr.indexOf('/');
        int end= urlStr.lastIndexOf('!');
        String fileStr;
        if (start < end) {
            fileStr= urlStr.substring(start, end);
            File jarFile= new File(fileStr);
            File jarsDir= jarFile.getParentFile();
            loadAllProps(new File(jarsDir,THIS_JAR), jarsDir);
        }
        else {
            System.out.println("Installer: Not a standard installation.");
            System.out.println("           "+ getThisClassFileName() +
                               " is not in a jar file.");
        }

        loadDirectoryProperties(resourcesDir);
        


        String aProps[]= Prop.getItems(ACCESSABLE_SERVER_PROPS);
        if (aProps != null) {
            for(String prop : aProps) {
                String value= AppProperties.getProperty(prop, null);
                if (value!=null) _webPdb.put(prop,value);
            }
        }

        // load runtime config properties if a client_override.prop exist.
        File clientOverride = ServerContext.getConfigFile("client_override.prop");
        if (clientOverride != null && clientOverride.canRead()) {
            System.out.println("Loading client_override.prop file...");
            loadProps(clientOverride, _webPdb);
        }
    }

    private static void loadProps(File source, Properties dest) {

        Properties props = new Properties();
        Reader sreader = null;
        try {
            sreader = new BufferedReader(new FileReader(source));
            props.load(sreader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.silentClose(sreader);
        }

        if (props.size() > 0) {
            dest.putAll(props);
        }
    }

    private static void loadAllProps(File thisJarFile,  File jarDir) {
        File jarFiles[]= FileUtil.listJarFiles(jarDir);
        if (thisJarFile!=null)  loadJar(thisJarFile);

        for(File jarFile : jarFiles) {
            if (!jarFile.equals(thisJarFile))  loadJar(jarFile);
        }
    }


    private static void loadJar(File jarFile) {
        try {
            loadPropertiesFromJar(new JarFile(jarFile));
        } catch (IOException e) {
            System.out.println("Could not open: "+jarFile.getPath());
        }
    }

    private static void loadPropertiesFromJar(JarFile jf) throws IOException {
        ZipEntry ze;
        InputStream is;
        String name;
        Properties targetPdb;

        Attributes att= jf.getManifest().getAttributes(WEBAPP_PROPERTIES);
        if (att!=null && att.containsKey(new Attributes.Name(WEB_PROPERTIES_DIR ))) {
            String directory= att.getValue(WEB_PROPERTIES_DIR );
            String dirStr= directory + "/";
            Enumeration<JarEntry> entries= jf.entries();
            List<String> logList= new ArrayList<String>(50);
            while (entries.hasMoreElements()) {
                ze= entries.nextElement();
                name= ze.getName();
                if (!ze.isDirectory() && name.startsWith(dirStr)) {
                    boolean serverOnly= name.endsWith(SERVER_PROP_FILE );
                    targetPdb= serverOnly ? null : _webPdb;
                    if (name.endsWith(".prop") || name.endsWith(".properties")) {
                        try {
                            is= jf.getInputStream(ze);
                            AppProperties.addApplicationProperties(is,targetPdb);
                            logList.add( (serverOnly ? "Loaded server only file: ": "Loaded file: ") +name );
                        } catch (IOException e) {
                            logList.add("Could not load: " + name);
                            logList.add("        "+ e.toString());
                        }
                    }
                }
            }
            Logger.info(logList.toArray(new String[logList.size()]));
            FileUtil.silentClose(jf);
        }
    }






    private static URL getThisClassURL() {
        return WebPropertyLoader.class.getClassLoader().getResource(getThisClassFileName());
    }

    private static String getThisClassFileName() {
        String cName= WebPropertyLoader.class.getName();
        return cName.replace(".", "/") + ".class";
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
