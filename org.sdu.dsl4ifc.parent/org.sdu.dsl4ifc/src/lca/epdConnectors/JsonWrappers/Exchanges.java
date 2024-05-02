package lca.epdConnectors.JsonWrappers;

import java.util.List;

public class Exchanges {
    private List<Exchange> exchange;

    public List<Exchange> getLCIAResult() {
        return exchange;
    }

    public void setLCIAResult(List<Exchange> exchange) {
        this.exchange = exchange;
    }
}
