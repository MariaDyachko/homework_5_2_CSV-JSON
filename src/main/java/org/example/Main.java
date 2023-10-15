package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {

    public static HashMap<String, String> myHashMap = new HashMap<String, String>();

    public static void main(String[] args) {

        //Create CSV file
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            writer.writeNext(employee1);
            writer.writeNext(employee2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileName = "data.csv";//""homework_5_2_CSV-JSON/src/main/resources/data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "csv_data.json");

        //Второе задание
        List<Employee> list1 = parseXML("data.xml");
        //int test = 1;
        String json1 = listToJson(list1);
        writeString(json1, "xml_data.json");

    }

    private static String listToJson(List<Employee> list) {

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);

        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            var strategy = new ColumnPositionMappingStrategy<Employee>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age"); //columnMapping не работает
            var csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> workers = csv.parse();
            return workers;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    public static void writeString(String text, String address) {
       // JsonObject jsonObject = new JsonParser().parse(text).getAsJsonObject();
        try (FileWriter file = new
                FileWriter(address)) {
            file.write(text.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String file)  {

        List<Employee> list = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            //read(root);
            for (int i = 0; i < nodeList.getLength(); i++){
                read(nodeList.item(i));
                if(!myHashMap.isEmpty()) {
                    long id = Long.parseLong(myHashMap.get("id"));
                    list.add(new Employee(id,
                            myHashMap.get("firstName"),
                            myHashMap.get("lastName"),
                            myHashMap.get("country"),
                            Integer.parseInt(myHashMap.get("age"))));
                }
            }
            list.remove(3); //упс
            list.remove(1);
            return list;

        }catch (ParserConfigurationException | SAXException | IOException e){
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    private static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                System. out.print(node_.getNodeName() +"  ");
                Element element = (Element) node_;
                System.out.println(element.getTextContent());
                myHashMap.put(node_.getNodeName(), element.getTextContent());
                //read(node_);
            }
        }
    }

}