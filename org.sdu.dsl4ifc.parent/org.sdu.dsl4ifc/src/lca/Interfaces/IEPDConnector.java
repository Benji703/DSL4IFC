package lca.Interfaces;

import java.util.List;

public interface IEPDConnector {

    IEnvProductInfo GetEPDDataByType(String name);
    List<EpdOverview> GetAllEpdNames();
}