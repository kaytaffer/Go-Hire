const baseUrl = '/api'

function sendPostRequest(endpoint, body){

    function checkIfServerReturnedError(response) {
        if('errorType' in response) {
            throw new Error(response.errorType)
        } else
            return response
    }

    return fetch(baseUrl + endpoint, {
        method: 'POST',
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: JSON.stringify(body)
    }).then(response => response.json()).then(checkIfServerReturnedError)
}

/**
 * Authenticates login credentials with the API
 * @param username the username
 * @param password the password
 * @returns {Promise<any>} a promise either resolving to a user object or an error object
 */
export function authenticateLogin(username, password){
    return sendPostRequest('/login', {username, password})
}

/**
 * Calls the API to create a new applicant with the supplied arguments.
 * @param firstName the new applicant's first name.
 * @param lastName the new applicant's surname.
 * @param email the new applicant's email.
 * @param personNumber the new applicant's person number.
 * @param username the username.
 * @param password the password.
 * @returns {Promise<any>} a promise either resolving to a user object or an error object
 */
export function createNewApplicant(firstName, lastName, email, personNumber, username, password) {
    return sendPostRequest('/createApplicant', {firstName, lastName, email, personNumber, username, password})
}

export function logout(){
    //return sendPostRequest('/logout', {firstName: 'test', id: 1});
    return sendPostRequest('/logout', {});
}