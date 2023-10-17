package com.elleined.marketplaceapi.service.validator;
import org.springframework.web.multipart.MultipartFile;

public interface Validator<T> {
    void validate(T t);

    static boolean notValidMultipartFile(MultipartFile multipartFile) {
        return multipartFile == null || multipartFile.isEmpty();
    }

    static boolean validMultipartFile(MultipartFile multipartFile) {
        return multipartFile != null && !multipartFile.isEmpty();
    }
}
