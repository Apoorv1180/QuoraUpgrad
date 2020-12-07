package com.upgrad.quora.api.Controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    /**
     * This api endpoint is used to post a new question
     *
     * @param QuestionRequest   question details for adding new question in QuestionRequest model
     *        authorization string authererisation token
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        // Create question entity
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setDate(ZonedDateTime.now());

        // Return response with created question entity
        final QuestionEntity createdQuestionEntity = questionService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * This api endpoint is used to show all posted questions
     *
     * @param  authorization string authererisation token
     *
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        // Get all questions
        List<QuestionEntity> allQuestions = questionService.getAllQuestions(authorization);

        // Create response
        List<QuestionDetailsResponse> allQuestionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        for (int i = 0; i < allQuestions.size(); i++) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .content(allQuestions.get(i).getContent())
                    .id(allQuestions.get(i).getUuid());
            allQuestionDetailsResponses.add(questionDetailsResponse);
        }

        // Return response
        return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponses, HttpStatus.OK);
    }

    /**
     * This api endpoint is used to edit question
     *
     * @param QuestionEditRequest  question details for editing exiting question
     *        authorization string authererisation token
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        // Creating question entity for further update
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setUuid(questionId);

        // Return response with updated question entity
        QuestionEntity updatedQuestionEntity = questionService.editQuestionContent(questionEntity, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
    /**
     * This api endpoint is used to Delete  question
     *
     * @param questionId string questionId details for deleting exiting question
     *        authorization string authererisation token
     *
     * @return JSON response with user uuid and message
     *
     * @throws AuthorizationFailedException if validation for user details conflicts
     * @throws    InvalidQuestionException if question doesn`t exist
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        // Delete requested question
        questionService.userQuestionDelete(questionId, authorization);

        // Return response
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionId).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path ="/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        // Get all questions for requested user
        List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUser(userId, authorization);

        // Create response
        List<QuestionDetailsResponse> allQuestionDetailsResponse = new ArrayList<QuestionDetailsResponse>();

        for (int i = 0; i < allQuestions.size(); i++) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .content(allQuestions.get(i).getContent())
                    .id(allQuestions.get(i).getUuid());
            allQuestionDetailsResponse.add(questionDetailsResponse);
        }

        // Return response
        return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponse, HttpStatus.FOUND);
    }
}
