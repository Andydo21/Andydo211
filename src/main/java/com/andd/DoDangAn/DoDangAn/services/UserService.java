package com.andd.DoDangAn.DoDangAn.services;

import com.andd.DoDangAn.DoDangAn.models.User;

public interface UserService {
    User findByUsername(String username);
}
