package com.ecommers.ecommers.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommers.ecommers.model.Barang;
import com.ecommers.ecommers.model.Keranjang;
import com.ecommers.ecommers.model.UserLogin;
import com.ecommers.ecommers.model.Users;
import com.ecommers.ecommers.repository.BarangRepository;
import com.ecommers.ecommers.repository.KeranjangRepository;
import com.ecommers.ecommers.repository.UserLoginRepository;
import com.ecommers.ecommers.repository.UsersRepository;
import com.ecommers.ecommers.service.UserLoginService;

@Controller
public class KeranjangController {

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private KeranjangRepository keranjangRepository;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private BarangRepository barangRepository;

    @Autowired
    private UsersRepository usersRepository;

     // melihat data di keranjang
  @GetMapping("/keranjang")
  public String keranjang(Model model){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }
    else{
      if(keranjangRepository.findAll().isEmpty()){
        return "user/keranjang-kosong";
      }
      else{

          List<UserLogin> listUser = userLoginRepository.findAll();
          UserLogin userLogin = listUser.get(0);
          Users user =usersRepository.findByGmail(userLogin.getGmail());

          // ambil semuda data keranjang berdasarkan users
          List<Keranjang> keranjang =  keranjangRepository.findByUsers(user);

          // hitung total jumlah barang di keranjang
          int totalJumlah = keranjang.stream().mapToInt(k -> k.getTotalHarga()).sum() ;

          model.addAttribute("users",usersRepository.findAll()); 
          model.addAttribute("barang",barangRepository.findAll()); 
          model.addAttribute("keranjang",keranjangRepository.findByUsers(user));
          model.addAttribute("totalJumlah",totalJumlah); //total jumlah barang
          return "user/keranjang";
      }
    }
  }

  //  menghapus data keranjang yang ada di keranjang
  @GetMapping("/delete-keranjang/{id}")
  public String deleteKeranjang(@PathVariable Integer id, Model model){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }
    else{
      Keranjang keranjang = keranjangRepository.getReferenceById(id);
      Barang barang = barangRepository.findByName(keranjang.getBarang().getName());
      barang.setStok(keranjang.getJumlah() + barang.getStok());
      barangRepository.save(barang);
      keranjangRepository.deleteById(id);
      model.addAttribute("keranjang", keranjangRepository.findAll());
      return "redirect:/keranjang";

    }
  }


  // view update jumlah per barang berdasarkan id keranjang yg di pilih
  @GetMapping("/update-keranjang/{id}")
  public String updateKeranjang(@PathVariable Integer id,Model model){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }
    else{
      Keranjang keranjang = keranjangRepository.getReferenceById(id);
      model.addAttribute("keranjang",keranjang);
      model.addAttribute("barang",barangRepository.findAll());
      return "user/jumlah-update-keranjang";
    }
  }


  // save update jumlah per barang di keranjang berdasarkan id yg di pilih tadi
  @PostMapping("/save-update-keranjang/{id}")
  public String saveUpdateKeranjang(@RequestParam(name = "jumlah") Integer jumlah,@PathVariable Integer id,Model model) {
    // Cek apakah ada data dalam tampungan
    if (userLoginService.userLoginIsEmpty()) {
      return "home/notif-login"; // Notifikasi jika user belum login
    } 
    else {
      // Ambil data keranjang berdasarkan id
      Keranjang keranjang = keranjangRepository.getReferenceById(id);

      // Ambil barang terkait dari keranjang
      Barang barang = keranjang.getBarang();

      // Validasi stok, jika stok kurang dari jumlah yang diminta
      if (barang.getStok() <= 0 || barang.getStok() - jumlah <= 0) {
        return "user/stok-kurang"; // Notifikasi stok tidak cukup
      }
       else {
        // tambah stok dulu ke barang agar pas di update stok nya tidak ikut ter hapus 
         barang.setStok(keranjang.getJumlah() + barang.getStok());
         barangRepository.save(barang);
      

        // Jika barang sudah ada, update jumlahnya
        keranjang.setJumlah(jumlah);
        keranjang.setTotalHarga(jumlah * barang.getHarga()); //hitung total harga
        keranjangRepository.save(keranjang); // Simpan update jumlah




        // Kurangi stok barang sesuai dengan jumlah yang dibeli
        barang.setStok(barang.getStok() - jumlah);
        barangRepository.save(barang); // Simpan perubahan stok

        return "redirect:/keranjang"; // Arahkan kembali ke halaman keranjang
      }
    }
  }





  // Untuk melempar ke view jumlah barang berdasarkan id yg di tambah kan ke keranjang
  @GetMapping("/jumlah-barang/{id}")
  public String jumlahkeranjang(@PathVariable Integer id,Model model){
    if(userLoginService.userLoginIsEmpty()){
        return "home/notif-login";
    }
    else{
        Barang barang = barangRepository.getReferenceById(id);
        model.addAttribute("barang",barang);
        return "user/jumlah-barang";
    }
}


// Menyimpan jumlah barang yg di tambahkan ke keranjang
  @PostMapping("/save-keranjang/{id}")
  public String saveKeranjang(@RequestParam(name = "jumlah") Integer jumlah,@PathVariable Integer id,Model model) {
    // Cek apakah ada data dalam tampungan
    if (userLoginService.userLoginIsEmpty()) {
      return "home/notif-login"; // Notifikasi jika user belum login
    } 
    else {
      // Ambil data barang berdasarkan id
      Barang barang = barangRepository.getReferenceById(id);

      // Validasi stok, jika stok kurang dari jumlah yang diminta
      if (barang.getStok() <= 0 || barang.getStok() - jumlah < 0) {
        return "user/stok-kurang"; // Notifikasi stok tidak cukup
      } else {
        // Ambil akun yang sedang login
        List<UserLogin> tamp = userLoginRepository.findAll();
        UserLogin tampungan = tamp.get(0); // Ambil data pertama dari tampungan
        Users user = new Users();
        user.setGmail(tampungan.getGmail());
        user = usersRepository.findByGmail(user.getGmail()); // Cari user berdasarkan Gmail

        
        // Cek apakah barang sudah ada di keranjang user
        Keranjang keranjang = keranjangRepository.findByUsersAndBarang(user,barang);

        if (keranjang != null) {
          // Jika barang sudah ada, update jumlahnya
          keranjang.setJumlah(keranjang.getJumlah() + jumlah);
          keranjang.setTotalHarga(jumlah * barang.getHarga()); //hitung total harga
          keranjangRepository.save(keranjang); // Simpan update jumlah

        } 
        else {
          // Jika barang belum ada, buatkan entri baru
          keranjang = new Keranjang();
          keranjang.setUsers(user);
          keranjang.setJumlah(jumlah);
          keranjang.setBarang(barang);
          keranjang.setTotalHarga(jumlah * barang.getHarga()); //hitung total harga
          // System.out.println("Total harga :" + keranjang.getTotalHarga());
          keranjangRepository.save(keranjang); // Simpan entri baru
        }

        // Kurangi stok barang sesuai dengan jumlah yang dibeli
        barang.setStok(barang.getStok() - jumlah);
        barangRepository.save(barang); // Simpan perubahan stok

        return "redirect:/"; // Arahkan kembali ke halaman utama
      }
    }
  }

}
