package EPDConnectors;

import DomainClasses.BR18ProductDeclaration;
import DomainClasses.Enums.IFCTypeEnum;
import DomainClasses.EnvProductInfo;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BR18Connector {

    public Map<String, List<String>> GetEPDDataByType(IFCTypeEnum type) {

        Map<String, List<String>> epdData = new HashMap<String, List<String>>() {
        };

        try {
            epdData = readExcel("src/ExcelData/BR18v2_201222_clean.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<BR18ProductDeclaration> productList = ConvertToBR18ObjectList(epdData);


        return epdData;
    }

    private List<BR18ProductDeclaration> ConvertToBR18ObjectList(Map<String, List<String>> epdData) {
        List<BR18ProductDeclaration> productList = new ArrayList<>();

        

        return productList;
    }

    public Map<String, List<String>> readExcel(String fileLocation) throws IOException {
        Map<String, List<String>> data = new HashMap<>();

        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            List<Row> rows = sheet.read();

            System.out.println(rows.size());

            for (int i = 4; i < rows.size(); i++) {
                String rowID = rows.get(i).getCell(1).getRawValue();
                data.put(rowID, new ArrayList<>());

                for (int j = 2; j < rows.get(i).getCellCount(); j++) {
                    data.get(rowID).add(rows.get(i).getCell(j).getRawValue());
                }

                System.out.println(data.get(rowID));
            }


        }

        return data;
    }
}
