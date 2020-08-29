package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileUtil {
	// private static final Logger LOGGER = Logger.getLogger(FileUtil.class);
	/*
	 * file io
	 */
	public static void writeStringtData(String str, String filePath) {
		// 파일 쓰기 전 디렉토리 생성
		createFilePath(filePath);

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filePath, false));
			bw.write(str);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String readStringData(String filePath) {
		BufferedReader br = null;
		String str = null;

		try {
			br = new BufferedReader(new FileReader(filePath));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				str = readLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public static boolean isFileExists(String filePath) {
		File file = new File(filePath);

		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private static void createFilePath(String filePath) {
		String path = new File(filePath).getParentFile().toString();
		File dir = new File(path);

		if (!dir.exists()) {
			dir.mkdirs();
		} else {
			// LOGGER.debug(filePath + " is already created.");
		}
	}

	public static List<Map<String, Object>> fileListFromFolder(String folderPath) {
		System.out.println("[fileListFromFolder] folderPath: ");
		List<Map<String, Object>> list = new ArrayList<>();

		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				System.out.println("[fileListFromFolder] file name: " + file.getName());
				// LOGGER.debug("[fileListFromFolder] file name: " + file.getName());
				list.add(jsonToMap(folderPath.concat(file.getName())));
			}
		}

		return list;
	}

	/*
	 * jackson api
	 */
	public static String mapToJson(HashMap<String, Object> hmap, String filePath) {
		ObjectMapper mapper = new ObjectMapper();
		String key = new File(filePath).getName();
		System.out.println("[mapToJson] filename:" + key);

		try {
			// MAP -> JSON
			String jsonString = mapper.writeValueAsString(hmap);
			String jsonStringEnc = "";
			
			// JSON -> JSON ENC
			EncryptUtil eu = new EncryptUtil(key);
			try {
				jsonStringEnc = eu.aesEncode(jsonString);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// JSON -> FILE
			writeStringtData(jsonStringEnc, filePath);

			System.out.println("[mapToJson] svae filePath: " + filePath);
			return filePath;
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, Object> jsonToMap(String filePath) {
		ObjectMapper mapper = new ObjectMapper();
		String key = new File(filePath).getName();
		
		// FILE -> JSON ENC
		String jsonStringEnc = readStringData(filePath);
		System.out.println("[jsonToMap] jsonStringEnc: " + jsonStringEnc);
		
		String jsonString = "";

		// JSON ENC -> JSON
		try {
			EncryptUtil eu = new EncryptUtil(key);
			jsonString = eu.aesDecode(jsonStringEnc);
			System.out.println("[jsonToMap] jsonString: " + jsonString);
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			// JSON -> MAP
			Map<String, Object> map = mapper.readValue(jsonString, Map.class);
			return map;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("[jsonToMap] load filePath: " + filePath);
		// LOGGER.debug("[jsonToMap] load filePath: " + filePath);
		return null;
	}
}
