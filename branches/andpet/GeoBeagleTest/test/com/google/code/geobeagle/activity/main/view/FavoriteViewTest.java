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

import static org.junit.Assert.*;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.Tags;
import com.google.code.geobeagle.activity.main.view.FavoriteView.FavoriteState;
import com.google.code.geobeagle.activity.main.view.FavoriteView.FavoriteViewDelegate;

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
    public void testFavoriteStateIsFavorite() {
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(dbFrontend.geocacheHasTag("GC123", Tags.FAVORITES))
                .andReturn(false);

        PowerMock.replayAll();
        FavoriteState favoriteState = new FavoriteState(dbFrontend, "GC123");
        assertFalse(favoriteState.isFavorite());
        PowerMock.verifyAll();
    }

    @Test
    public void testFavoriteStateToggleFavorite() {
        DbFrontend dbFrontend = PowerMock.createMock(DbFrontend.class);

        EasyMock.expect(dbFrontend.geocacheHasTag("GC123", Tags.FAVORITES))
                .andReturn(true);
        dbFrontend.setGeocacheTag("GC123", Tags.FAVORITES, false);

        PowerMock.replayAll();
        FavoriteState favoriteState = new FavoriteState(dbFrontend, "GC123");
        favoriteState.toggleFavorite();
        assertFalse(favoriteState.isFavorite());
        PowerMock.verifyAll();
    }

    @Test
    public void FavoriteViewDelegateToggleFavorite() {
        FavoriteView favoriteView = PowerMock.createMock(FavoriteView.class);
        FavoriteState favoriteState = PowerMock.createMock(FavoriteState.class);

        favoriteState.toggleFavorite();
        EasyMock.expect(favoriteState.isFavorite()).andReturn(true);
        favoriteView.setImageResource(R.drawable.btn_rating_star_on_normal);

        PowerMock.replayAll();
        new FavoriteViewDelegate(favoriteView, favoriteState).toggleFavorite();
        PowerMock.verifyAll();
    }

    @Test
    public void FavoriteViewDelegateUpdateImage() {
        FavoriteView favoriteView = PowerMock.createMock(FavoriteView.class);
        FavoriteState favoriteState = PowerMock.createMock(FavoriteState.class);

        EasyMock.expect(favoriteState.isFavorite()).andReturn(false);
        favoriteView.setImageResource(R.drawable.btn_rating_star_off_normal);
        suppress(method(ImageView.class, "setImageResource"));

        PowerMock.replayAll();
        new FavoriteViewDelegate(favoriteView, favoriteState).updateImage();
        PowerMock.verifyAll();
    }

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
    public void testSetGeocache() {
        Context context = PowerMock.createMock(Context.class);
        FavoriteViewDelegate favoriteViewDelegate = PowerMock
                .createMock(FavoriteViewDelegate.class);

        suppress(method(ImageView.class, "setOnClickListener"));
        suppress(constructor(ImageView.class, Context.class));

        favoriteViewDelegate.updateImage();

        PowerMock.replayAll();
        new FavoriteView(context).setGeocache(favoriteViewDelegate);

        PowerMock.verifyAll();
    }

    @Test
    public void testToggleFavorite() {
        Context context = PowerMock.createMock(Context.class);
        FavoriteViewDelegate favoriteViewDelegate = PowerMock
                .createMock(FavoriteViewDelegate.class);

        suppress(constructor(ImageView.class, Context.class));
        suppress(method(ImageView.class, "setOnClickListener"));
        favoriteViewDelegate.updateImage();
        favoriteViewDelegate.toggleFavorite();

        PowerMock.replayAll();
        final FavoriteView favoriteView = new FavoriteView(context);
        favoriteView.setGeocache(favoriteViewDelegate);
        favoriteView.toggleFavorite();
        PowerMock.verifyAll();
    }

    @Test
    public void testClickFavorite() {
        FavoriteView favoriteView = PowerMock.createMock(FavoriteView.class);

        favoriteView.toggleFavorite();

        PowerMock.replayAll();
        new FavoriteView.OnFavoriteClick(favoriteView).onClick(null);
        PowerMock.verifyAll();
    }

}
