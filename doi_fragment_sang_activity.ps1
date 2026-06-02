$base = "app/src/main/java/com/example/onlineclothingstoreapp"
$profilePath = "$base/profile"
$fragmentPath = "$base/fragment"
$activitiesPath = "$base/activities"
$manifestPath = "app/src/main/AndroidManifest.xml"

# Đảm bảo thư mục tồn tại
New-Item -ItemType Directory -Force -Path $fragmentPath | Out-Null
New-Item -ItemType Directory -Force -Path $activitiesPath | Out-Null

# 1. Tạo ProfileFragment đúng package fragment
@'
package com.example.onlineclothingstoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.activities.EditProfileActivity
import com.example.onlineclothingstoreapp.activities.PaymentActivity
import com.example.onlineclothingstoreapp.activities.SettingsActivity
import com.example.onlineclothingstoreapp.activities.WishlistActivity
import com.example.onlineclothingstoreapp.profile.QuanLyDangXuat
import com.example.onlineclothingstoreapp.profile.QuanLyThongTinNguoiDung

class ProfileFragment : Fragment() {

    private lateinit var tvAvatar: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView

    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    private lateinit var btnMyOrders: LinearLayout
    private lateinit var btnWishlist: LinearLayout
    private lateinit var btnSettings: LinearLayout
    private lateinit var btnPayment: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        AnhXa(view)
        GanDuLieu()
        SuKien()

        return view
    }

    private fun AnhXa(view: View) {
        tvAvatar = view.findViewById(R.id.tvAvatar)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)

        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.btnLogout)

        btnMyOrders = view.findViewById(R.id.btnMyOrders)
        btnWishlist = view.findViewById(R.id.btnWishlist)
        btnSettings = view.findViewById(R.id.btnSettings)
        btnPayment = view.findViewById(R.id.btnPayment)
    }

    private fun GanDuLieu() {
        tvUserName.text = "Đang tải..."
        tvUserEmail.text = ""
        tvAvatar.text = "U"

        QuanLyThongTinNguoiDung.TaiThongTin(requireContext()) {
            tvUserName.text = QuanLyThongTinNguoiDung.tenHienThi
            tvUserEmail.text = QuanLyThongTinNguoiDung.email

            tvAvatar.text =
                QuanLyThongTinNguoiDung.tenHienThi
                    .ifEmpty { "U" }
                    .first()
                    .toString()
                    .uppercase()
        }
    }

    private fun SuKien() {
        tvAvatar.setOnClickListener {
            MoSuaThongTin()
        }

        btnEditProfile.setOnClickListener {
            MoSuaThongTin()
        }

        btnMyOrders.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }

        btnWishlist.setOnClickListener {
            startActivity(Intent(requireContext(), WishlistActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        btnPayment.setOnClickListener {
            startActivity(Intent(requireContext(), PaymentActivity::class.java))
        }

        btnLogout.setOnClickListener {
            QuanLyDangXuat.DangXuat(requireActivity())
        }
    }

    private fun MoSuaThongTin() {
        startActivity(Intent(requireContext(), EditProfileActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        if (::tvUserName.isInitialized) {
            GanDuLieu()
        }
    }
}
'@ | Set-Content "$fragmentPath/ProfileFragment.kt" -Encoding UTF8

# 2. Di chuyển Activity từ profile sang activities và đổi package
$activityFiles = @(
    "EditProfileActivity.kt",
    "WishlistActivity.kt",
    "SettingsActivity.kt",
    "PaymentActivity.kt"
)

foreach ($file in $activityFiles) {
    $oldFile = "$profilePath/$file"
    $newFile = "$activitiesPath/$file"

    if (Test-Path $oldFile) {
        $content = Get-Content $oldFile -Raw
        $content = $content -replace "package com\.example\.onlineclothingstoreapp\.profile", "package com.example.onlineclothingstoreapp.activities"

        if ($content -notmatch "import com\.example\.onlineclothingstoreapp\.profile\.") {
            $content = $content -replace "import com\.example\.onlineclothingstoreapp\.R", "import com.example.onlineclothingstoreapp.R`r`nimport com.example.onlineclothingstoreapp.profile.*"
        }

        Set-Content $newFile $content -Encoding UTF8
        Remove-Item $oldFile -Force
    }
}

# 3. Xóa các Fragment phụ dư trong package profile
$oldFragments = @(
    "$profilePath/ProfileFragment.kt",
    "$profilePath/EditProfileFragment.kt",
    "$profilePath/WishlistFragment.kt",
    "$profilePath/SettingsFragment.kt",
    "$profilePath/PaymentFragment.kt"
)

foreach ($file in $oldFragments) {
    if (Test-Path $file) {
        Remove-Item $file -Force
    }
}

# 4. Sửa MainActivity import ProfileFragment về package fragment
$mainActivity = "$activitiesPath/MainActivity.kt"

if (Test-Path $mainActivity) {
    $main = Get-Content $mainActivity -Raw
    $main = $main -replace "import com\.example\.onlineclothingstoreapp\.profile\.ProfileFragment", "import com.example.onlineclothingstoreapp.fragment.ProfileFragment"
    Set-Content $mainActivity $main -Encoding UTF8
}

# 5. Sửa AndroidManifest khai báo Activity đúng package activities
if (Test-Path $manifestPath) {
    $manifest = Get-Content $manifestPath -Raw

    $manifest = $manifest -replace '\.profile\.EditProfileActivity', '.activities.EditProfileActivity'
    $manifest = $manifest -replace '\.profile\.WishlistActivity', '.activities.WishlistActivity'
    $manifest = $manifest -replace '\.profile\.SettingsActivity', '.activities.SettingsActivity'
    $manifest = $manifest -replace '\.profile\.PaymentActivity', '.activities.PaymentActivity'

    $activities = @(
        '<activity android:name=".activities.EditProfileActivity" />',
        '<activity android:name=".activities.WishlistActivity" />',
        '<activity android:name=".activities.SettingsActivity" />',
        '<activity android:name=".activities.PaymentActivity" />'
    )

    foreach ($activity in $activities) {
        if ($manifest -notlike "*$activity*") {
            $manifest = $manifest -replace "</application>", "        $activity`r`n    </application>"
        }
    }

    Set-Content $manifestPath $manifest -Encoding UTF8
}

Write-Host "DONE: Da chuyen ProfileFragment ve package fragment, chuyen Activity ve package activities, xoa Fragment du." -ForegroundColor Green
Write-Host "Tiep theo chay: .\gradlew assembleDebug" -ForegroundColor Yellow