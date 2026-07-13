# 🛡️ MGuard VPN - Android Application

MGuard VPN သည် အသုံးပြုသူများ၏ အင်တာနက်လုံခြုံရေးကို အပြည့်အဝ ကာကွယ်ပေးနိုင်ပြီး မြန်နှုန်းမြင့် ဆာဗာများကို အခမဲ့ အသုံးပြုနိုင်ရန် တည်ဆောက်ထားသော Android VPN Application တစ်ခု ဖြစ်သည်။ ၎င်းတွင် အသုံးပြုသူများအတွက် အချိန်တိုးမြှင့်နိုင်မည့် ကြော်ငြာစနစ် (Ad Rewards) နှင့် Developer/Admin များအတွက် သီးသန့် Dashboard Panel တို့ ပါဝင်ဖွဲ့စည်းထားသည်။

---

## ✨ Features (အဓိက ပါဝင်သော လုပ်ဆောင်ချက်များ)

*   **Secure VPN Connection:** စိတ်ချရသော လုံခြုံရေးစနစ်ဖြင့် အင်တာနက်ကို လွတ်လပ်စွာ ကျော်ဖြတ်အသုံးပြုနိုင်ခြင်း။
*   **Ad Rewards System:** ဗီဒီယိုကြော်ငြာများ ကြည့်ရှုရုံဖြင့် VPN အသုံးပြုခွင့် သက်တမ်း/အချိန်ကို အလွယ်တကူ တိုးမြှင့်ရယူနိုင်ခြင်း။
*   **Real-time Speed Monitor:** အက်ပ်အတွင်း တိုက်ရိုက်ကြည့်ရှုနိုင်မည့် Download နှင့် Upload မြန်နှုန်းပြစနစ်။
*   **Premium Server Purchase:** KBZPay အသုံးပြု၍ Premium Server များ ဝယ်ယူနိုင်ခြင်းနှင့် TxID အတည်ပြုချက် တင်သွင်းနိုင်ခြင်း။
*   **Developer⚙️ Dashboard:** စုစုပေါင်း ကြည့်ရှုမှုနှုန်း (Total Views) နှင့် ရရှိသော ဝင်ငွေ (Revenue) တို့ကို စစ်ဆေးနိုင်သည့် ကွက်မျက်နှာပြင်။
*   **Modern Dark UI:** မျက်စိအေးပြီး အသုံးပြုရလွယ်ကူသော Dark Mode ဒီဇိုင်း။

---

## 🏗️ Project Architecture & Tech Stack

*   **OS Target:** Android 5.0 (API 21) မှ Android 14 (API 34) အထိ။
*   **Language:** Kotlin / Java
*   **Build System:** Gradle 8.4
*   **CI/CD Pipeline:** GitHub Actions (အလိုအလျောက် APK တည်ဆောက်ပေးသည့် စနစ်ပါဝင်သည်)

---

## 🚀 GitHub Actions CI/CD (အသုံးပြုပုံ)

ဤပရောဂျက်တွင် GitHub Actions ပါဝင်ပြီးသား ဖြစ်သောကြောင့် သင်၏ `main`, `master` သို့မဟုတ် `develop` branch များသို့ ကုဒ်များ `git push` လုပ်လိုက်သည်နှင့် လုပ်ငန်းစဉ်များကို အလိုအလျောက် ပတ်ပေးသွားမည်ဖြစ်သည်။

### အလိုအလျောက် ဆောင်ရွက်ပေးမည့် အဆင့်များ:
1.  Java JDK 17 နှင့် Gradle Wrapper ပတ်ဝန်းကျင်ကို အသင့်ပြင်ဆင်ပေးခြင်း။
2.  ပျောက်ဆုံးနေသော Android Resource (Icons, Themes, Colors) များကို Pipeline ပေါ်တွင် အလိုအလျောက် ဖြည့်ဆောက်ပေးခြင်း။
3.  Java နှင့် Kotlin JVM Target Compatibility ကို Version 17 သို့ အတင်းညှိပေးခြင်း။
4.  အမှားအယွင်းမရှိ အောင်မြင်စွာ Build လုပ်ပြီးနောက် `app-debug.apk` ဖိုင်ကို Artifact အနေဖြင့် ထုတ်ပေးခြင်း။

---

## 🛠️ Local တွင် စမ်းသပ်ပတ်ကြည့်နည်း

ပရောဂျက်ကို မိမိ၏ ကွန်ပျူတာထဲတွင် Build လုပ်လိုပါက အောက်ပါအတိုင်း ဆောင်ရွက်နိုင်သည်:

```bash
# ၁။ ပရောဂျက်ကို Clone ဖတ်ပါ
git clone [https://github.com/YOUR_USERNAME/MGUARD-VPN.git](https://github.com/YOUR_USERNAME/MGUARD-VPN.git)

# ၂။ ပရောဂျက် ဖိုဒါထဲသို့ ဝင်ပါ
cd MGUARD-VPN

# ၃။ Gradle Permissions ပေးပါ (Linux/Mac အတွက်သာ)
chmod +x gradlew

# ၄။ Debug APK ကို ဆောက်ပါ
./gradlew assembleDebug
