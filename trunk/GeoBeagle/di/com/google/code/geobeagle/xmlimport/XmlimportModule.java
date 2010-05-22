package com.google.code.geobeagle.xmlimport;

import com.google.code.geobeagle.xmlimport.GpxImporterDI.MessageHandler;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;

public class XmlimportModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        bind(MessageHandler.class).in(ContextScoped.class);
    }

}
