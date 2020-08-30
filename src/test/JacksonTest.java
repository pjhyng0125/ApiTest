package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.FileUtil;
import vo.Person;

public class JacksonTest {
	public static void main(String[] args) {HashMap<String, Object> hmap = new HashMap<String, Object>();
        //Adding elements to HashMap
        hmap.put("first", "AB");
        hmap.put("second", "CD");
        hmap.put("third", "EF");
        
        ArrayList<Integer> listInt = new ArrayList<>();
        listInt.add(1);
        listInt.add(2);
        listInt.add(3);
        
        hmap.put("list", listInt);
        
        Person A = new Person("박진형", 28);
        Person B = new Person("고명수", 28);
        Person C = new Person("전희영", 27);
        ArrayList<Person> listPeople = new ArrayList<>();
        
        listPeople.add(A);
        listPeople.add(B);
        listPeople.add(C);
        
        hmap.put("listPeople", listPeople);
        
        String filePath = "C:\\SHARE\\JSON\\jacksonTest.json";
        String folderPath = "C:\\SHARE\\JSON\\";
        
        // String savedFilePath = FileUtil.mapToJson(hmap, filePath);
        Map<String, Object> rtnMap = FileUtil.jsonToMap(filePath);
        
        // System.out.println("[main] rtnMap list: " + rtnMap.get("list"));
        //System.out.println("[main] rtnMap listPeople: " + rtnMap.get("listPeople"));
        // List<Person> personList = rtnMap.get("listPeople");
        //System.out.println("[main] rtnMap first: " + rtnMap.get("first"));
        //System.out.println("[main] rtnMap first2: " + rtnMap.get("first2"));
        // FileUtil.fileListFromFolder(folderPath);
        // System.out.println(FileUtil.isFileExists(filePath));
        // System.out.println(FileUtil.isFileExists(filePath+"\\ss.json"));
        
        
	}
}
