package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserDao userDao;

    /**
     *  method helps create new user signUp and SignIn and SignOut
     *
     * @param userEntity the UserEntity object from which new user will be created
     *
     * @return UserEntity object
     *
     * @throws SignUpRestrictedException if validation for user details conflicts
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        return adminService.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String authorizationToken) throws SignOutRestrictedException {
        return adminService.logoutUser(authorizationToken);
    }

}
