package com.mguard.vpn

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private lateinit var txtHeaderTime: TextView
    private lateinit var contentFrame: FrameLayout
    private lateinit var layoutVpnTab: View
    private lateinit var layoutEarnTab: View
    private lateinit var layoutPremiumTab: View

    private lateinit var navVpn: LinearLayout
    private lateinit var navEarn: LinearLayout
    private lateinit var navPremium: LinearLayout
    private lateinit var imgNavVpn: ImageView
    private lateinit var txtNavVpn: TextView
    private lateinit var imgNavEarn: ImageView
    private lateinit var txtNavEarn: TextView
    private lateinit var imgNavPremium: ImageView
    private lateinit var txtNavPremium: TextView

    private lateinit var vpnPulseRing: View
    private lateinit var btnVpnSwitch: LinearLayout
    private lateinit var imgVpnPower: ImageView
    private lateinit var txtVpnPowerStatus: TextView
    private lateinit var txtVpnClickToConnect: TextView
    private lateinit var txtVpnIp: TextView
    private lateinit var txtConnectionTimer: TextView
    private lateinit var txtDownloadSpeed: TextView
    private lateinit var txtUploadSpeed: TextView
    private lateinit var btnServerSelect: LinearLayout
    private lateinit var txtCurrentServer: TextView

    private lateinit var txtTabRemainingTime: TextView
    private lateinit var progressTimeBar: ProgressBar
    private lateinit var adCampaignsContainer: LinearLayout

    private lateinit var txtPremiumUserStatus: TextView
    private lateinit var btnToggleDevMode: Button
    private lateinit var layoutUserPurchaseView: View
    private lateinit var layoutDevAdminView: View

    private lateinit var txtDevTotalViews: TextView
    private lateinit var txtDevTotalRevenue: TextView
    private lateinit var layoutDevPendingSubmissions: LinearLayout

    private lateinit var txtKPayNum: TextView
    private lateinit var btnCopyKPay: Button
    private lateinit var editTxId: EditText
    private lateinit var editUserPhone: EditText
    private lateinit var btnSubmitTx: Button

    private var isConnected = false
    private var isConnecting = false
    private var remainingSeconds = 7200
    private var isPremium = false
    private var connectionSeconds = 0
    private var currentServerName = "Singapore - Free High Speed"
    private var isPremiumServer = false

    private val handler = Handler(Looper.getMainLooper())
    private var tickRunnable: Runnable? = null
    private val VPN_REQUEST_CODE = 4096

    private val servers = listOf(
        VPNServer("sg-1", "Singapore - Free High Speed", "🇸🇬", false),
        VPNServer("jp-1", "Tokyo - Gaming Route", "🇯🇵", false),
        VPNServer("us-premium", "New York - Premium Route", "🇺🇸", true),
        VPNServer("uk-premium", "London - Ultimate VIP", "🇬🇧", true)
    )

    private val campaigns = listOf(
        AdCampaign("camp_1", "Wave Casino Lucky Wheel", "အခမဲ့ဆုကြေးများနှင့် ဂိမ်းအစုံအလင်ဆော့ကစားလိုက်ပါ။", 15, 5),
        AdCampaign("camp_2", "Shwe Bo Direct Rice Supply", "ရွှေဘိုမင်းတုန်းမင်းမွှေးဆန်များကို အိမ်တိုင်ရာရောက်ပို့ဆောင်ပေးပါသည်။", 30, 5),
        AdCampaign("camp_3", "MPT Super Pack Direct Promo", "MPT သုံးစွဲသူများအတွက် အထူးအင်တာနက်လျှော့စျေးပက်ကေ့ချ်။", 60, 5),
        AdCampaign("camp_4", "Yoma Bank Direct Flexi Deposit", "အတိုးနှုန်းမြင့်မြင့်ဖြင့် စုဆောင်းငွေများကို အကျိုးရှိရှိစုဆောင်းပါ။", 180, 5)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("MGuardPrefs", Context.MODE_PRIVATE)
        loadSavedState()

        initializeViews()
        setupListeners()
        setupBottomNav()
        populateAdCampaigns()
        updateUIState()

        startGlobalTick()
    }

    private fun loadSavedState() {
        remainingSeconds = prefs.getInt("remaining_seconds", 7200)
        isPremium = prefs.getBoolean("is_premium", false)
        currentServerName = prefs.getString("current_server", "Singapore - Free High Speed") ?: "Singapore - Free High Speed"
        isPremiumServer = prefs.getBoolean("is_premium_server", false)
    }

    private fun saveState() {
        prefs.edit().apply {
            putInt("remaining_seconds", remainingSeconds)
            putBoolean("is_premium", isPremium)
            putString("current_server", currentServerName)
            putBoolean("is_premium_server", isPremiumServer)
            apply()
        }
    }

    private fun initializeViews() {
        txtHeaderTime = findViewById(R.id.txtHeaderTime)
        contentFrame = findViewById(R.id.contentFrame)
        layoutVpnTab = findViewById(R.id.layoutVpnTab)
        layoutEarnTab = findViewById(R.id.layoutEarnTab)
        layoutPremiumTab = findViewById(R.id.layoutPremiumTab)

        navVpn = findViewById(R.id.navVpn)
        navEarn = findViewById(R.id.navEarn)
        navPremium = findViewById(R.id.navPremium)
        imgNavVpn = findViewById(R.id.imgNavVpn)
        txtNavVpn = findViewById(R.id.txtNavVpn)
        imgNavEarn = findViewById(R.id.imgNavEarn)
        txtNavEarn = findViewById(R.id.txtNavEarn)
        imgNavPremium = findViewById(R.id.imgNavPremium)
        txtNavPremium = findViewById(R.id.txtNavPremium)

        vpnPulseRing = findViewById(R.id.vpnPulseRing)
        btnVpnSwitch = findViewById(R.id.btnVpnSwitch)
        imgVpnPower = findViewById(R.id.imgVpnPower)
        txtVpnPowerStatus = findViewById(R.id.txtVpnPowerStatus)
        txtVpnClickToConnect = findViewById(R.id.txtVpnClickToConnect)
        txtVpnIp = findViewById(R.id.txtVpnIp)
        txtConnectionTimer = findViewById(R.id.txtConnectionTimer)
        txtDownloadSpeed = findViewById(R.id.txtDownloadSpeed)
        txtUploadSpeed = findViewById(R.id.txtUploadSpeed)
        btnServerSelect = findViewById(R.id.btnServerSelect)
        txtCurrentServer = findViewById(R.id.txtCurrentServer)

        txtTabRemainingTime = findViewById(R.id.txtTabRemainingTime)
        progressTimeBar = findViewById(R.id.progressTimeBar)
        adCampaignsContainer = findViewById(R.id.adCampaignsContainer)

        txtPremiumUserStatus = findViewById(R.id.txtPremiumUserStatus)
        btnToggleDevMode = findViewById(R.id.btnToggleDevMode)
        layoutUserPurchaseView = findViewById(R.id.layoutUserPurchaseView)
        layoutDevAdminView = findViewById(R.id.layoutDevAdminView)

        txtKPayNum = findViewById(R.id.txtKPayNum)
        btnCopyKPay = findViewById(R.id.btnCopyKPay)
        editTxId = findViewById(R.id.editTxId)
        editUserPhone = findViewById(R.id.editUserPhone)
        btnSubmitTx = findViewById(R.id.btnSubmitTx)

        txtDevTotalViews = findViewById(R.id.txtDevTotalViews)
        txtDevTotalRevenue = findViewById(R.id.txtDevTotalRevenue)
        layoutDevPendingSubmissions = findViewById(R.id.layoutDevPendingSubmissions)
    }

    private fun setupListeners() {
        btnVpnSwitch.setOnClickListener {
            if (isConnecting) return@setOnClickListener

            if (isConnected) {
                disconnectVPN()
            } else {
                if (isPremiumServer && !isPremium) {
                    showPremiumRestrictionDialog()
                    return@setOnClickListener
                }
                if (remainingSeconds <= 0 && !isPremium) {
                    Toast.makeText(this, "⚠️ သင့်ချိတ်ဆက်ခွင့်အချိန် ကုန်ဆုံးနေပါသည်။", Toast.LENGTH_LONG).show()
                    switchTab("earn")
                    return@setOnClickListener
                }
                connectVPN()
            }
        }

        btnServerSelect.setOnClickListener {
            showServerListDialog()
        }

        btnCopyKPay.setOnClickListener {
            copyToClipboard("KBZPay Account", txtKPayNum.text.toString().split(" ")[0])
        }

        btnSubmitTx.setOnClickListener {
            val txId = editTxId.text.toString().trim()
            val phone = editUserPhone.text.toString().trim()

            if (txId.length < 6 || phone.isEmpty()) {
                Toast.makeText(this, "အချက်အလက်များကို ပြည့်စုံစွာ ဖြည့်စွက်ပါ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val submissionId = "SUB_" + System.currentTimeMillis()
            val submissionStr = "$txId|$phone|$submissionId"
            
            val pendingSet = prefs.getStringSet("pending_submissions", mutableSetOf()) ?: mutableSetOf()
            val updatedSet = HashSet(pendingSet)
            updatedSet.add(submissionStr)
            prefs.edit().putStringSet("pending_submissions", updatedSet).apply()

            editTxId.text.clear()
            editUserPhone.text.clear()

            Toast.makeText(this, "✅ အချက်အလက် တင်သွင်းမှု အောင်မြင်ပါသည်။", Toast.LENGTH_LONG).show()
            populatePendingSubmissionsList()
        }

        btnToggleDevMode.setOnClickListener {
            if (layoutDevAdminView.visibility == View.VISIBLE) {
                layoutDevAdminView.visibility = View.GONE
                layoutUserPurchaseView.visibility = View.VISIBLE
                btnToggleDevMode.text = "Dev Mode ⚙️"
            } else {
                layoutDevAdminView.visibility = View.VISIBLE
                layoutUserPurchaseView.visibility = View.GONE
                btnToggleDevMode.text = "User Mode 👤"
                populateDevStats()
                populatePendingSubmissionsList()
            }
        }
    }

    private fun setupBottomNav() {
        navVpn.setOnClickListener { switchTab("vpn") }
        navEarn.setOnClickListener { switchTab("earn") }
        navPremium.setOnClickListener { switchTab("premium") }
    }

    private fun switchTab(tab: String) {
        layoutVpnTab.visibility = View.GONE
        layoutEarnTab.visibility = View.GONE
        layoutPremiumTab.visibility = View.GONE

        when (tab) {
            "vpn" -> layoutVpnTab.visibility = View.VISIBLE
            "earn" -> {
                layoutEarnTab.visibility = View.VISIBLE
                updateTimeTabUI()
            }
            "premium" -> layoutPremiumTab.visibility = View.VISIBLE
        }
    }

    private fun updateUIState() {
        txtCurrentServer.text = currentServerName
        if (isPremium) {
            txtPremiumUserStatus.text = "👑 Premium Active"
        } else {
            txtPremiumUserStatus.text = "Free User (ကြော်ငြာကြည့်ရှုသူ)"
        }
        updateTimeTabUI()
    }

    private fun updateTimeTabUI() {
        if (isPremium) {
            txtHeaderTime.text = "👑 UNLIMITED"
            txtTabRemainingTime.text = "အကန့်အသတ်မရှိ (Unlimited) 👑"
            progressTimeBar.progress = 100
        } else {
            val hrs = remainingSeconds / 3600
            val mins = (remainingSeconds % 3600) / 60
            val secs = remainingSeconds % 60
            txtHeaderTime.text = String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
            txtTabRemainingTime.text = String.format(Locale.getDefault(), "%02d နာရီ : %02d မိနစ် : %02d စက္ကန့်", hrs, mins, secs)
            progressTimeBar.progress = ((remainingSeconds.toFloat() / 43200f) * 100).toInt().coerceIn(0, 100)
        }
    }

    private fun startGlobalTick() {
        tickRunnable = object : Runnable {
            override fun run() {
                if (isConnected && !isPremium) {
                    if (remainingSeconds > 0) {
                        remainingSeconds--
                        saveState()
                        updateTimeTabUI()

                        if (remainingSeconds == 0) {
                            disconnectVPN()
                            showTimeExpiredDialog()
                        }
                    }
                }

                if (isConnected) {
                    connectionSeconds++
                    val hrs = connectionSeconds / 3600
                    val mins = (connectionSeconds % 3600) / 60
                    val secs = connectionSeconds % 60
                    txtConnectionTimer.text = String.format(Locale.getDefault(), "ချိတ်ဆက်ချိန်: %02d:%02d:%02d", hrs, mins, secs)

                    txtDownloadSpeed.text = String.format(Locale.getDefault(), "%.1f Mbps", Random.nextDouble(12.5, 94.2))
                    txtUploadSpeed.text = String.format(Locale.getDefault(), "%.1f Mbps", Random.nextDouble(5.1, 42.8))
                }

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(tickRunnable!!)
    }

    private fun connectVPN() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, VPN_REQUEST_CODE)
        } else {
            onActivityResult(VPN_REQUEST_CODE, Activity.RESULT_OK, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            isConnecting = true
            txtVpnPowerStatus.text = "ချိတ်ဆက်နေသည်..."

            handler.postDelayed({
                isConnecting = false
                isConnected = true
                connectionSeconds = 0

                txtVpnPowerStatus.text = "ချိတ်ဆက်ပြီးပါပြီ"
                txtVpnClickToConnect.text = "အဆက်ဖြတ်ရန် နှိပ်ပါ"
                txtVpnIp.text = "156.23.45.182"

                val serviceIntent = Intent(this, LocalVpnService::class.java).apply {
                    action = LocalVpnService.ACTION_CONNECT
                }
                startService(serviceIntent)

                Toast.makeText(this, "🔒 VPN ချိတ်ဆက်မှု အောင်မြင်ပါသည်။", Toast.LENGTH_SHORT).show()
            }, 1500)
        }
    }

    private fun disconnectVPN() {
        isConnected = false
        val serviceIntent = Intent(this, LocalVpnService::class.java).apply {
            action = LocalVpnService.ACTION_DISCONNECT
        }
        startService(serviceIntent)

        txtVpnPowerStatus.text = "မချိတ်ဆက်ပါ"
        txtVpnClickToConnect.text = "ချိတ်ဆက်ရန် နှိပ်ပါ"
        txtVpnIp.text = "103.115.112.56 (ရန်ကုန်)"
        txtConnectionTimer.text = "ချိတ်ဆက်ချိန်: 00:00:00"
        txtDownloadSpeed.text = "0.0 Mbps"
        txtUploadSpeed.text = "0.0 Mbps"
    }

    private fun showServerListDialog() {
        val builder = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("ဆာဗာ ရွေးချယ်ပါ")

        val serverNames = servers.map { "${it.flag} ${it.name}" }.toTypedArray()

        builder.setItems(serverNames) { _, which ->
            val selected = servers[which]
            if (selected.isPremium && !isPremium) {
                showPremiumRestrictionDialog()
            } else {
                currentServerName = selected.name
                isPremiumServer = selected.isPremium
                saveState()
                updateUIState()
                if (isConnected) {
                    disconnectVPN()
                    handler.postDelayed({ connectVPN() }, 1000)
                }
            }
        }
        builder.create().show()
    }

    private fun showPremiumRestrictionDialog() {
        AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("Premium Server 🔒")
            .setMessage("ဤဆာဗာသည် Premium သုံးစွဲသူများအတွက်သာ ဖြစ်ပါသည်။")
            .setPositiveButton("ဝယ်ယူရန် သွားမည်") { _, _ -> switchTab("premium") }
            .setNegativeButton("မလိုပါ", null)
            .show()
    }

    private fun showTimeExpiredDialog() {
        AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("ချိတ်ဆက်ခွင့်ကုန်ဆုံးသွားပါပြီ ⏳")
            .setMessage("အချိန်တိုးရန် စပွန်ဆာကြော်ငြာများကို ကြည့်ရှုပေးပါ။")
            .setPositiveButton("အချိန်တိုးမည်") { _, _ -> switchTab("earn") }
            .setNegativeButton("ပိတ်ပါ", null)
            .show()
    }

    private fun populateAdCampaigns() {
        adCampaignsContainer.removeAllViews()

        for (campaign in campaigns) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
            }

            val info = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val title = TextView(this).apply {
                text = campaign.title
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            }
            val tagline = TextView(this).apply {
                text = campaign.tagline
                textSize = 10f
            }

            info.addView(title)
            info.addView(tagline)

            val playBtn = Button(this).apply {
                text = "ကြည့်မည်"
            }
            playBtn.setOnClickListener {
                if (isPremium) return@setOnClickListener
                simulateWatchAd(campaign)
            }

            row.addView(info)
            row.addView(playBtn)
            adCampaignsContainer.addView(row)
        }
    }

    private fun simulateWatchAd(campaign: AdCampaign) {
        val progressText = TextView(this).apply {
            text = "ကြော်ငြာ စတင်ပြသနေပါသည်..."
            setPadding(30, 30, 30, 30)
        }

        val adDialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle(campaign.title)
            .setView(progressText)
            .setCancelable(false)
            .create()

        adDialog.show()

        var secondsLeft = campaign.adDurationSeconds
        val adHandler = Handler(Looper.getMainLooper())
        var adRunnable: Runnable? = null

        adRunnable = object : Runnable {
            override fun run() {
                if (secondsLeft > 0) {
                    progressText.text = "စပွန်ဆာကြော်ငြာ ပြသနေပါသည်...\nစောင့်ဆိုင်းရန်: $secondsLeft စက္ကန့်"
                    secondsLeft--
                    adHandler.postDelayed(this, 1000)
                } else {
                    adDialog.dismiss()
                    remainingSeconds += campaign.rewardMinutes * 60
                    saveState()
                    updateUIState()

                    val totalViews = prefs.getInt("dev_total_views", 148) + 1
                    val totalRev = prefs.getInt("dev_total_revenue", 45000) + 150
                    prefs.edit().apply {
                        putInt("dev_total_views", totalViews)
                        putInt("dev_total_revenue", totalRev)
                        apply()
                    }

                    Toast.makeText(this@MainActivity, "🎉 +${campaign.rewardMinutes} မိနစ် အချိန်တိုးပေးလိုက်ပါပြီ!", Toast.LENGTH_LONG).show()
                }
            }
        }
        adHandler.post(adRunnable)
    }

    private fun populateDevStats() {
        val views = prefs.getInt("dev_total_views", 148)
        val rev = prefs.getInt("dev_total_revenue", 45000)

        txtDevTotalViews.text = "Total Views: $views"
        txtDevTotalRevenue.text = String.format(Locale.getDefault(), "Revenue: %,d MMK", rev)
    }

    private fun populatePendingSubmissionsList() {
        layoutDevPendingSubmissions.removeAllViews()
        val pendingSet = prefs.getStringSet("pending_submissions", mutableSetOf()) ?: mutableSetOf()

        for (sub in pendingSet) {
            val parts = sub.split("|")
            if (parts.size < 3) continue
            val txId = parts[0]
            val phone = parts[1]

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(10, 10, 10, 10)
            }

            val txt = TextView(this).apply {
                text = "Tx: $txId | Phone: $phone"
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val approveBtn = Button(this).apply {
                text = "Approve"
            }
            approveBtn.setOnClickListener {
                isPremium = true
                saveState()
                updateUIState()

                val updatedSet = HashSet(pendingSet)
                updatedSet.remove(sub)
                prefs.edit().putStringSet("pending_submissions", updatedSet).apply()

                Toast.makeText(this@MainActivity, "👑 Premium Granted", Toast.LENGTH_SHORT).show()
                populatePendingSubmissionsList()
            }

            row.addView(txt)
            row.addView(approveBtn)
            layoutDevPendingSubmissions.addView(row)
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(this, "📋 Copy ကူးပြီးပါပြီ။", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        tickRunnable?.let { handler.removeCallbacks(it) }
        super.onDestroy()
    }

    data class VPNServer(val id: String, val name: String, val flag: String, val isPremium: Boolean)
    data class AdCampaign(val id: String, val title: String, val tagline: String, val rewardMinutes: Int, val adDurationSeconds: Int)
}
