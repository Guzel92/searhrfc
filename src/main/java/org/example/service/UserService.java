package org.example.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.example.dto.*;
import org.example.entity.TokenEntity;
import org.example.entity.UserEntity;
import org.example.exception.LoginNotFoundException;
import org.example.exception.TokenGenerationException;
import org.example.exception.UsernameAlreadyRegisteredException;
import org.example.repository.UserRepository;
import org.example.security.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RSASSASigner signer;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final byte[] encodedKeyBytes = Files.readAllBytes(Paths.get(
                Optional.ofNullable(System.getenv("SIGN_KEY"))
                        .orElse("C:\\Users\\79178\\Desktop\\tomcat-embed (2)\\sign.key")
        ));
        final byte[] keyBytes = Base64.getMimeDecoder().decode(encodedKeyBytes);
        final PrivateKey signKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(
                keyBytes
        ));
        this.signer = new RSASSASigner(signKey);
    }

    public UserMeResponseDTO getMe(Authentication auth) {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        return new UserMeResponseDTO(
                auth.getName(),
                auth.getRoles()
        );
    }


    public List<UserGetAllResponseDTO> getAll(Authentication auth) {
        if (!auth.hasRole(Roles.USERS_VIEW_ALL)) {
            throw new ForbiddenException();
        }
        return repository.getAll()
                .stream()
                .map(o-> new UserGetAllResponseDTO(o.getId(),o.getLogin()))
                .collect(Collectors.toList());
        // filter,map, reduce

    }

    public UserRegisterResponseDTO register(UserRegisterRequestDTO requestData) {
        final String hashedPassword = passwordEncoder.encode(requestData.getPassword());
        // transaction
        repository.findByLogin(requestData.getLogin())
                .ifPresent(o -> {
                    throw new UsernameAlreadyRegisteredException(o.getLogin());
                });
        final UserEntity saved = repository.save(new UserEntity(
                0L,
                requestData.getLogin(),
                hashedPassword,
                new String[]{}
        ));
//генераөия tokena в отдельный метод надо выносить
        try {

            final String token = createToken(saved);

            return new UserRegisterResponseDTO(
                    saved.getId(),
                    saved.getLogin(),
                    token
            );
        }catch (JOSEException e) {
            throw new TokenGenerationException(e);
        }
    }

    public LoginAuthentication authenticateByLoginAndPassword(String login, String password) {
        //подходы:
        //1.hash, matches долгие
        final UserEntity entity = repository.findByLogin(login)
                .orElseThrow(NotFoundException::new);
        //password незахэшированный
        //entity.getPassword захэшированный
        if (!passwordEncoder.matches(password,entity.getPassword())){
            throw new CredentialsNotMatchesException();

        }

        return new LoginAuthentication(entity.getId(), login, entity.getRoles());
    }

    public TokenAuthentication authenticateByToken(String token) {
        return repository.findByToken(token)
                .map(o-> new TokenAuthentication(o.getId(), o.getLogin(),o.getRoles()) )
                .orElseThrow(TokenNotFoundException::new)
                ;
    }

    public X509Authentication authenticateByCommonName(String commonName) {
        return repository.findByLogin(commonName)
                .map(o-> new X509Authentication(o.getId(), commonName,o.getRoles()) )
                .orElseThrow(LoginNotFoundException::new)
                ;
    }

//JWTToken можно добавить
    public UserLoginResponseDTO login(UserLoginRequestDTO requestData) {
        //взяли из login всё
        final UserEntity entity = repository.findByLogin(requestData.getLogin())
                .orElseThrow(NotFoundException::new);
        //password незахэшированный
        //entity.getPassword захэшированный
        if (!passwordEncoder.matches(requestData.getPassword(),entity.getPassword())){
            throw new CredentialsNotMatchesException();
        }
        try {

            final String token = createToken(entity);

            return new UserLoginResponseDTO(
                    entity.getId(),
                    entity.getLogin(),
                    token
            );
        }catch (JOSEException e) {
            throw new TokenGenerationException(e);
        }
    }
    //saved переименовали entity
    private String createToken(UserEntity entity) throws JOSEException {
   /* byte[] buffer = new byte[128];
    random.nextBytes(buffer);
    final String token = Base64.getUrlEncoder().
            withoutPadding().
            encodeToString(buffer);
    repository.save(new TokenEntity(saved.getId(),token));
    return token;*/
        final Instant now = Instant.now();
        final JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS512)
                .build();

        final JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                .subject(entity.getLogin())
                .claim("id", entity.getId())
                // надо добавить время жизни
                .expirationTime(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .claim("roles", entity.getRoles())
                .build();

        final SignedJWT jwt = new SignedJWT(header, claimSet);
        jwt.sign(signer);
        return jwt.serialize();
    }

    //  public UserCreateResponseDTO create(UserCreateRequestDTO requestData) {

    //  }

    public UserCreateResponseDTO create(Authentication auth, UserCreateRequestDTO requestData) {
        if (!auth.hasRole(Roles.USERS_VIEW_ALL)){
            throw new ForbiddenException();
        }

        final String hashedPassword = passwordEncoder.encode(requestData.getPassword());
        // transaction
        repository.findByLogin(requestData.getLogin())
                .ifPresent(o-> {
                    throw new UsernameAlreadyRegisteredException(o.getLogin());
                });
        final UserEntity saved = repository.save(new UserEntity(
                0L,
                requestData.getLogin(),
                hashedPassword,
                requestData.getRoles()
        ));

        return new UserCreateResponseDTO(
                saved.getId(),
                saved.getLogin()
        );

    }

    public UserChangeRolesResponseDTO changeRoles(Authentication auth, UserChangeRolesRequestDTO requestData) {
        if (!auth.hasRole(Roles.USERS_EDIT_ALL)){
            throw new ForbiddenException();
        }
        final UserEntity entity = repository.setRolesByLogin (
                requestData.getLogin(),
                requestData.getRoles());

        return new UserChangeRolesResponseDTO(
                entity.getId(),
                entity.getLogin()
        );
    }
}
