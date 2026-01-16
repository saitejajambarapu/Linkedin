package com.linkedinproject.uploader_service.service;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryUploaderService implements UploaderService{

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file) {
        log.info("in upload service");
        try {
            Map uploads = cloudinary.uploader().upload(file.getBytes(),Map.of());
            return uploads.get("secure_url").toString();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
