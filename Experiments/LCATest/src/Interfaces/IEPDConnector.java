package Interfaces;

import DomainClasses.EnvProductInfo;
import DomainClasses.Enums.IFCTypeEnum;

public interface IEPDConnector {

    IEnvProductInfo GetEPDDataByType(String name);
}
