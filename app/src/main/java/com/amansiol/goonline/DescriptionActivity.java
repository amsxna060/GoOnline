package com.amansiol.goonline;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amansiol.goonline.adapter.CommentAdapter;
import com.amansiol.goonline.adapter.ViewPagerAdapter;
import com.amansiol.goonline.constant.AddressKey;
import com.amansiol.goonline.models.Cart;
import com.amansiol.goonline.models.Comment;
import com.amansiol.goonline.models.Favorite;
import com.amansiol.goonline.models.Product;
import com.amansiol.goonline.models.Shops;
import com.amansiol.goonline.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class DescriptionActivity extends AppCompatActivity {


    ViewPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;
    ArrayList<String> fourimages;
    TextView product_title;
    TextView product_long_title;
    TextView product_desc;
    TextView product_price;
    Button add_to_cart;
    Product product;
    ImageView fav_product;
    RatingBar ratingBar;
    TextView product_rating;
    float totalRate = 0.0f;
    private int c = 0;
    CircularImageView profilepic;
    TextView username;
    TextInputLayout commenteditext;
    Button sendcomment;
    public static String tempprofilepic;
    public static String permprofilepic;

    ArrayList<Comment> commentList;
    CommentAdapter commentAdapter;
    RecyclerView commentRecycler;

    TextView shop_Name;
    TextView address;

    TextView distancer_textview;


    void initialize() {
        fourimages = new ArrayList<>();
        product_title = findViewById(R.id.product_title);
        product_long_title = findViewById(R.id.product_long_title);
        product_desc = findViewById(R.id.product_desc);
        product_price = findViewById(R.id.product_price);
        add_to_cart = findViewById(R.id.add_to_cart);
        fav_product = findViewById(R.id.fav_product);
        ratingBar = findViewById(R.id.ratingBar);
        product_rating = findViewById(R.id.product_rating);
        profilepic = findViewById(R.id.profilepic);
        username = findViewById(R.id.username);
        commenteditext = findViewById(R.id.commenteditext);
        sendcomment = findViewById(R.id.sendcomment);
        commentList = new ArrayList<>();
        commentRecycler = findViewById(R.id.commentsrecycler);
        shop_Name = findViewById(R.id.shop_name);
        address = findViewById(R.id.address_shop);
        distancer_textview = findViewById(R.id.distance);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_description);
        initialize();
        product = (Product) getIntent().getSerializableExtra("myproduct");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        commentRecycler.setLayoutManager(linearLayoutManager);

        /*
        For Sliding Images
         */

        mViewPager = (ViewPager) findViewById(R.id.imageviewer);
        fourimages.add(product.getImage1());
        fourimages.add(product.getImage2());
        fourimages.add(product.getImage3());
        fourimages.add(product.getImage4());

        // set up viewpager adapter
        mViewPagerAdapter = new ViewPagerAdapter(DescriptionActivity.this, fourimages);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);



        /*
          display all detail of product
          in textviews by taking from product class
         */
        float strike_price = Float.parseFloat(product.getProduct_price());
        float dis_price = Float.parseFloat(product.getDiscount());
        float price = strike_price - dis_price;
        product_title.setText(product.getProduct_short_name());
        product_long_title.setText(product.getProduct_long_title());
        product_desc.setText(product.getProduct_desc());
        product_price.setText("$ " + String.format("%.2f", price));

        /*
        Add to cart by adding id of product to Cart Collection.
         */
        Cart cart = new Cart();
        cart.setId(product.getId());
        cart.setPrice(String.valueOf(price));
        Favorite favorite = new Favorite();
        favorite.setId(product.getId());

        add_to_cart.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(MainActivity.usernameUid)
                    .collection("Cart")
                    .document(product.getId())
                    .set(cart, SetOptions.merge())
                    .addOnSuccessListener(unused -> startActivity(new Intent(DescriptionActivity.this, CartActivity.class)));
        });

        fav_product.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(MainActivity.usernameUid)
                    .collection("Fav")
                    .document(product.getId())
                    .set(favorite, SetOptions.merge())
                    .addOnSuccessListener(unused -> startActivity(new Intent(DescriptionActivity.this, FavActivity.class)));
        });

        FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .document(product.getShopId())
                .collection("Products")
                .document(product.getId())
                .collection("Ratings")
                .document(MainActivity.usernameUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            ratingBar.setRating(Float.valueOf(documentSnapshot.getData().get("rating").toString()));
                        } else {
                            ratingBar.setRating(0);
                        }
                    }
                });

        // when rating change it listen for that change event
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar1, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                HashMap<String, Float> ratingMap = new HashMap<>();
                ratingMap.put("rating", rating);
                FirebaseFirestore
                        .getInstance()
                        .collection("Shops")
                        .document(product.getShopId())
                        .collection("Products")
                        .document(product.getId())
                        .collection("Ratings")
                        .document(MainActivity.usernameUid)
                        .set(ratingMap, SetOptions.merge());
                totalRate = 0.0f;
                getAvgRating();

            }
        });
        // get rating
        getAvgRating();

        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(MainActivity.usernameUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        tempprofilepic = user.getProfile_img();
                        permprofilepic = tempprofilepic;
                        if (tempprofilepic == null) {
                            Picasso.get().load(R.drawable.profile).into(profilepic);
                        } else {
                            Picasso.get().load(tempprofilepic).into(profilepic);

                        }
                        username.setText(user.getName());
                    }
                });
        sendcomment.setOnClickListener(v -> {
            if (commenteditext.getEditText().getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Can't Empty Comment", Toast.LENGTH_LONG).show();
                return;
            }
            Comment comment = new Comment();
            comment.setCommentbody(commenteditext.getEditText().getText().toString().trim());
            comment.setUsername(username.getText().toString().trim());
            comment.setImage(permprofilepic);
            String timestamp = String.valueOf(System.currentTimeMillis());
            comment.setTimestamp(timestamp);
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Commenting...");
            pd.show();
            HashMap<String, String> idmap = new HashMap<>();
            idmap.put("id", MainActivity.usernameUid);
            FirebaseFirestore
                    .getInstance()
                    .collection("Shops")
                    .document(product.getShopId())
                    .collection("Products")
                    .document(product.getId())
                    .collection("Comments")
                    .document(MainActivity.usernameUid)
                    .set(idmap);
            FirebaseFirestore
                    .getInstance()
                    .collection("Shops")
                    .document(product.getShopId())
                    .collection("Products")
                    .document(product.getId())
                    .collection("Comments")
                    .document(MainActivity.usernameUid)
                    .collection("HisComments")
                    .document(timestamp)
                    .set(comment, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        getAllCommentsFromFirebase();
                        commenteditext.getEditText().setText("");
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Comment Successfully", Toast.LENGTH_LONG).show();
                    });
        });
        getAllCommentsFromFirebase();

        if (product.getShopId() != null) {
            FirebaseFirestore
                    .getInstance()
                    .collection("Shops")
                    .document(product.getShopId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Shops shops = documentSnapshot.toObject(Shops.class);
                            HashMap<String, String> addressmap;
                            addressmap = shops.getAddress();
                            shop_Name.setText(shops.getShopname());
                            address.setText(String.format(
                                    "%s\n%s\n%s ,%s\n%s ,%s "
                                    , shops.getName()
                                    , addressmap.get(AddressKey.SUBLOCALITY)
                                    , addressmap.get(AddressKey.LOCALITY)
                                    , addressmap.get(AddressKey.POSTAL_CODE)
                                    , addressmap.get(AddressKey.STATE_NAME)
                                    , addressmap.get(AddressKey.COUNTRY_NAME)
                            ));

                            FirebaseFirestore
                                    .getInstance()
                                    .collection("Users")
                                    .document(MainActivity.usernameUid)
                                    .addSnapshotListener((value, error) -> {
                                        if (value.exists()) {
                                            User user = value.toObject(User.class);
                                            double distance = filterByRadius(user.getLocation().getLatitude(), user.getLocation().getLongitude(), shops.getLocation().getLatitude(), shops.getLocation().getLongitude());
                                            distancer_textview.setVisibility(View.VISIBLE);
                                            distancer_textview.setText(shops.getShopname() + " is just " + String.format("%1.2f", distance) + "Km Far from you.");
                                        } else {
                                            distancer_textview.setVisibility(View.GONE);
                                        }
                                    });

                        }
                    });


        }

    }

    private static double filterByRadius(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            // Haversine Formula to find distance between two points on the globe.
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;

            return (dist);
        }
    }

    private void getAllCommentsFromFirebase() {
        commentList.clear();
        commentAdapter = new CommentAdapter(DescriptionActivity.this, commentList);
        commentRecycler.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
        Log.d("DescriptionActivity", "Comment List : " + "before");

        FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .document(product.getShopId())
                .collection("Products")
                .document(product.getId())
                .collection("Comments")
                .addSnapshotListener(this, (users, error) -> {
                    for (QueryDocumentSnapshot user : users) {
                        Log.d("DescriptionActivity", "Comment List : " + user.getId());
                        FirebaseFirestore
                                .getInstance()
                                .collection("Shops")
                                .document(product.getShopId())
                                .collection("Products")
                                .document(product.getId())
                                .collection("Comments")
                                .document(user.getId())
                                .collection("HisComments")
                                .addSnapshotListener(DescriptionActivity.this, (value, error1) -> {

                                    for (QueryDocumentSnapshot hiscomment : value) {
                                        Comment comment1 = hiscomment.toObject(Comment.class);
                                        commentList.add(comment1);
                                    }
                                    Log.d("DescriptionActivity", "Comment List : " + commentList.size());
                                    commentAdapter = new CommentAdapter(DescriptionActivity.this, commentList);
                                    commentRecycler.setAdapter(commentAdapter);
                                    commentAdapter.notifyDataSetChanged();

                                });
                    }
                });

    }

    private void getAvgRating() {
        FirebaseFirestore
                .getInstance()
                .collection("Shops")
                .document(product.getShopId())
                .collection("Products")
                .document(product.getId())
                .collection("Ratings")
                .addSnapshotListener(DescriptionActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot rate : value) {
                            Float onerate = Float.valueOf(rate.getData().get("rating").toString());
                            totalRate += onerate;
                            c = value.size();
                        }
                        if (c > 0) {
                            float avg_rate = totalRate / c;
                            Log.d("TAG", "" + c);
                            product_rating.setText(String.valueOf(avg_rate));
                            totalRate = 0;
                        } else {
                            product_rating.setText(String.valueOf(0.0f));
                        }

                    }
                });
    }

}