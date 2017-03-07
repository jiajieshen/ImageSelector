package com.fubaisum.imageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fubaisum.imageselector.lib.ImageSelector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_SELECTOR = 0x100;

    private RadioGroup mChoiceMode, mShowCamera, mShowPreview, mShowGif, mOnlyShowGif;
    private EditText mRequestNum;
    private TextView mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChoiceMode = (RadioGroup) findViewById(R.id.choice_mode);
        mShowCamera = (RadioGroup) findViewById(R.id.show_camera);
        mShowPreview = (RadioGroup) findViewById(R.id.show_preview);
        mShowGif = (RadioGroup) findViewById(R.id.show_gif);
        mOnlyShowGif = (RadioGroup) findViewById(R.id.only_show_gif);
        mRequestNum = (EditText) findViewById(R.id.request_num);
        mResultText = (TextView) findViewById(R.id.result);

        mChoiceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.multi) {
                    mRequestNum.setEnabled(true);
                } else {
                    mRequestNum.setEnabled(false);
                    mRequestNum.setText("");
                }
            }
        });

        Button btnLaunch = (Button) findViewById(R.id.btn_main_launch);
        assert btnLaunch != null;
        btnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImageSelector();
            }
        });
    }

    private void launchImageSelector() {

        boolean isMultipleChoice = mChoiceMode.getCheckedRadioButtonId() != R.id.single;
        boolean showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;
        boolean showPreview = mShowPreview.getCheckedRadioButtonId() == R.id.preview;
        boolean showGif = mShowGif.getCheckedRadioButtonId() == R.id.yes_show_gif;
        boolean onlyShowGif = mOnlyShowGif.getCheckedRadioButtonId() == R.id.yes_only_show_gif;
        int maxNum = 9;
        if (!TextUtils.isEmpty(mRequestNum.getText())) {
            try {
                maxNum = Integer.valueOf(mRequestNum.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        new ImageSelector.Builder()
                .setGalleryTitle(getString(R.string.app_name))
                .setMultipleChoice(isMultipleChoice)
                .setMaxSelectedSize(maxNum)
                .setCameraEnable(showCamera)
                .setPreviewEnable(showPreview)
//                .setHook(new ImageSelectorHook() {
//                    @Override
//                    public void onImageThumbnailClick(Activity activity, String imagePath) {
//                        List<String> imageList = new ArrayList<>();
//                        imageList.add(imagePath);
//                        printResult(imageList);
//                    }
//                })
                .build()
                .launch(MainActivity.this, REQUEST_IMAGE_SELECTOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_SELECTOR) {
            ArrayList<String> pathList = data.getStringArrayListExtra(ImageSelector.EXTRA_IMAGE_PATH_LIST);
            printResult(pathList);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void printResult(List<String> pathList) {
        if (pathList == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String p : pathList) {
            sb.append(p);
            sb.append("\n");
        }
        mResultText.setText(sb.toString());
    }
}
