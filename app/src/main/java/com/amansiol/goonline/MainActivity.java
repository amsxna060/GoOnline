package com.amansiol.goonline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amansiol.goonline.models.User;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    Toolbar toolbar;
    private DrawerLayout mNavDrawer;
    public static String susername;
    private FirebaseAuth mAuth;
    NavigationView navigationView;
    CollectionReference collectionReference;
    TextView title;
    public static String usernameUid;
    String useremail;
    TextView headername;
    TextView headeremail;
    FirebaseFirestore db;
    boolean alreadyreg=false;
    ImageView shop_icon;
    TextView shop_owner;
    /*
    For setting profil picture
     */
    CircularImageView profileimage;
    CircularImageView addprofileimage;
    ProgressDialog pd;
    private final int requestPermissionCode=777;
    public Uri profileUri;
    String downloadUrlofProfile ;
    private StorageReference mStorageRef;
    FloatingActionButton fab;

    void initView()
    {
        toolbar=findViewById(R.id.toolbar);
        mNavDrawer=findViewById(R.id.nav_drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        title=findViewById(R.id.title);
        View headerView =  navigationView.getHeaderView(0);
        headeremail=(TextView)headerView.findViewById(R.id.headeremail);
        headername=(TextView)headerView.findViewById(R.id.headername);
        profileimage=(CircularImageView)headerView.findViewById(R.id.profilepic);
        addprofileimage=(CircularImageView)headerView.findViewById(R.id.addprofilepic);
        pd = new ProgressDialog(MainActivity.this);
        collectionReference=FirebaseFirestore.getInstance().collection("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        shop_icon=(ImageView) headerView.findViewById(R.id.shop_icon);
        shop_owner=(TextView) headerView.findViewById(R.id.shop_owner);
        fab=findViewById(R.id.add_product);

    }

    private void getCurrentLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        progressbar.setVisibility(View.GONE);
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("Users")
                                    .document(usernameUid)
                                    .update("location",geoPoint);
                        }
                    }
                }, Looper.getMainLooper());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.nav_drawer_layout);
        // initialize the view
        initView();
        checkUserLoginState();
        mAuth=FirebaseAuth.getInstance();
        // making toolbar as actionbar
        setSupportActionBar(toolbar);

        // making toogle button to open navigation drawer
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,mNavDrawer,toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorTheme));
        // replace the homefragment at the first time mainActivity open
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout,new FragmentHome())
                    .commit(); }

        // this is two function calls is to connecting togglebutton to navigation drawer.
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // this is for setup onclick listener on navigation drawer menu item or button.
        navigationView.setNavigationItemSelectedListener(this);
        Log.d("back", ""+getSupportFragmentManager().getBackStackEntryCount());
        /*
         Here i m writing code for
         show the name and email
         in the header on navigation
         Drawer at the top by getting name
         from the cloud firestore by
         going to userprofile and fetch
         the name and email.
         */
        // start code

            usernameUid=FirebaseAuth.getInstance().getUid();
            if(usernameUid==null)
            {
                startActivity(new Intent(MainActivity.this,SignInActivity.class));
                finish();
            }else {
                getCurrentLocation();
                collectionReference
                        .document(usernameUid)
                        .get()
                        .addOnSuccessListener(document -> {
                            if(document.exists()){
                                User user= new User();
                                user=document.toObject(User.class);
                                susername=user.getName();
                                headername.setText(susername);
                                headeremail.setText(useremail);
                                if(user.getProfile_img()!=null && !user.getProfile_img().isEmpty())
                                    Picasso.get().load(user.getProfile_img()).into(profileimage);
                                else
                                    Picasso.get().load(R.drawable.profile).into(profileimage);
                                if(user.getHaveshop()){
                                    alreadyreg=true;
                                    shop_icon.setVisibility(View.VISIBLE);
                                    shop_owner.setVisibility(View.VISIBLE);
                                    Menu menu = navigationView.getMenu();
                                    // find MenuItem you want to change
                                    MenuItem reg_ur_shop = menu.findItem(R.id.reg_ur_shop);
                                    // set new title to the MenuItem
                                    reg_ur_shop.setTitle("DashBoard");
                                    fab.setVisibility(View.VISIBLE);
                                    fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,AddProduct.class)));

                                }

                            }
                        });
            }

            addprofileimage.setOnClickListener(v -> {
                // If All permission is enabled successfully then this block will execute.
                if(CheckingPermissionIsEnabledOrNot())
                {
                    showImagePickerOptions();
                }
                // If, If permission is not enabled then else condition will execute.
                else {
                    //Calling method to enable permission.
                    RequestMultiplePermission();
                }
            });
            profileimage.setOnClickListener(v -> {

                // If All permission is enabled successfully then this block will execute.
                if(CheckingPermissionIsEnabledOrNot())
                {
                    showImagePickerOptions();
                }
                // If, If permission is not enabled then else condition will execute.
                else {
                    //Calling method to enable permission.
                    RequestMultiplePermission();
                }
            });

        }

    private void Uploading_profile_tofirebase() {
        pd.setTitle("Upload");
        pd.setMessage("Profile Pic Uploading....");
        pd.show();
        String FilePathName = "Profiles/" + "" + "image" + "_" + usernameUid;
        StorageReference storageref2 = mStorageRef.child(FilePathName);

        storageref2.putFile(profileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        final Uri profile_pic_uri_path = uriTask.getResult();
                        if (uriTask.isSuccessful()) {
                            //image uploaded...
                            pd.dismiss();
                            downloadUrlofProfile = profile_pic_uri_path.toString();

                            User useruploadpic= new User();
                            useruploadpic.setProfile_img(downloadUrlofProfile);
                            collectionReference
                                    .document(usernameUid)
                                    .set(useruploadpic,SetOptions.mergeFields("profile_img"));

                        } else {
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "Error while uploading image", Toast.LENGTH_LONG).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        // ...
                    }
                });
    }

    private void showImagePickerOptions() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAutoZoomEnabled(true)
                .setFixAspectRatio(true)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(30)
                .start(MainActivity.this);
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        CAMERA
                        ,Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE

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

        return  FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED ;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileUri= result.getUri();
                profileimage.setImageURI(profileUri);
                Uploading_profile_tofirebase();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings. ");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MainActivity.this.openSettings();
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


        //end code
        int count=0;
    // Here i m overriding the BackButton of andriod to close the drawer first then the Activity
    @Override
    public void onBackPressed() {
        if(mNavDrawer.isDrawerOpen(GravityCompat.START)){
            mNavDrawer.closeDrawer(GravityCompat.START);
        }
        else {
            Log.d("back", ""+getSupportFragmentManager().getBackStackEntryCount());

            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {

                // only show dialog while there's back stack entry
                getSupportFragmentManager().popBackStack();

            } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // or just go back to main activity
                super.onBackPressed();
            }
        }

    }
   // this overrided function will perform action of on navi_drawer_item_click.
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navhome:
                Intent res = getIntent();
                overridePendingTransition(0, 0);
                res.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(res);
                title.setText("Home");
                break;
            case R.id.newoffer:
                Toast.makeText(this,"new offers",Toast.LENGTH_LONG).show();
                break;
            case R.id.reg_ur_shop:
                if(alreadyreg)
                {
                    Intent intent = new Intent(MainActivity.this,DashBoard.class);
                    startActivity(intent);

                }else {
                    Intent intent = new Intent(MainActivity.this,RegisterShopActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.nearbystore:
                title.setText("Near By Stores");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout,new NearByStore())
                        .commit();
                break;
            case R.id.ur_profile:
                title.setText("Profile");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout,new MyAccountFragment())
                        .commit();
                break;
            case R.id.ur_orders:
                Toast.makeText(this,"ur_orders",Toast.LENGTH_LONG).show();
                break;
            case R.id.myfav:
                startActivity(new Intent(MainActivity.this,FavActivity.class));
                break;
            case R.id.mycart:
                startActivity(new Intent(MainActivity.this,CartActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                break;
            case R.id.contact_us:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:jpr1521@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact GoOffline Developer");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi, I'm Here to ask for your help...");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                checkUserLoginState();
                Toast.makeText(this,"log out",Toast.LENGTH_LONG).show();
                break;
        }
        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
 // this is for inflating  menu  for cart and favourite button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.fav:
                startActivity(new Intent(MainActivity.this,ImageBasedSeachActivity.class));
                break;
            case R.id.cart:
                startActivity(new Intent(MainActivity.this,CartActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                break;
        }
        return false;
    }
    //  Here i m checking User is already login or logout.
    private void checkUserLoginState() {
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null||mAuth==null){
            startActivity(new Intent(MainActivity.this,SignInActivity.class));
            finish();
        }else {
            usernameUid=user.getUid();
            useremail=user.getEmail();
        }
    }

    @Override
    protected void onResume() {
        checkUserLoginState();
        super.onResume();
    }

    @Override
    protected void onStart() {
        checkUserLoginState();
        super.onStart();
        if(!NetworkUtil.isConnected(getApplicationContext())){
            startActivity(new Intent(getApplicationContext(),NoInternet.class));
            finish();
        }
    }
}