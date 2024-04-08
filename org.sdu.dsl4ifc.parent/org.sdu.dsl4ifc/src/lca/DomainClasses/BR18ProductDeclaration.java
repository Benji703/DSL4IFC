package lca.DomainClasses;

import lca.DomainClasses.Enums.DeclaredUnitEnum;
import lca.Interfaces.IEnvProductInfo;

public class BR18ProductDeclaration implements IEnvProductInfo {

        private String sortID;
        private String dataType;
        private String name;
        private String dkName;
        private Double a1a3;
        private Double c3;
        private Double c4;
        private Double d;
        private Double declaredFactor;
        private DeclaredUnitEnum declaredUnit;
        private Double massFactor;
        private String url;

    public BR18ProductDeclaration(String sortID, String dataType, String name, String dkName, Double a1a3, Double c3, Double c4, Double d, Double declaredFactor, DeclaredUnitEnum declaredUnit, Double massFactor, String url) {
        this.sortID = sortID;
        this.dataType = dataType;
        this.name = name;
        this.dkName = dkName;
        this.a1a3 = a1a3;
        this.c3 = c3;
        this.c4 = c4;
        this.d = d;
        this.declaredFactor = declaredFactor;
        this.declaredUnit = declaredUnit;
        this.massFactor = massFactor;
        this.url = url;
    }

    // Getters and setters for each private field

        public String getSortID() {
            return sortID;
        }

        public String getDataType() {
            return dataType;
        }

        public String getName() {
            return name;
        }


        public String getDkName() {
            return dkName;
        }

        public Double getA() {
            return a1a3;
        }

        public Double getC3() {
            return c3;
        }

        public Double getC4() {
            return c4;
        }

        public Double getD() {
            return d;
        }

        public Double getDeclaredFactor() {
            return declaredFactor;
        }

        public DeclaredUnitEnum getDeclaredUnit() {
            return declaredUnit;
        }

        public Double getMassFactor() {
            return massFactor;
        }

        public String getUrl() {
            return url;
        }

    @Override
    public String toString() {
        return "BR18ProductDeclaration{" +
                "sortID='" + sortID + '\'' +
                ", dataType='" + dataType + '\'' +
                ", name='" + name + '\'' +
                ", dkName='" + dkName + '\'' +
                ", a1a3=" + a1a3 +
                ", c3=" + c3 +
                ", c4=" + c4 +
                ", d=" + d +
                ", declaredFactor=" + declaredFactor +
                ", declaredUnit=" + declaredUnit +
                ", massFactor=" + massFactor +
                ", url='" + url + '\'' +
                '}';
    }
}
