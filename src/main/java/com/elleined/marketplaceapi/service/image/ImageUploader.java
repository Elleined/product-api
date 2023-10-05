package com.elleined.marketplaceapi.service.image;

import com.elleined.marketplaceapi.exception.PathNotValidException;
import com.elleined.marketplaceapi.exception.resource.ResourceException;
import com.elleined.marketplaceapi.utils.StringUtil;
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
    public void upload(String fullPathDirectory, MultipartFile attachment) throws IOException, PathNotValidException {
        if (StringUtil.isNotValid(fullPathDirectory)) throw new PathNotValidException("Please specify valid full path url!");
        if (attachment == null) throw new ResourceException("Picture attachment cannot be null!");
        final String fullPathDirectoryWithImg = fullPathDirectory + attachment.getOriginalFilename();
        attachment.transferTo(new File(fullPathDirectoryWithImg));
        log.debug("Picture uploaded successfully to {}", fullPathDirectoryWithImg);
    }
}
