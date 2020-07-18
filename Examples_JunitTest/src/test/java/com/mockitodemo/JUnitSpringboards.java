package com.mockitodemo;

import com.examples.MagicBuilderServiceImpl;
import com.examples.MagicConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MagicConfig.class)
@ActiveProfiles("Magic")
public class JUnitSpringboards {

    @Autowired
    private MagicBuilderServiceImpl magicBuilderService;

    @BeforeClass
    public static void setUp() {
        System.out.println("-----> SETUP <-----");
    }

    @Test
    public  void testSampleServiceGetAccountDescription() {
        assertTrue(magicBuilderService.getLucky().contains("Me"));
    }

}
