package com.jollitycn.mobilekeybroad;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;
import com.jollitycn.mobilekeybroad.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// This is all you need.
//        DynamicColors.applyToActivitiesIfAvailable(this);
        // 使用数据绑定来设置布局
//        binding = DataBindingUtil.setContentView(this, R.layout.);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startKeyboardActivity();
//        setSupportActionBar(binding.appBarMain.toolbar);
//        if (binding.appBarMain.fab != null) {
//            binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show());
//        }

//        if (binding.appBarMain.fab != null) {
//            binding.appBarMain.fab.setOnClickListener(v -> {
//                        startKeyboardActivity();
//                    }
//            );
//        }

//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
//        assert navHostFragment != null;
//        NavController navController = navHostFragment.getNavController();
//
//        NavigationView navigationView = binding.navView;
//        if (navigationView != null) {
//            mAppBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings)
//                    .setOpenableLayout(binding.drawerLayout)
//                    .build();
//            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//            NavigationUI.setupWithNavController(navigationView, navController);
//        }
//
//        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
//        if (bottomNavigationView != null) {
//            mAppBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow)
//                    .build();
//            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//            NavigationUI.setupWithNavController(bottomNavigationView, navController);
//        }
    }

    public void startKeyboardActivity() {
        // 创建一个新的Intent，指定要启动的Activity
        Intent intent = new Intent(this, KeyboardActivity.class);

        // 启动新的Activity
        startActivity(intent);

        // 如果需要，你还可以添加额外的数据到Intent中，例如：
        // intent.putExtra("key", value);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        boolean result = super.onCreateOptionsMenu(menu);
//        // Using findViewById because NavigationView exists in different layout files
//        // between w600dp and w1240dp
//        NavigationView navView = findViewById(R.id.nav_view);
//        if (navView == null) {
//            // The navigation drawer already has the items including the items in the overflow menu
//            // We only inflate the overflow menu if the navigation drawer isn't visible
//            getMenuInflater().inflate(R.menu.overflow, menu);
//        }
//        return result;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}