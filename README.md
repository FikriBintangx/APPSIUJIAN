# 🎓 SiUjian (AntiExam System)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![Neo-Brutalism](https://img.shields.io/badge/Design-Neo--Brutalism-black?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

> **"Ujian anti curang curang club!"** 🚀

**SiUjian** bukan aplikasi ujian biasa. Ini adalah platform ujian berbasis desktop dengan desain **Neo-Brutalism** yang _eye-catching_ dan fitur keamanan yang bikin mahasiswa mikir dua kali buat nyontek. Dibuat khusus buat dosen gaul dan pengawas tegas!

---

## 🔥 Fitur Kece

### 🎨 Neo-Brutalism UI

Tampilan antarmuka yang bold, kontras tinggi, dan estetik parah. Ga bosenin kayak aplikasi ujian jaman purba. Desain ini bikin pengalaman make aplikasi jadi _fresh_!

### 👥 Multi-Role System

- **🛠️ Admin Kampus**: Bos besar. Atur jadwal, bikin shell ujian, import data mahasiswa via CSV. Tampilan dashboardnya rapi jali.
- **👨‍🏫 Dosen**: Kreator soal. Input soal ala **Google Form** (beneran mirip kartu-kartunya!), set kunci jawaban, dan liat hasil ujian lengkap. Dosen friendly banget!
- **👀 Pengawas**: Si Mata Elang. Generate **Token Ujian** (kode rahasia), live monitoring status mahasiswa, dan punya tombol "Nuklir" buat _Freeze_ atau _Kick_ peserta yang mencurigakan.
- **🎓 Mahasiswa**: Peserta ujian. Login simpel, kerjain soal mode fokus, dan UI-nya enak dipandang.

### 🛡️ Anti-Cheat Mechanism

- **Live Monitoring**: Pengawas bisa liat real-time siapa yang lagi ngerjain.
- **Session Locking**: Peserta bandel bisa dibekukan (Freeze) dari server pengawas.
- **Token Access**: Ujian cuma bisa dimulai kalo punya Token 6-digit yang digenerate pengawas.
- **Secure Access**: Role-based login yang aman.

---

## 📸 Sneak Peek

| Dosen Dashboard (Neo Style) | Editor Soal (G-Form Style) |
| :-------------------------: | :------------------------: |
|  _(Screenshot Dashboard)_   |   _(Screenshot Editor)_    |

| Live Monitoring Pengawas  |   Token Generator    |
| :-----------------------: | :------------------: |
| _(Screenshot Monitoring)_ | _(Screenshot Token)_ |

---

## 🚀 Cara Jalanin (How to Run)

Pastikan lo udah install **Java JDK 8+** dan **Apache Ant** (atau pake NetBeans biar gampang).

1.  **Clone Repo ini**:

    ```bash
    git clone https://github.com/FikriBintangx/SiUjian.git
    cd SiUjian
    ```

2.  **Build & Run pake Ant**:

    ```bash
    ant clean jar run
    ```

    _Atau kalo pake NetBeans, tinggal klik kanan project -> Run. Sat set wat wet._

3.  **Login Default (Buat Ngetes)**:
    - **Admin**: `admin` / `admin123`
    - **Dosen**: `dosen` / `dosen123`
    - **Pengawas**: `pengawas` / `pengawas123`
    - **Mahasiswa**: `Mahasiswa Test` / `12345678`

---

## 🛠️ Teknologi

Dibuat dengan cinta dan kopi, menggunakan:

- **Language**: Java (JDK 17 recommended)
- **GUI Framework**: Java Swing (Custom Components & Layouts)
- **Design Pattern**: MVC (Model-View-Controller) - ish
- **Database**: SQLite (Embedded, ringkes)
- **External Libs**:
  - `sqlite-jdbc`
  - `flatlaf`

---

## 👨‍💻 Author

**Fikri Bintang**
_Coding with style._

---

_Made with ❤️ for education._
