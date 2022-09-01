package ca.corefacility.bioinformatics.irida.security;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Thrown if an authentication request is rejected because the account's role is ROLE_SEQUENCER.
 */
public class SequencerUILoginException extends AccountStatusException {

    /**
     * Constructs a <code>SequencerUILoginException</code> with the specified message.
     * 
     * @param msg the detail message
     */
    public SequencerUILoginException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>SequencerUILoginException</code> with the specified message and root cause.
     * 
     * @param msg   the detail message
     * @param cause root cause
     */
    public SequencerUILoginException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
