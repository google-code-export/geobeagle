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

package com.google.code.geobeagle.activity.cachelist.actions.context;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import com.google.code.geobeagle.OnClickCancelListener;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.cachelist.actions.context.delete.ContextActionDeleteDialogHelper;
import com.google.code.geobeagle.activity.cachelist.actions.context.delete.ContextActionDeleteStore;
import com.google.code.geobeagle.activity.cachelist.actions.context.delete.OnClickOk;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVector;
import com.google.code.geobeagle.activity.cachelist.model.GeocacheVectors;
import com.google.code.geobeagle.activity.cachelist.presenter.CacheListRefresh;
import com.google.code.geobeagle.database.CacheSqlWriter;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Activity.class, TextView.class
})
public class ContextActionDeleteTest {

    private Activity activity;
    private AlertDialog dialog;
    private GeocacheVectors geocacheVectors;
    private OnClickOk onClickOk;
    private ContextActionDelete contextActionDelete;
    private Provider<CacheSqlWriter> cacheWriterProvider;
    private CacheListRefresh cacheListRefresh;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        geocacheVectors = createMock(GeocacheVectors.class);
        activity = createMock(Activity.class);
        onClickOk = createMock(OnClickOk.class);
        dialog = createMock(AlertDialog.class);
        contextActionDelete = createMock(ContextActionDelete.class);
        cacheWriterProvider = createMock(Provider.class);
        cacheListRefresh = createMock(CacheListRefresh.class);
    }

    @Test
    public void testAct() {
        GeocacheVector geocacheVector = createMock(GeocacheVector.class);
        ContextActionDeleteStore contextActionDeleteStore = createMock(ContextActionDeleteStore.class);

        contextActionDeleteStore.saveCacheToDelete("GC123", "My cache");
        activity.showDialog(R.id.delete_cache);
        expect(geocacheVectors.get(17)).andReturn(geocacheVector);
        expect(geocacheVector.getId()).andReturn("GC123");
        expect(geocacheVector.getName()).andReturn("My cache");

        replayAll();
        new ContextActionDelete(geocacheVectors, cacheWriterProvider, activity,
                contextActionDeleteStore, cacheListRefresh)
                .act(17);
        verifyAll();
    }

    @Test
    public void testDelete() {
        CacheSqlWriter cacheSqlWriter = createMock(CacheSqlWriter.class);
        CacheListRefresh cacheListRefresh = createMock(CacheListRefresh.class);
        ContextActionDeleteStore contextActionDeleteStore = createMock(ContextActionDeleteStore.class);

        expect(cacheWriterProvider.get()).andReturn(cacheSqlWriter);
        expect(contextActionDeleteStore.getCacheId()).andReturn("GC123");
        cacheSqlWriter.deleteCache("GC123");
        cacheListRefresh.forceRefresh();

        replayAll();
        ContextActionDelete contextActionDelete = new ContextActionDelete(geocacheVectors,
                cacheWriterProvider, activity, contextActionDeleteStore, cacheListRefresh);
        contextActionDelete.delete();
        verifyAll();
    }

    @Test
    public void testOnClickOk() {
        DialogInterface dialog = createMock(DialogInterface.class);

        contextActionDelete.delete();
        dialog.dismiss();

        replayAll();
        new OnClickOk(contextActionDelete).onClick(dialog, 0);
        verifyAll();
    }

    @Test
    public void testGetConfirmBodyText() {
        ContextActionDeleteStore contextActionDeleteStore = createMock(ContextActionDeleteStore.class);

        expect(contextActionDeleteStore.getCacheId()).andReturn("GC123");
        expect(contextActionDeleteStore.getCacheName()).andReturn("my cache");
        expect(activity.getString(R.string.confirm_delete_body_text)).andReturn(
                "Delete %1$s: \"%2$s\"?");

        replayAll();
        ContextActionDelete contextActionDelete = new ContextActionDelete(geocacheVectors, null,
                activity, contextActionDeleteStore, cacheListRefresh);
        assertEquals("Delete GC123: \"my cache\"?", contextActionDelete.getConfirmDeleteBodyText());
        verifyAll();
    }

    @Test
    public void testGetConfirmDeleteTitle() {
        ContextActionDeleteStore contextActionDeleteStore = createMock(ContextActionDeleteStore.class);

        expect(contextActionDeleteStore.getCacheId()).andReturn("GC123");
        expect(activity.getString(R.string.confirm_delete_title)).andReturn("Confirm delete %1$s");

        replayAll();
        ContextActionDelete contextActionDelete = new ContextActionDelete(geocacheVectors, null,
                activity, contextActionDeleteStore, cacheListRefresh);
        assertEquals("Confirm delete GC123", contextActionDelete.getConfirmDeleteTitle());
        verifyAll();
    }

    @Test
    public void testOnCreateDialog() {
        Builder builder = createMock(Builder.class);
        AlertDialog dialog = createMock(AlertDialog.class);
        LayoutInflater layoutInflater = createMock(LayoutInflater.class);
        View confirmDeleteCacheView = createMock(View.class);
        OnClickCancelListener onClickCancelListener = createMock(OnClickCancelListener.class);

        expect(layoutInflater.inflate(R.layout.confirm_delete_cache, null)).andReturn(
                confirmDeleteCacheView);
        expect(builder.setNegativeButton(R.string.confirm_delete_negative, onClickCancelListener))
                .andReturn(builder);
        expect(builder.setPositiveButton(R.string.delete_cache, onClickOk)).andReturn(builder);
        expect(builder.setView(confirmDeleteCacheView)).andReturn(builder);
        expect(builder.create()).andReturn(dialog);

        replayAll();
        new ContextActionDeleteDialogHelper(null, onClickOk, builder, layoutInflater,
                onClickCancelListener).onCreateDialog();
        verifyAll();
    }

    @Test
    public void testOnPrepareDialog() {
        ContextActionDelete contextActionDelete = createMock(ContextActionDelete.class);
        TextView textView = createMock(TextView.class);
        LayoutInflater layoutInflater = createMock(LayoutInflater.class);

        expect(contextActionDelete.getConfirmDeleteTitle()).andReturn(
                "Confirm delete GC123");
        dialog.setTitle("Confirm delete GC123");

        expect(dialog.findViewById(R.id.delete_cache)).andReturn(textView);
        expect(contextActionDelete.getConfirmDeleteBodyText()).andReturn(
                "Delete GC123: \"my cache\"?");
        textView.setText("Delete GC123: \"my cache\"?");

        replayAll();
        ContextActionDeleteDialogHelper contextActionDeleteDialogHelper = new ContextActionDeleteDialogHelper(
                contextActionDelete, onClickOk, null, layoutInflater, null);
        contextActionDeleteDialogHelper.onPrepareDialog(dialog);
        verifyAll();
    }
}
