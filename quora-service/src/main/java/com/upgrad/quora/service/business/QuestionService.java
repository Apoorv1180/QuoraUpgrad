package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        questionEntity.setUser(userAuthEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        // Validate if requested question exist or not
        QuestionEntity existingQuestionEntity = questionDao.getQuestionByUuid(questionEntity.getUuid());
        if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        // Validate if current user is the owner of requested question
        UserEntity currentUser = userAuthEntity.getUser();
        UserEntity questionOwner = questionDao.getQuestionByUuid(questionEntity.getUuid()).getUser();
        if (currentUser.getId() != questionOwner.getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        questionEntity.setId(existingQuestionEntity.getId());
        questionEntity.setUser(existingQuestionEntity.getUser());
        questionEntity.setDate(existingQuestionEntity.getDate());

        return questionDao.editQuestionContent(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void userQuestionDelete(final String questionId, final String authorization) throws InvalidQuestionException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        // Validate if requested question exist or not
        if (questionDao.getQuestionByUuid(questionId) == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        // Validate if current user is the owner of requested question or the role of user is not nonadmin
        if (!userAuthEntity.getUser().getUuid().equals(questionDao.getQuestionByUuid(questionId).getUser().getUuid())) {
            if (userAuthEntity.getUser().getRole().equals("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003", "Oly the question owner or admin can delete the question");
            }
        }

        questionDao.userQuestionDelete(questionId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        // Validate if requested user exist or not
        if (userDao.getUserByUuid(userId) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionsByUser(userId);
    }
}