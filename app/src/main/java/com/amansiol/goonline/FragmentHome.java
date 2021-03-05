package com.amansiol.goonline;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amansiol.goonline.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
/*
This is Home Fragment in which we have populate
data in recycler view for different product.

But Till now we just pass the temp data manually in
the codding and then put that data into an Array list.

Then We pass this array list to Product Adapter constructor

Product Adapter is used for populate data into Ui (RecyclerView)
 */


public class FragmentHome extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // init

        tabLayout=v.findViewById(R.id.tablayout);
        viewPager=v.findViewById(R.id.categories_pager);

        // prepare view pager
        preparePageViewwer(viewPager);

        // Setup with view pager
        tabLayout.setupWithViewPager(viewPager);

        FirebaseFirestore.getInstance().collection("Users")
                .document(MainActivity.usernameUid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user= documentSnapshot.toObject(User.class);
                if(user.getGender().equals("Female"))
                {
                    viewPager.setCurrentItem(1);
                }
            }
        });
        return v;
    }

    private void preparePageViewwer(ViewPager viewPager) {
        // Initialize main Adapter
        MainAdapter adapter=new MainAdapter(getActivity().getSupportFragmentManager());

        adapter.addFragment(new FragmentMens(),"MEN'S");
        adapter.addFragment(new FragementWomens(),"WOMEN'S");
        adapter.addFragment(new FragementKidswear(),"KID'S WEAR");
        adapter.addFragment(new FragmentUnisex(),"UNISEX WEAR");

        viewPager.setAdapter(adapter);
    }




    private class MainAdapter extends FragmentPagerAdapter {
       // initialize array list
        ArrayList<String> tablist = new ArrayList<>();
        List<Fragment> fragmentList=new ArrayList<>();


        public void addFragment(Fragment fragment, String title){
             // add title
            tablist.add(title);
            // Add fragment
            fragmentList.add(fragment);
        }
        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tablist.get(position);
        }
    }
}