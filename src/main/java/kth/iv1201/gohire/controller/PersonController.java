package kth.iv1201.gohire.controller;

import jakarta.validation.Valid;
import kth.iv1201.gohire.DTO.CreateApplicantRequestDTO;
import kth.iv1201.gohire.DTO.LoggedInPersonDTO;
import kth.iv1201.gohire.DTO.LoginRequestDTO;
import kth.iv1201.gohire.service.PersonService;
import kth.iv1201.gohire.service.exception.LoginFailedException;
import kth.iv1201.gohire.service.exception.UserCreationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for API calls related to a <code>PersonEntity</code>
 */
@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonService personService;

    /**
     * Creates a new <code>PersonController</code>
     * @param personService The <code>PersonService</code> to use
     */
    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Handles the login API-request
     * @param loginRequest DTO containing login request data
     * @throws LoginFailedException If the username and password do not match an existing user.
     * @throws MethodArgumentNotValidException if any data does not match expected values.
     * @return <code>LoggedInPersonDTO</code> representing the logged-in user
     */
    @PostMapping("/login")
    public LoggedInPersonDTO login(@RequestBody @Valid LoginRequestDTO loginRequest)
            throws LoginFailedException, MethodArgumentNotValidException {
        return personService.login(loginRequest);
    }

    /**
     * Handles the create applicant API-request.
     * @param createApplicantRequest DTO containing applicant request data.
     * @return <code>LoggedInPersonDTO</code> representing the newly created and logged-in user
     * @throws UserCreationFailedException If the requested username already exists.
     * @throws MethodArgumentNotValidException if any data does not match expected values.
     */
    @PostMapping("/createApplicant")
    public LoggedInPersonDTO createNewApplicant(@RequestBody @Valid CreateApplicantRequestDTO createApplicantRequest)
            throws UserCreationFailedException, MethodArgumentNotValidException {
        return personService.createApplicantAccount(createApplicantRequest);
    }
}
