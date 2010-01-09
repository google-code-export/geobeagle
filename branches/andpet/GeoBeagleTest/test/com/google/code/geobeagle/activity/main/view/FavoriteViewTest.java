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

package com.google.code.geobeagle.activity.main.view;

import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.database.DbFrontend;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
    FavoriteView.class
})
public class FavoriteViewTest {
    @Test
    public void testCreateFavoriteView() {
        Context context = PowerMock.createMock(Context.class);
        AttributeSet attrs = PowerMock.createMock(AttributeSet.class);

        suppress(constructor(ImageView.class, Context.class));
        suppress(method(ImageView.class, "setOnClickListener"));
        suppress(constructor(ImageView.class, Context.class, AttributeSet.class));
        suppress(method(ImageView.class, "setOnClickListener"));
        suppress(constructor(ImageView.class, Context.class,
                AttributeSet.class, Integer.class));
        suppress(method(ImageView.class, "setOnClickListener"));

        PowerMock.replayAll();
        new FavoriteView(context);
        new FavoriteView(context, attrs);
        new FavoriteView(context, attrs, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetGeoCacheToFavorite() {
        Context context = PowerMock.createMock(Context.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        suppress(method(ImageView.class, "setOnClickListener"));
        suppress(constructor(ImageView.class, Context.class));

        EasyMock.expect(dbFrontend.geocacheHasTag("GC123", Tags.FAVORITES))
                .andReturn(false);
        suppress(method(ImageView.class, "setImageResource"));

        PowerMock.replayAll();
        new FavoriteView(context).setGeocache(dbFrontend, "GC123");
        PowerMock.verifyAll();
    }

    @Test
    public void testSetFavorite() {
        Context context = PowerMock.createMock(Context.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        suppress(method(ImageView.class, "setOnClickListener"));
        suppress(constructor(ImageView.class, Context.class));
        EasyMock.expect(dbFrontend.geocacheHasTag("GC123", Tags.FAVORITES))
                .andReturn(true);
        suppress(method(ImageView.class, "setImageResource"));
        dbFrontend.setGeocacheTag("GC123", Tags.FAVORITES, true);
        suppress(method(ImageView.class, "setImageResource"));

        PowerMock.replayAll();
        final FavoriteView favoriteView = new FavoriteView(context);
        favoriteView.setGeocache(dbFrontend, "GC123");
        favoriteView.setFavorite(true);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetGeoCacheToUnfavorite() {
        Context context = PowerMock.createMock(Context.class);
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        suppress(constructor(ImageView.class, Context.class));
        suppress(method(ImageView.class, "setOnClickListener"));

        EasyMock.expect(dbFrontend.geocacheHasTag("GC123", Tags.FAVORITES))
                .andReturn(true);
        suppress(method(ImageView.class, "setImageResource"));

        PowerMock.replayAll();
        new FavoriteView(context).setGeocache(dbFrontend, "GC123");
        PowerMock.verifyAll();
    }

    @Test
    public void testClickFavorite() {
        FavoriteView favoriteView = PowerMock.createMock(FavoriteView.class);

        favoriteView.setFavorite(true);

        PowerMock.replayAll();
        favoriteView.new OnFavoriteClick().onClick(null);
        PowerMock.verifyAll();
    }

    @Test
    public void testClickFavoriteUnset() {
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        suppress(constructor(ImageView.class, Context.class));
        suppress(method(ImageView.class, "setOnClickListener"));
        FavoriteView favoriteView = new FavoriteView(null);

        EasyMock.expect(dbFrontend.geocacheHasTag("GC123", Tags.FAVORITES))
                .andReturn(true);
        suppress(method(ImageView.class, "setImageResource"));

        dbFrontend.setGeocacheTag("GC123", Tags.FAVORITES, false);
        suppress(method(ImageView.class, "setImageResource"));

        PowerMock.replayAll();
        favoriteView.setGeocache(dbFrontend, "GC123");
        favoriteView.new OnFavoriteClick().onClick(null);
        PowerMock.verifyAll();
    }
}
