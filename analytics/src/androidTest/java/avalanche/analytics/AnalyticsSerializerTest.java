package avalanche.analytics;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import avalanche.analytics.ingestion.models.EndSessionLog;
import avalanche.analytics.ingestion.models.EventLog;
import avalanche.analytics.ingestion.models.PageLog;
import avalanche.analytics.ingestion.models.json.EndSessionLogFactory;
import avalanche.analytics.ingestion.models.json.EventLogFactory;
import avalanche.analytics.ingestion.models.json.PageLogFactory;
import avalanche.base.ingestion.models.Device;
import avalanche.base.ingestion.models.Log;
import avalanche.base.ingestion.models.LogContainer;
import avalanche.base.ingestion.models.json.DefaultLogSerializer;
import avalanche.base.ingestion.models.json.LogSerializer;

public class AnalyticsSerializerTest {

    private static final String TAG = "TestRunner";

    @Test
    public void someBatch() throws JSONException {
        LogContainer expectedContainer = new LogContainer();
        Device device = new Device();
        device.setSdkVersion("1.2.3");
        device.setModel("S5");
        device.setOemName("HTC");
        device.setOsName("Android");
        device.setOsVersion("4.0.3");
        device.setOsApiLevel(15);
        device.setLocale("en_US");
        device.setTimeZoneOffset(120);
        device.setScreenSize("800x600");
        device.setAppVersion("3.2.1");
        device.setAppBuild("42");
        List<Log> logs = new ArrayList<>();
        expectedContainer.setLogs(logs);
        {
            PageLog pageLog = new PageLog();
            pageLog.setName("home");
            logs.add(pageLog);
        }
        {
            PageLog pageLog = new PageLog();
            pageLog.setName("settings");
            pageLog.setProperties(new HashMap<String, String>() {{
                put("from", "home_menu");
                put("orientation", "portrait");
            }});
            logs.add(pageLog);
        }
        {
            EventLog eventLog = new EventLog();
            eventLog.setId(UUID.randomUUID());
            eventLog.setName("subscribe");
            logs.add(eventLog);
        }
        {
            EventLog eventLog = new EventLog();
            eventLog.setId(UUID.randomUUID());
            eventLog.setName("click");
            eventLog.setProperties(new HashMap<String, String>() {{
                put("x", "1");
                put("y", "2");
            }});
            logs.add(eventLog);
        }
        {
            logs.add(new EndSessionLog());
        }
        UUID sid = UUID.randomUUID();
        for (Log log : logs) {
            log.setSid(sid);
            log.setDevice(device);
        }
        LogSerializer serializer = new DefaultLogSerializer();
        serializer.addLogFactory(EndSessionLog.TYPE, new EndSessionLogFactory());
        serializer.addLogFactory(PageLog.TYPE, new PageLogFactory());
        serializer.addLogFactory(EventLog.TYPE, new EventLogFactory());
        String payload = serializer.serializeContainer(expectedContainer);
        android.util.Log.v(TAG, payload);
        LogContainer actualContainer = serializer.deserializeContainer(payload);
        Assert.assertEquals(expectedContainer, actualContainer);
    }
}