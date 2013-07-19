package com.kedzie.vbox.test.soap;

import java.util.List;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.jaxb.IMediumAttachment;
import com.kedzie.vbox.test.VBoxTestCase;

public class MediumTest extends VBoxTestCase {
	private static final String TAG = "MediumTest";
	
	
	@SmallTest
	public void testCompact() throws Exception {
		IMachine test = getVBox().findMachine("TEST");
		List<IMediumAttachment> attachments = test.getMediumAttachments();
		IMedium testDisk = null;
		for(IMediumAttachment attach : attachments) {
			if(attach.getMedium().getName().toLowerCase().equals("testdisk.vdi")) {
				testDisk = attach.getMedium();
				break;
			}
		}
		assertNotNull("Test Disk", testDisk);
		Log.i(TAG, "Found test disk: " + testDisk);
		
		handleProgress(TAG, testDisk.compact());
	}
}
