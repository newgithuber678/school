package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class FacultyControllerTestWebMvc {
    @Autowired
    MockMvc mvc;

    @MockBean
    FacultyRepository facultyRepository;
    @SpyBean
    FacultyService facultyService;
    @MockBean
    AvatarService avatarService;
    @MockBean
    StudentService studentService;

    @InjectMocks
    FacultyController controller;

    @Test
    void testGet() throws Exception {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(new Faculty(1L, "test_faculty_mvc", "test_color_mvc")));
        mvc.perform(MockMvcRequestBuilders.get("/faculty?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_faculty_mvc"))
                .andExpect(jsonPath("$.color").value("test_color_mvc"));
    }

    @Test
    void testUpdate() throws Exception {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(new Faculty(1L, "test_faculty_mvc", "test_color_mvc")));
        Faculty faculty = new Faculty(1L, "updated_name", "updated_color");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mvc.perform(MockMvcRequestBuilders.get("/faculty?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_faculty_mvc"))
                .andExpect(jsonPath("$.color").value("test_color_mvc"));
    }

    @Test
    void testDelete() throws Exception {
        when(facultyRepository.findById(2L)).thenReturn(Optional.of(new Faculty(1L, "test_faculty_mvc", "test_color_mvc")));
        mvc.perform(MockMvcRequestBuilders.delete("/faculty?id=2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        when(facultyRepository.findById(333L)).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders.delete("/faculty?id=333"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testAdd() throws Exception {

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", "test_faculty_mvc");

        Faculty faculty = new Faculty(1L, "test_faculty_mvc", "test_color_mvc");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));

        mvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test_faculty_mvc"))
                .andExpect(jsonPath("$.color").value("test_color_mvc"));
    }

    @Test
    void testByColorOrName() throws Exception {
        when(facultyRepository.findAllByColorOrNameIgnoreCase(anyString(), anyString())).
                thenReturn(List.of(
                        new Faculty(1L, "name1", "color1"),
                        new Faculty(2L, "name2", "color2")
                ));

        mvc.perform(MockMvcRequestBuilders.get("/faculty/byColorOrName?name=name1&color=color2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[0].color").value("color1"))
                .andExpect(jsonPath("$[1].name").value("name2"))
                .andExpect(jsonPath("$[1].color").value("color2"));
    }

    @Test
    void testGetStudents() throws Exception {
        Faculty f = new Faculty(1L, "f1", "c1");
        f.setStudents(List.of(new Student(1L, "s1", 10)));

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(f));

        mvc.perform(MockMvcRequestBuilders.get("/faculty/students?facultyId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("s1"))
                .andExpect(jsonPath("$[0].age").value(10));

        mvc.perform(MockMvcRequestBuilders.get("/faculty/students?facultyId="))
                .andExpect(status().is(400));
    }
}
