package com.ecommers.ecommers.constans;


//  ini untuk mengatur status pesanan menggunakan enum 
// enum adalah kumpulan konstanta yang digunakan untuk menyimpan nilai unik
//   atau  mendefinisikan sekumpulan konstanta yang terkait satu sama lain. 
public enum OrderStatus {
    IN_PROGRESS(1,"Sedang Diproses"),
    ORDER_RECEIVED(2,"Pesanan Diterima"),
    PRODUCT_PACKED(3,"Produk Dikemas"),
    OUT_FOR_DELIVERY(4,"Sedang Dikirim"),
    DELIVERED(5,"Terkirim"),
    CANCEL(6,"Dibatalkan"),
    SUCCESS(7,"Berhasil");
    

    private Integer id;
    private String name;

    private OrderStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
