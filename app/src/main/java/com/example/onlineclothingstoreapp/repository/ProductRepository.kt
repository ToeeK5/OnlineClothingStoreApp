package com.example.onlineclothingstoreapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.Product

class ProductRepository {
//    val autoProducts = listOf(
//        // ================= NHÓM: ÁO (6 món: 1 -> 6) =================
//        Product(
//            name = "Áo thun Cotton Unisex", category = "Áo", price = 150000.0,
//            description = "Áo thun chất liệu 100% cotton thoáng mát, thấm hút mồ hôi tốt, form rộng dễ phối đồ.",
//            stockQuantity = 45, rating = 4.8f, sizes = listOf("S", "M", "L", "XL"),
//            colorImages = mapOf(
//                "đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F1_den.jpg?alt=media&token=63cfc6e9-2d09-48a9-90e8-f9d732758eae",
//                "Trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F1_trang.jpg?alt=media&token=d18806a0-f2a2-4bb0-8169-7d9fab1ba876",
//                "Xam" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F1_xam.jpg?alt=media&token=7aa4b876-470c-4369-a36d-95bd9d9b6dd7"
//            )
//        ),
//        Product(
//            name = "Áo Polo Thể Thao Nam", category = "Áo", price = 220000.0,
//            description = "Áo polo nam lịch sự, chất vải cá sấu co giãn 4 chiều, thích hợp đi làm và đi chơi.",
//            stockQuantity = 30, rating = 4.5f, sizes = listOf("M", "L", "XL", "XXL"),
//            colorImages = mapOf(
//                "Xanh navy" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F2_navy.jpg?alt=media&token=66acb638-90ee-4ebb-9d38-d2e27da324c4",
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F2_den.jpg?alt=media&token=e9ec0974-2df8-4586-acfb-4f976f1d814d"
//            )
//        ),
//        Product(
//            name = "Áo Sơ Mi Tay Dài Công Sở", category = "Áo", price = 250000.0,
//            description = "Sơ mi nam tay dài dáng ôm nhẹ, chất vải chống nhăn cao cấp, phẳng phiu cả ngày dài.",
//            stockQuantity = 25, rating = 4.6f, sizes = listOf("S", "M", "L", "XL"),
//            colorImages = mapOf(
//                "Trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F3_trang.jpg?alt=media&token=94a8704f-e183-4e4c-84a4-b0dc1ebf464e",
//                "Xanh nhạt" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F3_xanh.jpg?alt=media&token=9807c424-b265-4994-adf7-91f96e3948aa"
//            )
//        ),
//        Product(
//            name = "Áo Croptop Nữ Cá Tính", category = "Áo", price = 120000.0,
//            description = "Áo croptop nữ ôm sát năng động, chất thun gân co giãn tốt, tôn dáng quyến rũ.",
//            stockQuantity = 50, rating = 4.7f, sizes = listOf("S", "M", "L"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F4_den.jpg?alt=media&token=71c77f26-5dba-4de0-8707-5b8343713def",
//                "Hồng baby" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F4_hong.jpg?alt=media&token=12dc2751-6d7f-4721-b2da-418ed45af80e"
//            )
//        ),
//        Product(
//            name = "Áo Hoodie Nỉ Bông Oversize", category = "Áo", price = 320000.0,
//            description = "Áo hoodie nỉ ngoại dày dặn, lót bông ấm áp, form oversize chuẩn phong cách Streetwear.",
//            stockQuantity = 15, rating = 4.9f, sizes = listOf("M", "L", "XL"),
//            colorImages = mapOf(
//                "Xám tiêu" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F5_xam.jpg?alt=media&token=c770fbc5-b87d-4b2a-8d3d-1b53db8e1977",
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F5_den.jpg?alt=media&token=32e72d59-25d3-42ef-8610-2e6bbcb82058"
//            )
//        ),
//        Product(
//            name = "Áo Thun Tay Lỡ Form Rộng", category = "Áo", price = 135000.0,
//            description = "Áo thun phong cách lỡ tay form rộng tay lửng giấu quần, hình in dễ thương sắc nét.",
//            stockQuantity = 40, rating = 4.8f, sizes = listOf("M", "L", "XL"),
//            colorImages = mapOf(
//                "Trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F6_trang.jpg?alt=media&token=17f15691-43a5-4408-9f0a-991e0bd00fc6",
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F6_den.jpg?alt=media&token=1a7c6add-b6d3-4e80-acd5-0ff3314623ae"
//            )
//        ),
//
//        // ================= NHÓM: QUẦN (5 món: 7 -> 11) =================
//        Product(
//            name = "Quần Jeans Baggy Nam", category = "Quần", price = 350000.0,
//            description = "Quần jeans dáng baggy ống rộng vừa phải, chất bò dày dặn không phai màu khi giặt.",
//            stockQuantity = 33, rating = 4.6f, sizes = listOf("28", "29", "30", "31", "32"),
//            colorImages = mapOf(
//                "Xanh nhạt" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F7_xanh.jpg?alt=media&token=fd8360b1-a7d4-4fca-ab1f-cb00aaef310f",
//                "Đen khói" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F7_den.jpg?alt=media&token=b7a819bf-edf4-420c-a984-7d12e6dbe03b"
//            )
//        ),
//        Product(
//            name = "Quần Jeans Ôm Co Giãn Nữ", category = "Quần", price = 320000.0,
//            description = "Quần skinny jeans nữ cạp cao tôn dáng, co giãn mạnh cực kỳ thoải mái khi di chuyển.",
//            stockQuantity = 28, rating = 4.5f, sizes = listOf("26", "27", "28", "29"),
//            colorImages = mapOf(
//                "Xanh đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F8_xanh.jpg?alt=media&token=ac6579e0-d8c9-4647-94e1-2e5ff063be1b",
//                "Đen tuyền" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F8_den.jpg?alt=media&token=1cdfe15a-9766-4518-9858-cfa57652924d"
//            )
//        ),
//        Product(
//            name = "Quần Kaki Ống Đứng", category = "Quần", price = 280000.0,
//            description = "Quần kaki nam trơn ống đứng công sở lịch sự, chất vải mềm mịn bền bỉ.",
//            stockQuantity = 19, rating = 4.4f, sizes = listOf("29", "30", "31", "32", "34"),
//            colorImages = mapOf(
//                "Vàng be" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F9_vang.jpg?alt=media&token=e25944dc-5d92-454f-8210-0d1789856109",
//                "Xám" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F9_xam.jpg?alt=media&token=04cbe57e-713f-4627-964e-24c67a39fc00"
//            )
//        ),
//        Product(
//            name = "Quần Jogger Thể Thao", category = "Quần", price = 190000.0,
//            description = "Quần jogger bo gấu thun co giãn năng động, thích hợp tập thể dục hoặc mặc nhà.",
//            stockQuantity = 55, rating = 4.7f, sizes = listOf("M", "L", "XL", "XXL"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F10_den.jpg?alt=media&token=97c6a130-6394-4f55-8ad2-bef98b2802e4",
//                "Xám" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F10_xam.jpg?alt=media&token=a039f968-ab22-43fe-a9d9-4e5021f1b0e3"
//            )
//        ),
//        Product(
//            name = "Quần Ống Rộng Culottes", category = "Quần", price = 210000.0,
//            description = "Quần culottes ống rộng suông dài cạp cao tôn dáng kéo dài chân cho phái nữ.",
//            stockQuantity = 30, rating = 4.6f, sizes = listOf("S", "M", "L", "XL"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F11_den.jpg?alt=media&token=6e9304de-3ba3-404d-8dd6-d21a24840ae5",
//                "Trắng sữa" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F11_trang.jpg?alt=media&token=69b4fc5c-8795-4306-b3c9-2f5180c2df7f"
//            )
//        ),
//
//        // ================= NHÓM: ÁO KHOÁC (5 món: 12 -> 16) =================
//        Product(
//            name = "Áo Khoác Bomber Kaki", category = "Áo khoác", price = 380000.0,
//            description = "Áo khoác bomer vải kaki hai lớp dày dặn, cổ bo thun thể thao năng động gài nút bấm.",
//            stockQuantity = 15, rating = 4.7f, sizes = listOf("M", "L", "XL"),
//            colorImages = mapOf(
//                "Đen phối trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F12_den.jpg?alt=media&token=d6bf10b9-4bac-4ede-97a0-a363e5971a16",
//                "Xanh rêu" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F12_xanh.jpg?alt=media&token=05b6c1f1-5b58-40a0-af32-eb492491f0c4"
//            )
//        ),
//        Product(
//            name = "Áo Khoác Dù 2 Lớp Chống Nước", category = "Áo khoác", price = 250000.0,
//            description = "Áo gió dù nhẹ chống thấm nước mưa nhẹ, cản gió tốt thích hợp chạy xe đi phượt.",
//            stockQuantity = 40, rating = 4.5f, sizes = listOf("S", "M", "L", "XL", "XXL"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F13_den.jpg?alt=media&token=e5fb9855-f341-4217-ad0a-c14d80573f36",
//                "Trắng sửa" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F13_trang.jpg?alt=media&token=e033a658-2934-4785-b1dc-1fa0e537162f"
//            )
//        ),
//        Product(
//            name = "Áo Khoác Denim Vintage", category = "Áo khoác", price = 420000.0,
//            description = "Áo khoác jean bò denim phong cách vintage cổ điển, wash màu bụi bặm thời thượng.",
//            stockQuantity = 22, rating = 4.8f, sizes = listOf("M", "L", "XL"),
//            colorImages = mapOf(
//                "Xanh retro" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F14_xanh.jpg?alt=media&token=d7008a35-8960-40a2-9994-eac7615ae9fb",
//                "Đen bạc" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F14_den.jpg?alt=media&token=49992f4d-8998-4d75-b122-2f36f6d8bf2e"
//            )
//        ),
//        Product(
//            name = "Áo Blazer Dáng Hàn Quốc", category = "Áo khoác", price = 450000.0,
//            description = "Áo blazer khoác ngoài form rộng vừa vặn chuẩn style nam thần Hàn Quốc lịch lãm.",
//            stockQuantity = 14, rating = 4.6f, sizes = listOf("S", "M", "L", "XL"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F15_den.jpg?alt=media&token=b4bcd56d-d0ea-4cef-837b-855ad94cb102",
//                "Xám nhạt" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F15_xam.jpg?alt=media&token=9483db6c-f904-4d65-8b34-9b16a7ee0b0a"
//            )
//        ),
//        Product(
//            name = "Áo Khoác Chống Nắng UV", category = "Áo khoác", price = 160000.0,
//            description = "Áo khoác chống nắng chất thun thông hơi thoáng khí, chỉ số chống tia UV UPF 50+ bảo vệ da.",
//            stockQuantity = 70, rating = 4.6f, sizes = listOf("S", "M", "L", "XL"),
//            colorImages = mapOf(
//                "Xám" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F16_xam.jpg?alt=media&token=fe2ca746-bbf8-4ffc-941c-7e1489d48596",
//                "Xanh Đậm" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F16_xanh.jpg?alt=media&token=80612fb2-5965-441b-b260-806f4a939ede"
//            )
//        ),
//
//        // ================= NHÓM: NÓN / MŨ (5 món: 17 -> 21) =================
//        Product(
//            name = "Nón Lưỡi Trai Kaki Trơn", category = "Nón", price = 70000.0,
//            description = "Mũ kết lưỡi trai chất kaki trơn basic, có khóa kim loại điều chỉnh vòng đầu dễ dàng.",
//            stockQuantity = 100, rating = 4.7f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F17_den.jpg?alt=media&token=1f6b559f-4f2f-405e-bf5b-c961ee4309c1",
//                "Trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F17_trang.jpg?alt=media&token=d806f04b-bb14-4696-b065-412dd94b9ca4"
//            )
//        ),
//        Product(
//            name = "Nón Bucket 2 Mặt", category = "Nón", price = 90000.0,
//            description = "Nón tai bèo bucket vải dù/cotton thiết kế đặc biệt có thể đội lộn ngược thay đổi 2 mặt màu.",
//            stockQuantity = 45, rating = 4.5f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Đen - Trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F18_dentrang.jpg?alt=media&token=4ce01885-af8b-4531-bc7a-b4b1abaaba25",
//                "Vàng - Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F18_vangden.jpg?alt=media&token=6a6db005-9f7d-4041-915f-4ca6ea687a03"
//            )
//        ),
//        Product(
//            name = "Nón Len Beanie Mùa Đông", category = "Nón", price = 65000.0,
//            description = "Mũ len beanie dáng tròn co giãn ôm sát đầu giữ ấm tai tóc cực tốt.",
//            stockQuantity = 30, rating = 4.4f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Xám" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F19_xam.jpg?alt=media&token=600f228f-6e3f-4862-aa23-2821164d089a",
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F19_den.jpg?alt=media&token=405da8ef-1e01-40f4-8aac-6769183310da"
//            )
//        ),
//        Product(
//            name = "Nón Cói Đi Biển Rộng Vành", category = "Nón", price = 150000.0,
//            description = "Nón cói nữ rộng vành thắt nơ điệu đà che nắng hoàn hảo khi đi du lịch biển chụp hình checkin.",
//            stockQuantity = 20, rating = 4.8f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Màu tự nhiên" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F19_xam.jpg?alt=media&token=600f228f-6e3f-4862-aa23-2821164d089a",
//                "Trắng kem" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F20_trang.jpg?alt=media&token=557c54f0-41d0-41f2-8aa1-a65f30feec57"
//            )
//        ),
//        Product(
//            name = "Nón Nồi Beret Thời Trang", category = "Nón", price = 110000.0,
//            description = "Mũ nồi beret chất dạ nỉ phong cách tiểu thư cổ điển sang chảnh quyến rũ.",
//            stockQuantity = 12, rating = 4.7f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Đỏ cổ điển" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F21_do.jpg?alt=media&token=87247433-5124-4e75-8ffb-c83d477e4586",
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F21_den.jpg?alt=media&token=1c8370ff-ce23-4922-865b-01959397fbae"
//            )
//        ),
//
//        // ================= NHÓM: GIÀY (5 món: 22 -> 26) =================
//        Product(
//            name = "Giày Sneakers Trắng Cổ Thấp", category = "Giày", price = 450000.0,
//            description = "Giày thể thao trắng da PU trơn basic dễ phối đồ với mọi loại quần áo jeans hay váy.",
//            stockQuantity = 24, rating = 4.8f, sizes = listOf("36", "37", "38", "39", "40", "41", "42"),
//            colorImages = mapOf(
//                "Trắng hoàn toàn" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F22_trang.jpg?alt=media&token=b5934231-827e-42bf-9f4c-cb567c2d346e",
//                "Trắng gót đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F22_gotden.jpg?alt=media&token=9fb65306-cfa8-45fb-8d08-1ae68a979054"
//            )
//        ),
//        Product(
//            name = "Giày Chạy Bộ Thể Thao", category = "Giày", price = 550000.0,
//            description = "Giày chạy bộ đế cao su đúc nguyên khối êm ái đàn hồi tốt, lớp lưới thoáng khí chống hôi chân.",
//            stockQuantity = 30, rating = 4.6f, sizes = listOf("39", "40", "41", "42", "43"),
//            colorImages = mapOf(
//                "Xám đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F23_xam.jpg?alt=media&token=8a04598f-243e-4f97-ab5f-80ca794f09c9",
//                "Xanh dạ quang" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F23_xanh.jpg?alt=media&token=87b823aa-1ce8-40d2-8df9-280d688d1a1b"
//            )
//        ),
//        Product(
//            name = "Giày Lười Loafer Da Bóng", category = "Giày", price = 480000.0,
//            description = "Giày lười da bò thật xử lý bóng loáng sang trọng, tiện lợi xỏ chân đi ngay không cần buộc dây.",
//            stockQuantity = 15, rating = 4.5f, sizes = listOf("39", "40", "41", "42"),
//            colorImages = mapOf(
//                "Đen bóng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F24_den.jpg?alt=media&token=ac7e97cf-5e8c-4ef1-958a-940929da6cb5",
//                "Nâu bóng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F24_nau.jpg?alt=media&token=79de7477-3ccc-43c7-9a9d-e94a55328d93"
//            )
//        ),
//        Product(
//            name = "Giày Tây Oxford Lịch Lãm", category = "Giày", price = 620000.0,
//            description = "Giày tây nam Oxford mũi nhọn buộc dây lịch lãm chuẩn quý ông dự tiệc hay chú rể ngày cưới.",
//            stockQuantity = 10, rating = 4.9f, sizes = listOf("39", "40", "41", "42", "43"),
//            colorImages = mapOf(
//                "Đen mờ" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F25_den.jpg?alt=media&token=3b0e13af-c634-4b68-b3be-b286ff32b953",
//                "Nâu đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F25_nau.jpg?alt=media&token=ca001f0c-09b2-422e-8494-7d1cf4c13cf6"
//            )
//        ),
//        Product(
//            name = "Giày Cao Gót Mũi Nhọn Nữ", category = "Giày", price = 350000.0,
//            description = "Giày cao gót nữ mũi nhọn da lộn gót nhọn cao 7 phân thanh lịch tôn dáng nữ tính.",
//            stockQuantity = 18, rating = 4.7f, sizes = listOf("35", "36", "37", "38", "39"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F26_den.jpg?alt=media&token=fc0e4390-24d4-43ed-b04b-776b61f16110",
//                "Trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F26_trang.jpg?alt=media&token=890f9117-f19a-44f8-8439-590b1547096b"
//            )
//        ),
//
//        // ================= NHÓM: BALO (4 món: 27 -> 30) =================
//        Product(
//            name = "Balo Đi Học Canvas", category = "Balo", price = 195000.0,
//            description = "Balo học sinh vải canvas bố dày dặn form vuông rộng rãi đựng vừa tập vở sách A4 thoải mái.",
//            stockQuantity = 50, rating = 4.6f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Xanh pastel" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F27_xanh.jpg?alt=media&token=fd434b01-d5c9-4dde-9555-61e291e5175a",
//                "Trắng" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F27_trang.jpg?alt=media&token=b4d34313-969c-472c-a836-b73f903b5bf0"
//            )
//        ),
//        Product(
//            name = "Balo Laptop Chống Nước", category = "Balo", price = 350000.0,
//            description = "Balo công sở đựng laptop 15.6 inch vải oxford chống nước cao cấp, có đệm lưng chống đau vai.",
//            stockQuantity = 22, rating = 4.8f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F28_den.jpg?alt=media&token=64894b0c-c8c3-427e-bb43-3f3e2b467813",
//                "Xám" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F28_xam.jpg?alt=media&token=ed8e99d2-f513-4814-9c36-bb3471fab482"
//            )
//        ),
//        Product(
//            name = "Túi Đeo Chéo Canvas Mini", category = "Balo", price = 90000.0,
//            description = "Túi vải nhỏ đeo chéo phong cách Hàn Quốc đựng vừa điện thoại ví tiền dạo phố nhẹ nhàng.",
//            stockQuantity = 60, rating = 4.5f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Trắng ngà" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F29_trang.jpg?alt=media&token=e28a4147-9da1-4a55-80cb-1dbf9e327b72",
//                "Đen" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F29_den.jpg?alt=media&token=56d7e879-9063-4c4c-9322-bc5b9429a674"
//            )
//        ),
//        Product(
//            name = "Balo Thời Trang Da Mini Nữ", category = "Balo", price = 220000.0,
//            description = "Balo nữ chất liệu da PU mềm nhỏ xinh phối quai xích điệu đà đi chơi đi hẹn hò sang chảnh.",
//            stockQuantity = 19, rating = 4.7f, sizes = listOf("Freesize"),
//            colorImages = mapOf(
//                "Đen da mềm" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F30_den.jpg?alt=media&token=191b540b-0e87-4b03-8742-278aeb0b714e",
//                "Trắng ngọc" to "https://firebasestorage.googleapis.com/v0/b/onlineclothingstoreapp.firebasestorage.app/o/Products%2F30_trang.jpg?alt=media&token=a02b2224-c3c1-49cf-ad4a-1d549ae7090a"
//            )
//        )
//    )
    val firebaseService = FirebaseService()

    fun getAllProducts(): LiveData<List<Product>> {
        val data = MutableLiveData<List<Product>>(emptyList())

        firebaseService.db.collection("products").get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Product::class.java)
                data.value = list
            }
        return data
    }

    fun getProductById(productId: String): LiveData<Product?> {
        val data = MutableLiveData<Product?>(null)
        
        // Kiểm tra productId hợp lệ để tránh lỗi "Invalid document reference"
        if (productId.isBlank()) {
            return data
        }

        firebaseService.db.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    data.value = document.toObject(Product::class.java)
                }
            }
        return data
    }

//    fun push50ProductsToFirebase() {
//        val db = firebaseService.db
//        val collectionRef = db.collection("products") // Tên bảng trên Firestore
//
//        Log.d("Firebase_Auto", "Đang bắt đầu tải dữ liệu...")
//
//        for (product in autoProducts) {
//            collectionRef.add(product)
//                .addOnSuccessListener { documentReference ->
//                    Log.d("Firebase_Auto", "Thêm thành công: ${product.name} (ID: ${documentReference.id})")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("Firebase_Auto", "Lỗi khi thêm: ${product.name}", e)
//                }
//        }
//    }
}
