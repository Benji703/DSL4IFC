package EPDConnectors;

import DomainClasses.EnvProductInfo;
import DomainClasses.IFCType;
import Interfaces.IEPDConnector;

public class EcoPortalConnector implements IEPDConnector {

    private final String epdUrl = "";

    @Override
    public EnvProductInfo GetEPDDataByType(IFCType type) {
        return null;
    }

    
}
