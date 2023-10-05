package com.elleined.marketplaceapi.service.image;

import com.elleined.marketplaceapi.exception.PathNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface Uploader {
    void upload(String fullPathDirectory, MultipartFile attachment)
            throws IOException, PathNotValidException;
}
