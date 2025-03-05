package com.shop.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID(); //랜덤 UUID
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); //확장자 추출
        String savedFileName = uuid.toString() + extension; //저장될 파일 명
        String fileUploadFullUrl = uploadPath + "/" + savedFileName; //지정된 경로에 파일 저장할 url
        //바이트 단위의 출력을 보내는 클래스, 저장 위치와 파일명을 받아서 파일 출력 스트림을 생성한다.
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData); //파일 데이터를 출력 스트림에 전송
        fos.close();
        return savedFileName; //업로드된 파일명 반환
    }

    public void deleteFile(String filePath) throws Exception {

        File deleteFile = new File(filePath);

        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        }
        else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
