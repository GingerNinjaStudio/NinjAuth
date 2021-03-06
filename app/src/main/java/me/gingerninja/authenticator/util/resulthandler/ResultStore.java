package me.gingerninja.authenticator.util.resulthandler;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

class ResultStore {
    private Map<String, Result> pendingResults = new HashMap<>();

    void clear() {
        pendingResults.clear();
    }

    void addPendingResultRequest(@NonNull String who, int requestCode) {
        Timber.v("Adding pending result request for %s", who);
        pendingResults.put(who, new Result(requestCode));
    }

    void remove(@NonNull String who) {
        pendingResults.remove(who);
    }

    @Nullable
    Result getResult(@NonNull String who) {
        Result result = pendingResults.get(who);

        if (result != null) {
            /*if (result.isPending()) {
                return null;
            } else {*/
            pendingResults.remove(who);
            //}
        }

        return result;
    }

    void setResult(@NonNull String who, int resultCode, @Nullable Object data) {
        Timber.v("Set result for %s: resultCode: %d", who, resultCode);
        Result result = pendingResults.get(who);

        if (result != null) {
            result.setResults(resultCode, data);
        }
    }

    static class Result {
        private final int requestCode;
        private int resultCode = Activity.RESULT_CANCELED;
        private boolean pending = true;

        @Nullable
        private Object data;

        Result(int requestCode) {
            this.requestCode = requestCode;
        }

        int getRequestCode() {
            return requestCode;
        }

        int getResultCode() {
            return resultCode;
        }

        @Nullable
        <T> T getData() {
            return (T) data;
        }

        Result setResults(int resultCode, @Nullable Object data) {
            this.resultCode = resultCode;
            this.data = data;
            pending = false;
            return this;
        }

        boolean isPending() {
            return pending;
        }
    }
}
