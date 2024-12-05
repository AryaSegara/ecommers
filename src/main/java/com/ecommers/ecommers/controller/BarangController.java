package com.ecommers.ecommers.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.ecommers.ecommers.model.Barang;
import com.ecommers.ecommers.repository.BarangRepository;
import com.ecommers.ecommers.repository.CategoryRepository;
import com.ecommers.ecommers.repository.CustomerRepository;
import com.ecommers.ecommers.service.AdminService;
import com.ecommers.ecommers.service.UserLoginService;

@Controller
public class BarangController {
    @Autowired
    private BarangRepository barangRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private AdminService adminService;


    // list product yg ada di admin
    @GetMapping("/list-product")
    public String showBarang(Model model){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            model.addAttribute("barang",barangRepository.findAll());
            return "admin/list-product";
        }else{
            return "admin/notif-login";
        }
    }


    // semua product yg di tampilkan di home/atau beranda
    @GetMapping("/product")
    public String ShowProduct(Model model){
        if(userLoginService.userLoginIsEmpty()){
            model.addAttribute("barang",barangRepository.findAll());
            return "home/product";
        }else{
            model.addAttribute("barang",barangRepository.findAll());
            return "user/product";
        }
    }


    // tambahkan product oleh admin
    @GetMapping("/add-product")
    public String addProduct(Model model){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            Barang barang = new Barang();
            model.addAttribute("barang", barang);
            model.addAttribute("category",categoryRepository.findAll());
            return "admin/add-product";
        }
        else{
            return "admin/notif-login";
        }
    }


    // Update Product berdasarkan id
    @GetMapping("/update-product/{id}")
    public String updateProduct(@PathVariable Integer id,Model model){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            Barang barang = barangRepository.getReferenceById(id);
            model.addAttribute("barang", barang);
            model.addAttribute("category", categoryRepository.findAll());
            return "admin/update-product";
        }
        else{
            return "admin/notif-login";
        }
    }


    // memperbarui data obat yg sudah ada
    @PostMapping("/update-product/{id}")
    //@RequestPart("file") MultipartFile file: Mengambil file dari request HTTP dengan nama bagian "file". Biasanya digunakan untuk mengunggah file
    public String saveProduct(@PathVariable Integer id, @ModelAttribute("barang") Barang barang, @RequestPart("file")  MultipartFile file){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            // menampatkan Id yang mau di edit
            Barang update = adminService.findById(id);
            update.setName(barang.getName());
            update.setDescription(barang.getDescription());
            update.setCategory(barang.getCategory());
            update.setHarga(barang.getHarga());
            update.setStok(barang.getStok());


            try {
                // Membuat path untuk menyimpan file yang diunggah ke folder upload
                // system.getProperty itu = sering digunakan untuk menentukan lokasi di mana aplikasi sedang berjalan.
                // path = Membuat path (jalur) file atau direktori berdasarkan argumen yang diberikan
                //  file.getOriginalFileName = 
                // Mengambil nama asli file yang diunggah dari objek MultipartFile. Ini adalah nama file seperti yang ada di perangkat pengguna sebelum diunggah.
                Path targetPath = Paths.get(System.getProperty("user.dir"),"src","main","resources","static" ,file.getOriginalFilename());

                // Menyalin file yang diunggah ke lokasi tujuan yang telah ditentukan
                file.transferTo(targetPath.toFile());
                String url ="http://localhost:8081/" + file.getOriginalFilename();
                update.setImage(url);
            } catch (Exception e) {
                e.printStackTrace(); //Mencetak detail kesalahan ke konsol untuk debugging.
            }

            
            barangRepository.save(update);
            return "redirect:/list-product";
        }
        else{
            return "admin/notif/login";
        }
    }


    // meng save apa yg sudah di tambah atau pun yg di update
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("barang") Barang barang, @RequestPart("file") MultipartFile file){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){

            if(barangRepository.findAllByName(barang.getName()).isEmpty() || barangRepository.findByCategory(barang.getCategory()).isEmpty() || barangRepository.findByHarga(barang.getHarga()).isEmpty()){

                // Membuat path untuk menyimpan file yang diunggah ke folder upload
                // system.getProperty itu = sering digunakan untuk menentukan lokasi di mana aplikasi sedang berjalan.
                // path = Membuat path (jalur) file atau direktori berdasarkan argumen yang diberikan
                //  file.getOriginalFileName = 
                // Mengambil nama asli file yang diunggah dari objek MultipartFile. Ini adalah nama file seperti yang ada di perangkat pengguna sebelum diunggah.

                try{
                    Path targetPath = Paths.get(System.getProperty("user.dir"),"src","main","resources","static", file.getOriginalFilename());
                    file.transferTo(targetPath.toFile());
                    String url = "http://localhost:8081/"+file.getOriginalFilename();
                    barang.setImage(url);
                    barangRepository.save(barang);
                } catch(IOException e){
                    e.printStackTrace(); // Mencetak detail kesalahan ke konsol untuk debugging.
                }
                return "redirect:/list-product";
            }
            else{
                return "admin/save-gagal";
            }
        }
        else{
            return "admin/notif-login";
        }
    }



    //  delete product
    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Integer id){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            Barang barang = barangRepository.getReferenceById(id);
            if(customerRepository.findByBarang(barang).isEmpty()){
                barangRepository.deleteById(id);
                return "redirect:/list-product";
            }
            else{
                return "admin/hapus-gagal";
            }
        }
        else{
            return "admin/notif-login";
        }
    }


        //  untuk melihat view detail 
        @GetMapping("/product-detail/{id}")
        public String barang(@PathVariable("id") Integer id, Model model){
            Barang barang = barangRepository.getReferenceById(id);

            if(userLoginService.userLoginIsEmpty()){
                model.addAttribute("barang", barang);
                return "home/detail-product";
            }
            else{
                model.addAttribute("barang", barang);
                return "user/detail-product";
            }
        }



        // Sort harga kecil ke besar dan sebaliknya
    @GetMapping("/sorted-harga")
    public String getSortedHargaBarang(@RequestParam (defaultValue = "asc") String sortOrder , Model model){
        List<Barang> barangList;

        if("desc".equalsIgnoreCase(sortOrder)){
            barangList = barangRepository.findAllByOrderByHargaDesc();
        }else{
            barangList = barangRepository.findAllByOrderByHargaAsc();
        }

        model.addAttribute("barang", barangList);
        return "admin/list-product";
    }


    
}
