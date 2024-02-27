package kth.iv1201.gohire.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kth.iv1201.gohire.DTO.ApplicantDTO;
import kth.iv1201.gohire.DTO.CreateApplicantRequestDTO;
import kth.iv1201.gohire.DTO.LoggedInPersonDTO;
import kth.iv1201.gohire.DTO.LoginRequestDTO;
import kth.iv1201.gohire.controller.util.Logger;
import kth.iv1201.gohire.controller.util.LoggerException;
import kth.iv1201.gohire.service.PersonService;
import kth.iv1201.gohire.service.exception.UserCreationFailedException;
import kth.iv1201.gohire.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for API calls related to a <code>PersonEntity</code>
 */
@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonService personService;
    private final AuthenticationManager authenticationManager;

    /**
     * Creates a new <code>PersonController</code>
     * @param personService The <code>PersonService</code> to use
     */
    @Autowired
    public PersonController(PersonService personService, AuthenticationManager authenticationManager) {
        this.personService = personService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Handles the login API-request
     * @param loginRequest DTO containing login request data
     * @throws LoggerException if there is a problem with logging an event.
     * @throws UserNotFoundException If the user is authenticated but can not be fetched from the database.
     * @return <code>LoggedInPersonDTO</code> representing the logged-in user
     */
    @PostMapping("/login")
    public LoggedInPersonDTO login(@RequestBody @Valid LoginRequestDTO loginRequest, HttpSession session)
            throws LoggerException, UserNotFoundException {
        Authentication authenticationResponse = authenticateLoginRequest(loginRequest);
        saveAuthenticatedUserInSession(authenticationResponse, session);
        Logger.logEvent("User logged in: " + loginRequest.getUsername());
        return personService.fetchLoggedInPersonByUsername(loginRequest.getUsername());
    }

    /**
     * Handles the create applicant API-request.
     * @param createApplicantRequest DTO containing applicant request data.
     * @return <code>LoggedInPersonDTO</code> representing the newly created and logged-in user
     * @throws UserCreationFailedException If the requested username already exists.
     * @throws LoggerException if there is a problem with logging an event.
     */
    @PostMapping("/createApplicant")
    public LoggedInPersonDTO createNewApplicant(@RequestBody @Valid CreateApplicantRequestDTO createApplicantRequest)
            throws UserCreationFailedException, LoggerException {
        LoggedInPersonDTO newApplicant = personService.createApplicantAccount(createApplicantRequest);
        Logger.logEvent("New applicant registered: " + newApplicant.getUsername());
        return newApplicant;
    }

    @GetMapping("/who")
    public String notSecret() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> performLogout(HttpSession session) throws LoggerException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.setAuthenticated(false);
        session.invalidate();
        Logger.logEvent("User logged out: " + auth.getName());
        return ResponseEntity.ok().build();
    }

    /* The following methods are for testing purposes since there is no other protected content */
    @PreAuthorize("hasRole('recruiter')")
    @GetMapping("/recruiter")
    public String getRecruiterSecret() {
        return "Secret thing";
    }

    @PreAuthorize("hasRole('applicant')")
    @GetMapping("/applicant")
    public String getApplicantSecret() {
        return "Secret thing";
    }

    private Authentication authenticateLoginRequest(LoginRequestDTO loginRequest) throws BadCredentialsException {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getUsername(),
                        loginRequest.getPassword());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        if(!authenticationResponse.isAuthenticated())
            throw new BadCredentialsException("Person with given credentials does not exist.");
        return authenticationResponse;
    }

    private void saveAuthenticatedUserInSession(Authentication authenticationResponse, HttpSession session) {
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }

    /**
     * Fetches all applications
     * @return All applications
     */
    @PreAuthorize("hasRole('recruiter')")
    @GetMapping("/applications")
    public List<ApplicantDTO> fetchApplicants(){
        return personService.fetchApplicants();
    }
}
