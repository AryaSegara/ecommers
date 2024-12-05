package com.ecommers.ecommers.controller;

import com.ecommers.ecommers.constans.OrderStatus;
import com.ecommers.ecommers.model.Admin;
import com.ecommers.ecommers.model.Barang;
import com.ecommers.ecommers.model.Customer;
import com.ecommers.ecommers.model.Keranjang;
import com.ecommers.ecommers.model.OrderAddress;
import com.ecommers.ecommers.model.OrderRequest;
import com.ecommers.ecommers.model.ProductOrder;
import com.ecommers.ecommers.model.UserLogin;
import com.ecommers.ecommers.model.Users;
import com.ecommers.ecommers.repository.AdminRepository;
import com.ecommers.ecommers.repository.BarangRepository;
import com.ecommers.ecommers.repository.CategoryRepository;
import com.ecommers.ecommers.repository.CustomerRepository;
import com.ecommers.ecommers.repository.KeranjangRepository;
import com.ecommers.ecommers.repository.ProductOrderRepository;
import com.ecommers.ecommers.repository.UserLoginRepository;
import com.ecommers.ecommers.repository.UsersRepository;
import com.ecommers.ecommers.service.UserLoginService;
import com.ecommers.ecommers.service.UsersService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
  private static UserLogin currentUser; // menyimpan data user yang sedang login

  @Autowired
  private UserLoginService userLoginService;

  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private UsersService usersService;

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private BarangRepository barangRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private KeranjangRepository keranjangRepository;

  @Autowired
  private ProductOrderRepository productOrderRepository;

  @Autowired
  private CustomerRepository customerRepository; 

  @Autowired
  private UserLoginRepository userLoginRepository;



  // Home
  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("barang", barangRepository.findAll());
    model.addAttribute("category", categoryRepository.findAll());
    
    if (userLoginService.userLoginIsEmpty() ){
      Integer id = 1;
      // untuk membuat akun admin nya , jadi jika akun admin belum ada , maka akan masuk ke if di bawah ini dan setKeterangan dan setPin nya
      if(adminRepository.findById(id).isEmpty()){
          Admin admin = new Admin();
          admin.setKeterangan("belum login");
          admin.setPin("123456789");
          adminRepository.save(admin);
      }
      return "home/index";
    } else {
      return "user/beranda";
    }
  }

  // tampilan Registrasi
  @GetMapping("/register")
  public String register(Model model) {
    if (userLoginService.userLoginIsEmpty()) {
      Users users = new Users();
      model.addAttribute("regis", users);
      return "home/register";
    } else {
      return "user/beranda";
    }
  }


  // Cek Registrasi
  @PostMapping("/cek-regis")
  public String cekRegis(@ModelAttribute("regis") Users users,@RequestParam(name = "gmail") String gmail,Model model) {
    // cek apakah tampungannya kosong
    if (userLoginService.userLoginIsEmpty()) {
      // cek apakah gmail sudah ada atau belum
      if (usersRepository.findGmailByGmail(gmail).isEmpty()) {
        // validasi nama
        String name = users.getName();
        if (name.length() < 3 || name.length() > 20) {
          model.addAttribute(
            "nameError",
            "Nama minimal 3 karakter dan maksimal 20 karakter"
          );
        } else {
          // Validasi hanya huruf a-z atau A-Z
          Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
          Matcher matcher = pattern.matcher(name); //Membandingkan nama dengan pola regex untuk memeriksa apakah hanya berisi karakter yang diizinkan.

          // hitung jumlah spasinya
           //chars disini untuk Menghasilkan kode Unicode untuk setiap karakter dalam string sebagai int.
           // filter disini menyaring karakter yg merupakan spasi
          long spaceJumlah = name.chars().filter(ch -> (char) ch == ' ').count();


          // jika nama mengandung karakter ilegal maka pesan error
          if (!matcher.matches()) {
              model.addAttribute(
                  "nameError",
                  "Nama hanya boleh berisi huruf a-z atau A-Z"
              );
          }
          // validasi jika jumlah spasi lebih dari 2
          else if(spaceJumlah > 2){
            model.addAttribute(
                "nameError",
                "Nama hanya boleh memiliki maksimal 2 spasi di dalam inputan"
            );
          }
      }


      // simpan gmail di variabel string email
      String email = users.getGmail();
      if(email.length() < 14 || email.length() > 28) {
        model.addAttribute(
          "emailError",
          "Email minimal 4 karakter diawal sebelum @ dan jumlah maksimal 28 karakter dan sudah di hitung @gmail.com"
        );
      }else{

        // Validasi format Gmail hanya boleh inputan sebelum @ itu huruf, dan angka boleh setelah 4 huruf pertama
        // pattern dan matcher adalah kelas untuk regular expression(regex) yg untuk mencocokkan ,mencari dan memanipulasi string berdasarkan yg di tentukan
        // parttern untuk menyimpan pola regex yang ingin anda gunakan dalam pencocokan string
        // compile(String regex) digunakan untuk mengkompilasi string regex menjadi objek Pattern
        // matcher untuk mencocokkan pola regex dengan string

        // jadi pola regex disini harus diawali huruf yg minimal 4 yang diikuti harus 2 angka dan diakhiri @gmail.com
        Pattern emailPattern = Pattern.compile("^[a-zA-Z]{4,}[0-9]{2,}@gmail\\.com$"); 
        Matcher emailMatcher = emailPattern.matcher(email); //untuk mencocok kan pola dengan inputan gmail
   
  
        //jika inputan gmail ilegal maka pesan error
        if (!emailMatcher.matches()) {
              model.addAttribute(
                "emailError",
                "Format email tidak valid. Contoh: user12@gmail.com"
              );
        }
      }


        // validasi password
        String password = users.getPassword();
        if (password.length() < 8 || password.length() > 14) {
          model.addAttribute(
            "passwordError",
            "Password minimal 8 karakter dan maksimal 14 karakter"
          );
        }

        // jika ada validasi error , maka kembalikan ke halaman register
        if (
          model.containsAttribute("nameError") ||
          model.containsAttribute("emailError") ||
          model.containsAttribute("passwordError")
        ) {
          return "home/register";
        }

        // Jika validasi berhasil, simpan data login
        users.setGmail(gmail);
        users.setSaldo(0);
        usersService.save(users);
        return "home/register-berhasil";
      }
      return "home/register-gagal"; //gmail sudah terdaftar
    }
    return "user/beranda";
  }


  // Login ke view login-user
  @GetMapping("/login-user")
  public String login(Model model) {
    if (userLoginService.userLoginIsEmpty()) {
      return "home/login-user";
    } else {
      return "user/beranda";
    }
  }

  // mengecek ketika ingin login apakah sesuai dengan database atau tidak
  @PostMapping("/cek-login")
  public String cekLogin(@RequestParam(name = "username") String username,@RequestParam(name = "password") String password,Model model) {
    ArrayList<Users> users = new ArrayList<>();
    users.addAll(usersService.getAllUsers());
    int index = -1;

    // tampilkan semua users yg ada di database
    for (int i = 0; i < users.size(); i++) {
      //kemudian cek gmail dan passwordnya dari inputan ke database di cek apakah sesuai atau tidak
      if (username.equals(users.get(i).getGmail()) && password.equals(users.get(i).getPassword())) {
        index = i;
      }
    }

    if (index == -1) {
      return "home/login-user-gagal";
    } else {

      // jika sesuai maka akan di ambil data yang sesuai tadi
      Users user = usersRepository.findByGmail(username);

      UserLogin userLogin = new UserLogin();

      // kemudian set ke entitas userLogin data tadi yg di entitas users 
      userLogin.setGmail(user.getGmail());
      userLogin.setName(user.getName());
      userLogin.setPassword(user.getPassword());
      userLogin.setSaldo(user.getSaldo());

      // dan di sini ada setCurrentUser yang akan mengambil data tadi untuk di simpan ke variabel global yang bernama currentUSer
      UserController.setCurrentUser(userLogin); 
      userLoginService.save(userLogin);
      return "redirect:/";
    }
  }

  // Log out atau menghapus data user yang terakhir login di entitas UserLogin
  @GetMapping("/keluar-user")
  public String keluarUser() {
    if (userLoginService.userLoginIsEmpty()) {
      return "home/notif-login";
    } else {
      userLoginService.delete();
      return "redirect:/";
    }
  }



  // untuk ke view my-profile
  @GetMapping("/profile")
  public String profile(Model model){
    if (userLoginService.userLoginIsEmpty()) {
      return "home/notif-login";
    } else {
      Users users = usersRepository.findByGmail(currentUser.getGmail());
      model.addAttribute("users", users);
      return "user/my-profile";
    }
  }


  // untuk mengupdate akun pengguna
  @PostMapping("/update-profile")
  public String updateProfile(Users updateUser, Model model) {
      // Cek apakah user login kosong
      if (userLoginService.userLoginIsEmpty()) {
          return "home/notif-login";
      } else {
          // Mendapatkan pengguna yang sedang login
          Users sedangLogin = usersRepository.findByGmail(currentUser.getGmail());
  
          // Validasi nama
          String name = updateUser.getName();
          if (name.length() < 3 || name.length() > 20) {
              model.addAttribute("nameError", "Nama minimal 3 karakter dan maksimal 20 karakter");
          } else {
              // Validasi nama hanya berisi huruf a-z atau A-Z
              Pattern namePattern = Pattern.compile("^[a-zA-Z ]+$");
              Matcher nameMatcher = namePattern.matcher(name);
  
              long spaceCount = name.chars().filter(ch -> (char) ch == ' ').count();
  
              if (!nameMatcher.matches()) {
                  model.addAttribute("nameError", "Nama hanya boleh berisi huruf a-z atau A-Z");
              } else if (spaceCount > 2) {
                  model.addAttribute("nameError", "Nama hanya boleh memiliki maksimal 2 spasi di dalam inputan");
              }
          }


          // validasi email
          String gmail = updateUser.getGmail();
          if(gmail.length() < 14 || gmail.length() > 28){
            model.addAttribute(
              "emailError",
              "Email minimal 4 karakter diawal sebelum @ dan jumlah maksimal 28 karakter dan sudah di hitung @gmail.com"
            );
          }
          else{

            Pattern emailPattern = Pattern.compile("^[a-zA-Z]{4,}[0-9]{2,}@gmail\\.com$");
            Matcher emailMatcher = emailPattern.matcher(gmail);
  
            if (!emailMatcher.matches()) {
                model.addAttribute("emailError", "Format email tidak valid. Contoh: user@gmail.com");
            }
          }
          
  
          // Validasi password
          String password = updateUser.getPassword();
          if (password.length() < 8 || password.length() > 14) {
              model.addAttribute("passwordError", "Password minimal 8 karakter dan maksimal 14 karakter");
          }
  
          // Jika ada validasi error, kembali ke halaman update profile
          if (model.containsAttribute("nameError") || 
              model.containsAttribute("emailError") || 
              model.containsAttribute("passwordError")) {
              model.addAttribute("users", sedangLogin); // Kirim data user untuk ditampilkan di form
              return "user/my-profile";
          }
  
          // Memperbarui data pengguna
          sedangLogin.setName(updateUser.getName());
          sedangLogin.setGmail(updateUser.getGmail());
          sedangLogin.setPassword(updateUser.getPassword());
  
          // Saldo tetap
          sedangLogin.setSaldo(usersRepository.findByGmail(currentUser.getGmail()).getSaldo());
  
          // Menyimpan perubahan ke database
          usersRepository.save(sedangLogin);
  
          // Menghapus data login dan memaksa login ulang
          userLoginRepository.deleteAll();
  
          return "redirect:/";
      }
  }
  

  // untuk view order
  @GetMapping("/order")
  public String orderPage(Model model){

    if (userLoginService.userLoginIsEmpty()) {
      return "home/notif-login";
    } else {

      // ambil data keranjang pengguna yang sedang login
      List<Keranjang> keranjang = keranjangRepository.findByUsers(usersRepository.findByGmail(currentUser.getGmail()));

      // menghitung total harga dari setiap barang di keranjang
      int totalJumlah = keranjang.stream().mapToInt(k -> k.getTotalHarga()).sum() ;

      // biaya tambahan
      Integer ongkir = 20000;
      Integer pajak = 5000;

      Integer totalKeseluruhan = totalJumlah +ongkir + pajak;


      // mengirim data ke halaman order
      model.addAttribute("keranjang", keranjangRepository.findAll());
      model.addAttribute("totalHarga", totalJumlah);
      model.addAttribute("ongkir", ongkir);
      model.addAttribute("pajak", pajak);
      model.addAttribute("totalKeseluruhan", totalKeseluruhan);
      return "user/order";
    }
  }


  // untuk menyimpan order pengguna
  @PostMapping("/save-order")
  public String saveOrder(@ModelAttribute OrderRequest orderRequest ){

    if (userLoginService.userLoginIsEmpty()) {
      return "home/notif-login";
    } 
    else {
            List<UserLogin> userLogin = userLoginRepository.findAll();
            UserLogin tamp = userLogin.get(0); //Mengambil login pertama (asumsi hanya ada satu login aktif).
            String gmail = UserController.getCurrentUser().getGmail(); //Mendapatkan email pengguna yang sedang login.
            Users user = usersRepository.findByGmail(gmail); //Mencari objek pengguna berdasarkan email yang login
            Integer userId = user.getId(); //Mendapatkan userId untuk digunakan sebagai referensi selanjutnya.


            // Mengecek apakah saldo ada atau tidak.
            int saldoPengguna = user.getSaldo();

            // Ambil daftar keranjang berdasarkan userId
            List<Keranjang> keranjangList = keranjangRepository.findByUsersId(userId);


            // Hitung total jumlah harga barang dari keranjang
            // stream = adalah aliran data yang memungkinkan kita memproses elemen-elemen di dalam koleksi (seperti List) secara deklaratif (menggunakan fungsi)
            // mapToInt adalah operasi yang mengubah setiap elemen dalam stream menjadi nilai int
            // -> simbol ini dalam kode Java disebut lambda operator atau arrow operator
            // fungsi Lambda operator memisahkan parameter di sebelah kiri dengan body (badan) ekspresi di sebelah kanan.
            //  sum adalah Metode ini menghitung jumlah total dari semua nilai int dalam stream.
            int totalJumlah = keranjangList.stream().mapToInt(k -> k.getTotalHarga()).sum() ;

                  // Cek jenis pembayaran.
              if ("Online".equalsIgnoreCase(orderRequest.getPaymentType())) {
                // Periksa saldo pengguna.
                if (saldoPengguna < totalJumlah) {
                    return "user/saldo-kurang";
                }
                // Kurangi saldo pengguna jika cukup.
                user.setSaldo(saldoPengguna - totalJumlah - 25000);
                tamp.setSaldo(saldoPengguna - totalJumlah - 25000);
            }

            // Proses setiap item di keranjang menjadi pesanan
            for (Keranjang keranjang : keranjangList) {
                ProductOrder order = new ProductOrder();
                Barang barang = barangRepository.findByName(keranjangList.get(0).getBarang().getName());

                // Set data ProductOrder
                order.setOrderId(UUID.randomUUID().toString());
                order.setOrderDate(LocalDate.now());
                order.setBarang(keranjang.getBarang());
                order.setHarga(keranjang.getBarang().getHarga());
                order.setJumlah(keranjang.getJumlah());
                order.setUsers(keranjang.getUsers());
                order.setStatus(OrderStatus.IN_PROGRESS.getName());
                order.setPaymentType(orderRequest.getPaymentType());

                // Set data OrderAddress
                OrderAddress address = new OrderAddress();
                address.setFirstName(orderRequest.getFirstName());
                address.setLastName(orderRequest.getLastName());
                address.setEmail(orderRequest.getEmail());
                address.setMobileNo(orderRequest.getMobileNo());
                address.setAddress(orderRequest.getAddress());
                address.setCity(orderRequest.getCity());
                address.setState(orderRequest.getState());
                address.setPincode(orderRequest.getPincode());
                order.setOrderAddress(address);

                // Simpan pesanan ke database
                productOrderRepository.save(order);


                // set data customer
                Customer customerOrder = new Customer();
                customerOrder.setJumlah(keranjang.getJumlah());
                customerOrder.setBarang(barang);
                customerOrder.setUsers(user);
                customerRepository.save(customerOrder);
            }   

            // Jika jenis pembayaran Online, simpan perubahan saldo pengguna.
              if ("Online".equalsIgnoreCase(orderRequest.getPaymentType())) {
                usersRepository.save(user);
                userLoginRepository.save(tamp);
                return "redirect:/payment";

            }

            // Hapus semua keranjang
              keranjangRepository.deleteAll();

      return "user/success";
    }

  }


  // tampilan payment jika pilih payment
  @GetMapping("/payment")
  public String showPaymentCard(Model model) {
      // Ambil pengguna yang sedang login
      String gmail = UserController.getCurrentUser().getGmail(); 
      Users user = usersRepository.findByGmail(gmail);
  
      // Ambil userId
      Integer userId = user.getId();
  
      // Ambil daftar keranjang berdasarkan userId
      List<Keranjang> keranjangList = keranjangRepository.findByUsersId(userId);
  
      // Hitung total harga dari keranjang
      Integer totalAmount = keranjangList.stream().mapToInt(k -> k.getTotalHarga()).sum();
  
      // Masukkan data ke model
      model.addAttribute("totalAmount", totalAmount);
  
      return "user/payment-card";
  }
  

// konfirmasi pembayaran
  @PostMapping("/konfirmasi-pembayaran")
    public String konfirmasiPembayaran(@RequestParam("nomorKartu") String nomorKartu, @RequestParam("tanggalKedaluwarsa") String tanggalKedaluwarsa,@RequestParam("cvv") String cvv,Model model) {

        // hapus data keranjang
        keranjangRepository.deleteAll();

        return "user/success";
    }


    // tampilan untuk top-up
  @GetMapping("/top-up")
  public String topUp(Model model){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }
    else{
      model.addAttribute("userLogin", userLoginRepository.findAll().get(0));
      return "user/top-up";
    }
  }


  // mengsave top up pengguna
  @PostMapping("/save-top-up")
  public String saveTopUp(@RequestParam(name = "saldo") Integer saldo){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }
    else{
      // ambil semua data yg ada di userLogin dari database
      List<UserLogin> user =userLoginRepository.findAll();
      UserLogin userLogin = user.get(0); //kemudia ambil data pertama yg ada di database
      userLogin.setSaldo(userLogin.getSaldo() + saldo - 1100); // tambah saldo di userLogin
      userLoginRepository.save(userLogin);

      
      // ambil semua data yg ada di users dari database berdasarkan gmail dari userLogin
      Users users = usersRepository.findByGmail(userLogin.getGmail());
      users.setSaldo(userLogin.getSaldo()); // tambah saldo di users
      usersRepository.save(users);
      return "redirect:/";
    }
  }


  @GetMapping("/success-order")
  public String loadingSuccess(){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }
    else{
      return "user/success";
    }
  }



  //  tampilan semua data order pengguna
  @GetMapping("/user-orders")
  public String usersOrders(Model model){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }
    else{
        String gmail = UserController.getCurrentUser().getGmail();
        Users user = usersRepository.findByGmail(gmail);
        Integer userId = user.getId();
        List<ProductOrder> orders = productOrderRepository.findByUsersId(userId);;
      
        model.addAttribute("orders", orders); 
      return "user/my-orders";
    }
  }



  // untuk mengubah status order pengguna bilang mau meng cancel
  @GetMapping("/update-status")
  public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st){
    if(userLoginService.userLoginIsEmpty()){
      return "home/notif-login";
    }else{
      // mendapatkan status berdasarkan ID status (st)
      OrderStatus[] values = OrderStatus.values();
      String status = null;

      for(OrderStatus orderSt : values){
        if(orderSt.getId().equals(st)){
          status = orderSt.getName();
          break;
        }
      }

      Optional<ProductOrder> findById = productOrderRepository.findById(id);
      if(findById.isPresent()){
        ProductOrder productOrder = findById.get();
        productOrder.setStatus(status);
        productOrderRepository.save(productOrder);
      }

      return "redirect:/user-orders";

    }
  }


  // tampilan about
  @GetMapping("/about")
  public String aboutPage(){
    if(userLoginService.userLoginIsEmpty()){
      return "home/about";
    }
    else{
      return "user/about";
    }
  }


// tampilan untuk search barang
  @GetMapping("/search-page")
  public String searchPage(){
    if(userLoginService.userLoginIsEmpty()){
      return "home/search-page";
    }
    else{
      return "user/search-page";
    }
  }


  //  untuk meng seacrh barang yang di cari berdasarkan name barang nya
  @GetMapping("/search-barang")
  public String searchBarang(@RequestParam (value = "name") String name, Model model){
    if(userLoginService.userLoginIsEmpty()){
      // jika name barang kosong , maka tampilkan semua barang
      if(name == null || name.isEmpty()){
        model.addAttribute("barang", barangRepository.findAll());
        return "home/search-page";
      }
      else{
        // jika ada nama barang , maka tampilkan barang berdasarkan nama barang yg di cari
        model.addAttribute("barang", barangRepository.findByNameContainingIgnoreCase(name));
        return "home/search-page";
      }
    }
    else{
        // jika name barang kosong , maka tampilkan semua barang
        if(name == null || name.isEmpty()){
          model.addAttribute("barang", barangRepository.findAll());
          return "user/search-page";
        }
        else{
          // jika ada nama barang , maka tampilkan barang berdasarkan nama barang yg di cari
          model.addAttribute("barang", barangRepository.findByNameContainingIgnoreCase(name));
          return "user/search-page";
            }
    }
  }

  



  // method untuk mendapatkan pengguna yang sedang login
  public static UserLogin getCurrentUser(){
    return currentUser;
  }

  // method untuk menyimpan pengguna yang sedang login
  public static void setCurrentUser (UserLogin user){
     currentUser = user;
  }


}
