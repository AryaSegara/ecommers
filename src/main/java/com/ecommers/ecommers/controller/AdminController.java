package com.ecommers.ecommers.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommers.ecommers.constans.OrderStatus;
import com.ecommers.ecommers.model.Admin;
import com.ecommers.ecommers.model.Customer;
import com.ecommers.ecommers.model.ProductOrder;
import com.ecommers.ecommers.repository.CustomerRepository;
import com.ecommers.ecommers.repository.ProductOrderRepository;
import com.ecommers.ecommers.service.AdminService;

@Controller // untuk menangani request atau permintaan HTTP dari pengguna
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // untuk ke halaman admin
    @GetMapping("/login-admin")
    public String dashboard(){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("belum login")){
            return "admin/login-admin";
        }else{
            return "admin/dashboard";
        }
    }

    // cek login admin
    @PostMapping("/dashboard")
    public String cekAdmin(@RequestParam (name = "pin") String pin){

        // cek apakah keterengan admin belum login
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("belum login")){
            Admin admin = adminService.getAdminByPin(pin);

            // cek apakah admin sesuai atau tidak
            if(admin == null){
                return "admin/login-admin-gagal";
            }

            // jika pin sesuai dengan database maka keterangan menjadi sudah login
            else{
                if(admin.equals(adminService.getAdminByPin(pin))){
                    admin.setKeterangan("sudah login");
                    adminService.save(admin);
                    return "admin/dashboard";
                }
                else{
                    return "admin/login-admin-gagal";
                }
            }
        }else{
            return "admin/dashboard";
        }   
    }

    // log out dari admin
    @GetMapping("/keluar-admin")
    public String keluarAdmin(Model model){
        Admin admin = adminService.getAllAdmin().get(0); 
        admin.setKeterangan("belum login"); // ubah keterangan menjadi belum login
        adminService.save(admin);
        return "redirect:/";
    }


    //  melihat semua orders yang ada
    @GetMapping("/list-orders")
    public String getAllOrders(Model model){
        if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
            List<ProductOrder> allOrders = productOrderRepository.findAll();
            model.addAttribute("orders", allOrders);
            return "admin/list-orders";
        }else{
            return "admin/notif-login";
        }
    }

    
    //  untuk meng update status order yang ada 
  @PostMapping("/update-order-status")
  public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st){
    if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){

      // mendapatkan status berdasarkan ID status (st)
      OrderStatus[] values = OrderStatus.values(); //mendapatkan semua nilai enum OrderStatus ke dalam array values
      String status = null;

      for(OrderStatus orderSt : values){
        // mengecek apakah id status enum sama dengan parameter st
        if(orderSt.getId().equals(st)){
            // Jika ditemukan, ambil nama status dari enum
          status = orderSt.getName();
          break;
        }
      }

    //   Mencari data ProductOrder dengan ID yang sesuai di database, menggunakan repository
    // Bungkus opsional untuk menangani kasus ketika data tidak ditemukan
      Optional<ProductOrder> findById = productOrderRepository.findById(id);

    //   Mengecek apakah data ProductOrder ditemukan
      if(findById.isPresent()){
        ProductOrder productOrder = findById.get();
        productOrder.setStatus(status);
        productOrderRepository.save(productOrder);
      }
      return "redirect:/list-orders";
      
    }else{
        return "admin/notif-login";
    }
  }


  // data data yg jadi customer 
  @GetMapping("/list-customer")
  public String getAllCustomer(Model model){
    if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
      List<Customer> allCustomer = customerRepository.findAll();
      model.addAttribute("customers", allCustomer);
      return "admin/list-customer";
    }else{
      return "admin/notif-login";
    }
  }


  // delete customer berdasarkan id
  @GetMapping("/delete-customer/{id}")
  public String deleteCustomer(@PathVariable Integer id){
    if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
      customerRepository.deleteById(id);
      return "redirect:/list-customer";
    }else{
      return "admin/notif-login";
    }

  }


  // delete semua customer 
  @GetMapping("/delete-all-customer")
  public String deleteAllCustomer(){
    if(adminService.getAllAdmin().get(0).getKeterangan().equals("sudah login")){
      customerRepository.deleteAll();
      return "redirect:/list-customer";
    }else{
      return "admin/notif-login";
    }
  }



 
}
