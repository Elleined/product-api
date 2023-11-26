package com.elleined.marketplaceapi.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploader implements Uploader {
    @Override
    public void upload(String uploadPath, MultipartFile attachment) throws IOException {
        File uploadPathWithImg = new File(uploadPath + attachment.getOriginalFilename());
        if (uploadPathWithImg.exists()) return;
        attachment.transferTo(uploadPathWithImg);
        attachment.transferTo(uploadPathWithImg);
        log.debug("Picture uploaded successfully to {}", uploadPathWithImg);
    }
}
