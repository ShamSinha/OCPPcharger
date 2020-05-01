package DataType;

import EnumDataType.OCPPInterfaceEnumType;
import EnumDataType.OCPPTransportEnumType;
import EnumDataType.OCPPVersionEnumType;

public class NetworkConnectionProfileType {

    public OCPPVersionEnumType ocppVersion ;
    public OCPPTransportEnumType ocppTransport ;
    public String ocppCsmsUrl ;
    public int messageTimeout ;
    public OCPPInterfaceEnumType ocppInterface ;

}
