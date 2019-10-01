package com.kedzie.vbox.harness;

import android.os.Parcel;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.IVirtualBoxErrorInfo;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

import java.io.IOException;
import java.util.Map;

/**
 * Created by kedzie on 10/15/13.
 */
public class MockProgress implements IProgress {

    private long started = System.currentTimeMillis();
    private long now = started;
    private boolean canceled;

    @Override public void waitForCompletion(@KSOAP(type = "int", value = "timeout") int millseconds) throws IOException {}
    @Override public void waitForCompletion(@KSOAP(type = "unsignedInt", value = "operation") int operation, @KSOAP(type = "int", value = "timeout") int millseconds) throws IOException {}
    @Override public void waitForAsyncProgressCompletion(@KSOAP("pProgressAsync") String pProgressAsync) {}

    @Override
    public Integer getTimeout() {
        return 0;
    }

    @Override
    public Integer getResultCode() {
        return 0;
    }

    @Override
    public IVirtualBoxErrorInfo getErrorInfo() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Mock Progress";
    }

    @Override
    public Integer getPercent() {
        return (int)(now-started)/1000;
    }

    @Override
    public Integer getTimeRemaining() {
        return 100-getPercent();
    }

    @Override
    public Integer getOperation() {
        return getPercent()/10+1;
    }

    @Override
    public Integer getOperationCount() {
        return 10;
    }

    @Override
    public String getOperationDescription() {
        return "Mock Operation";
    }

    @Override
    public Integer getOperationPercent() {
        return (getPercent()%10)*10;
    }

    @Override
    public Integer getOperationWeight() {
        return 1;
    }

    @Override
    public String getInitiator() {
        return null;
    }

    @Override
    public Boolean getCancelled() {
        return canceled;
    }

    @Override
    public Boolean getCancelable() {
        return true;
    }

    @Override
    public Boolean getCompleted() {
        return getPercent()>=100;
    }

    @Override
    public void cancel() {
        canceled=true;
    }

    @Override
    public void setTimeout(@KSOAP(type = "unsignedInt", value = "timeout") int timeout) throws IOException {
    }

    @Override
    public void setCurrentOperationProgress(@KSOAP(type = "unsignedInt", value = "percent") int percent) throws IOException {
    }

    @Override
    public void setNextOperation(@KSOAP("nextOperationDescription") String nextOperationDescription, @KSOAP(type = "unsignedInt", value = "nextOperationsWeight") int nextOperationsWeight) throws IOException {
    }

    @Override
    public String getIdRef() {
        return "MOCK";
    }

    @Override
    public void clearCache() {
        now = System.currentTimeMillis();
    }

    @Override
    public void clearCacheNamed(String... names) {
        now = System.currentTimeMillis();
    }

    @Override
    public Map<String, Object> getCache() {
        return null;
    }

    @Override
    public VBoxSvc getAPI() {
        return null;
    }

    @Override
    public String getInterfaceName() {
        return null;
    }

    @Override
    public void release() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
