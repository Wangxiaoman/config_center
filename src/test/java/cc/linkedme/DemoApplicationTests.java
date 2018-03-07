package cc.linkedme;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cc.linkedme.service.AppConfigService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DemoApplicationTests {
    
    @Resource
    private AppConfigService appConfigService; 
    
    @Test
    public void test() throws Exception{
        try{
            System.out.println(appConfigService.getApps());
            System.out.println(appConfigService.getAppConfigs("active"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
}