package model;

import com.warehouse.model.ProductModel;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductModelTest {

    private static ProductModel productModel;

    @BeforeAll
    static void setUp() {
        productModel = new ProductModel();
        System.out.println("✅ Bắt đầu kiểm thử ProductModel");
    }

    @Test
    @Order(1)
    @DisplayName("TC01: Lấy danh sách sản phẩm")
    void testGetAllProducts() {
        List<Map<String, Object>> products = productModel.getAllProducts();
        assertNotNull(products, "Danh sách sản phẩm không được null");
        assertTrue(products.size() > 0, "Phải có ít nhất 1 sản phẩm");
        System.out.println("✅ Có " + products.size() + " sản phẩm trong kho");
    }

    @Test
    @Order(2)
    @DisplayName("TC02: Thêm sản phẩm mới thành công")
    void testAddProduct() {
        Map<String, Object> product = new java.util.HashMap<>();
        product.put("code", "UNIT_TEST_01");
        product.put("name", "Sản phẩm kiểm thử");
        product.put("category", "Kiểm thử");
        product.put("unit", "cái");
        product.put("quantity", 100);
        product.put("import_price", 10000.0);
        product.put("sell_price", 15000.0);
        product.put("supplier_id", 1);

        boolean result = productModel.addProduct(product);
        assertTrue(result, "Thêm sản phẩm phải thành công");
        System.out.println("✅ Thêm sản phẩm thành công");
    }

    @Test
    @Order(3)
    @DisplayName("TC03: Thêm sản phẩm trùng mã phải thất bại")
    void testAddProductDuplicateCode() {
        Map<String, Object> product = new java.util.HashMap<>();
        product.put("code", "UNIT_TEST_01"); // Mã đã tồn tại từ test trên
        product.put("name", "Sản phẩm trùng mã");
        product.put("category", "Kiểm thử");
        product.put("unit", "cái");
        product.put("quantity", 50);
        product.put("import_price", 20000.0);
        product.put("sell_price", 25000.0);
        product.put("supplier_id", 1);

        boolean result = productModel.addProduct(product);
        assertFalse(result, "Thêm sản phẩm trùng mã phải thất bại");
        System.out.println("✅ Hệ thống từ chối mã trùng");
    }

    @Test
    @Order(4)
    @DisplayName("TC04: Tìm kiếm sản phẩm")
    void testSearchProducts() {
        List<Map<String, Object>> results = productModel.searchProducts("UNIT_TEST_01");
        assertNotNull(results);
        assertTrue(results.size() > 0, "Phải tìm thấy sản phẩm vừa thêm");
        System.out.println("✅ Tìm thấy " + results.size() + " sản phẩm");
    }

    @Test
    @Order(5)
    @DisplayName("TC05: Kiểm tra mã sản phẩm tồn tại")
    void testIsCodeExists() {
        boolean exists = productModel.isCodeExists("UNIT_TEST_01", -1);
        assertTrue(exists, "Mã sản phẩm phải tồn tại");
        System.out.println("✅ Mã sản phẩm tồn tại");
    }

    @Test
    @Order(6)
    @DisplayName("TC06: Xóa sản phẩm kiểm thử")
    void testDeleteProduct() {
        List<Map<String, Object>> products = productModel.getAllProducts();
        for (Map<String, Object> p : products) {
            if ("UNIT_TEST_01".equals(p.get("code"))) {
                int id = (int) p.get("id");
                boolean result = productModel.deleteProduct(id);
                assertTrue(result, "Xóa sản phẩm phải thành công");
                System.out.println("✅ Xóa sản phẩm test thành công");
                return;
            }
        }
        fail("Không tìm thấy sản phẩm để xóa");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("✅ Kết thúc kiểm thử ProductModel");
    }
}