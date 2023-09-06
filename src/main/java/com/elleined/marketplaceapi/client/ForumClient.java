package com.elleined.marketplaceapi.client;

import com.elleined.marketplaceapi.dto.forum.ForumUserDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "FORUM-API", url = "http://localhost:8081/api/v1/forum/users")
public interface ForumClient {


    @PostMapping
    ForumUserDTO save(@Valid @RequestBody ForumUserDTO forumUserDTO);
}
