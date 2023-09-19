package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.model.Moderator;

import java.util.List;
import java.util.Set;

public interface Request<T> {
    List<T> getAllRequest();
    void accept(Moderator moderator, T t);
    void acceptAll(Moderator moderator, Set<T> t);
    void reject(Moderator moderator, T t);
    void rejectAll(Moderator moderator, Set<T> t);
}
