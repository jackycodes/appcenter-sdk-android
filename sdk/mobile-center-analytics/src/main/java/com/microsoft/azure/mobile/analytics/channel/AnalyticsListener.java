package com.microsoft.azure.mobile.analytics.channel;

import com.microsoft.azure.mobile.ingestion.models.Log;

public interface AnalyticsListener {

    /**
     * Called right before sending a log. The callback can be invoked multiple times based on the number of logs.
     *
     * @param log The log that will be sent.
     */
    void onBeforeSending(Log log);

    /**
     * Called when sending a log failed.
     * The report failed to send after the maximum retries so it will be discarded and won't be retried.
     *
     * @param log The log that failed to send.
     * @param e           An exception that caused failure.
     */
    void onSendingFailed(Log log, Exception e);

    /**
     * Called when a log is sent successfully.
     *
     * @param log The log that was sent successfully.
     */
    void onSendingSucceeded(Log log);
}
