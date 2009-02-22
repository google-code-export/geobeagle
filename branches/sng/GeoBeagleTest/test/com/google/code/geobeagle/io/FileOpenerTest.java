package com.google.code.geobeagle.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.code.geobeagle.io.FileOpener.FileReaderFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;

import junit.framework.TestCase;

public class FileOpenerTest extends TestCase {

    public void testOpen() throws FileNotFoundException {
        FileReaderFactory fileReaderFactory = createMock(FileReaderFactory.class);
        FileReader fileReader = createMock(FileReader.class);
        expect(fileReaderFactory.create()).andReturn(fileReader);
        
        replay(fileReaderFactory);
        FileOpener fileOpener = new FileOpener();
        assertEquals(fileReader, fileOpener.open(fileReaderFactory));
        verify(fileReaderFactory);
    }
    
    public void testOpenFileNotFound() throws FileNotFoundException  {
        FileReaderFactory fileReaderFactory = createMock(FileReaderFactory.class);
        expect(fileReaderFactory.create()).andThrow(new FileNotFoundException());
                
        replay(fileReaderFactory);
        FileOpener fileOpener = new FileOpener();
        assertEquals(null, fileOpener.open(fileReaderFactory));
        verify(fileReaderFactory);
    }
}
