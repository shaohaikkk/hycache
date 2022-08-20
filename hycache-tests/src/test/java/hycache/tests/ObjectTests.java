package hycache.tests;

import com.zero.hycache.HyCacheApplication;
import com.zero.hycache.bean.UserBean;
import hycache.service.ObjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zero
 * @date Create on 2022/8/20
 * @description
 */
@SpringBootTest(classes = HyCacheApplication.class)
public class ObjectTests {

    ObjectService objectService = new ObjectService();

    @Test
    public void testIncreInt() {
        for (int i = 0; i < 2; i++) {
            int result = objectService.testIncreInt();
            Assertions.assertEquals(result, 1);
        }
    }

    @Test
    public void testIncreInteger() {
        for (int i = 0; i < 2; i++) {
            int result = objectService.testIncreInteger();
            Assertions.assertEquals(result, 1);
        }
    }

    @Test
    public void testIncreObject() {
        for (int i = 0; i < 2; i++) {
            UserBean userBean = objectService.testObject();
            Assertions.assertEquals(userBean.getAge(), 1);
        }
    }
}
