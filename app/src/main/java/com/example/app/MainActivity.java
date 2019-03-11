/*
 * Copyright (C) 2019, Pavel Dubrova <pashadubrova@gmail.com>
 * Copyright (C) 2019, Vladislav Sokurenko <whatewer7@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button okButton;
    protected final String SYS_FS_MODEGET = "/sdcard/Download/a";
    protected final String SYS_FS_MODESET = "/sdcard/Download/b";
    List<String> lines = new ArrayList<String>();
    protected final String LOG_TAG = "--------------LOG------------";
    private RadioGroup radioGroup;
    private String currentState = "";
    private TextView responseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readFromFile();
        createRadioButtons();
        responseView = findViewById(R.id.responseView);
        responseView.setText("Current resolution: "+currentState);
        okButton = findViewById(R.id.button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton r = findViewById(radioGroup.getCheckedRadioButtonId());
                for (int i = 0; i < lines.size(); i++) {
                    if (r.getText().equals(lines.get(i))) {
                        writeToFile(i);
                        responseView.setText("Resolution was changed to: "+lines.get(i));
                    }
                }
            }
        });
    }

    public void readFromFile() {
        try (FileReader sysfsFile_modget = new FileReader(SYS_FS_MODEGET);
             BufferedReader fileReader_modget = new BufferedReader(sysfsFile_modget);
             FileReader sysfsFile_modset = new FileReader(SYS_FS_MODESET);
             BufferedReader fileReader_modset = new BufferedReader(sysfsFile_modset)) {

            String line = null;
            while ((line = fileReader_modget.readLine()) != null) {
                lines.add(line);
            }
            currentState = fileReader_modset.readLine();
            Log.e(LOG_TAG, "var" + currentState);
            if (currentState == null)
                Log.e(LOG_TAG, "error reading modset");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRadioButtons() {
        radioGroup = findViewById(R.id.radioGroup);
        for (int i = 0; i < lines.size(); i++) {
            RadioButton r = new RadioButton(this);
            r.setText(lines.get(i));
            radioGroup.addView(r);
            if (currentModeCheck(i))
                radioGroup.check(r.getId());
        }
    }

    public boolean currentModeCheck(int i) {
        if (currentState.equals(lines.get(i))) {
            Log.e(LOG_TAG, "match found " + lines.get(i));
            return true;
        } else {
            Log.e(LOG_TAG, "match not found");
            return false;
        }
    }

    public void writeToFile(int index) {
        try (FileWriter sysfsFile = new FileWriter(SYS_FS_MODESET);
             BufferedWriter fileWriter = new BufferedWriter(sysfsFile)) {

            fileWriter.write(lines.get(index) + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
