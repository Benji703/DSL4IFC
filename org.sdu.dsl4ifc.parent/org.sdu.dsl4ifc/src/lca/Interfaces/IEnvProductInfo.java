package lca.Interfaces;

import lca.DomainClasses.Enums.DeclaredUnitEnum;

public interface IEnvProductInfo {
	public Double getD();
	
    public Double getC4();

    public Double getC3();

    public Double getA();

    public String getName();
    
    public DeclaredUnitEnum getDeclaredUnit();
    
    public Double getDeclaredFactor();
    
    public Double getMassFactor();
}
