package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.mock.MockContext;
import android.util.Log;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

/**
 * Created by arivu on 1/28/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class DataAPITest {
    DataAPI dataAPI;
    SharedPreferences pref;
    Context context;

    @Before
    public void setUp() throws Exception {
        context = Mockito.mock(MockContext.class);
        pref = Mockito.mock(SharedPreferences.class);
        PowerMockito.mockStatic(Log.class);
        dataAPI = new DataAPI(context);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGet_coursesId() throws Exception {
        JSONArray course_list = dataAPI.get_courses();
        Log.v("MyLogger:::::: ",course_list.toString());
        assertTrue(course_list.toString().contains("id"));
    }

    @Test
    public void testGet_coursesProducer() throws Exception {
        JSONArray course_list = dataAPI.get_courses();
        Log.v("MyLogger:::::: ",course_list.toString());
        assertTrue(course_list.toString().contains("producer"));
    }

    @Test
    public void testGet_coursesName() throws Exception {
        JSONArray course_list = dataAPI.get_courses();
        Log.v("MyLogger:::::: ",course_list.toString());
        assertTrue(course_list.toString().contains("name"));
    }

    @Test
    public void testGet_articlesId() throws Exception {
        JSONArray article_list = dataAPI.get_articles("1");
        Log.v("MyLogger:::::: ",article_list.toString());
        assertTrue(article_list.toString().contains("id"));
    }

    @Test
    public void testGet_articlesName() throws Exception {
        JSONArray article_list = dataAPI.get_articles("1");
        Log.v("MyLogger:::::: ",article_list.toString());
        assertTrue(article_list.toString().contains("name"));
    }

    @Test
    public void testGet_articlesProducer() throws Exception {
        JSONArray article_list = dataAPI.get_articles("1");
        Log.v("MyLogger:::::: ",article_list.toString());
        assertTrue(article_list.toString().contains("producer"));
    }

    @Test
    public void testGet_articlesCourse() throws Exception {
        JSONArray article_list = dataAPI.get_articles("1");
        Log.v("MyLogger:::::: ",article_list.toString());
        assertTrue(article_list.toString().contains("course"));
    }

    @Test
    public void testGet_articles_contentId() throws Exception {
        JSONArray article_content_list = dataAPI.get_articles_content("1");
        Log.v("MyLogger:::::: ",article_content_list.toString());
        assertTrue(article_content_list.toString().contains("id"));
    }

    @Test
    public void testGet_articles_contentData() throws Exception {
        JSONArray article_content_list = dataAPI.get_articles_content("1");
        Log.v("MyLogger:::::: ",article_content_list.toString());
        assertTrue(article_content_list.toString().contains("data"));
    }

    @Test
    public void testGet_articles_contentType() throws Exception {
        JSONArray article_content_list = dataAPI.get_articles_content("1");
        Log.v("MyLogger:::::: ",article_content_list.toString());
        assertTrue(article_content_list.toString().contains("type"));
    }

    @Test
    public void testGet_articles_contentArticle() throws Exception {
        JSONArray article_content_list = dataAPI.get_articles_content("1");
        Log.v("MyLogger:::::: ",article_content_list.toString());
        assertTrue(article_content_list.toString().contains("article"));
    }

    @Test
    public void testGet_quizzesId() throws Exception {
        JSONArray quiz_list = dataAPI.get_quizzes();
        Log.v("MyLogger:::::: ",quiz_list.toString());
        assertTrue(quiz_list.toString().contains("id"));
    }

    @Test
    public void testGet_quizzesName() throws Exception {
        JSONArray quiz_list = dataAPI.get_quizzes();
        Log.v("MyLogger:::::: ",quiz_list.toString());
        assertTrue(quiz_list.toString().contains("name"));
    }

    @Test
    public void testGet_quizzesCourse() throws Exception {
        JSONArray quiz_list = dataAPI.get_quizzes();
        Log.v("MyLogger:::::: ",quiz_list.toString());
        assertTrue(quiz_list.toString().contains("course"));
    }

    @Test
    public void testGet_questionsId() throws Exception {
        JSONArray question_list = dataAPI.get_questions("1");
        Log.v("MyLogger:::::: ",question_list.toString());
        assertTrue(question_list.toString().contains("id"));
    }

    @Test
    public void testGet_questionsQuiz() throws Exception {
        JSONArray question_list = dataAPI.get_questions("1");
        Log.v("MyLogger:::::: ",question_list.toString());
        assertTrue(question_list.toString().contains("quiz"));
    }

    @Test
    public void testGet_questionsData() throws Exception {
        JSONArray question_list = dataAPI.get_questions("1");
        Log.v("MyLogger:::::: ",question_list.toString());
        assertTrue(question_list.toString().contains("data"));
    }

    @Test
    public void testGet_questionsType() throws Exception {
        JSONArray question_list = dataAPI.get_questions("1");
        Log.v("MyLogger:::::: ",question_list.toString());
        assertTrue(question_list.toString().contains("type"));
    }

    @Test
    public void testGet_questionsMarks() throws Exception {
        JSONArray question_list = dataAPI.get_questions("1");
        Log.v("MyLogger:::::: ",question_list.toString());
        assertTrue(question_list.toString().contains("marks"));
    }

    @Test
    public void testGet_questionsIsCorrect() throws Exception {
        JSONArray question_list = dataAPI.get_questions("1");
        Log.v("MyLogger:::::: ",question_list.toString());
        assertTrue(question_list.toString().contains("iscorrect"));
    }

    @Test
    public void testGet_questionsAnswer() throws Exception {
        JSONArray question_list = dataAPI.get_questions("1");
        Log.v("MyLogger:::::: ",question_list.toString());
        assertTrue(question_list.toString().contains("answer"));
    }
}