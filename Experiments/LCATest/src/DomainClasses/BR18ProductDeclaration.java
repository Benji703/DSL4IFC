package DomainClasses;

import DomainClasses.Enums.DeclaredUnitEnum;

public class BR18ProductDeclaration {

        private String sortID;
        private String dataType;
        private String name;
        private String dkName;
        private String a1a3;
        private String c3;
        private String c4;
        private String d;
        private String declaredFactor;
        private DeclaredUnitEnum declaredUnit;
        private double massFactor;
        private String url;

    public BR18ProductDeclaration(String sortID, String dataType, String name, String dkName, String a1a3, String c3, String c4, String d, String declaredFactor, DeclaredUnitEnum declaredUnit, double massFactor, String url) {
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

        public String getA1a3() {
            return a1a3;
        }

        public String getC3() {
            return c3;
        }

        public String getC4() {
            return c4;
        }

        public String getD() {
            return d;
        }

        public String getDeclaredFactor() {
            return declaredFactor;
        }

        public DeclaredUnitEnum getDeclaredUnit() {
            return declaredUnit;
        }

        public double getMassFactor() {
            return massFactor;
        }

        public String getUrl() {
            return url;
        }
}
