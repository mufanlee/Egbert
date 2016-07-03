package model;

public class OFPort {
	// private int constants (OF1.1+) to avoid duplication in the code
    // should not have to use these outside this class
    private static final int OFPP_ANY_INT = 0xFFffFFff;
    private static final int OFPP_LOCAL_INT = 0xFFffFFfe;
    private static final int OFPP_CONTROLLER_INT = 0xFFffFFfd;
    private static final int OFPP_ALL_INT = 0xFFffFFfc;
    private static final int OFPP_FLOOD_INT = 0xFFffFFfb;
    private static final int OFPP_NORMAL_INT = 0xFFffFFfa;
    private static final int OFPP_TABLE_INT = 0xFFffFFf9;
    private static final int OFPP_MAX_INT = 0xFFffFF00;
    private static final int OFPP_IN_PORT_INT = 0xFFffFFf8;
    
 // ////////////// public constants - use to access well known OpenFlow ports

    /** Maximum number of physical and logical switch ports. */
    public final static OFPort MAX = new OFPort(OFPP_MAX_INT);

    /**
     * Send the packet out the input port. This reserved port must be explicitly
     * used in order to send back out of the input port.
     */
    public final static OFPort IN_PORT = new OFPort(OFPP_IN_PORT_INT);

    /**
     * Submit the packet to the first flow table NB: This destination port can
     * only be used in packet-out messages.
     */
    public final static OFPort TABLE = new OFPort(OFPP_TABLE_INT);

    /** Process with normal L2/L3 switching. */
    public final static OFPort NORMAL = new OFPort(OFPP_NORMAL_INT);

    /**
     * All physical ports in VLAN, except input port and those blocked or link
     * down
     */
    public final static OFPort FLOOD = new OFPort(OFPP_FLOOD_INT);

    /** All physical ports except input port */
    public final static OFPort ALL = new OFPort(OFPP_ALL_INT);

    /** Send to controller */
    public final static OFPort CONTROLLER = new OFPort(OFPP_CONTROLLER_INT);

    /** local openflow "port" */
    public final static OFPort LOCAL = new OFPort(OFPP_LOCAL_INT);

    /**
     * Wildcard port used only for flow mod (delete) and flow stats requests.
     * Selects all flows regardless of output port (including flows with no
     * output port). NOTE: OpenFlow 1.0 calls this 'NONE'
     */
    public final static OFPort ANY = new OFPort(OFPP_ANY_INT);
    /** the wildcarded default for OpenFlow 1.0 (value: 0). Elsewhere in OpenFlow
     *  we need "ANY" as the default
     */
    public static final OFPort ZERO = OFPort.of(0);

    /** raw openflow port number as a signed 32 bit integer */
    private final int portNumber;

    /** private constructor. use of*-Factory methods instead */
    private OFPort(final int portNumber) {
        this.portNumber = portNumber;
    }
    
    public static OFPort of(final int portNumber) {
        switch (portNumber) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            	return new OFPort(portNumber);
            case OFPP_MAX_INT:
                return MAX;
            case OFPP_IN_PORT_INT:
                return IN_PORT;
            case OFPP_TABLE_INT:
                return TABLE;
            case OFPP_NORMAL_INT:
                return NORMAL;
            case OFPP_FLOOD_INT:
                return FLOOD;
            case OFPP_ALL_INT:
                return ALL;
            case OFPP_CONTROLLER_INT:
                return CONTROLLER;
            case OFPP_LOCAL_INT:
                return LOCAL;
            case OFPP_ANY_INT:
                return ANY;
            default:
                // note: This means effectively : portNumber > OFPP_MAX_SHORT
                // accounting for
                // signedness of both portNumber and OFPP_MAX_INT(which is
                // -256).
                // Any unsigned integer value > OFPP_MAX_INT will be ]-256:0[
                // when read signed
                if (portNumber < 0 && portNumber > OFPP_MAX_INT)
                    throw new IllegalArgumentException("Unknown special port number: "
                            + portNumber);
                return new OFPort(portNumber);
        }
    }
    
    public static OFPort of(final String port) {
        switch (port) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "10":
            case "11":
            case "12":
            case "13":
            case "14":
            case "15":
            case "16":
            case "17":
            case "18":
            case "19":
            case "20":
            case "21":
            case "22":
            case "23":
            case "24":
            	return new OFPort(Integer.parseInt(port));
            case "max":
                return MAX;
            case "in_port":
                return IN_PORT;
            case "table":
                return TABLE;
            case "normal":
                return NORMAL;
            case "flood":
                return FLOOD;
            case "all":
                return ALL;
            case "controller":
                return CONTROLLER;
            case "local":
                return LOCAL;
            case "any":
                return ANY;
            default:
                // note: This means effectively : portNumber > OFPP_MAX_SHORT
                // accounting for
                // signedness of both portNumber and OFPP_MAX_INT(which is
                // -256).
                // Any unsigned integer value > OFPP_MAX_INT will be ]-256:0[
                // when read signed
                if (Integer.parseInt(port) < 0 && Integer.parseInt(port) > OFPP_MAX_INT)
                    throw new IllegalArgumentException("Unknown special port number: "
                            + Integer.parseInt(port));
                return new OFPort(Integer.parseInt(port));
        }
    }
    /** return the port number as a int32 */
    public int getPortNumber() {
        return portNumber;
    }

	@Override
	public String toString() {
		if (portNumber == OFPP_ALL_INT)
			return "OFPort [portNumber=all]";
		else if (portNumber == OFPP_ANY_INT)
			return "OFPort [portNumber=any]";
		else if (portNumber == OFPP_CONTROLLER_INT)
			return "OFPort [portNumber=controller]";
		else if (portNumber == OFPP_FLOOD_INT)
			return "OFPort [portNumber=flood]";
		else if (portNumber == OFPP_IN_PORT_INT)
			return "OFPort [portNumber=in_port]";
		else if (portNumber == OFPP_LOCAL_INT)
			return "OFPort [portNumber=local]";
		else if (portNumber == OFPP_NORMAL_INT)
			return "OFPort [portNumber=normal]";
		else if (portNumber == OFPP_TABLE_INT)
			return "OFPort [portNumber=table]";
		else if (portNumber == OFPP_MAX_INT)
			return "OFPort [portNumber=max]";
		else
			return "OFPort [portNumber=" + portNumber + "]";
	}
}
