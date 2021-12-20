package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController postController;
    private final String PATH_POSTS = "/api/posts";
    private final String PATH_WITH_NUMBER_POST = PATH_POSTS + "/\\d+";


    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        final var controller = context.getBean("postController");
        this.postController = (PostController) controller;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals("GET") && path.equals(PATH_POSTS)) {
                postController.all(resp);
                return;
            }
            if (method.equals("GET") && path.matches(PATH_WITH_NUMBER_POST)) {
                // easy way
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                postController.getById(id, resp);
                return;
            }
            if (method.equals("POST") && path.equals(PATH_POSTS)) {
                postController.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches(PATH_WITH_NUMBER_POST)) {
                // easy way
                final long id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                postController.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

