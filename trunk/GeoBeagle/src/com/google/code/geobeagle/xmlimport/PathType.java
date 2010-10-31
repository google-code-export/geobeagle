/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.GeocacheFactory.Source;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;

enum PathType {
    CACHE {
        @Override
        public void startTag(XmlPullParser xmlPullParser,
                CacheXmlTagHandler cacheXmlTagHandler) {
            cacheXmlTagHandler.available(xmlPullParser.getAttributeValue(null, "available"));
            cacheXmlTagHandler.archived(xmlPullParser.getAttributeValue(null, "archived"));
        }

        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.symbol(text);
            return true;
        }
    },
    CACHE_TYPE {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.cacheType(text);
            return true;
        }
    },
    CONTAINER {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.container(text);
            return true;
        }
    },
    DESC {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.wptDesc(text);
            return true;
        }
    },
    DIFFICULTY {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.difficulty(text);
            return true;
        }
    },
    GPX_TIME {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            return cacheXmlTagHandler.gpxTime(text);
        }
    },
    HINT {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            if (!text.equals(""))
                cacheXmlTagHandler.hint(text);
            return true;
        }
    },
    LAST_MODIFIED {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            return true;
        }
    },
    LINE {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.line(text);
            return true;
        }
    },
    LOC_COORD {
        @Override
        public void startTag(XmlPullParser xmlPullParser,
                CacheXmlTagHandler cacheXmlTagHandler) {
            cacheXmlTagHandler.wpt(xmlPullParser.getAttributeValue(null, "lat"),
                    xmlPullParser.getAttributeValue(null, "lon"));
        }
    },
    LOC_WPT {
        @Override
        public void endTag(CacheXmlTagHandler cacheXmlTagHandler) throws IOException {
            cacheXmlTagHandler.endCache(Source.LOC);
        }
    },
    LOC_WPTNAME {
        @Override
        public void startTag(XmlPullParser xmlPullParser,
                CacheXmlTagHandler cacheXmlTagHandler) throws IOException {
            cacheXmlTagHandler.startCache();
            cacheXmlTagHandler.wptName(xmlPullParser.getAttributeValue(null, "id"));
        }

        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.groundspeakName(text.trim());
            return true;
        }
    },
    LOG_DATE {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.logDate(text);
            return true;
        }
    },
    LOG_TEXT {
        @Override
        public void endTag(CacheXmlTagHandler cacheXmlTagHandler) throws IOException {
            cacheXmlTagHandler.setEncrypted(false);
        }

        @Override
        public void startTag(XmlPullParser xmlPullParser,
                CacheXmlTagHandler cacheXmlTagHandler) {
            cacheXmlTagHandler.setEncrypted("true".equalsIgnoreCase(xmlPullParser
                    .getAttributeValue(null, "encoded")));
        }

        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.logText(text);
            return true;
        }
    },
    LOG_TYPE {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.logType(text);
            return true;
        }
    },
    LONG_DESCRIPTION {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.longDescription(text);
            return true;
        }
    },
    NAME {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.groundspeakName(text);
            return true;
        }
    },
    NOP {
        @Override
        public void startTag(XmlPullParser xmlPullParser,
                CacheXmlTagHandler cacheXmlTagHandler) {
        }

        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            return true;
        }
    },
    PLACED_BY {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.placedBy(text);
            return true;
        }
    },
    SHORT_DESCRIPTION {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.shortDescription(text);
            return true;
        }
    },
    SYMBOL {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.symbol(text);
            return true;
        }
    },
    TERRAIN {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.terrain(text);
            return true;
        }
    },
    WPT {
        @Override
        public void endTag(CacheXmlTagHandler cacheXmlTagHandler) throws IOException {
            cacheXmlTagHandler.endCache(Source.GPX);
        }

        @Override
        public void startTag(XmlPullParser xmlPullParser,
                CacheXmlTagHandler cacheXmlTagHandler) {
            cacheXmlTagHandler.startCache();
            cacheXmlTagHandler.wpt(xmlPullParser.getAttributeValue(null, "lat"),
                    xmlPullParser.getAttributeValue(null, "lon"));
        }
    },
    WPT_NAME {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.wptName(text);
            return true;
        }
    },
    WPT_TIME {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.wptTime(text);
            return true;
        }
    },
    LOG_FINDER {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.logFinder(text);
            return true;
        }
    },
    GPX_URL {
        @Override
        public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler)
                throws IOException {
            cacheXmlTagHandler.url(text);
            return true;
        }
    };

    @SuppressWarnings("unused")
    public void endTag(CacheXmlTagHandler cacheXmlTagHandler) throws IOException {
    }

    @SuppressWarnings("unused")
    public void startTag(XmlPullParser xmlPullParser,
            CacheXmlTagHandler cacheXmlTagHandler) throws IOException {
    }

    @SuppressWarnings("unused")
    public boolean text(String text, CacheXmlTagHandler cacheXmlTagHandler) throws IOException {
        return true;
    }
}
