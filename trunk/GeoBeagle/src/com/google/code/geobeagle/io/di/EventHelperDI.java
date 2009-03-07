package com.google.code.geobeagle.io.di;

import com.google.code.geobeagle.io.CachePersisterFacade;
import com.google.code.geobeagle.io.EventHelper;
import com.google.code.geobeagle.io.GpxEventHandler;

public class EventHelperDI {

    public static EventHelper create(GpxToCacheDI.XmlPullParserWrapper xmlPullParser,
            CachePersisterFacade cachePersisterFacade) {
        final GpxEventHandler gpxEventHandler = new GpxEventHandler(cachePersisterFacade);
        final EventHelper.XmlPathBuilder xmlPathBuilder = new EventHelper.XmlPathBuilder();
        return new EventHelper(xmlPathBuilder, gpxEventHandler, xmlPullParser);
    }

}
