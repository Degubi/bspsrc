package bsplib.app;

import bsplib.log.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.xml.parsers.*;

public final class SourceAppDB {
    private static final Logger L = LogUtils.getLogger();
    private static SourceAppDB instance;

    private List<SourceApp> appList = new ArrayList<>();
    private Map<Integer, SourceApp> appMap = new HashMap<>();
    private float score;

    public static SourceAppDB getInstance() {
        if (instance == null) {
            instance = new SourceAppDB();
        }

        return instance;
    }

    private SourceAppDB() {
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try (InputStream is = getClass().getResourceAsStream("appdb.xml")) {
            SAXParser sp = spf.newSAXParser();
            SourceAppHandler handler = new SourceAppHandler();
            sp.parse(is, handler);
            appList = handler.getAppList();

            // generate ID map for validation and faster access
            for (SourceApp app : appList) {
                Integer appID = app.appID;

                // warn if we have more than one app for an ID
                if (appMap.containsKey(appID)) {
                    L.log(Level.WARNING, "Duplicate App ID {0} for \"{1}\" and \"{2}\"",
                            new Object[]{appID, appMap.get(appID), app});
                }

                appMap.put(appID, app);
            }
        } catch (Exception ex) {
            L.log(Level.SEVERE, "Can't load Source application database", ex);
        }
    }

    /**
     * Returns the source app for a Steam AppID.
     *
     * @param appID Steam AppID
     * @return the app identifier for the ID or SourceApp.UNKNOWN if it couldn't
     * be found.
     */
    public SourceApp fromID(int appID) {
        if (appMap == null || !appMap.containsKey(appID)) {
            return SourceApp.UNKNOWN;
        } else {
            return appMap.get(appID);
        }
    }

    /**
     * Tries to find the AppID though a heuristical search with the given
     * parameters.
     *
     * @param bspName BSP file name
     * @param bspVersion BSP file version
     * @param classNames Complete set of entity class names
     * @return
     */
    public SourceApp find(String bspName, int bspVersion, Set<String> classNames) {
        SourceApp candidate = SourceApp.UNKNOWN;
        score = 0;

        if (appList == null) {
            return candidate;
        }

        for (SourceApp app : appList) {
            // skip candidates with wrong version
            if (app.canCheckVersion() && !app.checkVersion(bspVersion)) {
                continue;
            }

            L.log(Level.FINER, "Testing {0}", app.name);

            float scoreNew = 0;

            // check entity class names
            if (app.canCheckEntities()) {
                scoreNew += app.checkEntities(classNames);
            }

            // check BSP file name pattern
            if (app.canCheckName()) {
                scoreNew += app.checkName(bspName);
            }

            if (scoreNew != 0 && scoreNew > score) {
                L.log(Level.FINER, "New candidate {0} with a score of {1}", new Object[]{ app.name, scoreNew });
                candidate = app;
                score = scoreNew;
            }
        }

        return candidate;
    }

    /**
     * Returns the total heuristic score for the last call of
     * {@link #find(java.lang.String, int, java.util.Set)}.
     *
     * @return total score
     */
    public float getScore() {
        return score;
    }

    /**
     * Returns the list of all Source apps from the database.
     *
     * @return list of Source apps
     */
    public List<SourceApp> getAppList() {
        return Collections.unmodifiableList(appList);
    }
}
