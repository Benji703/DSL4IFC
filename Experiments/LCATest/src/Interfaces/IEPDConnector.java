package Interfaces;

import DomainClasses.EnvProductInfo;
import DomainClasses.Enums.IFCTypeEnum;

public interface IEPDConnector {

    EnvProductInfo GetEPDDataByType(IFCTypeEnum type);
}
