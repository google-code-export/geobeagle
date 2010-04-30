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

package com.google.code.geobeagle;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.code.geobeagle.GraphicsGenerator.DifficultyAndTerrainPainter;
import com.google.code.geobeagle.GraphicsGenerator.IconRenderer;
import com.google.code.geobeagle.GraphicsGenerator.NullAttributesPainter;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import android.content.res.Resources;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import roboguice.config.AbstractAndroidModule;

// TODO rename to GeoBeagleModule
public class GeoBeaglePackageModule extends AbstractAndroidModule {

    @BindingAnnotation @Target( { FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface NullAttributesPainterAnnotation {}

    @BindingAnnotation @Target( { FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface DifficultyAndTerrainPainterAnnotation {}

    @Override
    protected void configure() {
    }

    @Provides
    @NullAttributesPainterAnnotation
    public IconRenderer providesNullAttributesIconRenderer(Resources resources,
            NullAttributesPainter nullAttributesPainter) {
        return new IconRenderer(resources, nullAttributesPainter);
    }

    @Provides
    @DifficultyAndTerrainPainterAnnotation
    public IconRenderer providesDifficultyAndTerrainIconRenderer(Resources resources,
            DifficultyAndTerrainPainter difficultyAndTerrainPainter) {
        return new IconRenderer(resources, difficultyAndTerrainPainter);
    }

}
