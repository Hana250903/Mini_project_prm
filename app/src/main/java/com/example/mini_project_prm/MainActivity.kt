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
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imgUser: ImageView
    private lateinit var imgCategory: ImageView
    private var isCategoryVisible = false

    private lateinit var fabChat: FloatingActionButton
    private lateinit var chatboxView: CardView
    private var isChatboxOpen = false

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
        fabChat = findViewById(R.id.fab_chat)
        chatboxView = findViewById(R.id.chatbox_view)

        setupChatbox()

        // Thiết lập các listener cho chatbox
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

    // Trong MainActivity.kt

    private fun setupChatbox() {
        // Logic kéo/thả icon đã được nâng cấp
        fabChat.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private val CLICK_THRESHOLD = 10

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = v.x.toInt()
                        initialY = v.y.toInt()
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Di chuyển icon theo ngón tay
                        v.x = initialX + (event.rawX - initialTouchX)
                        v.y = initialY + (event.rawY - initialTouchY)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val dX = (event.rawX - initialTouchX).toInt()
                        val dY = (event.rawY - initialTouchY).toInt()

                        // Nếu di chuyển ít, coi như là một cú click
                        if (Math.abs(dX) < CLICK_THRESHOLD && Math.abs(dY) < CLICK_THRESHOLD) {
                            v.performClick()
                        } else {
                            // === LOGIC MỚI: TỰ ĐỘNG DI CHUYỂN VỀ CẠNH ===
                            val screenWidth = resources.displayMetrics.widthPixels
                            // Nếu icon ở nửa bên trái màn hình
                            if (v.x + v.width / 2 < screenWidth / 2) {
                                // Di chuyển về cạnh trái
                                v.animate().x(24f).setDuration(200).start()
                            } else {
                                // Di chuyển về cạnh phải
                                v.animate().x((screenWidth - v.width - 24).toFloat()).setDuration(200).start()
                            }
                        }
                        return true
                    }
                }
                return false
            }
        })

        // Logic hiện/ẩn chatbox khi click (giữ nguyên)
        fabChat.setOnClickListener {
            toggleChatbox()
        }

        findViewById<ImageView>(R.id.btn_close_chatbox).setOnClickListener {
            toggleChatbox()
        }

        // Logic cho các nút chọn sẵn (giữ nguyên)
        findViewById<Chip>(R.id.chip_option_1).setOnClickListener { sendQuickReply((it as Chip).text.toString()) }
        findViewById<Chip>(R.id.chip_option_2).setOnClickListener { sendQuickReply((it as Chip).text.toString()) }
        findViewById<Chip>(R.id.chip_option_3).setOnClickListener { sendQuickReply((it as Chip).text.toString()) }
    }

    // Hàm mới để hiện/ẩn chatbox với animation
    private fun toggleChatbox() {
        if (isChatboxOpen) {
            // Đóng chatbox
            chatboxView.animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        chatboxView.visibility = View.GONE
                        fabChat.show() // Hiện lại icon
                    }
                })
        } else {
            // Mở chatbox
            fabChat.hide() // Ẩn icon đi
            chatboxView.visibility = View.VISIBLE
            chatboxView.alpha = 0f
            chatboxView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .setListener(null)
        }
        isChatboxOpen = !isChatboxOpen
    }

    // Hàm mới để xử lý khi người dùng chọn option
    private fun sendQuickReply(text: String) {
        // Tạm thời chỉ hiển thị Toast
        Toast.makeText(this, "Bạn đã chọn: $text", Toast.LENGTH_SHORT).show()

        // TODO: Gửi `text` này đến AI của bạn để xử lý
        // Sau đó nhận kết quả và hiển thị lên RecyclerView (rv_chat_messages)
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
        // Dòng xử lý click cho imgCategory đã được xóa bỏ
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