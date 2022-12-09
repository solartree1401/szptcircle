/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.zhang.szptcircle.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;
import java.util.List;

public class ImageTextAnalyseActivity {
    private static final String TAG = ImageTextAnalyseActivity.class.getSimpleName();

    /**
     * Text recognition on the device
     */
    public void localAnalyzer(int imageId, Resources resources) {
        // Create the text analyzer MLTextAnalyzer to recognize characters in images. You can set MLLocalTextSetting to
        // specify languages that can be recognized.
        // If you do not set the languages, only Romance languages can be recognized by default.
        // Use default parameter settings to configure the on-device text analyzer. Only Romance languages can be
        // recognized.
        // analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer();
        // Use the customized parameter MLLocalTextSetting to configure the text analyzer on the device.
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage("en")
                .create();
        MLTextAnalyzer analyzer = MLAnalyzerFactory.getInstance()
                .getLocalTextAnalyzer(setting);
        // Create an MLFrame by using android.graphics.Bitmap.
        Bitmap bitmap = BitmapFactory.decodeResource(resources, imageId);
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
            @Override
            public void onSuccess(MLText text) {
                // Recognition success.
                String result = ImageTextAnalyseActivity.this.displaySuccess(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Recognition failure.
                Log.e(ImageTextAnalyseActivity.TAG, "failed: " + e.getMessage());
            }
        });
    }


    private String displaySuccess(MLText mlText) {
        String result = "";
        List<MLText.Block> blocks = mlText.getBlocks();
        for (MLText.Block block : blocks) {
            for (MLText.TextLine line : block.getContents()) {
                result += line.getStringValue() + "\n";
            }
        }
        return result;
    }

    protected void stop(MLTextAnalyzer analyzer) {
        if (analyzer == null) {
            return;
        }
        try {
            analyzer.stop();
        } catch (IOException e) {
            Log.e(ImageTextAnalyseActivity.TAG, "Stop failed: " + e.getMessage());
        }
    }
}
