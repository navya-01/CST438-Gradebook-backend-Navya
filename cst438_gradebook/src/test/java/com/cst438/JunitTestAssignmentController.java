package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cst438.domain.Course;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cst438.controllers.AssignmentController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestAssignmentController {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AssignmentRepository assignmentRepository;

    @Test
    public void addAssignmentTest() throws Exception {
        AssignmentDTO dto = new AssignmentDTO(1, "Code Review", "2001-03-01", "BUS 203 - Financial Accounting", 31249);
        Assignment ass = new Assignment();
        ass.setId(dto.id());
        ass.setName(dto.assignmentName());
        ass.setDueDate(Date.valueOf(dto.dueDate()));
        Course course = new Course();
        course.setCourse_id(dto.courseId());
        course.setTitle(dto.courseTitle());
        ass.setCourse(course);

        when(assignmentRepository.save(ass)).thenReturn(ass);

        MockHttpServletResponse resp;

        resp = mvc.perform(MockMvcRequestBuilders.post("/assignment")
                .content(asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();


        assertEquals(200, resp.getStatus());
    }

    @Test
    public void getAllAssignmentsTest() throws Exception {
        String email = "nshetty@csumb.edu";
        Course course = new Course();
        course.setCourse_id(31045);
        course.setTitle("CST 363 - Introduction to Database Systems");
        course.setSemester("Spring");
        course.setYear(2000);

        List<Assignment> assList = new ArrayList<>();

        Assignment first = new Assignment();
        first.setDueDate(Date.valueOf("2002-10-13"));
        first.setName("Assignment 3");
        first.setCourse(course);
        assList.add(first);

        Assignment second = new Assignment();
        second.setDueDate(Date.valueOf("2001-03-01"));
        second.setName("Assignment 4");
        second.setCourse(course);
        assList.add(second);

        course.setAssignments(assList);

        when(assignmentRepository.findByEmail(email)).thenReturn(assList);

        MockHttpServletResponse resp;
        resp = mvc.perform(MockMvcRequestBuilders.get("/assignment")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse();

        assertEquals(200, resp.getStatus());

        ArrayList<AssignmentDTO> arr = new ArrayList<>();
        for(Assignment a : assList){
            AssignmentDTO newDTO = new AssignmentDTO(a.getId(), a.getName(), a.getDueDate().toString(), a.getCourse().getTitle(), a.getCourse().getCourse_id());
            arr.add(newDTO);
        }
        assertEquals(2, arr.size());
        assertEquals("CST 363 - Introduction to Database Systems", arr.get(0).courseTitle());
        assertEquals("CST 363 - Introduction to Database Systems", arr.get(1).courseTitle());
        assertEquals("2002-10-13", arr.get(0).dueDate());
        assertEquals("2001-03-01", arr.get(1).dueDate());
        }

    @Test
    public void changeAssignmentTest() throws Exception {
        AssignmentDTO dto = new AssignmentDTO(1, "Code Review", "2001-03-01", "BUS 203 - Financial Accounting", 31249);
        Assignment ass = new Assignment();
        ass.setId(dto.id());
        ass.setName(dto.assignmentName());
        ass.setDueDate(Date.valueOf(dto.dueDate()));
        Course course = new Course();
        course.setCourse_id(dto.courseId());
        course.setTitle(dto.courseTitle());
        ass.setCourse(course);

        assertEquals("Code Review", ass.getName());
        assertEquals("2001-03-01", ass.getDueDate().toString());

        when(assignmentRepository.save(ass)).thenAnswer(invocation -> {
            Assignment assignment = new Assignment();
            ass.setName("Assignment 2");
            ass.setDueDate(Date.valueOf("2002-10-13"));
            return assignment;
        });

        assignmentRepository.save(ass);

        assertEquals("Assignment 2", ass.getName());
        assertEquals("2002-10-13", ass.getDueDate().toString());

    }

    @Test
    public void deleteAssignmentTest() throws Exception {
        AssignmentDTO dto = new AssignmentDTO(1, "Code Review", "2001-03-01", "BUS 203 - Financial Accounting", 31249);
        Assignment ass = new Assignment();
        ass.setId(dto.id());
        ass.setName(dto.assignmentName());
        ass.setDueDate(Date.valueOf(dto.dueDate()));
        Course course = new Course();
        course.setCourse_id(dto.courseId());
        course.setTitle(dto.courseTitle());
        ass.setCourse(course);

        when(assignmentRepository.findById(ass.getId())).thenReturn(Optional.of(ass));

        assignmentRepository.deleteById(ass.getId());

        verify(assignmentRepository, times(1)).deleteById(ass.getId());
    }

    private static String asJsonString(final Object obj) {
        try {

            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
