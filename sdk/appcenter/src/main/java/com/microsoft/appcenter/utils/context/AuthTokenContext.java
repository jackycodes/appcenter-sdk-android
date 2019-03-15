/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.appcenter.utils.context;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Utility to store and retrieve the latest authorization token.
 */
public class AuthTokenContext {

    /**
       Tokens storage max size
     */
    private  final static  int TOKENS_STORAGE_LIMIT = 5;

    /**
     * Unique instance.
     */
    private static AuthTokenContext sInstance;

    /**
     * Global listeners collection.
     */
    private final Collection<Listener> mListeners = new LinkedHashSet<>();

    /**
     * Current value of auth token.
     */
    private String mAuthToken;



    /**
     * Token and time when it was valid storage
    */
    private final List<Pair<String,Long>> tokenAndTimePairList = new ArrayList<>();

    /**
     * Current value of home account id.
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
     * Unset singleton instance.
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
    public synchronized void addListener(@NonNull Listener listener) {
        mListeners.add(listener);
    }

    /**
     * Removes a specific listener.
     *
     * @param listener listener to be removed.
     */
    public synchronized void removeListener(@NonNull Listener listener) {
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
     * @param authToken     authorization token.
     * @param homeAccountId unique user id.
     */
    public synchronized void setAuthToken(String authToken, String homeAccountId) {
        mAuthToken = authToken;
        addTokenToList(authToken);

        /* Call listeners so that they can react on new token. */
        for (Listener listener : mListeners) {
            listener.onNewAuthToken(authToken);
            if (isNewUser(homeAccountId)) {
                listener.onNewUser(authToken);
            }
        }
        mLastHomeAccountId = homeAccountId;
    }

    /**
     * Check whether the user is new.
     *
     * @param newHomeAccountId account id of the logged in user.
     * @return true if this user is not the same as previous, false otherwise.
     */
    private synchronized boolean isNewUser(String newHomeAccountId) {
        return mLastHomeAccountId == null || !mLastHomeAccountId.equals(newHomeAccountId);
    }

    /**
     * Clears info about the token.
     */
    public synchronized void clearToken() {
        mAuthToken = null;
        mLastHomeAccountId = null;
        for (Listener listener : mListeners) {
            listener.onNewAuthToken(null);
            listener.onNewUser(null);
        }
    }


    public List<Pair<String, Long>> getTokenAndTimePairList() {
        return tokenAndTimePairList;
    }


    /**
     *
     * @param token
     * Saves each token and time when it was valid into list
     */
	@VisibleForTesting
    private void addTokenToList(String token){
        tokenAndTimePairList.add(new Pair<>(token,new Date().getTime()));
        if (tokenAndTimePairList.size()>TOKENS_STORAGE_LIMIT){
            tokenAndTimePairList.remove(0);
        }
    }

    /**
     * Token context global listener specification.
     */
    public interface Listener {

        /**
         * Called whenever a new token is set.
         */
        void onNewAuthToken(String authToken);

        /**
         * Called whenever a new user signs in.
         */
        void onNewUser(String authToken);
    }
}
