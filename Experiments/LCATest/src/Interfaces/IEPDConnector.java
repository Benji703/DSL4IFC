package Interfaces;

import DomainClasses.EnvProductInfo;
import DomainClasses.IFCType;

public interface IEPDConnector {

    EnvProductInfo GetEPDDataByType(IFCType type);
}
