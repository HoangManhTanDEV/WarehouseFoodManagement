package com.warehouse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MockitoTest {

    @Test
    void testMockitoWorks() {
        // Tạo mock object
        List<String> mockList = mock(List.class);

        // Giả lập hành vi
        when(mockList.size()).thenReturn(100);

        // Kiểm tra
        assertEquals(100, mockList.size());
        System.out.println("✅ Mockito đã hoạt động!");
    }
}