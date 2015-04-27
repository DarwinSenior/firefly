/*
 * License information at https://github.com/Caltech-IPAC/firefly/blob/master/License.txt
 */
package edu.caltech.ipac.firefly.data;
/**
 * User: roby
 * Date: 2/18/14
 * Time: 2:35 PM
 */


import edu.caltech.ipac.firefly.util.event.Name;

import java.io.Serializable;

/**
 * @author Trey Roby
 */
public class ServerEvent implements Serializable {
    public enum Scope {CLIENT, CHANNEL, SERVER}
    public static final String SERVER_TERM_ID = "-1";
    public enum DataType {JSON, BG_STATUS, STRING};

    private Name name;
    private EventTarget target;
    private DataType dataType = DataType.STRING;
    private Serializable data;
    private String from;

//======================================================================
//----------------------- Constructors ---------------------------------
//======================================================================
    public ServerEvent() {}

    public ServerEvent(Name name, Scope scope, Serializable data) {
        this(name, new EventTarget(scope), DataType.JSON, data, null);
    }

    public ServerEvent(Name name, Scope scope, DataType dataType, Serializable data) {
        this(name, new EventTarget(scope), dataType, data, null);
    }

    public ServerEvent(Name name, EventTarget target, Serializable data) {
        this(name, target, DataType.BG_STATUS, data, SERVER_TERM_ID);
    }

    public ServerEvent(Name name, EventTarget target, DataType dataType, Serializable data) {
        this(name, target, dataType, data, SERVER_TERM_ID);
    }

    public ServerEvent(Name name, EventTarget target, DataType dataType, Serializable data, String from) {
        this.name = name;
        this.target = target;
        this.dataType = dataType;
        this.data = data;
        this.from = from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Name getName() {
        return name;
    }

    public EventTarget getTarget() {
        return target;
    }

    public Serializable getData() {
        return data;
    }

    public String getFrom() {
        return from;
    }

//====================================================================
//
//====================================================================

    public static class EventTarget implements Serializable {

        private Scope scope;
        private String termID;
        private String channel;

        public EventTarget() {}

        public EventTarget(Scope scope) {
            this.scope = scope;
        }

        /**
         * This is typically used on the server-side where termID
         * and channel can be easily injected.
         * @param scope
         * @param termID
         * @param channel
         */
        public EventTarget(Scope scope, String termID, String channel) {
            this.scope = scope;
            this.termID = termID;
            this.channel = channel;
        }

        /**
         * returns true if a destination information is available.
         * Either termID or channel must contains a valid value.
         * @return
         */
        public boolean hasDestination() {
            return termID != null || channel != null;
        }

        public void setTermID(String termID) {
            this.termID = termID;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public Scope getScope() {
            return scope;
        }

        public String getTermID() {
            return termID;
        }

        public String getChannel() {
            return channel;
        }
    }
}

