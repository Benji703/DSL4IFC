package EPDConnectors;

import DomainClasses.BR18ProductDeclaration;
import DomainClasses.Enums.DeclaredUnitEnum;
import DomainClasses.EnvProductInfo;
import Interfaces.IEPDConnector;
import Interfaces.IEnvProductInfo;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BR18Connector implements IEPDConnector {

    private List<BR18ProductDeclaration> productList;


    public IEnvProductInfo GetEPDDataByType(String name) {

        if (productList != null) {
            return productList.stream()
                    .filter(br18Dec -> br18Dec.getDkName().equals(name))
                    .findFirst().orElse(null);
        }

        Map<String, List<String>> epdData = new HashMap<String, List<String>>() {
        };

        try {
            epdData = readExcel("src/ExcelData/BR18v2_201222_clean.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        productList = ConvertToBR18ObjectList(epdData);

        BR18ProductDeclaration br18Declaration = productList.stream()
                .filter(br18Dec -> br18Dec.getDkName().equals(name))
                .findFirst().orElse(null);

        return br18Declaration;
    }

    private List<BR18ProductDeclaration> ConvertToBR18ObjectList(Map<String, List<String>> epdData) {
        List<BR18ProductDeclaration> productList = new ArrayList<>();

        for (Map.Entry<String, List<String>> set : epdData.entrySet()) {
            String sortID = set.getKey();
            String dataType = set.getValue().get(0);
            String name = set.getValue().get(1);
            String nameDK = set.getValue().get(2);
            Double a1a3 = GetCO2Data(set.getValue().get(3), 3, epdData);
            Double c3 = GetCO2Data(set.getValue().get(4), 4, epdData);
            Double c4 = GetCO2Data(set.getValue().get(5), 5, epdData);
            Double d = GetCO2Data(set.getValue().get(6), 6, epdData);
            Double declaredFactor = parseStringToDouble(set.getValue().get(7));
            DeclaredUnitEnum declaredUnit = ParseToEnum(set.getValue().get(8));
            Double mass = parseStringToDouble(set.getValue().get(9));
            String url = set.getValue().get(10);
            String comment = set.getValue().get(11);

            productList.add(new BR18ProductDeclaration(sortID,dataType,name,nameDK,a1a3,c3,c4,d,declaredFactor,declaredUnit,mass,url));
        }

        return productList;
    }
    private double parseStringToDouble(String value) {
        return value == null || value.isEmpty() ? Double.NaN : Double.parseDouble(value);
    }

    private DeclaredUnitEnum ParseToEnum(String s) {
        DeclaredUnitEnum declaredEnum;

        try {
            declaredEnum = DeclaredUnitEnum.valueOf(s);
        } catch (NullPointerException e) {
            declaredEnum = null;
        }

        return declaredEnum;
    }

    private Double GetCO2Data(String s, int i, Map<String, List<String>> edpData) {
        Double d;
        String sNumber = s;

        if (s == null) {
            return null;
        }

        if (s.equals("-")) {
            return null;
        }

        if (s.contains("#")) {
            sNumber = edpData.get(s).get(i);
        }

        try {
            d = Double.parseDouble(sNumber);
        } catch (NumberFormatException e) {
            return null;
        }

        return d;
    }

    public Map<String, List<String>> readExcel(String fileLocation) throws IOException {
        Map<String, List<String>> data = new HashMap<>();

        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            List<Row> rows = sheet.read();

            for (int i = 4; i < rows.size(); i++) {
                String rowID = rows.get(i).getCell(1).getRawValue();
                data.put(rowID, new ArrayList<>());

                for (int j = 2; j < rows.get(i).getCellCount(); j++) {
                    data.get(rowID).add(rows.get(i).getCell(j).getRawValue());
                }
            }
        }
        return data;
    }


}
