package com.bjtl.projectmanagement;

import com.bjtl.projectmanagement.model.ProjectVO;
import com.bjtl.projectmanagement.service.ProjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectmanagementApplicationTests {

    @Autowired
    ProjectService projectService;

//    @Test
//    public void contextLoads() {
//        List<ProjectVO> list = projectService.listProjects(null);
//        for (ProjectVO projectVO : list) {
//            System.out.println(projectVO);
//        }
//    }

}
