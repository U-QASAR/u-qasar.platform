package eu.uqasar.service;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import eu.uqasar.exception.auth.NoPendingRegistrationException;
import eu.uqasar.exception.auth.RegistrationCancelledException;
import eu.uqasar.exception.auth.RegistrationNotYetConfirmedException;
import eu.uqasar.exception.auth.UnknownUserException;
import eu.uqasar.exception.auth.WrongUserCredentialsException;
import eu.uqasar.exception.auth.register.UserMailAlreadyExistsException;
import eu.uqasar.exception.auth.register.UserNameAlreadyExistsException;
import eu.uqasar.exception.auth.reset.NoPendingResetPWRequestException;
import eu.uqasar.exception.auth.reset.ResetPWRequestTimeoutException;
import eu.uqasar.model.user.RegistrationStatus;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.UserSource;
import static eu.uqasar.model.user.UserSource.LDAP;
import eu.uqasar.service.settings.LdapSettingsService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.util.ldap.LdapManager;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.lang.Args;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
@ApplicationScoped
public class AuthenticationService {

    public static final String PW_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!§$%&@#_,\\.\\-|<>]).{6,256})";

    @Inject
    Logger logger;

    @Inject
    UserService userService;
    
    @Inject
    LdapSettingsService ldapSettingsService;

    /**
     *
     * @return @throws NoSuchAlgorithmException
     */
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        // VERY important to use SecureRandom instead of just Random
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        return salt;
    }

    /**
     *
     * @param password
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static byte[] getEncryptedPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
        // specifically names SHA-1 as an acceptable hashing algorithm for
        // PBKDF2
        String algorithm = "PBKDF2WithHmacSHA1";
        // SHA-1 generates 160 bit hashes, so that's what makes sense here
        int derivedKeyLength = 160;
        // Pick an iteration count that works for you. The NIST recommends at
        // least 1,000 iterations:
        // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
        // iOS 4.x reportedly uses 10,000:
        // http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
        int iterations = 20000;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations,
                derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
        return f.generateSecret(spec).getEncoded();
    }

    /**
     *
     * @return
     */
    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void checkMailAlreadyRegistered(final String email) throws UserMailAlreadyExistsException {
        // check if user already exists
        User existingUser = userService.getByMail(email);
        if ((existingUser != null)
                && !existingUser.getRegistrationStatus().
                equals(RegistrationStatus.CANCELLED)) {
            // user already exists and has pending or confirmed registration
            throw new UserMailAlreadyExistsException("A user with the given e-Mail address already exists!");
        }
    }

    public void checkUserNameAlreadyRegistered(final String username) throws UserNameAlreadyExistsException {
        // check if user already exists
        User existingUser = userService.getByUserName(username);
        if ((existingUser != null)
                && !existingUser.getRegistrationStatus().
                equals(RegistrationStatus.CANCELLED)) {
            // user already exists and has pending or confirmed registration
            throw new UserNameAlreadyExistsException("A user with the given username already exists!");
        }
    }

    /**
     *
     * @param email
     * @return
     * @throws NoPendingRegistrationException
     */
    public User resetRegistrationInformation(String email)
            throws NoPendingRegistrationException {
        // check if user already exists
        User user = userService.getByMail(email);
        if ((user == null)
                || !user.getRegistrationStatus().equals(
                        RegistrationStatus.PENDING)) {
            // user doesn't exist or has already confirmed/cancelled
            // registration
            throw new NoPendingRegistrationException();
        }

        // set new registration information
        user.setRegistrationToken(generateToken());
        user.setRegistrationStatus(RegistrationStatus.PENDING);
        user.setRegistrationDate(new Date());
        return userService.update(user);
    }

    public User register(User user, final String password) {
        try {
            byte[] salt = generateSalt();
            byte[] encryptedPassword = getEncryptedPassword(password, salt);

            user.setPassword(encryptedPassword);
            user.setPwSalt(salt);

            // set registration information
            user.setRegistrationToken(generateToken());
            user.setRegistrationStatus(RegistrationStatus.PENDING);
            user.setRegistrationDate(new Date());

            user = userService.create(user);
            return user;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("An unexpected error occured during registration!");
        }
    }

    public User registerLdapBasedUser(User user, boolean needToConfirmRegistration) {
        if (needToConfirmRegistration) {
            // set registration information
            user.setRegistrationToken(generateToken());
            user.setRegistrationStatus(RegistrationStatus.PENDING);
            user.setRegistrationDate(new Date());
        } else {
            user.setRegistrationStatus(RegistrationStatus.CONFIRMED);
            user.setRegistrationDate(new Date());
        }
        user.setSource(UserSource.LDAP);
        user = userService.create(user);
        return user;
    }

    public User confirmRegistration(User user) {
        user.setRegistrationStatus(RegistrationStatus.CONFIRMED);
        user.setRegistrationToken(null);
        user = userService.update(user);
        return user;
    }

    public void cancelRegistration(User user) {
        userService.delete(user);
    }

    public User authenticate(final String login, final String password) throws UnknownUserException, WrongUserCredentialsException, RegistrationCancelledException, RegistrationNotYetConfirmedException {
        User user = userService.getByMail(login);
        if (user == null) {
            user = userService.getByLdapLogin(login);
            if(user == null) {
                throw new UnknownUserException();
            }
        } else if (user.getRegistrationStatus() == RegistrationStatus.CANCELLED) {
            throw new RegistrationCancelledException();
        }

        boolean authenticated = authenticate(password, user);
        if (!authenticated) {
            throw new WrongUserCredentialsException();
        } else if (authenticated && user.getRegistrationStatus() == RegistrationStatus.PENDING) {
            throw new RegistrationNotYetConfirmedException();
        }
        return user;
    }

    private boolean authenticate(String attemptedPassword, User user) {
        if(user.getSource() == LDAP) {
            try {
                LdapManager manager = LdapManager.getInstance(ldapSettingsService);
                return manager.authenticateBySAMAccountName(user.getUserName(), attemptedPassword);
            } catch (NamingException ex) {
                logger.error(ex.getMessage(), ex);
                return false;
            }
        } else {
            return authenticate(attemptedPassword, user.getPassword(), user.getPwSalt());
        }
    }
    
    private boolean authenticate(String attemptedPassword,
            byte[] encryptedPassword, byte[] salt) {
        try {
            // Encrypt the clear-text password using the same salt that was used to
            // encrypt the original password
            byte[] encryptedAttemptedPassword = getEncryptedPassword(
                    attemptedPassword, salt);

            // Authentication succeeds if encrypted password that the user entered
            // is equal to the stored hash
            return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(e);
            throw new RuntimeException("An unexpected error occurred while trying to authenticate against a password!", e);
        }
    }

    public User requestNewPassword(final String mail) {
        // get user by mail
        User user = userService.getByMail(mail);
        // check if user is registered properly (and confirmed)
        if (user == null || user.getRegistrationStatus() != RegistrationStatus.CONFIRMED) {
            return null;
        }

        // set reset token and date
        user.setResetPWRequestToken(generateToken());
        user.setResetPWRequestDate(new Date());
        // update user
        return userService.update(user);
    }

    public boolean checkNonEmptyPasswords(final String password, final String passwordConfirmation) {
        return !StringUtils.isBlank(password) && !StringUtils.
                isBlank(passwordConfirmation);
    }

    public boolean checkPasswordsEqual(final String password, final String passwordConfirmation) {
        return StringUtils.equals(password, passwordConfirmation);
    }

    public boolean updateUserPassword(User user, final String password, final String passwordConfirmation) {
        Args.notNull(user, "User may not be null!");
        Args.notEmpty(password, "Password may not be null or empty!");
        Args.
                notEmpty(passwordConfirmation, "Password confirmation may not be null or empty!");
        try {
            byte[] salt = generateSalt();
            byte[] encryptedPassword = getEncryptedPassword(password, salt);

            user.setPassword(encryptedPassword);
            user.setPwSalt(salt);
            userService.update(user);
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("An unexpected error occurred!", e);
        }
    }

    public User resetPassword(User user, final String password) {
        // generate hashed + salted password and store user with password
        // and salt in db
        try {
            byte[] salt = generateSalt();
            byte[] encryptedPassword = getEncryptedPassword(password, salt);

            user.setPassword(encryptedPassword);
            user.setPwSalt(salt);

            // set registration information
            user.setResetPWRequestToken(null);
            user.setResetPWRequestDate(null);

            return userService.update(user);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("An unexpected error occurred!", e);
        }
    }

    public User getUserByPWResetToken(final String token) throws NoPendingResetPWRequestException, ResetPWRequestTimeoutException {
        if ((token == null) || token.isEmpty()) {
            throw new NoPendingResetPWRequestException(
                    "No pending request for resetting password found!");
        }
        User user = userService.getByPWResetToken(token);
        if (user == null) {
            throw new NoPendingResetPWRequestException("No pending request for resetting password found!");
        }
        // check if reset password token has timed out
        if (isResetPWRequestTimeout(user)) {
            throw new ResetPWRequestTimeoutException(
                    "Your request for resetting your password has timed out!");
        }
        return user;
    }

    /**
     * Checks if request for resetting password has been more than half a day
     * ago.
     * <p>
     * @param user
     * @return
     */
    private boolean isResetPWRequestTimeout(User user) {
        Date now = new Date();
        long halfDay = 43200000L;
        return (now.getTime() - user.getResetPWRequestDate().getTime()) > halfDay;
    }
}
