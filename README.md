# Tucil2_10122078
# Kompresi Gambar dengan Metode Quadtree
## Penjelasan Singkat Program
Program ini mengimplementasikan kompresi gambar menggunakan algoritma Divide and Conquer berbasis Quadtree. Gambar diolah dengan cara membagi secara rekursif menjadi blok-blok yang lebih kecil berdasarkan perhitungan error (misalnya, Variance, MAD, Max Pixel Difference, atau Entropy). Jika blok sudah homogen (error di bawah threshold) atau mencapai ukuran minimum, blok tersebut tidak dibagi lebih lanjut dan nilai rata-rata warnanya dihitung. Selanjutnya, struktur Quadtree yang terbentuk digunakan untuk merekonstruksi gambar yang telah dikompresi. Program juga menyediakan fitur bonus berupa penyesuaian threshold otomatis, penghitungan SSIM, serta pembuatan GIF animasi yang memvisualisasikan proses pembentukan Quadtree secara level-by-level.
## Requirement dan Instalasi
- **Java JDK**: Versi minimal JDK 8 atau lebih tinggi.
- **IDE atau Text Editor**: Misalnya IntelliJ IDEA, Eclispe, atau VS Code.
- **Terminal/Command Prompt**: Untuk kompilasi dan menjalankan program.
Pastikan Java telah tertinstal dengan benar, dapat diuji dengan perintah `java -version`.
## Struktur Proyek
```plaintext
Tucil2/
├── bin/
│   ├── Bonus$CompressionUtils.class
│   ├── Bonus$GifSequenceWriter.class
│   ├── Bonus.class
│   ├── ErrorMeasurementMethods.class
│   ├── Main.class
│   ├── QuadtreeCompressor.class
│   └── QuadtreeNode.class
├── doc/
│   └── Tucil2_K2_10122078_Ghaisan Zaki Pratama.pdf
├── src/
│   ├── Main.java
│   ├── ErrorMeasurementMethods.java
│   ├── QuadtreeNode.java
│   ├── QuadtreeCompressor.java
│   └── Bonus.java
├── test/
│   ├── Gambar1.JPG
│   ├── Gambar2.PNG
│   ├── Gambar3.JPEG
│   ├── GifOutput1.gif
│   ├── GifOutput2.gif
│   ├── GifOutput3.gif
│   ├── GifOutput4.gif
│   ├── GifOutput5.gif
│   ├── GifOutput6.gif
│   ├── Output1.JPG
│   ├── Output2.PNG
│   ├── Output3.JPEG
│   └── ...
└── README.md
```

**Catatan Penting Mengenai File Input dan Output**:
- **Input File**:
  Masukkan file gambar ke dalam folder `test` dengan nama lengkap (nama.ekstensi) misalnya `Gambar1.JPG`, `Foto2.PNG`, atau `Image3.JPEG`.
- **Output File**:
  Saat menjalankan program, user akan diminta memasukkan nama file output. Pastikan file output mencakup ekstensi yang sesuai (misalnya `heem1.JPG`). File output akan secara otomatis disimpan di folder `test`.
- **Output GIF**:
  Jika ingin menghasilkan GIF, masukkan nama file dengan ekstensi .gif (misalnya `output1.gif`). File GIF juga akan disimpan otomatis ke folder `test`.
## Cara Mengompilasi Program
Buka terminal pada direktori root repository (misalnya, direktori Tucil2) dan jalankan perintah berikut untuk mengompilasi semua file Java sekaligus dan menyimpannya ke folder `bin`:
`javac -d bin src/*.java`
Pastikan folder `bin` sudah ada atau akan dibuat secara otomatis oleh compiler.
## Cara Menjalankan dan Menggunakan Program
Setelah kompilasi selesai, jalankan program utama (Main) dengan perintah:
`java -cp bin Main`
Program akan meminta beberapa input melalui terminal, misalnya:
- Nama file gambar (misalnya: `Gambar1.JPG`)
- Pilihan metode perhitungan error (1 untuk Variance, 2 untuk MAD, dst.)
- Nilai ambang batas (threshold)
- Ukuran blok minimum
- Target persentase kompresi (nilai antara 0 dan 1; jika 0, mode otomatis dinonaktifkan)
- Nama file output untuk gambar hasil kompresi (harus disertai ekstensi, misalnya: `heem1.JPG`)
- Nama file output untuk GIF (opsional, misalnya: `output.gif`)

Pastikan user meletakkan file input di folder `test`. Output yang dihasilkan akan muncul di folder `test` dengan nama sesuai input yang user berikan (termasuk ekstensi file).
## Author / Identitas Pembuat
- Nama : Ghaisan Zaki Pratama
- NIM : 10122078
