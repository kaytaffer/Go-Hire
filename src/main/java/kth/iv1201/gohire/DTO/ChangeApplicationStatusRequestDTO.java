package kth.iv1201.gohire.DTO;

import jakarta.validation.constraints.*;

/**
 * DTO containing information about an applicant status change request.
 */
public class ChangeApplicationStatusRequestDTO {

    @NotNull(message = "Invalid id: Id can not be empty.")
    @Min(1)
    private final int id;

    @NotBlank(message = "Invalid status: Status must be either 'accepted' or 'rejected'.")
    @Pattern(regexp = "accepted|rejected")
    private final String newStatus;

    @NotBlank(message = "Invalid username: Username can not be empty")
    @Size(max = 255, message = "Invalid username: Username must be 1-255 characters long.")
    private final String username;

    @NotBlank(message = "Invalid password: Password can not be empty")
    @Size(max = 255, message = "Invalid password: Password must be 1-255 characters long.")
    private final String password;

    /**
     * Creates a <code>ChangeApplicationStatusRequestDTO</code>.
     * @param id the identifier of the applicant for which to change the status.
     * @param newStatus The new status of the application: 'accepted' or 'rejected'.
     * @param username The username of the recruiter changing the status.
     * @param password The username of the recruiter changing the status.
     */
    public ChangeApplicationStatusRequestDTO(int id, String newStatus, String username, String password) {
        this.id = id;
        this.newStatus = newStatus;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
