package com.elleined.marketplaceapi.service.image;

import com.elleined.marketplaceapi.exception.PathNotValidException;
import com.elleined.marketplaceapi.exception.resource.ResourceException;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageUploader implements Uploader {
    @Override
    public void upload(String fullPathDirectory, MultipartFile attachment) throws IOException, PathNotValidException {
        if (StringUtil.isNotValid(fullPathDirectory)) throw new PathNotValidException("Please specify valid full path url!");
        if (attachment == null) throw new ResourceException("Please specify you attachment to be saved!");
        final String fullPathDirectoryWithImg = fullPathDirectory + attachment.getOriginalFilename();
        attachment.transferTo(new File(fullPathDirectoryWithImg));
    }
}
