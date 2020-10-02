package test;

import java.io.File;
import java.io.FilenameFilter;

import util.FileUtil;

public class FileRenameTest {	
	public static void main(String[] args) {
		String completedFilePath = "/SHARE/OFD/COMPLETED";
		String custId = "12345678";
		String docuCd = "DA001";
		String signCd = "DH001";
		
		// 조건에 맞는 파일 정보만 가져오기
		File completedDir = new File(completedFilePath);
		File[] listFile = completedDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				 return name.equals(custId.concat("_").concat(docuCd).concat(".tiff")) ? true : false;
//				return true;
			}
		});
		
		Boolean result = false;
		
		// 파일명 변경
		if(null != listFile && listFile.length > 0) {
			System.out.println("listFile.length: " + listFile.length);
			System.out.println("listFile: " + listFile);
			File originFile = listFile[0];
			String originStr = docuCd;
			String replaceStr = signCd;
			
			/*
			String originFilePath = originFile.getAbsolutePath(); // 절대경로
			System.out.println("originFilePath: " + originFilePath); // C:\SHARE\OFD\COMPLETED\12345678_DA001.tiff
			
			String replaceFilePath = originFilePath.replace(docuCd, signCd); // C:\SHARE\OFD\COMPLETED\12345678_DH001.tiff
			System.out.println("replaceFilePath: " + replaceFilePath);
			
			File replaceFile = new File(replaceFilePath); // C:\SHARE\OFD\COMPLETED\12345678_DH001.tiff
			replaceFile.setExecutable(true, false);
			replaceFile.setReadable(true, false);
			replaceFile.setWritable(true, false);
			originFile.setExecutable(true, false);
			originFile.setReadable(true, false);
			originFile.setWritable(true, false);
			result = originFile.renameTo(replaceFile);
			System.out.println(custId + "_" + docuCd + " file Rename To" + replaceFilePath);
			 */
			
			result = FileUtil.renameFile(originFile, originStr, replaceStr);
			if(result) {
				System.out.println("파일명 변경 성공 *^^*");
			} else {
				System.out.println("파일명 변경 실패 ㅠㅠ");				
			}
		} else {
			System.out.println(custId + "_" + docuCd + " file Not Found");			
		}
	}
}
