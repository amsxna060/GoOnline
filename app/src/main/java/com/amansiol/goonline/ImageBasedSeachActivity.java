package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import static android.Manifest.permission.CAMERA;

public class ImageBasedSeachActivity extends AppCompatActivity {
/*
    first thing
    i have to train clothes images which are my training images
    approxx 100 images for perticular label
    for that i have used Google Teachable machine in
    which i uplaod my all training images and provide the
    labels and it will train my images and
    provide me my trained model and after that i just donwload
    the floating point tflite model from there.
    tflite is basically tensorflow lite models which
    we used in mobile application to recognize places, voices , objects, products...
    after downloading the model i copy the model in my assets directory of my android projects file..
    here you can see this assets directory in this navigation drawer..
    then after i wrote the coding part by taking the help of firebase model interpreter to interpret my model.
    these are the firebase model interpreter class
    by these objects i can open my model in the application and
    after then i will pass  the image as a input in the function or
    also i have to change that image into floating array of 4 dimensional array of floating point values
    and after that i can give this image in the form of 4 dimensional floating point array of which
    i have created by the input image which is selected by user to search
    then after model will run and recognize the image and find the similar floating point array in the model also that would be created
    when i trained my model with my training images..
    after that model will return a 2 dimensional floating point array in which i have the accuracy and label
    and from this whole array i just take out the label which have maximum accuracy.
    show that label to the user and search the images in my app regarding that label..
 */


    FirebaseCustomLocalModel localModel;
    FirebaseModelInterpreter interpreter;
    FirebaseModelInterpreterOptions options;
    FirebaseModelInputOutputOptions inputOutputOptions;
    FirebaseModelInputs inputs;

    private final int requestPermissionCode=778;
    Button takepicture;
    ImageView previewImage;
    HashMap<String, Float> labelMap;
    TextView labelText;
    ImageButton searchNow;
    ImageView retry;
    RelativeLayout wholeResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorBlack));
        setContentView(R.layout.activity_image_based_seach);
        FirebaseApp.initializeApp(this);
        takepicture=findViewById(R.id.takeimage);
        previewImage=findViewById(R.id.preview);
        labelText=findViewById(R.id.label);
        searchNow=findViewById(R.id.searchnow);
        retry=findViewById(R.id.retry);
        wholeResult=findViewById(R.id.wholeview);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wholeResult.setVisibility(View.GONE);
                takepicture.setVisibility(View.VISIBLE);
                previewImage.setVisibility(View.VISIBLE);
                previewImage.setImageResource(R.drawable.lens);
            }
        });

        labelMap=new HashMap<>();
        takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 If All permission is enabled successfully then this block will execute.
                if(CheckingPermissionIsEnabledOrNot())
                {
                    showImagePickerOptions();

                }
                // If, If permission is not enabled then else condition will execute.
                else {
                    //Calling method to enable permission.
                    RequestMultiplePermission();
                }
            }
        });

        localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("model_unquant.tflite")
                .build();

        try {

            options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (FirebaseMLException e) {
            Log.d("TAG",""+e.getMessage());
        }

        searchNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageBasedSeachActivity.this,SearchActivity.class);
                intent.putExtra("searchlabel",labelText.getText().toString());
                startActivity(intent);
            }
        });
//      AnalysePic(BitmapFactory.decodeResource(getResources(), R.drawable.testpic));

    }

    private void AnalysePic(Bitmap bitmap) {
        if(bitmap!=null)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            int batchNum = 0;
            float[][][][] input = new float[1][224][224][3];
            for (int x = 0; x < 224; x++) {
                for (int y = 0; y < 224; y++) {
                    int pixel = bitmap.getPixel(x, y);
                    // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                    // model. For example, some models might require values to be normalized
                    // to the range [0.0, 1.0] instead.
                    input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                    input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                    input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
                }
            }
            try {
                inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 224, 224, 3})
                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 23})
                        .build();
            } catch (FirebaseMLException e) {
                e.printStackTrace();
                Log.i("MLKit", "1"+e.getMessage());
            }

            try {
                inputs = new FirebaseModelInputs.Builder()
                        .add(input)  // add() as many input arrays as your model requires
                        .build();
            } catch (FirebaseMLException e) {
                e.printStackTrace();
                Log.i("MLKit", "2"+e.getMessage());
            }

            interpreter.run(inputs, inputOutputOptions)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseModelOutputs>() {
                                @Override
                                public void onSuccess(FirebaseModelOutputs result) {
                                    // ...
                                    float[][] output = result.getOutput(0);
                                    float[] probabilities = output[0];
                                    BufferedReader reader = null;
                                    try {
                                        reader = new BufferedReader(
                                                new InputStreamReader(getAssets().open("labels.txt")));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.i("MLKit", "3"+e.getMessage());
                                    }
                                    for (int i = 0; i < probabilities.length; i++) {
                                        String label = null;
                                        try {
                                            label = reader.readLine();
                                            String[]  labels=label.split(" ",2);
                                            label=labels[1].trim();
                                            labelMap.put(label,probabilities[i]*100);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.i("MLKit", "4"+e.getMessage());
                                        }
                                        Log.i("MLKit", String.format("%s: %1.4f", label, probabilities[i]*100));
                                    }
                                    if(labelMap.size()>0)
                                    {
                                        labelMap=sortHashMapByValues(labelMap);
                                    }
                                    Entry<String, Float>[] temp = new Entry[labelMap.size()];
                                    labelMap.entrySet().toArray(temp);
                                    Log.d("Map",temp[labelMap.size()-1].getKey()+" "+temp[labelMap.size()-1].getValue());
                                    Log.d("Map",temp[labelMap.size()-2].getKey()+" "+temp[labelMap.size()-2].getValue());
                                    labelText.setText(temp[labelMap.size()-1].getKey());
                                    wholeResult.setVisibility(View.VISIBLE);
                                    takepicture.setVisibility(View.GONE);
                                }

                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    Log.i("MLKit", "5"+e.getMessage());
                                }
                            });
        }
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(ImageBasedSeachActivity.this, new String[]
                {
                        CAMERA
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE

                }, requestPermissionCode);

    }
    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionCode) {
            if (grantResults.length > 0) {

                boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean ReadStoragePermisssion = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean WriteStoragePermisssion = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (CameraPermission && ReadStoragePermisssion || WriteStoragePermisssion) {
                    showImagePickerOptions();
                } else {
                    showSettingsDialog();

                }
            }
        }
    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return  FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED ;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageBasedSeachActivity.this);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings. ");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ImageBasedSeachActivity.this.openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri previewimageUri;
                previewimageUri= result.getUri();
                previewImage.setImageURI(previewimageUri);
                Bitmap bmap = null;
                try {
                    bmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), previewimageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AnalysePic(bmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    private void showImagePickerOptions() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAutoZoomEnabled(true)
                .setFixAspectRatio(false)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(40)
                .start(ImageBasedSeachActivity.this);
    }

    public LinkedHashMap<String, Float> sortHashMapByValues(
            HashMap<String, Float> passedMap) {
        List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
        List<Float> mapValues = new ArrayList<Float>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Float> sortedMap =
                new LinkedHashMap<>();

        Iterator<Float> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Float val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Float comp1 = passedMap.get(key);
                Float comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
