package com.example.mini_project_prm

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import coil.load // Import thư viện Coil
import com.example.mini_project_prm.fragments.CartFragment
import com.example.mini_project_prm.fragments.CategoryFragment
import com.example.mini_project_prm.fragments.HomeFragment
import com.example.mini_project_prm.fragments.PurchaseHistoryFragment // Import trang Lịch sử
import com.example.mini_project_prm.fragments.UserInfoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imgUser: ImageView
    private lateinit var imgCategory: ImageView
    private var isCategoryVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cập nhật listener cho back stack để xử lý cả 3 mục
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            when (currentFragment) {
                is HomeFragment -> bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
                is CartFragment -> bottomNavigationView.menu.findItem(R.id.nav_cart).isChecked = true
                is PurchaseHistoryFragment -> bottomNavigationView.menu.findItem(R.id.nav_history).isChecked = true
            }
        }

        // Ánh xạ View
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        imgUser = findViewById(R.id.imgUser)
        imgCategory = findViewById(R.id.imgCategory)

        // Tải ảnh cho các icon trên cùng bằng Coil
        loadTopBarImages()

        // Xử lý sự kiện click
        setupClickListeners()

        // Xử lý Bottom Navigation
        setupBottomNavigation()

        // Load fragment mặc định khi mở app
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.nav_home
        }
    }

    private fun loadTopBarImages() {
        val logoUrl = "https://file.hstatic.net/200000462939/file/jhfigure_logo_68b33ad888be477280248144923b2983_grande.png"
        val userIconUrl = "https://firebasestorage.googleapis.com/v0/b/imageuploadv3.appspot.com/o/UserImage%2F%E2%80%94Pngtree%E2%80%94user%20icon%20symbol%20design%20user_5061125.png?alt=media&token=27f1eeb7-0fed-4c07-9035-b8a006811cd5"

        imgCategory.load(logoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_foreground)
        }
        imgUser.load(userIconUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_foreground)
        }
    }

    private fun setupClickListeners() {
        imgUser.setOnClickListener {
            loadFragment(UserInfoFragment())
        }
        imgCategory.setOnClickListener {
            toggleCategoryFragment()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_cart -> CartFragment()
                R.id.nav_history -> PurchaseHistoryFragment() // Xử lý cho Lịch sử
                else -> return@setOnItemSelectedListener false
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment?.javaClass != fragment.javaClass) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null) // Giữ lại fragment trong back stack để có thể quay lại
                .commit()
        }
    }

    fun toggleCategoryFragment() {
        val container = findViewById<FrameLayout>(R.id.categoryContainer)
        if (!isCategoryVisible) {
            val fragment = CategoryFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.categoryContainer, fragment)
                .commit()
            container.visibility = View.VISIBLE
            container.animate().translationX(0f).setDuration(300).start()
        } else {
            container.animate().translationX(-container.width.toFloat()).setDuration(300)
                .withEndAction { container.visibility = View.GONE }
                .start()
        }
        isCategoryVisible = !isCategoryVisible
    }
}