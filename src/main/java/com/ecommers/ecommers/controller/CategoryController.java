package com.ecommers.ecommers.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.ecommers.ecommers.model.Category;
import com.ecommers.ecommers.repository.BarangRepository;
import com.ecommers.ecommers.repository.CategoryRepository;
import com.ecommers.ecommers.service.AdminService;

@Controller
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BarangRepository barangRepository;


    // data category
    @GetMapping("/list-category")
    public String listCategory(Model model){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            model.addAttribute("category", categoryRepository.findAll());
            return "admin/list-category";
        }
        else{
            return "admin/notif-login";
        }
    }


    // ke tampilan tambah category
    @GetMapping("/add-category")
    public String addCategory(Model model){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            Category category = new Category();
            model.addAttribute("category", category);
            return "admin/add-category";
        }
        else{
            return "admin/notif-login";
        }
    }


    //  menyimpan category baru
    @PostMapping("/save-category")
    public String saveProduct(@ModelAttribute("category") Category category, @RequestPart("file") MultipartFile file){
        // periksa apakah admin sudah login atau belum
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){

            // periksa apakah category dengan nama ini sudah ada atau belum
            if(categoryRepository.findByName(category.getName()).isEmpty()){
                try{

                    Path targetPath = Paths.get(System.getProperty("user.dir"),"src","main","resources","static", file.getOriginalFilename());
                    file.transferTo(targetPath.toFile());

                    String url = "http://localhost:8081/" + file.getOriginalFilename();
                    category.setImage(url);

                    categoryRepository.save(category);
                } catch(IOException e){
                    e.printStackTrace();
                }
                return "redirect:/list-category";
            }
            else{
                return "admin/save-category-gagal";
            }
        }
        else{
            return "admin/notif-login";
        }
    }

    
    @GetMapping("/update-category/{id}")
    public String updateCategory(@PathVariable Integer id, Model model){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            Category category = categoryRepository.getReferenceById(id);
            model.addAttribute("category", category);
            return "admin/update-category";
        }
        else{
            return "admin/notif-login";
        }
    }


    @PostMapping("/update-category/{id}")
    public String updateCategory(@PathVariable Integer id, @ModelAttribute("category") Category category, @RequestPart("file") MultipartFile file){
        // Periksa apakah admin sudah login atau belum
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            // periksa apakah kategory dengan Id yang diberikan ada
            Category kategory = categoryRepository.getReferenceById(id);

                try {
                    // Periksa apakah ada file baru yang diunggah
                    if (!file.isEmpty()) {
                        Path targetPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static",  file.getOriginalFilename());
                        file.transferTo(targetPath.toFile());
    
                        String url = "http://localhost:8081/" + file.getOriginalFilename();
                        category.setImage(url); // Set URL gambar baru
                    } else {
                        // Jika tidak ada file baru, gunakan gambar yang sudah ada
                        category.setImage(kategory.getImage());
                    }
    
                    // Perbarui data kategori lainnya
                    kategory.setName(category.getName());
                    kategory.setImage(category.getImage());
    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            categoryRepository.save(kategory);
            return "redirect:/list-category";
        
        }
         else {
            return "admin/notif-login";
        }
           
    }
    
        


    @GetMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Integer id){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            Category category = categoryRepository.getReferenceById(id);
            if(barangRepository.findByCategory(category).isEmpty()){
                categoryRepository.deleteById(id);
                return "redirect:/list-category";
            }
            else{
                return "admin/hapus-gagal";
            }
        }
        else{
            return "admin/notif-login";
        }
    }


}
