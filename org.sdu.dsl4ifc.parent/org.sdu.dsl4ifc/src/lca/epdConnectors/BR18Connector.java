package lca.epdConnectors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import lca.DomainClasses.BR18ProductDeclaration;
import lca.DomainClasses.Enums.DeclaredUnitEnum;
import lca.Interfaces.IEPDConnector;
import lca.Interfaces.IEnvProductInfo;

public class BR18Connector implements IEPDConnector {

    private List<BR18ProductDeclaration> productList;


    public IEnvProductInfo GetEPDDataByType(String id) {

        if (productList != null) {
            return getProdDecById(id);
        }

        Map<String, List<String>> epdData = new HashMap<String, List<String>>();

        try {
        	Bundle bundle = FrameworkUtil.getBundle(BR18Connector.class);
            
        	URL fileURL = bundle.getEntry("/src/lca/ExcelData/BR18v2_201222_clean.xlsx"); // Replace "/path/to/your/file.xlsx" with the path to your file within the plugin

            // Resolve the file URL to a filesystem path
            URL resolvedFileURL = FileLocator.toFileURL(fileURL);
            String filePath = resolvedFileURL.getPath();
            
            // Create a File object from the resolved path
            File file = new File(filePath);
      
            epdData = readExcel(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        productList = ConvertToBR18ObjectList(epdData);

        return getProdDecById(id);
    }
    
    private BR18ProductDeclaration getProdDecById(String id) {
    	return productList.stream()
                .filter(br18Dec -> br18Dec.getSortID().equals(id))
                .findFirst().orElse(null);
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

    public Map<String, List<String>> readExcel(File file) throws IOException {
        Map<String, List<String>> data = new HashMap<>();

        try (ReadableWorkbook wb = new ReadableWorkbook(file)) {
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
