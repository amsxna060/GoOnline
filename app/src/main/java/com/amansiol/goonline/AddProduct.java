package com.amansiol.goonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amansiol.goonline.models.Product;
import com.amansiol.goonline.models.Shops;
import com.amansiol.goonline.tokenizer.SpaceTokenizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.Manifest.permission.CAMERA;

public class AddProduct extends AppCompatActivity {

    TextInputLayout productName, productPrice, productLongTitle;
    TextInputLayout productColor, productKeywords, productDiscount;
    TextInputLayout productType, productGenderType, productDescription;
    CircularImageView image1, image2, image3, image4;
    Button uploadProduct;
    Product product;
    FirebaseFirestore db;
    EditText eproduct_name, eproduct_price, edproduct_long_title, eproductdiscount, eproduct_description;
    AppCompatMultiAutoCompleteTextView mproductKeyword, mProductColor, mProductType, mProductGenderType;
    String result;
    String short_title_str;
    String long_title_str;
    String color_str;
    String price_str;
    String description_str;
    String discount_str;
    String product_type_str;
    String[] keywords;
    String product_gender_type_str;
    private final int requestPermissionCode = 778;
    ProgressDialog pd;
    private Uri image1Uri;
    private Uri image2Uri;
    private Uri image3Uri;
    private Uri image4Uri;
    private StorageReference mStorageRef;
    public boolean currentImg1 = false;
    public boolean currentImg2 = false;
    public boolean currentImg3 = false;
    public boolean currentImg4 = false;
    ArrayList<String> imageUrlList = new ArrayList<String>();
    DocumentReference addproductRef;
    String[] product_types;
    String[] product_gender_types;
    String productId = null;
    String edit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_add_product);
        pd = new ProgressDialog(this);
        init();
        edit = getIntent().getStringExtra("edit");
        productId = getIntent().getStringExtra("id");
        uploadProduct.setOnClickListener(v12 -> {
            AtomicBoolean isUploaded = uploadDataToFireStore();
            if (isUploaded.get()) {
                Toast.makeText(getApplicationContext(), "Uploaded Successfully...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Try Again..", Toast.LENGTH_LONG).show();
            }
        });

        if (edit != null) {
            FirebaseFirestore
                    .getInstance()
                    .collection("Shops")
                    .document(MainActivity.usernameUid)
                    .collection("Products")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Product product = documentSnapshot.toObject(Product.class);
                            eproduct_name.setText(product.getProduct_short_name());
                            edproduct_long_title.setText(product.getProduct_long_title());
                            eproduct_price.setText(product.getProduct_price());
                            eproductdiscount.setText(product.getDiscount());
                            mProductColor.setText(product.getProduct_color());
                            mProductGenderType.setText(product.getGender());
                            mProductType.setText(product.getProduct_type());
                            mproductKeyword.setText(product.getFullkeyword());
                            eproduct_description.setText(product.getProduct_desc());

                            if (product.getImage1() != null) {
                                Picasso.get().load(product.getImage1()).into(image1);
                            }
                            if (product.getImage2() != null) {
                                Picasso.get().load(product.getImage2()).into(image2);
                            }
                            if (product.getImage3() != null) {
                                Picasso.get().load(product.getImage3()).into(image3);
                            }
                            if (product.getImage4() != null) {
                                Picasso.get().load(product.getImage4()).into(image4);
                            }
                        }
                    });
        }


        image1.setOnClickListener(v13 -> {

            if (CheckingPermissionIsEnabledOrNot()) {
                if (edproduct_long_title.getText().toString().trim().isEmpty()) {
                    edproduct_long_title.setError("!Fill It First");
                    edproduct_long_title.requestFocus();
                    return;
                } else if (eproduct_name.getText().toString().trim().isEmpty()) {
                    eproduct_name.setError("Fill It First");
                    eproduct_name.requestFocus();
                    return;
                } else {
                    currentImg1 = true;
                    currentImg2 = false;
                    currentImg3 = false;
                    currentImg4 = false;
                    showImagePickerOptions();
                }
            }
            // If, If permission is not enabled then else condition will execute.
            else {
                //Calling method to enable permission.
                RequestMultiplePermission();
            }
        });

        image2.setOnClickListener(v14 -> {
            if (CheckingPermissionIsEnabledOrNot()) {
                if (edproduct_long_title.getText().toString().trim().isEmpty()) {
                    edproduct_long_title.setError("!Fill It First");
                    edproduct_long_title.requestFocus();
                    return;
                } else if (eproduct_name.getText().toString().trim().isEmpty()) {
                    eproduct_name.setError("Fill It First");
                    eproduct_name.requestFocus();
                    return;
                } else {
                    currentImg1 = false;
                    currentImg2 = true;
                    currentImg3 = false;
                    currentImg4 = false;
                    showImagePickerOptions();
                }
            }
            // If, If permission is not enabled then else condition will execute.
            else {
                //Calling method to enable permission.
                RequestMultiplePermission();
            }
        });

        image3.setOnClickListener(v15 -> {
            if (CheckingPermissionIsEnabledOrNot()) {
                if (edproduct_long_title.getText().toString().trim().isEmpty()) {
                    edproduct_long_title.setError("!Fill It First");
                    edproduct_long_title.requestFocus();
                    return;
                } else if (eproduct_name.getText().toString().trim().isEmpty()) {
                    eproduct_name.setError("Fill It First");
                    eproduct_name.requestFocus();
                    return;
                } else {
                    currentImg1 = false;
                    currentImg2 = false;
                    currentImg3 = true;
                    currentImg4 = false;
                    showImagePickerOptions();
                }
            }
            // If, If permission is not enabled then else condition will execute.
            else {
                //Calling method to enable permission.
                RequestMultiplePermission();
            }
        });

        image4.setOnClickListener(v16 -> {
            if (CheckingPermissionIsEnabledOrNot()) {
                if (edproduct_long_title.getText().toString().trim().isEmpty()) {
                    edproduct_long_title.setError("!Fill It First");
                    edproduct_long_title.requestFocus();
                    return;
                } else if (eproduct_name.getText().toString().trim().isEmpty()) {
                    eproduct_name.setError("Fill It First");
                    eproduct_name.requestFocus();
                    return;
                } else {
                    currentImg1 = false;
                    currentImg2 = false;
                    currentImg3 = false;
                    currentImg4 = true;
                    showImagePickerOptions();
                }
            }
            // If, If permission is not enabled then else condition will execute.
            else {
                //Calling method to enable permission.
                RequestMultiplePermission();
            }
        });

        String[] colors = {
                "Red", "Yellow", "blue", "Baby", "Aquamarine", "Brick", "red", "Black", "Blue", "Blue-green", "Blue-violet", "Blush", "Bronze"
                , "Brown", "Burgundy", "green", "Chocolate", "Coffee", "Copper", "Cyan", "Desert", "Sand", "Emerald", "Gold", "Gray", "Green", "Lemon", "Magenta", "Rose", "Maroon", "Navy blue", "Orange", "Pink", "Purple", "Red", "MultiColor"
                , "Violet", "White", "Yellow"
        };
        ArrayAdapter<String> colorArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, colors);
        mProductColor.setAdapter(colorArrayAdapter);
        mProductColor.setThreshold(1);
        mProductColor.setTokenizer(new SpaceTokenizer());

        product_types = new String[]{"Shirt", "Tee-Shirt", "Jeans", "Hoddies", "Blazers", "Lehenga", "Tops", "Sports", "Saree", "Sweaters", "Casual", "Party Wear", "Tee-Shirts", "Shirts"};
        ArrayAdapter<String> product_typesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, product_types);
        mProductType.setAdapter(product_typesArrayAdapter);
        mProductType.setThreshold(1);
        mProductType.setTokenizer(new SpaceTokenizer());

        product_gender_types = new String[]{"Womens", "Mens", "Kids Wears", "Unisex"};
        ArrayAdapter<String> product_gender_typesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, product_gender_types);
        mProductGenderType.setAdapter(product_gender_typesArrayAdapter);
        mProductGenderType.setThreshold(1);
        mProductGenderType.setTokenizer(new SpaceTokenizer());

        ArrayList<String> product_keywords = new ArrayList<>();
        FirebaseFirestore
                .getInstance()
                .collection("Keyword")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot x : queryDocumentSnapshots) {
                            String keyword = x.getData().get("word").toString();
                            product_keywords.add(keyword);
                        }
                        ArrayAdapter<String> product_keywordsArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, product_keywords);
                        mproductKeyword.setAdapter(product_keywordsArrayAdapter);
                    }
                });

        mproductKeyword.setThreshold(1);
        mproductKeyword.setTokenizer(new SpaceTokenizer());
        product = new Product();
    }

    private AtomicBoolean uploadDataToFireStore() {
        AtomicBoolean isUpload = new AtomicBoolean(false);
        FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .document(MainActivity.usernameUid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Shops shops = document.toObject(Shops.class);
                        product.setShop_name(shops.getShopname());
                        short_title_str = eproduct_name.getText().toString().trim();
                        long_title_str = edproduct_long_title.getText().toString().trim();
                        color_str = mProductColor.getText().toString();
                        price_str = eproduct_price.getText().toString();
                        description_str = eproduct_description.getText().toString().trim();
                        discount_str = eproductdiscount.getText().toString();
                        product_type_str = mProductType.getText().toString();
                        product_gender_type_str = mProductGenderType.getText().toString();
                        keywords = mproductKeyword.getText().toString().split(" ");
                        product.setProduct_short_name(short_title_str.trim());
                        product.setProduct_long_title(long_title_str.trim());
                        product.setProduct_color(color_str.trim());
                        product.setProduct_price(price_str.trim());
                        product.setDiscount(discount_str.trim());
                        product.setShopId(MainActivity.usernameUid);
                        product.setProduct_desc(description_str.trim());
                        product.setProduct_type(product_type_str.trim());
                        product.setGender(product_gender_type_str.trim());
                        List<String> tempkey = Arrays.asList(keywords);
                        ArrayList<String> keywordsList = new ArrayList<>(tempkey);
                        keywordsList.add(color_str);
                        keywordsList.add(product_type_str);
                        keywordsList.add(long_title_str);
                        keywordsList.add(short_title_str);
                        keywordsList.add(product_gender_type_str);
                        ArrayList<String> finalKeywords = new ArrayList<>();
                        for (String x : keywordsList)
                            finalKeywords.add(x.trim().toUpperCase().toLowerCase());
                        product.setKeywords(finalKeywords);
                        product.setId(addproductRef.getId());
                        product.setFullkeyword(mproductKeyword.getText().toString().trim().toUpperCase().toLowerCase());
                        HashMap<String, String> fullkeywordmap = new HashMap<>();
                        fullkeywordmap.put("word", mproductKeyword.getText().toString().trim().toUpperCase().toLowerCase());
                        FirebaseFirestore
                                .getInstance()
                                .collection("Keyword")
                                .document(mproductKeyword.getText().toString().trim().toUpperCase().toLowerCase())
                                .set(fullkeywordmap);
                        int size = imageUrlList.size();
                        switch (size) {
                            case 1:
                                product.setImage1(imageUrlList.get(0));
                                break;
                            case 2:
                                product.setImage1(imageUrlList.get(0));
                                product.setImage2(imageUrlList.get(1));
                                break;
                            case 3:
                                product.setImage1(imageUrlList.get(0));
                                product.setImage2(imageUrlList.get(1));
                                product.setImage3(imageUrlList.get(2));
                                break;
                            case 4:
                                product.setImage1(imageUrlList.get(0));
                                product.setImage2(imageUrlList.get(1));
                                product.setImage3(imageUrlList.get(2));
                                product.setImage4(imageUrlList.get(3));
                                break;
                        }


                        if (imageUrlList.size() < 1) {
                            Toast.makeText(getApplicationContext(), "Please Select Pictures", Toast.LENGTH_LONG).show();
                            isUpload.set(false);
                        } else if (product.getProduct_short_name().isEmpty()) {
                            eproduct_name.setError("Can't Be Empty...");
                            eproduct_name.requestFocus();
                            isUpload.set(false);
                        } else if (product.getProduct_long_title().isEmpty()) {
                            edproduct_long_title.setError("Can't Be Empty...");
                            edproduct_long_title.requestFocus();
                            isUpload.set(false);
                        } else if (product.getProduct_price().isEmpty()) {
                            eproduct_price.setError("Can't Be Empty...");
                            eproduct_price.requestFocus();
                            isUpload.set(false);
                        } else if (product.getProduct_type().isEmpty() || !Arrays.asList(product_types).contains(product_type_str.trim())) {
                            mProductType.setError("Can't Be Empty...");
                            mProductType.requestFocus();
                            isUpload.set(false);
                        } else if (product.getGender().isEmpty() || !Arrays.asList(product_gender_types).contains(product_gender_type_str.trim())) {
                            mProductGenderType.setError("Can't Be Empty...");
                            mProductGenderType.requestFocus();
                            isUpload.set(false);
                        } else {
                            pd.setTitle("Uploading...");
                            pd.setMessage("Product is Uploading...");
                            pd.show();
                            if (edit != null) {
                                FirebaseFirestore
                                        .getInstance()
                                        .collection("Shops")
                                        .document(MainActivity.usernameUid)
                                        .collection("Products")
                                        .document(productId)
                                        .set(product, SetOptions.merge())
                                        .addOnSuccessListener(unused -> {
                                            pd.dismiss();
                                            isUpload.set(true);
                                            Toast.makeText(getApplicationContext(), "Item Added Succesfully", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(AddProduct.this, DashBoard.class);
                                            startActivity(intent);
                                            finish();
                                        });

                            } else {
                                addproductRef.set(product, SetOptions.merge())
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                pd.dismiss();
                                                isUpload.set(true);
                                                Toast.makeText(getApplicationContext(), "Item Added Succesfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(AddProduct.this, DashBoard.class);
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                isUpload.set(false);
                                            }
                                        }).addOnFailureListener(e -> {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    isUpload.set(false);
                                });
                            }


                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Person Exists", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show());


        return isUpload;
    }

    private void init() {
        productName = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        productLongTitle = findViewById(R.id.product_long_title);
        productColor = findViewById(R.id.product_color);
        productDiscount = findViewById(R.id.product_discount);
        productKeywords = findViewById(R.id.product_keywords);
        productGenderType = findViewById(R.id.product_gender);
        productDescription = findViewById(R.id.product_desc);
        productType = findViewById(R.id.product_type);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        uploadProduct = findViewById(R.id.upload_product);
        mProductColor = (AppCompatMultiAutoCompleteTextView) findViewById(R.id.mproduct_color);
        mproductKeyword = (AppCompatMultiAutoCompleteTextView) findViewById(R.id.mproduct_keywords);
        mProductType = (AppCompatMultiAutoCompleteTextView) findViewById(R.id.mproduct_type);
        mProductGenderType = (AppCompatMultiAutoCompleteTextView) findViewById(R.id.mproduct_gender);
        edproduct_long_title = (EditText) findViewById(R.id.eproduct_long_title);
        eproduct_name = (EditText) findViewById(R.id.eproduct_title);
        eproduct_description = (EditText) findViewById(R.id.eproduct_desc);
        eproduct_price = (EditText) findViewById(R.id.eproduct_price);
        eproductdiscount = (EditText) findViewById(R.id.eproduct_discount);
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser!=null)
        {
            addproductRef = db
                    .collection("Shops")
                    .document(mUser.getUid())
                    .collection("Products")
                    .document();
        }

    }

    private void showImagePickerOptions() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 4)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAutoZoomEnabled(true)
                .setFixAspectRatio(true)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(25)
                .start(AddProduct.this);
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]
                {
                        CAMERA
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE

                }, requestPermissionCode);

    }

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

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

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ||
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                if (currentImg1) {
                    image1Uri = result.getUri();
                    image1.setImageURI(image1Uri);
                    uploadImageToFireBase(image1Uri, "Image1");
                }
                if (currentImg2) {
                    image2Uri = result.getUri();
                    image2.setImageURI(image2Uri);
                    uploadImageToFireBase(image2Uri, "Image2");
                }
                if (currentImg3) {
                    image3Uri = result.getUri();
                    image3.setImageURI(image3Uri);
                    uploadImageToFireBase(image3Uri, "Image3");
                }

                if (currentImg4) {
                    image4Uri = result.getUri();
                    image4.setImageURI(image4Uri);
                    uploadImageToFireBase(image4Uri, "Image4");
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("AddProduct",""+error.getMessage());
            }
        }
    }

    private void uploadImageToFireBase(Uri imageUri, String imagename) {

        pd.setTitle("Upload");
        pd.setMessage("Pic Uploading....");
        pd.show();
        String FilePathName = "Product/" + "" + MainActivity.usernameUid + "/" + eproduct_name.getText().toString().trim() + "/" + addproductRef.getId() + "_" + imagename;
        StorageReference storageref2 = mStorageRef.child(FilePathName);

        storageref2.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    final Uri profile_pic_uri_path = uriTask.getResult();
                    if (uriTask.isSuccessful()) {
                        //image uploaded...
                        pd.dismiss();
                        result = profile_pic_uri_path.toString();
                        if (currentImg1) {
                            imageUrlList.add(result);
                        }
                        if (currentImg2) {
                            imageUrlList.add(result);
                        }
                        if (currentImg3) {
                            imageUrlList.add(result);
                        }

                        if (currentImg4) {
                            imageUrlList.add(result);
                        }
                    } else {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Error while uploading image", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        // ...
                    }
                });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings. ");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();

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


}