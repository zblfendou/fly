package zbl.fly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration
@Transactional
public class AbstractTest {
    protected void outputJson(Object data) {
        try {
            System.out.println("JSON:");
            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data));
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
