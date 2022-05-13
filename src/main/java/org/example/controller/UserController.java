package org.example.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.attribute.RequestAttributes;
import org.example.dto.*;
import org.example.mime.ContentTypes;
import org.example.security.Authentication;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService service;
    private final Gson gson;

   public void getMe(HttpServletRequest request, HttpServletResponse response) throws IOException {
       final Authentication auth = (Authentication) request.getAttribute(
              RequestAttributes.AUTH_ATTR
    );

     final UserMeResponseDTO responseDTO = service.getMe(auth);
     response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseDTO));
}



    public void getAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );

        final List<UserGetAllResponseDTO> responseData = service.getAll(auth);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
       // final Authentication auth = (Authentication) request.getAttribute(
              //  RequestAttributes.AUTH_ATTR
      //  );

        final UserRegisterRequestDTO requestData = gson.fromJson(
                request.getReader(), // TODO: close ? Reader/InputStream
                UserRegisterRequestDTO.class
        );
        final UserRegisterResponseDTO responseData = service.register( requestData);
        // application/json
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }


    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final UserLoginRequestDTO requestData = gson.fromJson(
                request.getReader(),
                UserLoginRequestDTO.class
        );
        final UserLoginResponseDTO responseData = service.login(requestData);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void create (HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );

        final UserCreateRequestDTO requestData = gson.fromJson(
                request.getReader(), UserCreateRequestDTO.class);
        final UserCreateResponseDTO responseData = service.create(auth,requestData);
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));
    }

    public void changeRoles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Authentication auth = (Authentication) request.getAttribute(
                RequestAttributes.AUTH_ATTR
        );

        final UserChangeRolesRequestDTO requestData = gson.fromJson(
                request.getReader(), UserChangeRolesRequestDTO.class);

        final UserChangeRolesResponseDTO responseData = service.changeRoles(auth,requestData);
        //application/json
        response.setContentType(ContentTypes.APPLICATION_JSON);
        response.getWriter().write(gson.toJson(responseData));

    }

}
//TODO
// создать новый метод инфо обо мне

//user.me