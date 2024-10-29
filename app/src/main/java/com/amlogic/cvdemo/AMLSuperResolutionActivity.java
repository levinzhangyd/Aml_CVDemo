package com.amlogic.cvdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.amlogic.cvdemo.data.ModelData;
import com.amlogic.cvdemo.data.ModelKpiTime;
import com.amlogic.cvdemo.data.ModelParams;
import com.amlogic.cvdemo.dump.AMLDump;
import com.amlogic.cvdemo.interpreter.CVDetectListener;
import com.amlogic.cvdemo.interpreter.SuperResolutionHelper;
import com.amlogic.cvdemo.model.ModelUtils;
import com.amlogic.cvdemo.utils.BitmapUtils;
import com.amlogic.cvdemo.utils.FileUtils;
import com.amlogic.cvdemo.utils.StringUtils;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;

import com.amlogic.cvdemo.databinding.ActivityAmlSuperResolutionBinding;

import java.util.List;

public class AMLSuperResolutionActivity extends AppCompatActivity {
    private final String TAG = "AMLSuperResolutionActivity";
    private ActivityAmlSuperResolutionBinding binding;
    SuperResolutionHelper superResolutionHelper;
    String[] delegatePlts = {"CPU", "GPU", "NNAPI"};
    ModelParams workingModel;
    private ImageView originImg;
    private ImageView predictImg;
    private Button loadButton;
    private Button inferenceButton;
    private TextView kpiTimeTV;
    private TextView modelMsgTv;
    private ModelData inData;
    private ModelData outData;
    private String selectedImagePath = null;

    CVDetectListener listener = new CVDetectListener() {
        @Override
        public void onResult(int model_type, Bitmap retBitmap, ModelKpiTime kpiTime) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "ret = null" + (retBitmap == null) + retBitmap);
                    if (null != predictImg) {
                        Log.d(TAG, "ret = width =" + retBitmap.getWidth());

//                        predictImg.setImageBitmap(retBitmap);
                        predictImg.setImageBitmap(BitmapUtils.adjustBitmapSize(retBitmap));
                    } else {
                        Log.e(TAG, "preview widget is null");
                    }

                    if (originImg != null) {
                        int[] inputSize = inData.getShape();
                        int[] outSize = inData.getShape();
                        Log.d(TAG, "refactor  ori bitmap");
                        originImg.setImageBitmap(
                                BitmapUtils.adjustBitmapSize(
                                    BitmapUtils.generateBitmapWithSize(
                                        BitmapUtils.generateBitmapWithSize(
                                                BitmapFactory.decodeFile(selectedImagePath),
                                        inputSize[1],  inputSize[0]),
                                    outSize[1], outSize[0])
                                ));
                    }
                    if (null != kpiTimeTV) {
                        kpiTimeTV.setText(StringUtils.convertKpiData2String(kpiTime));
                    }
                    inferenceButton.setEnabled(true);
                }
            });
        }

        @Override
        public void onError(int model_type, int errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadButton.setEnabled(true);
                    modelMsgTv.setText(null);
                    Toast.makeText(getBaseContext(), "Load Model fail,Pls select other delegate platform", Toast.LENGTH_LONG).show();
                }
            });

        }

        @Override
        public void onLoadSuccess(ModelData in, ModelData out) {
            inData = in;
            outData = out;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inferenceButton.setEnabled(true);
                    if (null != modelMsgTv) {
                        modelMsgTv.setText(String.format("%s       %s",
                                StringUtils.convertModelData2String(StringUtils.MODEL_TYPE_IN, inData),
                                StringUtils.convertModelData2String(StringUtils.MODEL_TYPE_OUT, outData)));
                    }
                    Log.d(TAG,"finish dispaly model msg");
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAmlSuperResolutionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置Toolbar的标题
        toolbar.setTitle(getString(R.string.super_resolution));

        workingModel = new ModelParams();
        AMLDump.getInstance().init(getBaseContext(), ModelUtils.MODEL_TYPE_SUPER_RESOLUTION, false);
        initView();
    }

    private void initView() {
        superResolutionHelper = new SuperResolutionHelper(getBaseContext(), listener);
        Spinner spinner = findViewById(R.id.sr_model_spinner);
        loadButton = findViewById(R.id.sr_load_model_button);
//        List<String> modelList  = semanticSegmentationHelper.getModelList(0);
        List<String> modelList = FileUtils.getModelList(getBaseContext(), ModelUtils.MODEL_TYPE_SUPER_RESOLUTION);
        List<String>  modelNameList = StringUtils.getNameFromFullPath(modelList);
        ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, modelNameList);
        spinner.setAdapter(modelAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedModelName = modelList.get(position);
                Log.i(TAG, "model spinner onItemSelected" + "position" + "name =" + selectedModelName);
                workingModel.setModelName(modelNameList.get(position));
                workingModel.setModelFilePath(selectedModelName);
                loadButton.setEnabled(true);
                clearPreview();
                if (modelMsgTv != null) {
                    modelMsgTv.setText(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected");
            }
        });
        spinner.setSelection(0);

        Spinner delegatePltspinner = findViewById(R.id.sr_delegate_platform_spinner);
        ArrayAdapter<String> delegateAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, delegatePlts);
        delegatePltspinner.setAdapter(delegateAdapter);
        delegatePltspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String delegate = delegatePlts[position];
                Log.i(TAG, "delegate spinner onItemSelected" + "position" + "name =" + delegate);
                loadButton.setEnabled(true);
                clearPreview();
                workingModel.setDelegatePlatform(position);
                //todo: when load model, detail it's input&outout
                if (null != superResolutionHelper) {
//                    semanticSegmentationHelper.getModelList()
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected");
            }
        });
        delegatePltspinner.setSelection(delegatePlts.length - 1);


        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LOAD MODEL = " + workingModel);
                loadButton.setEnabled(false);
                inferenceButton.setEnabled(false);
                clearPreview();
                if (null != superResolutionHelper) {
                    superResolutionHelper.initInterpreter(workingModel);
                }
            }
        });
        modelMsgTv = findViewById(R.id.sr_model_msg);
        originImg = findViewById(R.id.sr_origin_bmp);
        predictImg = findViewById(R.id.sr_predict_bmp);
        inferenceButton = findViewById(R.id.sr_start_inference);
        inferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadButton.isEnabled()) {
                    Toast.makeText(getBaseContext(), "Pls load model firstly", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "start predict = ");
                if (null != superResolutionHelper) {
                    superResolutionHelper.inference(selectedImagePath);
                    inferenceButton.setEnabled(false);
                    clearPreview();
                }
            }
        });

        kpiTimeTV = findViewById(R.id.sr_inference_result);
        Spinner imageSpinner = findViewById(R.id.sr_src_spinner);
        List<String> nameList = FileUtils.getImageSrcList(getBaseContext(), ModelUtils.MODEL_TYPE_SUPER_RESOLUTION);
        List<String>  sampleNameList = StringUtils.getNameFromFullPath(nameList);
        ArrayAdapter<String> srcAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, sampleNameList);
        imageSpinner.setAdapter(srcAdapter);
        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (originImg != null) {
                    selectedImagePath = nameList.get(position);
                    originImg.setImageBitmap(BitmapUtils.adjustBitmapSize(
                            BitmapFactory.decodeFile(selectedImagePath)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "imageSpinner onNothingSelected");
            }
        });
        imageSpinner.setSelection(0);
        if (!nameList.isEmpty()) {
            imageSpinner.setSelection(0);
        }
    }

    private void clearPreview() {
        if (kpiTimeTV != null) {
            kpiTimeTV.setText(null);
        }
        if (predictImg != null) {
            predictImg.setImageBitmap(null);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (superResolutionHelper != null) {
            superResolutionHelper.clearInterpreter();
        }
    }
}