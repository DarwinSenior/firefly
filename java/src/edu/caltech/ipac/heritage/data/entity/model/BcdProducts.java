package edu.caltech.ipac.heritage.data.entity.model;
// Generated Oct 14, 2008 7:38:00 PM by Hibernate Tools 3.2.1.GA


import java.util.Date;

/**
 * BcdProducts generated by hbm2java
 */
public class BcdProducts  implements java.io.Serializable {


     private int bcdid;
     private RequestInformation requestinformation;
     private DceInformation dceinformation;
     private int dceinsid;
     private short plscriptid;
     private int campid;
     private String heritagefilename;
     private Integer ptid;
     private String ptcomment;
     private Integer filesize;
     private String checksum;
     private short primaryfield;
     private short channum;
     private Date scet;
     private String instrument;
     private short aperture;
     private String fovname;
     private String reqmode;
     private double ra;
     private double dec;
     private W3Info w3info;
     private String archivefilename;

    public BcdProducts() {
    }

	
    public BcdProducts(int bcdid, RequestInformation requestinformation, DceInformation dceinformation, int dceinsid, short plscriptid, int campid, short primaryfield, short channum, Date scet, short aperture, String fovname, String reqmode, double ra, double dec, W3Info w3info, String archivefilename) {
        this.bcdid = bcdid;
        this.requestinformation = requestinformation;
        this.dceinformation = dceinformation;
        this.dceinsid = dceinsid;
        this.plscriptid = plscriptid;
        this.campid = campid;
        this.primaryfield = primaryfield;
        this.channum = channum;
        this.scet = scet;
        this.aperture = aperture;
        this.fovname = fovname;
        this.reqmode = reqmode;
        this.ra = ra;
        this.dec = dec;
        this.w3info = w3info;
        this.archivefilename = archivefilename;
    }
    public BcdProducts(int bcdid, RequestInformation requestinformation, DceInformation dceinformation, int dceinsid, short plscriptid, int campid, String heritagefilename, Integer ptid, String ptcomment, Integer filesize, String checksum, short primaryfield, short channum, Date scet, String instrument, short aperture, String fovname, String reqmode, double ra, double dec, W3Info w3info, String archivefilename) {
       this.bcdid = bcdid;
       this.requestinformation = requestinformation;
       this.dceinformation = dceinformation;
       this.dceinsid = dceinsid;
       this.plscriptid = plscriptid;
       this.campid = campid;
       this.heritagefilename = heritagefilename;
       this.ptid = ptid;
       this.ptcomment = ptcomment;
       this.filesize = filesize;
       this.checksum = checksum;
       this.primaryfield = primaryfield;
       this.channum = channum;
       this.scet = scet;
       this.instrument = instrument;
       this.aperture = aperture;
       this.fovname = fovname;
       this.reqmode = reqmode;
       this.ra = ra;
       this.dec = dec;
       this.w3info = w3info;
       this.archivefilename = archivefilename;
    }
   
    public int getBcdid() {
        return this.bcdid;
    }
    
    public void setBcdid(int bcdid) {
        this.bcdid = bcdid;
    }
    public RequestInformation getRequestinformation() {
        return this.requestinformation;
    }
    
    public void setRequestinformation(RequestInformation requestinformation) {
        this.requestinformation = requestinformation;
    }
    public DceInformation getDceinformation() {
        return this.dceinformation;
    }
    
    public void setDceinformation(DceInformation dceinformation) {
        this.dceinformation = dceinformation;
    }
    public int getDceinsid() {
        return this.dceinsid;
    }
    
    public void setDceinsid(int dceinsid) {
        this.dceinsid = dceinsid;
    }
    public short getPlscriptid() {
        return this.plscriptid;
    }
    
    public void setPlscriptid(short plscriptid) {
        this.plscriptid = plscriptid;
    }
    public int getCampid() {
        return this.campid;
    }
    
    public void setCampid(int campid) {
        this.campid = campid;
    }
    public String getHeritagefilename() {
        return this.heritagefilename;
    }
    
    public void setHeritagefilename(String heritagefilename) {
        this.heritagefilename = heritagefilename;
    }
    public Integer getPtid() {
        return this.ptid;
    }
    
    public void setPtid(Integer ptid) {
        this.ptid = ptid;
    }
    public String getPtcomment() {
        return this.ptcomment;
    }
    
    public void setPtcomment(String ptcomment) {
        this.ptcomment = ptcomment;
    }
    public Integer getFilesize() {
        return this.filesize;
    }
    
    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }
    public String getChecksum() {
        return this.checksum;
    }
    
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    public short getPrimaryfield() {
        return this.primaryfield;
    }
    
    public void setPrimaryfield(short primaryfield) {
        this.primaryfield = primaryfield;
    }
    public short getChannum() {
        return this.channum;
    }
    
    public void setChannum(short channum) {
        this.channum = channum;
    }
    public Date getScet() {
        return this.scet;
    }
    
    public void setScet(Date scet) {
        this.scet = scet;
    }
    public String getInstrument() {
        return this.instrument;
    }
    
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    public short getAperture() {
        return this.aperture;
    }
    
    public void setAperture(short aperture) {
        this.aperture = aperture;
    }
    public String getFovname() {
        return this.fovname;
    }
    
    public void setFovname(String fovname) {
        this.fovname = fovname;
    }
    public String getReqmode() {
        return this.reqmode;
    }
    
    public void setReqmode(String reqmode) {
        this.reqmode = reqmode;
    }
    public double getRa() {
        return this.ra;
    }
    
    public void setRa(double ra) {
        this.ra = ra;
    }
    public double getDec() {
        return this.dec;
    }
    
    public void setDec(double dec) {
        this.dec = dec;
    }
    public W3Info getW3info() {
        return this.w3info;
    }
    
    public void setW3info(W3Info w3info) {
        this.w3info = w3info;
    }
    public String getArchivefilename() {
        return this.archivefilename;
    }
    
    public void setArchivefilename(String archivefilename) {
        this.archivefilename = archivefilename;
    }




}


