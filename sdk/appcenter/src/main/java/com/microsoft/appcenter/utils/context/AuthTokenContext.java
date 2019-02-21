package com.microsoft.appcenter.utils.context;

import android.support.annotation.VisibleForTesting;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Utility to store and retrieve the latest authorization token.
 */
public class AuthTokenContext {

    /**
     * Unique instance.
     */
    private static AuthTokenContext sInstance;

    /**
     * Global listeners collection.
     */
    private final Collection<Listener> mListeners;

    /**
     * Current value of auth token.
     */
    private String mAuthToken;

    /**
     * Current value of account id.
     */
    private String mLastHomeAccountId;

    /**
     * Get unique instance.
     *
     * @return unique instance.
     */
    public static synchronized AuthTokenContext getInstance() {
        if (sInstance == null) {
            sInstance = new AuthTokenContext();
        }
        return sInstance;
    }

    /**
     * A private constructor for the class.
     */
    private AuthTokenContext() {
        mListeners = new LinkedHashSet<>();
    }

    /**
     * Unsets singleton instance.
     */
    @VisibleForTesting
    public static synchronized void unsetInstance() {
        sInstance = null;
    }

    /**
     * Adds listener to token context.
     *
     * @param listener listener to be notified of changes.
     */
    public synchronized void addListener(Listener listener) {
        mListeners.add(listener);
    }

    /**
     * Removes a specific listener.
     *
     * @param listener listener to be removed.
     */
    public synchronized void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    /**
     * Gets current authorization token.
     *
     * @return authorization token.
     */
    public synchronized String getAuthToken() {
        return mAuthToken;
    }

    /**
     * Sets new authorization token.
     *
     * @param authToken authorization token.
     */
    public synchronized void setAuthToken(String authToken) {
        mAuthToken = authToken;

        /* Call listeners so that they can react on new token. */
        for (Listener listener : mListeners) {
            listener.onNewAuthToken(authToken);
        }
    }

    /**
     * Clears info about the token.
     */
    public void clearToken() {
        mAuthToken = null;
        mLastHomeAccountId = null;
    }

    /**
     * Token context global listener specification.
     */
    public interface Listener {

        /**
         * Called whenever a new token is set.
         */
        void onNewAuthToken(String authToken);
    }
}
