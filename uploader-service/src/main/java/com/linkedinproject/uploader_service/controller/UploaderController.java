package com.linkedinproject.uploader_service.controller;

import com.linkedinproject.uploader_service.service.UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class UploaderController {

    @Autowired
    private UploaderService uploaderService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file)
    {
        String url = uploaderService.upload(file);
        return ResponseEntity.ok(url);
    }
}
