package kth.iv1201.gohire.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kth.iv1201.gohire.DTO.*;
import kth.iv1201.gohire.controller.exception.AuthenticationForLoggedInUserFailed;
import kth.iv1201.gohire.controller.util.Logger;
import kth.iv1201.gohire.controller.util.LoggerException;
import kth.iv1201.gohire.service.PersonService;
import kth.iv1201.gohire.service.exception.ApplicationHandledException;
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
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsible for API calls related to a <code>PersonEntity</code>.
 */
@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonService personService;
    private final AuthenticationManager authenticationManager;

    /**
     * Creates a new <code>PersonController</code>.
     * @param personService The <code>PersonService</code> to use.
     */
    @Autowired
    public PersonController(PersonService personService, AuthenticationManager authenticationManager) {
        this.personService = personService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Handles the login API-request.
     * @param loginRequest DTO containing login request data.
     * @throws LoggerException if there is a problem with logging an event.
     * @throws UserNotFoundException If the user is authenticated but can not be fetched from the database.
     * @return <code>LoggedInPersonDTO</code> representing the logged-in user.
     */
    @PostMapping("/login")
    public LoggedInPersonDTO login(@RequestBody @Valid LoginRequestDTO loginRequest, HttpSession session)
            throws LoggerException, UserNotFoundException {
        Authentication authenticationResponse = authenticateRequest(loginRequest.getUsername(), loginRequest.getPassword());
        saveAuthenticatedUserInSession(authenticationResponse, session);
        Logger.logEvent("User logged in: " + loginRequest.getUsername());
        return personService.fetchLoggedInPersonByUsername(loginRequest.getUsername());
    }

    /**
     * Handles the logout API-request.
     * @param session The HttpSession associated with the logged-in user's session.
     * @return ResponseEntity with an ok status and logout successful message.
     * @throws LoggerException if there is a problem with logging an event.
     */
    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> performLogout(HttpSession session) throws LoggerException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.setAuthenticated(false);
        session.invalidate();
        Logger.logEvent("User logged out: " + auth.getName());

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Logout successful");
        return ResponseEntity.ok().body(responseMap);
    }

    /**
     * Handles the create applicant API-request.
     * @param createApplicantRequest DTO containing applicant request data.
     * @return <code>LoggedInPersonDTO</code> representing the newly created and logged-in user.
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

    /**
     * Fetches all applications.
     * @return All applications.
     */
    @PreAuthorize("hasRole('recruiter')")
    @GetMapping("/applications")
    public List<ApplicantDTO> fetchApplicants(){
        return personService.fetchApplicants();
    }

    /**
     * Changes the status of an application.
     * @param request DTO containing application change request data.
     * @return the changed and saved application.
     * @throws LoggerException if there is a problem with logging an event.
     * @throws ApplicationHandledException if the applicant has already been handled.
     */
    @PreAuthorize("hasRole('recruiter')")
    @PostMapping("/changeApplicationStatus")
    public ApplicantDTO changeApplicationStatus(@RequestBody @Valid ChangeApplicationStatusRequestDTO request)
            throws LoggerException, ApplicationHandledException, AuthenticationForLoggedInUserFailed {
        Authentication requestCredentialAuth;
        try {
            requestCredentialAuth = authenticateRequest(request.getUsername(), request.getPassword());
        } catch (Exception e) {
            throw new AuthenticationForLoggedInUserFailed("Logged in user provided username or password that did not " +
                    "match logged in account.");
        }
        Authentication currentLoggedInRecruiterAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentLoggedInRecruiterAuth.equals(requestCredentialAuth)) {
            ApplicantDTO changedApplicant = personService.changeApplicantStatus(request);
            Logger.logEvent("Recruiter " + request.getUsername() + " changed status of applicant " +
                    changedApplicant.getFirstName() + " " + changedApplicant.getLastName() + " to " +
                    changedApplicant.getStatus() + ".");
            return changedApplicant;
        } else {
            throw new AuthenticationForLoggedInUserFailed("Logged in user provided username or password that did not " +
                    "match logged in account.");
        }
    }

    private Authentication authenticateRequest(String username, String password) throws BadCredentialsException {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
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
}

