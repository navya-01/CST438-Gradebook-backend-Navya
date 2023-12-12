package com.cst438.controllers;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import java.security.Principal;

@RestController
@CrossOrigin
public class AssignmentController {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @GetMapping("/assignment")
    public AssignmentDTO[] getAllAssignmentsForInstructor(Principal principal) {

        // get all assignments for this instructor
        System.out.print(principal.getName());

        String instructorEmail = principal.getName();  // username (should be instructor's email)
        List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
        AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < assignments.size(); i++) {
            Assignment as = assignments.get(i);
            AssignmentDTO dto = new AssignmentDTO(
                    as.getId(),
                    as.getName(),
                    sdf.format(as.getDueDate()),
                    as.getCourse().getTitle(),
                    as.getCourse().getCourse_id());
            result[i] = dto;
        }
        return result;
    }

    @GetMapping("/assignment/{assignment_id}") //get by ID

    public AssignmentDTO getListAssignment(@PathVariable("assignment_id") int assignment_id, Principal principal) {
        String userEmail = principal.getName();
        Assignment assignment = assignmentRepository.findById(assignment_id).orElse(null);
        if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found " + assignment_id);
        }
        if (!assignment.getCourse().getInstructor().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized " + assignment_id);
        }
        return new AssignmentDTO(
                assignment.getId(),
                assignment.getName(),
                assignment.getDueDate().toString(),
                assignment.getCourse().getTitle(),
                assignment.getCourse().getCourse_id()
        );

    }
    @PostMapping("/assignment")
    public AssignmentDTO createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        // Create a new assignment based on the provided AssignmentDTO
        Assignment assignment = new Assignment();
        assignment.setName(assignmentDTO.assignmentName());
        assignment.setCourse(courseRepository.findById(assignmentDTO.courseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found")));
        assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
        // Set other properties based on assignmentDTO...
        assignmentRepository.save(assignment);

        return new AssignmentDTO(
                assignment.getId(),
                assignment.getName(),
                assignment.getDueDate().toString(),
                assignment.getCourse().getTitle(),
                assignment.getCourse().getCourse_id());
    }

    @PutMapping("/assignment/{assignmentId}")
    public AssignmentDTO updateAssignment(@PathVariable int assignmentId, @RequestBody AssignmentDTO assignmentDTO) {
        // Update an existing assignment by its ID
        Assignment existingAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        // Update assignment properties based on assignmentDTO...
        existingAssignment.setName(assignmentDTO.assignmentName());
        existingAssignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
        existingAssignment.setCourse(courseRepository.findById(assignmentDTO.courseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course doesn't exist")));
        // Save the updated assignment to the database
        assignmentRepository.save(existingAssignment);

        return new AssignmentDTO(
                existingAssignment.getId(),
                existingAssignment.getName(),
                existingAssignment.getDueDate().toString(),
                existingAssignment.getCourse().getTitle(),
                existingAssignment.getCourse().getCourse_id());
    }

    @DeleteMapping("/assignment/{assignmentId}")
    public void deleteAssignment(@PathVariable int assignmentId,
                                 @RequestParam("force") Optional<Boolean> force) {
        // Delete an assignment by its ID
        Assignment existingAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        if((force.isEmpty() || (force.isPresent() && !force.get())) && !existingAssignment.getAssignmentGrades().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grades exist for this assignment! Use the force to delete.");
        }else if((force.isPresent() && force.get()) || existingAssignment.getAssignmentGrades().isEmpty()){
            assignmentRepository.delete(existingAssignment);
        }

    }


}

