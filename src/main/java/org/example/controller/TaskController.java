package org.example.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.example.attribute.RequestAttributes;
import org.example.dto.SearchResultDTO;
import org.example.dto.TaskGetByIdResponseDTO;
import org.example.mime.ContentTypes;
import org.example.security.Authentication;
import org.example.service.SearchService;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class TaskController {
private final SearchService service;
private final Gson gson;

    public void search (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );

        final SearchResultDTO responseData = service.search(auth, request.getParameter("text")); // https://localhost:9999/tasks.search?text=...
       // response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void getById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR);

        final long id = Long.parseLong(request.getParameter("id"));
        final TaskGetByIdResponseDTO responseData = service.getById(auth,id );

      //  resp.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

}
