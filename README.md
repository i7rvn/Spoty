# Spoty — دليل الإعداد والنشر الكامل

**الحقيقة قبل ما تبدا:** هذا الزيب (`spoty-source-complete.zip`) عمرو ما تبنى ولا تجرب. كل خطوة تحت مكتوب عليها confidence label — Confirmed (متأكد فعليا)، Likely (احتمال قوي بلا تجربة)، Speculation (تخمين بلا دليل). الملف الأصلي ديال المشروع (VIVI Music) محفوظ فـ `README_ORIGINAL_VIVI.md` إيلا حبيت تشوفو.

---

## 1. المتطلبات (Requirements)

| الحاجة | ليه خاصك | Confidence |
|---|---|---|
| Android Studio (Ladybug 2024.2 أو أحدث) | فتح، بناء، rename آمن | خاصك: Confirmed / الإصدار المضبوط: Speculation |
| JDK 21 | مكتوب فـ `.github/workflows/*.yml` أنو هو المستعمل | Confirmed |
| Android SDK + Build-Tools 35.0.0 | مكتوب فـ `nightly.yml` للتوقيع | Confirmed |
| حساب GitHub | لخلق repo `i7rvn/Spoty` وزيادة secrets | Confirmed |
| Keystore ديالك (.jks) | لتوقيع الـ APK — ماكاينش جاهز، خاصك تولدو | Confirmed |
| حساب Last.fm Developer | لـ API key/secret مستعملين فالكود | Confirmed |
| أيقونة التطبيق (PNG/SVG) | ماكاينة فالزيب | Confirmed أنها ناقصة |
| إنترنت مستقر | Gradle كايحمل dependencies من Google/Maven أول مرة | Confirmed |

---

## 2. فك الضغط والفتح فـ Android Studio

```bash
unzip spoty-source-complete.zip
cd spoty-source
rm -f "et --hard 33c82f9"   # ملف زبل جا من الريبو الأصلي، امسحو إيلا لقيتو
```

فـ Android Studio: `File → Open` → اختار فولدر `spoty-source` (فيه `settings.gradle.kts` فالروت).

Android Studio غايبدا **Gradle Sync** أوتوماتيك. هاذي أول محطة خطر حقيقية — **Likely** تعدي بلا مشاكل (rename كان ميكانيكي وبسيط)، **ماشي Confirmed** حيت ما جربتش هنا.

---

## 3. مشاكل محتملة فالـ Sync (Speculation، بلا دليل مباشر)

- `Unresolved reference` → غالبا حتة نسيتها فالـ rename، دور عليها بالاسم فـ error message
- مشكل مرتبط بـ `google-services.json` (flavor GMS) → ما تحققتش وشنو وضعو فالريبو الأصلي، إيلا ناقص جيبو من Firebase Console بحسابك
- Gradle ما قدرش يحمل dependencies → تأكد الشبكة ماشي محدودة (خاصها `dl.google.com`, `repo.maven.apache.org`, `plugins.gradle.org`)

---

## 4. البناء المحلي (قبل أي push)

```bash
./gradlew assembleDebug --no-daemon
```

**لا تكمل للخطوة الجاية إيلا هذا نجح.** push ديال كود ما كايبنيش موخر شي حاجة.

---

## 5. الرينيمات الباقية (اختياري، ديرها بـ Android Studio ماشي sed)

120 ملف مازال فيهم "vivimusic" (root project name، module `:vivimusiccanvas`، functions/constants). ما بدلتهمش أنا لأن sed بدل نص بلا يفهم السياق — هذا سبب bug طلع عندي قبل (substring collision خلا `vivimusiccanvas` تولي `updater_libcanvas` غلط).

الطريقة الآمنة:
1. `settings.gradle.kts` سطر `rootProject.name = "vivimusic"` → بدلها يدوي
2. Module `vivimusiccanvas` فـ Project panel → كليك يمين → `Refactor → Rename` → كايبدل الديركتوري + `include()` + كل `project()` refs أوتوماتيك
3. أي function/constant فيها "vivimusic" → `Shift+F6` → كايبدلها فكل الاستعمالات

---

## 6. الأيقونة (ناقصة، Confirmed)

ماعنديش tool يخرج adaptive icon assets حقيقية. جيب تصميمك، بعدين:
`File → New → Image Asset` → Adaptive Icon → حط الملف ديالك.

---

## 7. خلق الـ Repo على GitHub

من `github.com/new`: اسم `Spoty` تحت حساب `i7rvn`، **بلا** README/.gitignore/license عند الخلق.

```bash
git init
git add .
git commit -m "Initial Spoty commit (rebranded from vivi-music)"
git branch -M main
git remote add origin https://github.com/i7rvn/Spoty.git
git push -u origin main
```

---

## 8. الـ Secrets — منين تجيبهم بالضبط

`Settings → Secrets and variables → Actions → New repository secret` فريبو `Spoty`.

**ماكاين حتى "Variables" مستعملة** فهاذ الريبو، غير Secrets (Confirmed بـ grep على الـ workflows).

| Secret | منين تجيبها | Confidence |
|---|---|---|
| `LASTFM_API_KEY` | https://www.last.fm/api/account/create — سجل، خود API Key | Confirmed مطلوبة |
| `LASTFM_SECRET` | نفس الصفحة، Shared Secret | Confirmed |
| `KEYSTORE` | base64 ديال ملف `.jks` تولدو بروحك | Confirmed |
| `KEY_ALIAS` | الاسم لي تختارو فأمر `keytool` | Confirmed |
| `KEYSTORE_PASSWORD` | password دير فأمر `keytool` | Confirmed |
| `KEY_PASSWORD` | password ديال الـ key | Confirmed |
| `GITHUB_TOKEN` | ما خاصكش تزيدو — auto-generated | Confirmed |

### توليد الـ Keystore

```bash
keytool -genkeypair -v -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias spoty-key

base64 -w 0 my-release-key.jks > keystore_base64.txt
```

Content ديال `keystore_base64.txt` هو لي تحطو فـ secret `KEYSTORE`.

**تحذير (Confirmed risk):** هذا الـ keystore غايبقى مربوط بالتطبيق للأبد. إيلا خسرتيه، أي تحديث جاي ما يقدرش يتنصب فوق نسخة قديمة عند المستخدمين. احفظو فبلاصة آمنة منفصلة، ماشي غير فـ GitHub secret.

---

## 9. النشر (Release)

```bash
nano app/build.gradle.kts   # versionCode / versionName إيلا بغيتي تبدلهم

git add app/build.gradle.kts
git commit -m "Bump version"
git push origin main

git tag v1.0.0
git push origin v1.0.0
```

شوف: `https://github.com/i7rvn/Spoty/actions` — إيلا نجح، الـ APK الموقع غايكون فـ `https://github.com/i7rvn/Spoty/releases`.

---

## 10. جدول الخطوات (من الصفر للتطبيق الجاهز)

| # | الخطوة | نتيجة متوقعة |
|---|---|---|
| 1 | فك الضغط + مسح الملف الزبل | فولدر نظيف |
| 2 | فتح فـ Android Studio + Sync | نجح → كمل. فشل → رجع لقسم 3 |
| 3 | `assembleDebug` محلي | APK debug يخدم |
| 4 | (اختياري) رينيمات إضافية | كود نظيف 100% |
| 5 | أيقونة | شكل نهائي |
| 6 | خلق repo + push | كود على GitHub |
| 7 | زيادة 6 secrets | CI قادر يوقع |
| 8 | tag + push | APK موقع رسمي على Releases |

---

## ما بقاش تحت مسؤوليتي

- ماشي مضمون البناء يعدي بلا errors (Likely غير كذا)
- ماعنديش طريقة نتأكد الـ APK كايخدم فعليا على تلفون حقيقي
- ماعنديش دليل أنو repo `i7rvn/Spoty` موجود فعليا — خاصك تخلقو بروحك
